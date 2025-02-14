/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.natives.functions;

public enum FunctionStyleJava {
  ExpressionThenArgs(false), //
  ExpressionThenNameWithArgs(true), //
  RemoteCall(true), //
  InjectName(true), //
  InjectNameThenArgs(true), //
  InjectNameThenExpressionAndArgs(true), //
  None(false), //
  ;

  public final boolean useOnlyExpressionInLookup;

  FunctionStyleJava(final boolean useOnlyExpressionInLookup) {
    this.useOnlyExpressionInLookup = useOnlyExpressionInLookup;
  }

}
