package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class DocumentCreateRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;
  public final String key;
  public final String entropy;
  public final ObjectNode arg;

  public DocumentCreateRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy, final String key, final String entropy, final ObjectNode arg) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.key = key;
    this.entropy = entropy;
    this.arg = arg;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<DocumentCreateRequest> callback) {
    try {
      final BulkLatch<DocumentCreateRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 466947);
      final String entropy = request.getString("entropy", false, 0);
      final ObjectNode arg = request.getObject("arg", true, 461826);
      _latch.with(() -> new DocumentCreateRequest(identity, who.get(), space, policy.get(), key, entropy, arg));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
