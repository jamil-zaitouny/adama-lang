/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.impl;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.mysql.model.Domains;

/** find domains from the database */
public class GlobalDomainFinder implements DomainFinder {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(GlobalDomainFinder.class);
  private final DataBase dataBase;
  private final String masterKey;

  public GlobalDomainFinder(DataBase dataBase, String masterKey) {
    this.dataBase = dataBase;
    this.masterKey = masterKey;
  }

  @Override
  public void find(String domain, Callback<Domain> callback) {
    try {
      Domain result = Domains.get(dataBase, domain);
      if (result != null) {
        if (result.certificate != null) {
          String cert = MasterKey.decrypt(masterKey, result.certificate);
          result = new Domain(result.domain, result.owner, result.space, result.key, result.routeKey, cert, result.updated, result.timestamp);
        }
      }
      callback.success(result);
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DOMAIN_LOOKUP_FAILURE, ex, EXLOGGER));
    }
  }
}
