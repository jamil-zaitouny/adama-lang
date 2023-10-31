/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.support;

import org.adamalang.common.DefaultCopyright;
import org.adamalang.rxhtml.Bundler;
import org.adamalang.rxhtml.template.config.Feedback;
import org.adamalang.rxhtml.RxHtmlTool;
import org.adamalang.rxhtml.template.config.ShellConfig;
import org.adamalang.support.testgen.TestClass;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

public class GenerateTemplateTests {
  public static void generate(String inputRootPath, String outputJavaPath) throws Exception {
    if (isValid(inputRootPath) && isValid(outputJavaPath)) {
      final var root = new File(inputRootPath);
      final var outRoot = new File(outputJavaPath);
      for (File file : root.listFiles()) {
        if (!file.getName().endsWith(".rx.html")) {
          continue;
        }
        System.out.println("\u001b[36mTemplate:\u001b[0m" + file.getName());
        StringBuilder issues = new StringBuilder();
        Feedback feedback = (element, warning) -> {
          System.out.println("  " + warning);
          issues.append("WARNING:").append(warning).append("\n");
        };
        String gold = RxHtmlTool.convertStringToTemplateForest(Bundler.bundle(Collections.singletonList(file)), ShellConfig.start().withFeedback(feedback).end()).toString();
        String name = file.getName().substring(0, file.getName().length() - 8).replace(Pattern.quote("."), "_");
        name = name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        String classname = "Template" + name + "Tests";
        StringBuilder output = new StringBuilder();
        output.append(DefaultCopyright.COPYRIGHT_FILE_PREFIX);
        output.append("package org.adamalang.rxhtml;\n\n");
        output.append("public class ").append(classname).append(" extends BaseRxHtmlTest {\n");
        output.append("  @Override\n");
        output.append("  public String issues() {\n");
        TestClass.writeStringBuilder(issues.toString(), output, "issues");
        output.append("    return issues.toString();\n");
        output.append("  }\n");
        output.append("  @Override\n");
        output.append("  public String gold() {\n");
        TestClass.writeStringBuilder(gold, output, "gold");
        output.append("    return gold.toString();\n");
        output.append("  }\n");
        output.append("  @Override\n");
        output.append("  public String source() {\n");
        TestClass.writeStringBuilder(Files.readString(file.toPath()), output, "source");
        output.append("    return source.toString();\n");
        output.append("  }\n");
        output.append("}\n");
        Files.writeString(new File(outRoot, classname + ".java").toPath(), output.toString());
      }
    }
  }

  private static boolean isValid(String path) {
    File p = new File(path);
    return p.exists() && p.isDirectory();
  }
}
