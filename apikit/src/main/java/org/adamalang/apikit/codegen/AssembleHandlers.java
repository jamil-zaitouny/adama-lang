/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.apikit.codegen;

import org.adamalang.apikit.model.Common;
import org.adamalang.apikit.model.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AssembleHandlers {

  public static Map<String, String> make(String packageName, String sessionImport, Method[] methods) throws Exception {
    HashMap<String, ArrayList<Method>> byHandler = shred(methods);
    Map<String, String> files = new HashMap<>();
    for (Map.Entry<String, ArrayList<Method>> entry : byHandler.entrySet()) {
      String root = entry.getKey();
      StringBuilder java = new StringBuilder();
      java.append("package ").append(packageName).append(";\n\n");
      if (!root.startsWith("Root")) {
        java.append("import com.fasterxml.jackson.databind.node.ObjectNode;\n");
      }
      java.append("import " + sessionImport + ";\n");
      java.append("\n");
      String rootSuffix = "";
      java.append("public interface ").append(root).append("Handler {\n");
      if (!root.startsWith("Root")) {
        java.append("  public void bind();\n\n");
      }
      for (Method method : byHandler.get(root)) {
        java.append("  public ");
        if (method.create != null) {
          java.append(Common.camelize(method.create)).append("Handler");
        } else {
          java.append("void");
        }
        if (!root.startsWith("Root")) {
          java.append(" handle(").append(method.camelName).append("Request request, ").append(method.responder.camelName).append("Responder responder);\n\n");
        } else {
          java.append(" handle(Session session, ").append(method.camelName).append("Request request, ").append(method.responder.camelName).append("Responder responder);\n\n");
        }
      }
      if (!root.startsWith("Root")) {
        java.append("  public void logInto(ObjectNode node);\n\n");
      }
      if (root.startsWith("Root")) {
        java.append("  public void disconnect();\n\n");
      } else {
        java.append("  public void disconnect(long id);\n\n");
      }
      if (root.startsWith("Root")) {
        String rootScope = root.substring(4).trim().toLowerCase(Locale.ENGLISH);
        java.append("  public static boolean test(String method) {\n");
        java.append("    switch (method) {\n");
        for (Method method: methods) {
          if (rootScope.equals(method.scope)) {
            java.append("      case \"").append(method.name).append("\":\n");
          }
        }
        java.append("        return true;\n");
        java.append("      default:\n");
        java.append("        return false;\n");
        java.append("    }\n");
        java.append("  }\n");
      }
      java.append("}\n");
      files.put(root + "Handler.java", java.toString());
    }
    return files;
  }

  public static HashMap<String, ArrayList<Method>> shred(Method[] methods) {
    HashMap<String, ArrayList<Method>> byHandler = new HashMap<>();
    for (Method method : methods) {
      ArrayList<Method> methodsForHandler = byHandler.get(method.handler);
      if (methodsForHandler == null) {
        methodsForHandler = new ArrayList<>();
        byHandler.put(method.handler, methodsForHandler);
      }
      methodsForHandler.add(method);
    }
    return byHandler;
  }
}
