/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import org.adamalang.common.Escaping;
import org.adamalang.rxhtml.atl.ParseException;
import org.adamalang.rxhtml.atl.Parser;
import org.adamalang.rxhtml.atl.tree.Tree;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class RxObject {
  public final String rxObj;
  public final boolean delayed;
  private final Environment env;
  private final ArrayList<Attribute> attributes;

  public RxObject(Environment env, String... names) {
    this.env = env;
    this.attributes = new ArrayList<>();
    rxObj = env.pool.ask();
    env.writer.tab().append("var ").append(rxObj).append("=$.RX([");
    ArrayList<String> attrToRemove = new ArrayList<>();
    boolean addedUnder = false;
    boolean _delayed = false;
    for (String attrName : names) {
      String nameToUse = attrName;
      if (nameToUse.startsWith("parameter:")) {
        nameToUse = nameToUse.substring(10);
        attrToRemove.add(attrName);
      }
      nameToUse = nameToUse.replaceAll(Pattern.quote(":"), "_");
      if (env.element.hasAttr(attrName)) {
        try {
          Tree tree = Parser.parse(env.element.attr(attrName));
          if (tree.variables().size() > 0) {
            if (!addedUnder) {
              addedUnder = true;
            } else {
              env.writer.append(",");
            }
            env.writer.append("'").append(nameToUse).append("'");
          }
        } catch (ParseException pe) {
          env.feedback.warn(env.element, "attribute '" + attrName + "' has parser errors; " + pe.getMessage());
        }
      }
    }
    env.writer.append("]);").newline();
    for (String attrName : names) {
      String nameToUse = attrName;
      if (nameToUse.startsWith("parameter:")) {
        nameToUse = nameToUse.substring(10);
      }
      nameToUse = nameToUse.replaceAll(Pattern.quote(":"), "_");
      if (env.element.hasAttr(attrName)) {
        String value = env.element.attr(attrName);
        try {
          Tree tree = Parser.parse(value);
          Map<String, String> vars = tree.variables();
          if (vars.size() > 0) {
            for (Map.Entry<String, String> ve : vars.entrySet()) {
              StatePath path = StatePath.resolve(ve.getValue(), env.stateVar);
              String subItem = env.pool.ask();
              env.writer.tab().append("$.Y2(").append(path.command).append(",").append(rxObj).append(",'").append(nameToUse).append("','").append(path.name).append("',").append("function(").append(subItem).append(") {").tabUp().newline();
              env.writer.tab().append(rxObj).append(".").append(nameToUse).append("=").append(tree.js(env.contextOf(attrName), subItem)).newline();
              env.writer.tab().append(rxObj).append(".__();").tabDown().newline();
              env.writer.tab().append("});").newline();
              env.pool.give(subItem);
            }
            _delayed = true;
          } else {
            env.writer.tab().append(rxObj).append(".").append(nameToUse).append("='").append(new Escaping(value).switchQuotes().go()).append("';").newline();
          }
        } catch (ParseException pe) {
          env.feedback.warn(env.element, "attribute '" + attrName + "' has parse exception; " + pe.getMessage());
        }
      } else {
        env.writer.tab().append(rxObj).append(".").append(nameToUse).append("=true;").newline();
      }
    }
    this.delayed = _delayed;
    for (String param : attrToRemove) {
      env.element.removeAttr(param);
    }
  }

  public static String[] pullParameters(Element element) {
    ArrayList<String> params = new ArrayList<>();
    for (Attribute attr : element.attributes()) {
      if (attr.getKey().startsWith("parameter:")) {
        params.add(attr.getKey());
      }
    }
    return params.toArray(new String[params.size()]);
  }

  public void finish() {
    if (!delayed) {
      env.writer.tab().append(rxObj).append(".__();").newline();
    }
  }
}
