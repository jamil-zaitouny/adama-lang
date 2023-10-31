/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateReactiveCustomDataTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c=$.RX(['two','three']);");
    gold.append("\n    c.one='one';");
    gold.append("\n    $.Y2(a,c,'two','var',function(d) {");
    gold.append("\n      c.two=d['var']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2(a,c,'three','var1',function(d) {");
    gold.append("\n      c.three=d['var1'] + d['var2']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2(a,c,'three','var2',function(d) {");
    gold.append("\n      c.three=d['var1'] + d['var2']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    c.present='';");
    gold.append("\n    $.CUDA(b,a,'',c,'/sign-in',function(d) {");
    gold.append("\n    });");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.RX(['two','three']);");
    gold.append("\n    c.one='one';");
    gold.append("\n    $.Y2(a,c,'two','var',function(d) {");
    gold.append("\n      c.two=d['var']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2(a,c,'three','var1',function(d) {");
    gold.append("\n      c.three=d['var1'] + d['var2']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    $.Y2(a,c,'three','var2',function(d) {");
    gold.append("\n      c.three=d['var1'] + d['var2']");
    gold.append("\n      c.__();");
    gold.append("\n    });");
    gold.append("\n    c.present='';");
    gold.append("\n    $.CUDA(b,a,'',c,'/sign-in',function(d) {");
    gold.append("\n    });");
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
    source.append("\n        <customdata parameter:one=\"one\" parameter:two=\"{var}\"  parameter:three=\"{var1}{var2}\" parameter:present>");
    source.append("\n");
    source.append("\n        </customdata>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
