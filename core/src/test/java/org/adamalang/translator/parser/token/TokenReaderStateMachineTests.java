/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.parser.token;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

public class TokenReaderStateMachineTests {
  @Test
  public void symbol_cluster() throws Exception {
    final var list = new ArrayList<Token>();
    final var trsm = new TokenReaderStateMachine("Source", list::add);
    trsm.consume('+');
    trsm.consume('+');
    trsm.consume('+');
    trsm.consume('+');
    trsm.consume('1');
    Assert.assertEquals(4, list.size());
  }
}
