/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
import org.adamalang.web.io.*;

/** Update the viewer state of the document.
  * 
  * The viewer state is accessible to bubbles to provide view restriction and filtering.
  * For example, the viewer state is how a document can provide real-time search or pagination. */
public class ConnectionUpdateRequest {
  public final Long connection;
  public final ObjectNode viewerState;

  public ConnectionUpdateRequest(final Long connection, final ObjectNode viewerState) {
    this.connection = connection;
    this.viewerState = viewerState;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<ConnectionUpdateRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
      final ObjectNode viewerState = request.getObject("viewer-state", false, 0);
      nexus.executor.execute(new NamedRunnable("connectionupdate-success") {
        @Override
        public void execute() throws Exception {
           callback.success(new ConnectionUpdateRequest(connection, viewerState));
        }
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("connectionupdate-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
  }
}
