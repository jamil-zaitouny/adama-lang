/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import org.adamalang.common.ConfigObject;

import java.io.File;
import java.util.TreeSet;

public class WebConfig {
  public final String healthCheckPath;
  public final String deepHealthCheckPath;
  public final int maxContentLengthSize;
  public final int maxWebSocketFrameSize;
  public final int port;
  public final int redirectPort;
  public final int timeoutWebsocketHandshake;
  public final int heartbeatTimeMilliseconds;
  public final int readTimeoutSeconds;
  public final int writeTimeoutSeconds;
  public final int idleReadSeconds;
  public final int idleWriteSeconds;
  public final int idleAllSeconds;
  public final int bossThreads;
  public final int workerThreads;
  public final TreeSet<String> specialDomains;
  public final String regionalDomain;
  public final String[] globalDomains;
  public final int sharedConnectionPoolMaxLifetimeMilliseconds;
  public final int sharedConnectionPoolMaxUsageCount;
  public final int sharedConnectionPoolMaxPoolSize;
  public final File cacheRoot;
  public final String adamaJarDomain;

  public WebConfig(ConfigObject config) throws Exception {
    // HTTP properties
    this.port = config.intOf("http-port", 8080);
    this.redirectPort = config.intOf("http-redirect-port", 8085);
    this.maxContentLengthSize = config.intOf("http-max-content-length-size", 4194304);
    this.healthCheckPath = config.strOf("http-health-check-path", "/~health_check_lb");
    this.deepHealthCheckPath = config.strOf("http-deep-health-check-path", "/~deep_health_check_status_page");
    // WebSocket properties
    this.timeoutWebsocketHandshake = config.intOf("websocket-handshake-timeout-ms", 2500);
    this.readTimeoutSeconds = config.intOf("http-read-timeout-sec", 30);
    this.writeTimeoutSeconds = config.intOf("http-write-timeout-sec", 30);
    this.idleReadSeconds = config.intOf("http-read-idle-sec", 0);
    this.idleWriteSeconds = config.intOf("http-write-idle-sec", 0);
    this.idleAllSeconds = config.intOf("websocket-all-idle-sec", 30);
    this.maxWebSocketFrameSize = config.intOf("websocket-max-frame-size", 1048576);
    this.heartbeatTimeMilliseconds = config.intOf("websocket-heart-beat-ms", 1000);
    this.bossThreads = config.intOf("http-boss-threads", 2);
    this.workerThreads = config.intOf("http-worker-threads", 16);
    this.regionalDomain = config.strOf("regional-domain", "adama-platform.com");
    this.adamaJarDomain = config.strOf("adama-jar-domain", ".adama-platform.com");
    this.globalDomains = config.stringsOf("global-domains", new String[] { "adama.games" });
    this.specialDomains = new TreeSet<>();
    for (String sd : config.stringsOf("special-domains", new String[] { "www.adama-platform.com", "ide.adama-platform.com", "book.adama-platform.com" })) {
      specialDomains.add(sd);
    }
    this.sharedConnectionPoolMaxLifetimeMilliseconds = config.intOf("shared-connection-max-lifetime-ms", 10000);
    this.sharedConnectionPoolMaxUsageCount = config.intOf("shared-connection-max-usage-count", 50);
    this.sharedConnectionPoolMaxPoolSize = config.intOf("shared-connection-max-pool-size", 50);
    this.cacheRoot = new File(config.strOf("cache-root", "cache"));
    if (!cacheRoot.exists()) {
      cacheRoot.mkdir();
    }
    if (cacheRoot.exists() && !cacheRoot.isDirectory()) {
      throw new Exception("Cache root not directory");
    }
  }
}
