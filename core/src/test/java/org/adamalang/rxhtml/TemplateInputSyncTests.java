/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

public class TemplateInputSyncTests extends BaseRxHtmlTest {
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("WARNING:zoop should be a numeric value");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.E('input');");
    gold.append("\n    c.setAttribute('name','email');");
    gold.append("\n    b.append(c);");
    gold.append("\n    $.SY(c,$.pV(a),'foo',50.0);");
    gold.append("\n    var c = $.E('textarea');");
    gold.append("\n    c.setAttribute('name','email');");
    gold.append("\n    b.append(c);");
    gold.append("\n    $.SY(c,$.pD(a),'foo',100.0);");
    gold.append("\n    var c = $.E('select');");
    gold.append("\n    c.setAttribute('name','email');");
    gold.append("\n    var d = $.E('option');");
    gold.append("\n    d.setAttribute('value','foo');");
    gold.append("\n    d.append($.T('FOOO'));");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    $.SY(c,$.pV(a),'foo',100.0);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><link rel=\"stylesheet\" href=\"/template.css\"><script src=\"/template.js\"></script></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <input name=\"email\" rx:sync=\"foo\" rx:debounce=\"zoop\"/>");
    source.append("\n        <textarea name=\"email\" rx:sync=\"data:foo\" ></textarea>");
    source.append("\n        <select name=\"email\" rx:sync=\"view:foo\">");
    source.append("\n            <option value=\"foo\">FOOO</option>");
    source.append("\n        </select>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
