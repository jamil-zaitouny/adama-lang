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
package org.adamalang;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashSet;

public class ErrorCodesTests {
    @Test
    public void coverage() {
        new ErrorCodes();
    }

    @Test
    public void no_dupes() throws Exception {
        HashSet<Integer> values = new HashSet<>();
        for (Field field: ErrorCodes.class.getFields()) {
            int v = (int) field.get(null);
            Assert.assertFalse("dupe:" + v, values.contains(v));
        }
    }
}
