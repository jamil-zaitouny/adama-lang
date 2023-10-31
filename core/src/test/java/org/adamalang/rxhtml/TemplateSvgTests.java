/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateSvgTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('svg', 'http://www.w3.org/2000/svg');");
    gold.append("\n    $.SA(c,'height',\"210\");");
    gold.append("\n    $.SA(c,'width',\"400\");");
    gold.append("\n    var d = $.E('path', 'http://www.w3.org/2000/svg');");
    gold.append("\n    $.SA(d,'d',\"M150 0 L75 200 L225 200 Z\");");
    gold.append("\n    c.append(d);");
    gold.append("\n    c.append($.T(' Sorry, your browser does not support inline SVG. '));");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('svg', 'http://www.w3.org/2000/svg');");
    gold.append("\n    $.SA(c,'height',\"210\");");
    gold.append("\n    $.SA(c,'width',\"400\");");
    gold.append("\n    var d = $.E('path', 'http://www.w3.org/2000/svg');");
    gold.append("\n    $.SA(d,'d',\"M150 0 L75 200 L225 200 Z\");");
    gold.append("\n    c.append(d);");
    gold.append("\n    c.append($.T(' Sorry, your browser does not support inline SVG. '));");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</script><style>");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</style></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n");
    source.append("\n        <svg height=\"210\" width=\"400\">");
    source.append("\n            <path d=\"M150 0 L75 200 L225 200 Z\" />");
    source.append("\n            Sorry, your browser does not support inline SVG.");
    source.append("\n        </svg>");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
