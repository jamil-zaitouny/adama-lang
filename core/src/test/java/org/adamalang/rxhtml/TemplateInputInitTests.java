/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateInputInitTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c = $.E('input');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    {");
    gold.append("\n      var d = {};");
    gold.append("\n      d.__dom = c;");
    gold.append("\n      var e = (function() {");
    gold.append("\n        this.__dom.value=this['value'];");
    gold.append("\n      }).bind(d);");
    gold.append("\n      $.Y(a,d,'value',e);");
    gold.append("\n      e();");
    gold.append("\n    }");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('textarea');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    {");
    gold.append("\n      var d = {};");
    gold.append("\n      d.__dom = c;");
    gold.append("\n      var e = (function() {");
    gold.append("\n        this.__dom.value=this['value'];");
    gold.append("\n      }).bind(d);");
    gold.append("\n      $.Y(a,d,'value',e);");
    gold.append("\n      e();");
    gold.append("\n    }");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('select');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    {");
    gold.append("\n      var d = {};");
    gold.append("\n      d.__dom = c;");
    gold.append("\n      var e = (function() {");
    gold.append("\n        this.__dom.value=this['value'];");
    gold.append("\n      }).bind(d);");
    gold.append("\n      $.Y(a,d,'value',e);");
    gold.append("\n      e();");
    gold.append("\n    }");
    gold.append("\n    var d = $.E('option');");
    gold.append("\n    $.SA(d,'value',\"foo\");");
    gold.append("\n    d.append($.T('FOOO'));");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('input');");
    gold.append("\n    $.SA(c,'type',\"checkbox\");");
    gold.append("\n    c.checked=true;");
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
    gold.append("\n    var c = $.E('input');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    {");
    gold.append("\n      var d = {};");
    gold.append("\n      d.__dom = c;");
    gold.append("\n      var e = (function() {");
    gold.append("\n        this.__dom.value=this['value'];");
    gold.append("\n      }).bind(d);");
    gold.append("\n      $.Y(a,d,'value',e);");
    gold.append("\n      e();");
    gold.append("\n    }");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('textarea');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    {");
    gold.append("\n      var d = {};");
    gold.append("\n      d.__dom = c;");
    gold.append("\n      var e = (function() {");
    gold.append("\n        this.__dom.value=this['value'];");
    gold.append("\n      }).bind(d);");
    gold.append("\n      $.Y(a,d,'value',e);");
    gold.append("\n      e();");
    gold.append("\n    }");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('select');");
    gold.append("\n    $.SA(c,'name',\"email\");");
    gold.append("\n    {");
    gold.append("\n      var d = {};");
    gold.append("\n      d.__dom = c;");
    gold.append("\n      var e = (function() {");
    gold.append("\n        this.__dom.value=this['value'];");
    gold.append("\n      }).bind(d);");
    gold.append("\n      $.Y(a,d,'value',e);");
    gold.append("\n      e();");
    gold.append("\n    }");
    gold.append("\n    var d = $.E('option');");
    gold.append("\n    $.SA(d,'value',\"foo\");");
    gold.append("\n    d.append($.T('FOOO'));");
    gold.append("\n    c.append(d);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('input');");
    gold.append("\n    $.SA(c,'type',\"checkbox\");");
    gold.append("\n    c.checked=true;");
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
    source.append("\n        <input name=\"email\" value=\"{value}\"/>");
    source.append("\n        <textarea name=\"email\" value=\"{value}\"></textarea>");
    source.append("\n        <select name=\"email\" value=\"{value}\">");
    source.append("\n            <option value=\"foo\">FOOO</option>");
    source.append("\n        </select>");
    source.append("\n        <input type=\"checkbox\" checked rx:debounce=\"nope\" />");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
