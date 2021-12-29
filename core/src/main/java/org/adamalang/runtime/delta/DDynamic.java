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
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtDynamic;

/** a dynamic that will respect privacy and sends state to client only on changes */
public class DDynamic {
  private NtDynamic prior;

  public DDynamic() {
    prior = null;
  }

  /** the dynamic tree is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (prior != null) {
      writer.writeNull();
      prior = null;
    }
  }

  /** the dynamic tree is visible, so show changes */
  public void show(final NtDynamic value, final PrivateLazyDeltaWriter writer) {
    if (value == null || !value.equals(prior)) {
      writer.injectJson(value.json);
    }
    prior = value;
  }
}
