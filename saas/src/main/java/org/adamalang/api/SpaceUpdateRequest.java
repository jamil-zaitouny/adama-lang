package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.transforms.results.SpacePolicy;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.web.io.*;

/**  */
public class SpaceUpdateRequest {
  public final String identity;
  public final NtClient who;
  public final String space;
  public final SpacePolicy policy;
  public final ObjectNode plan;

  public SpaceUpdateRequest(final String identity, final NtClient who, final String space, final SpacePolicy policy, final ObjectNode plan) {
    this.identity = identity;
    this.who = who;
    this.space = space;
    this.policy = policy;
    this.plan = plan;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<SpaceUpdateRequest> callback) {
    try {
      final BulkLatch<SpaceUpdateRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<NtClient> who = new LatchRefCallback<>(_latch);
      final String space = request.getString("space", true, 461828);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final ObjectNode plan = request.getObject("plan", true, 322);
      _latch.with(() -> new SpaceUpdateRequest(identity, who.get(), space, policy.get(), plan));
      nexus.identityService.execute(identity, who);
      nexus.spaceService.execute(space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
