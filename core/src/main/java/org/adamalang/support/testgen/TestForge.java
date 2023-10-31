/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.support.testgen;

import org.adamalang.runtime.ops.SilentDocumentMonitor;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class TestForge {
  public static String forge(final boolean emission, final String className, final Path path, final Path inputRoot) {
    final var outputFile = new StringBuilder();
    final var passedTests = new AtomicBoolean(true);
    try {
      final var results = PhaseValidate.go(inputRoot, path, className, emission, outputFile);
      final var passedValidation = results.passedValidation;
      final var java = results.java;
      LivingDocumentFactory factory = null;
      if (passedValidation) {
        factory = PhaseCompile.go(className, java, outputFile);
      }
      if (factory != null) {
        final SilentDocumentMonitor monitor = new SilentDocumentMonitor() {
          @Override
          public void assertFailureAt(final int startLine, final int startPosition, final int endLine, final int endLinePosition, final int total, final int failures) {
            outputFile.append("ASSERT FAILURE:" + startLine + "," + startPosition + " --> " + endLine + "," + endLinePosition + " (" + failures + "/" + total + ")\n");
            passedTests.set(false);
          }
        };
        PhaseReflect.go(results, outputFile);
        PhaseRun.go(factory, monitor, passedTests, outputFile);
        PhaseTest.go(factory, monitor, passedTests, outputFile);
      }
      if (passedValidation) {
        if (passedTests.get()) {
          outputFile.append("Success").append("\n");
        } else {
          outputFile.append("AlmostTestsNotPassing").append("\n");
        }
      } else {
        outputFile.append("FailedValidation").append("\n");
      }
    } catch (final Exception ioe) {
      outputFile.append("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!").append("\n");
      outputFile.append("!!EXCEPTION!!!!!!!!!!!!!!!!!!").append("\n");
      outputFile.append("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!").append("\n");
      outputFile.append(String.format("path: %s failed due to to exception", className)).append("\n");
      final var memory = new ByteArrayOutputStream();
      final var writer = new PrintWriter(memory);
      ioe.printStackTrace(writer);
      writer.flush();
      outputFile.append(memory.toString()).append("\n");
    }
    return outputFile.toString() //
                     .replaceAll(Pattern.quote("\\\\test_code\\\\"), "/test_code/") //
                     .replaceAll(Pattern.quote("\\test_code\\"), "/test_code/") //
                     .replaceAll(Pattern.quote("\\\\\\\\test_code"), "/test_code") //
                     .replaceAll(Pattern.quote("\\\\test_code"), "/test_code") //
                     .replaceAll(Pattern.quote("\\test_code"), "/test_code"); //
  }

  public static TreeMap<String, TestClass> scan(final File root) throws IOException {
    final var classMap = new TreeMap<String, TestClass>();
    final var files = new ArrayList<File>();
    for (final File testFile : root.listFiles()) {
      files.add(testFile);
    }
    files.sort(Comparator.comparing(File::getName));
    String lastClazz = "";
    for (final File testFile : files) {
      final var test = TestFile.fromFilename(testFile.getName());
      if (!lastClazz.equals(test.clazz)) {
        System.out.print("\u001b[34mSuite: " + test.clazz + "\u001b[0m\n");
        lastClazz = test.clazz;
      }
      System.out.print("  " + test.name);
      for (int pad = test.name.length(); pad < 45; pad++) {
        System.out.print(" ");
      }
      System.out.print("... ");
      var testClass = classMap.get(test.clazz);
      if (testClass == null) {
        testClass = new TestClass(test.clazz);
        classMap.put(test.clazz, testClass);
      }
      boolean result = testClass.addTest(test);
      System.out.print((test.success == result) ? "\u001b[32mGOOD\u001b[0m\n" : "\u001b[31mBAD\u001b[0m\n");
    }
    return classMap;
  }
}
