package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.contracts.PlanFetcher;
import org.adamalang.runtime.data.Key;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.Collection;

/** ensures an instance is always alive by fetching plans... on demand  */
public class OndemandDeploymentFactoryBase implements LivingDocumentFactoryFactory  {
  private final DeploymentFactoryBase base;
  private final PlanFetcher fetcher;

  public OndemandDeploymentFactoryBase(DeploymentFactoryBase base, PlanFetcher fetcher) {
    this.base = base;
    this.fetcher = fetcher;
  }

  @Override
  public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    if (base.contains(key.space)) {
      base.fetch(key, callback);
    } else {
      fetcher.find(key.space, new Callback<DeploymentPlan>() {
        @Override
        public void success(DeploymentPlan plan) {
          try {
            base.deploy(key.space, plan);
            base.fetch(key, callback);
          } catch (ErrorCodeException ex) {
            callback.failure(ex);
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    }
  }

  @Override
  public Collection<String> spacesAvailable() {
    return null;
  }
}
