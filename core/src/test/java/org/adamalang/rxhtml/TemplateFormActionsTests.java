/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml;

public class TemplateFormActionsTests extends BaseRxHtmlTest {
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
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.rx_forward='/yes';");
    gold.append("\n    $.aSO(c,a,'default',d);");
    gold.append("\n    $.onS(c,'success',$.pV(a),'faied_to_signin',false);");
    gold.append("\n    $.onS(c,'failure',$.pV(a),'faied_to_signin',true);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','email');");
    gold.append("\n    g.setAttribute('type','email');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','password');");
    gold.append("\n    g.setAttribute('type','password');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('name','remember');");
    gold.append("\n    g.setAttribute('type','checkbox');");
    gold.append("\n    c.append(g);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    g.setAttribute('type','submit');");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var g=$.RX([]);");
    gold.append("\n    g.rx_forward='/w00t';");
    gold.append("\n    $.aDSO(c,a,'default',g);");
    gold.append("\n    $.onS(c,'success',$.pV(a),'docnope',false);");
    gold.append("\n    $.onS(c,'failure',$.pV(a),'docnope',true);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    j.setAttribute('type','hidden');");
    gold.append("\n    j.setAttribute('name','space');");
    gold.append("\n    j.value='s ';");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    j.setAttribute('type','hidden');");
    gold.append("\n    j.setAttribute('name','key');");
    gold.append("\n    j.value='k';");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    j.setAttribute('name','username');");
    gold.append("\n    j.setAttribute('type','username');");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    j.setAttribute('name','password');");
    gold.append("\n    j.setAttribute('type','password');");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    j.setAttribute('name','remember');");
    gold.append("\n    j.setAttribute('type','checkbox');");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    j.setAttribute('type','submit');");
    gold.append("\n    c.append(j);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var j=$.RX([]);");
    gold.append("\n    j.space=true;");
    gold.append("\n    j.key=true;");
    gold.append("\n    j.path=true;");
    gold.append("\n    j.rx_forward='/';");
    gold.append("\n    $.aDPUT(c,a,'default',j);");
    gold.append("\n    $.onS(c,'failure',$.pV(a),'fail',true);");
    gold.append("\n    $.onTE(c,'failure',$.pV(a),'msg');");
    gold.append("\n    var l = $.E('input');");
    gold.append("\n    l.setAttribute('type','submit');");
    gold.append("\n    c.append(l);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCC(c,a,'foo');");
    gold.append("\n    $.onS(c,'success',$.pV(a),'sign_in_failed',false);");
    gold.append("\n    $.onS(c,'failure',$.pV(a),'sign_in_failed',true);");
    gold.append("\n    var n = $.E('input');");
    gold.append("\n    n.setAttribute('type','submit');");
    gold.append("\n    c.append(n);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSU(c,a,'/');");
    gold.append("\n    $.onS(c,'success',$.pV(a),'sign_up_failed',false);");
    gold.append("\n    $.onS(c,'failure',$.pV(a),'sign_up_failed',true);");
    gold.append("\n    var p = $.E('input');");
    gold.append("\n    p.setAttribute('name','email');");
    gold.append("\n    p.setAttribute('type','email');");
    gold.append("\n    c.append(p);");
    gold.append("\n    var p = $.E('input');");
    gold.append("\n    p.setAttribute('type','submit');");
    gold.append("\n    c.append(p);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSP(c,a,'/');");
    gold.append("\n    $.onS(c,'success',$.pV(a),'set_password_failed',false);");
    gold.append("\n    $.onS(c,'failure',$.pV(a),'set_password_failed',true);");
    gold.append("\n    var r = $.E('input');");
    gold.append("\n    r.setAttribute('name','email');");
    gold.append("\n    r.setAttribute('type','email');");
    gold.append("\n    c.append(r);");
    gold.append("\n    var r = $.E('input');");
    gold.append("\n    r.setAttribute('name','password');");
    gold.append("\n    r.setAttribute('type','password');");
    gold.append("\n    c.append(r);");
    gold.append("\n    var r = $.E('input');");
    gold.append("\n    r.setAttribute('name','code');");
    gold.append("\n    c.append(r);");
    gold.append("\n    var r = $.E('input');");
    gold.append("\n    r.setAttribute('type','submit');");
    gold.append("\n    c.append(r);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'channel');");
    gold.append("\n    $.onS(c,'success',$.pV(a),'send_failed',false);");
    gold.append("\n    $.onS(c,'failure',$.pV(a),'send_failed',true);");
    gold.append("\n    var t = $.E('input');");
    gold.append("\n    t.setAttribute('name','param');");
    gold.append("\n    c.append(t);");
    gold.append("\n    var t = $.E('input');");
    gold.append("\n    t.setAttribute('type','submit');");
    gold.append("\n    c.append(t);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCP(c,$.pV(a),'xyz');");
    gold.append("\n    var t = $.E('input');");
    gold.append("\n    t.setAttribute('name','param');");
    gold.append("\n    c.append(t);");
    gold.append("\n    var t = $.E('input');");
    gold.append("\n    t.setAttribute('type','submit');");
    gold.append("\n    c.append(t);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var t=$.RX([]);");
    gold.append("\n    t.rx_forward=true;");
    gold.append("\n    $.aUP(c,a,'default',t);");
    gold.append("\n    $.onS(c,'success',$.pV(a),'asset_upload_failed',false);");
    gold.append("\n    $.onS(c,'failure',$.pV(a),'asset_upload_failed',true);");
    gold.append("\n    var w = $.E('input');");
    gold.append("\n    w.setAttribute('name','space');");
    gold.append("\n    c.append(w);");
    gold.append("\n    var w = $.E('input');");
    gold.append("\n    w.setAttribute('name','key');");
    gold.append("\n    c.append(w);");
    gold.append("\n    var w = $.E('input');");
    gold.append("\n    w.setAttribute('type','submit');");
    gold.append("\n    c.append(w);");
    gold.append("\n    b.append(c);");
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
    source.append("\n        Simple Page");
    source.append("\n        <form rx:action=\"adama:sign-in\" rx:failure-variable=\"faied_to_signin\" rx:forward=\"/yes\">");
    source.append("\n            <input name=\"email\" type=\"email\" />");
    source.append("\n            <input name=\"password\" type=\"password\" />");
    source.append("\n            <input name=\"remember\" type=\"checkbox\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"document:sign-in\" rx:failure-variable=\"docnope\" rx:forward=\"/w00t\">");
    source.append("\n            <input type=\"hidden\" name=\"space\" value=\"s \"/>");
    source.append("\n            <input type=\"hidden\" name=\"key\" value=\"k\" />");
    source.append("\n            <input name=\"username\" type=\"username\" />");
    source.append("\n            <input name=\"password\" type=\"password\" />");
    source.append("\n            <input name=\"remember\" type=\"checkbox\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"document:put\" rx:failure=\"raise:fail te:msg\">");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"custom:foo\">");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:sign-up\">");
    source.append("\n            <input name=\"email\" type=\"email\"/>");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:set-password\">");
    source.append("\n            <input name=\"email\" type=\"email\" />");
    source.append("\n            <input name=\"password\" type=\"password\" />");
    source.append("\n            <input name=\"code\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"send:channel\">");
    source.append("\n            <input name=\"param\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"copy:xyz\">");
    source.append("\n            <input name=\"param\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:upload-asset\">");
    source.append("\n            <input name=\"space\" />");
    source.append("\n            <input name=\"key\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
