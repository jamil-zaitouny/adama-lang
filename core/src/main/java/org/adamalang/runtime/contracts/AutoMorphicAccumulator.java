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
package org.adamalang.runtime.contracts;

/** provides a mechanism to accumulate data into a final bundle */
public interface AutoMorphicAccumulator<T> {
    /** is the accumulator empty */
    public boolean empty();

    /** provide a data element */
    public void next(T data);

    /** finish the stream */
    public T finish();
}
