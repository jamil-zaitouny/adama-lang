/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator;

import org.junit.Test;

public class GeneratedBlockTests extends GeneratedBase {
  private String cached_DeadCode_1 = null;
  private String get_DeadCode_1() {
    if (cached_DeadCode_1 != null) {
      return cached_DeadCode_1;
    }
    cached_DeadCode_1 = generateTestOutput(false, "DeadCode_1", "./test_code/Block_DeadCode_failure.a");
    return cached_DeadCode_1;
  }

  @Test
  public void testDeadCodeFailure() {
    assertLiveFail(get_DeadCode_1());
  }

  @Test
  public void testDeadCodeNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DeadCode_1());
  }

  @Test
  public void testDeadCodeExceptionFree() {
    assertExceptionFree(get_DeadCode_1());
  }

  @Test
  public void testDeadCodeTODOFree() {
    assertTODOFree(get_DeadCode_1());
  }

  @Test
  public void stable_DeadCode_1() {
    String live = get_DeadCode_1();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Block_DeadCode_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":2,\"character\":2,\"byte\":25},\"end\":{\"line\":2,\"character\":14,\"byte\":37}},\"severity\":1,\"source\":\"error\",\"message\":\"This code is unreachable.\",\"file\":\"./test_code/Block_DeadCode_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
  private String cached_DuplicateVariable_2 = null;
  private String get_DuplicateVariable_2() {
    if (cached_DuplicateVariable_2 != null) {
      return cached_DuplicateVariable_2;
    }
    cached_DuplicateVariable_2 = generateTestOutput(false, "DuplicateVariable_2", "./test_code/Block_DuplicateVariable_failure.a");
    return cached_DuplicateVariable_2;
  }

  @Test
  public void testDuplicateVariableFailure() {
    assertLiveFail(get_DuplicateVariable_2());
  }

  @Test
  public void testDuplicateVariableNotTerribleLineNumbers() {
    assertNotTerribleLineNumbers(get_DuplicateVariable_2());
  }

  @Test
  public void testDuplicateVariableExceptionFree() {
    assertExceptionFree(get_DuplicateVariable_2());
  }

  @Test
  public void testDuplicateVariableTODOFree() {
    assertTODOFree(get_DuplicateVariable_2());
  }

  @Test
  public void stable_DuplicateVariable_2() {
    String live = get_DuplicateVariable_2();
    StringBuilder gold = new StringBuilder();
    gold.append("Path:Block_DuplicateVariable_failure.a");
    gold.append("\n--ISSUES-------------------------------------------");
    gold.append("\n[{\"range\":{\"start\":{\"line\":2,\"character\":2,\"byte\":24},\"end\":{\"line\":2,\"character\":8,\"byte\":30}},\"severity\":1,\"source\":\"error\",\"message\":\"Variable 'x' was already defined\",\"file\":\"./test_code/Block_DuplicateVariable_failure.a\"}]\"--JAVA---------------------------------------------");
    gold.append("\n");
    gold.append("\nFailedValidation");
    assertStable(live, gold);
  }
}
