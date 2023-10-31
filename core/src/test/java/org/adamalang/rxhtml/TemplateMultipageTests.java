/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml;

public class TemplateMultipageTests extends BaseRxHtmlTest {
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
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Text type user profile '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['string','usery'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Text type user profile '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','profile'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' No type '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','settings','text','key'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','sets1','number','key'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','sets2','int','key'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','sets3','double','key'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','yolo'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/bounce');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','bounce'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','login'], function(b,a) {");
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Text type user profile '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['string','usery'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Text type user profile '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','profile'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' No type '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','settings','text','key'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','sets1','number','key'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','sets2','int','key'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['text','user','fixed','sets3','double','key'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n    b.append($.T(' Keys for a user '));");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','yolo'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/bounce');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','bounce'], function(b,a) {");
    gold.append("\n    var c = $.aRDz('/login');");
    gold.append("\n    if ($.ID('default',c).abort) {");
    gold.append("\n      return;");
    gold.append("\n    }");
    gold.append("\n  });");
    gold.append("\n  $.PG(['fixed','login'], function(b,a) {");
    gold.append("\n    b.append($.T(' Simple Page '));");
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
    source.append("\n    <page uri=\"/\" authenticate>");
    source.append("\n        Simple Page");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user:text\" authenticate>");
    source.append("\n        Text type user profile");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$usery:string\" authenticate>");
    source.append("\n        Text type user profile");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/profile\" authenticate>");
    source.append("\n        No type");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/settings/$key\" authenticate>");
    source.append("\n        Keys for a user");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/sets1/$key:number\" authenticate>");
    source.append("\n        Keys for a user");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/sets2/$key:int\" authenticate>");
    source.append("\n        Keys for a user");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/$user/sets3/$key:double\" authenticate>");
    source.append("\n        Keys for a user");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/yolo\" authenticate redirect=\"/bounce\">");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/bounce\" authenticate>");
    source.append("\n");
    source.append("\n    </page>");
    source.append("\n    <page uri=\"/login\" default-redirect-source>");
    source.append("\n        Simple Page");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}
