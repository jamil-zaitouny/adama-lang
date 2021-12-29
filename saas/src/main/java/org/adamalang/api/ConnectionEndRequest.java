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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

/**  */
public class ConnectionEndRequest {
  public final Long connection;

  public ConnectionEndRequest(final Long connection) {
    this.connection = connection;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<ConnectionEndRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
      nexus.executor.execute(() -> {
        callback.success(new ConnectionEndRequest(connection));
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
