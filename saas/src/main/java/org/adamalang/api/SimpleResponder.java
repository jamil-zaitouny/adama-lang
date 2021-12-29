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
package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class SimpleResponder {
  public final JsonResponder responder;

  public SimpleResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete() {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
