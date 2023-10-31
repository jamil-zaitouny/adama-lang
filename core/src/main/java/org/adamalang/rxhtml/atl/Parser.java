/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl;

import org.adamalang.rxhtml.atl.tree.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public class Parser {

  // handle transforms
  private static Tree wrapTransforms(Tree root, TokenStream.Token token) {
    Tree n = root;
    for (int k = 0; k < token.transforms.length; k++) {
      n = new Transform(n, token.transforms[k]);
    }
    return n;
  }

  private static Tree of(ArrayList<Tree> children) {
    if (children.size() > 1) {
      return new Concat(children.toArray(new Tree[children.size()]));
    } else if (children.size() == 1) {
      return children.get(0);
    } else {
      return new Empty();
    }
  }

  private static Tree condition(Iterator<TokenStream.Token> it, TokenStream.Token name) {
    ArrayList<Tree> childrenTrue = new ArrayList<>();
    ArrayList<Tree> childrenFalse = new ArrayList<>();
    ArrayList<Tree> active = childrenTrue;
    while (it.hasNext()) {
      TokenStream.Token token = it.next();
      if (token.type == TokenStream.Type.Condition && token.base.equals(name.base)) {
        if (token.mod == TokenStream.Modifier.Else) {
          active = childrenFalse;
          continue;
        } else if (token.mod == TokenStream.Modifier.End) {
          break;
        }
      } else {
        route(active, it, token);
      }
    }
    Tree lookup;
    if (name.base.contains("=")) {
      String[] parts = name.base.split(Pattern.quote("="));
      lookup = new Equals(new Lookup(parts[0].trim()), parts[1].trim());
    } else {
      lookup = new Lookup(name.base);
    }
    Tree guard_lookup = wrapTransforms(lookup, name);
    return new Condition(name.mod == TokenStream.Modifier.Not ? new Negate(guard_lookup) : guard_lookup, of(childrenTrue), of(childrenFalse));
  }

  private static void route(ArrayList<Tree> children, Iterator<TokenStream.Token> it, TokenStream.Token token) {
    switch (token.type) {
      case Text:
        children.add(new Text(token.base));
        return;
      case Variable:
        children.add(wrapTransforms(new Lookup(token.base), token));
        return;
      case Condition:
        children.add(condition(it, token));
    }
  }

  private static Tree parse(Iterator<TokenStream.Token> it) {
    ArrayList<Tree> children = new ArrayList<>();
    while (it.hasNext()) {
      route(children, it, it.next());
    }
    return of(children);
  }

  public static Tree parse(String text) {
    return parse(TokenStream.tokenize(text).iterator());
  }
}
