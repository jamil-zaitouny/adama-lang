/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.apikit.model;

import org.adamalang.apikit.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class Method {
  public final String name;
  public final String camelName;
  public final String camelName2;
  public final ParameterDefinition[] parameters;
  public final String documentation;
  public final Responder responder;
  public final String handler;
  public final String create;
  public final String createCamel;
  public final String findBy;
  public final int errorCantFindBy;
  public final boolean destroy;
  public final boolean callOnDisconnect;
  public final boolean devbox;
  public final String scope;

  public Method(String name, ParameterDefinition[] parameters, String documentation, Responder responder, String handler, String create, String findBy, int errorCantFindBy, boolean destroy, boolean callOnDisconnect, boolean devbox, String scope) {
    this.name = name;
    this.camelName = Common.camelize(name);
    this.camelName2 = Common.camelize(name, true);
    this.parameters = parameters;
    this.documentation = documentation;
    this.responder = responder;
    this.handler = handler != null && handler.length() == 0 ? null : handler;
    this.create = create != null && create.length() == 0 ? null : create;
    this.findBy = findBy != null && findBy.length() == 0 ? null : findBy;
    this.createCamel = Common.camelize(create);
    this.errorCantFindBy = errorCantFindBy;
    this.destroy = destroy;
    this.callOnDisconnect = callOnDisconnect;
    this.devbox = devbox;
    this.scope = scope;
  }

  public static Method[] methodsOf(Document document, Map<String, ParameterDefinition> parameters, Map<String, Responder> responders) throws Exception {
    NodeList list = document.getElementsByTagName("method");
    ArrayList<Method> methodsArrayList = new ArrayList<>();
    for (int k = 0; k < list.getLength(); k++) {
      Node node = list.item(k);
      Element element = (Element) node;
      String name = DocumentHelper.attribute(element, "name");
      String responderValue = DocumentHelper.attribute(element, "responder");
      Responder responder = responders.get(responderValue);
      if (responder == null) {
        throw new Exception("responder not found:" + responderValue);
      }
      String createValue = element.getAttribute("create");
      String findByValue = element.getAttribute("find-by");
      String errorCantFindByText = element.getAttribute("error-find-by");
      int errorCantFindBy = 0;
      if (findByValue != null && findByValue.length() > 0) {
        errorCantFindBy = Integer.parseInt(errorCantFindByText);
      }
      String handlerValue = element.getAttribute("handler");
      String scope = element.getAttribute("scope");
      if (handlerValue != null && handlerValue.length() > 0) {
        handlerValue = Common.camelize(handlerValue);
      } else {
        handlerValue = "Root" + Common.camelize(scope);
      }
      boolean destroy = "true".equals(element.getAttribute("destroy"));
      boolean devbox = "true".equals(element.getAttribute("devbox"));
      boolean callOnDisconnect = "true".equals(element.getAttribute("call-on-disconnect"));

      String documentation = null;
      ArrayList<ParameterDefinition> parametersArrayList = new ArrayList<>();
      NodeList children = node.getChildNodes();
      for (int j = 0; j < children.getLength(); j++) {
        Node childItem = children.item(j);
        if (childItem.getNodeType() == Node.ELEMENT_NODE) {
          Element childElement = (Element) childItem;
          if ("parameter".equals(childElement.getTagName())) {
            String parameterName = DocumentHelper.attribute(childElement, "name");
            ParameterDefinition parameter = parameters.get(parameterName);
            if (parameter == null) {
              throw new Exception("unable to find parameter: " + parameterName);
            }
            parametersArrayList.add(parameter);
          } else if ("documentation".equals(childElement.getTagName())) {
            documentation = childElement.getTextContent();
          } else {
            throw new Exception("unrecognized tag under method:" + childElement);
          }
        }
      }
      if (documentation == null) {
        throw new Exception("method has no documentation");
      }
      System.out.println("\u001b[36mAPI:\u001b[0m" + name);
      methodsArrayList.add(new Method(name, parametersArrayList.toArray(new ParameterDefinition[parametersArrayList.size()]), documentation, responder, handlerValue, createValue, findByValue, errorCantFindBy, destroy, callOnDisconnect, devbox, scope));
    }
    return methodsArrayList.toArray(new Method[methodsArrayList.size()]);
  }

  public TreeSet<String> imports(String sessionImport) {
    TreeSet<String> set = new TreeSet<>();
    set.add("org.adamalang.common.ErrorCodeException");
    set.add("org.adamalang.web.io.*");
    set.add("org.adamalang.common.Callback");
    set.add(sessionImport);
    set.add("org.adamalang.common.NamedRunnable");
    set.add("com.fasterxml.jackson.databind.node.ObjectNode");

    for (ParameterDefinition parameter : parameters) {
      Transform transform = parameter.getTransform(this.name);
      if (transform != null) {
        if (transform.outputJavaType.indexOf('.') > 0) {
          set.add(transform.outputJavaType);
        }
      }
      if (parameter.validator != null) {
        set.add(parameter.validator.service);
      }
      if (parameter.type == Type.JsonObject) {
        set.add("com.fasterxml.jackson.databind.node.ObjectNode");
      }
      if (parameter.type == Type.JsonObjectOrArray) {
        set.add("com.fasterxml.jackson.databind.JsonNode");
      }
    }
    return set;
  }
}
