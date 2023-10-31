/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.service;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.MetricsFactory;

public class WebMetrics {
  public final Inflight websockets_active;
  public final Inflight websockets_active_child_connections;
  public final Runnable websockets_send_heartbeat;
  public final Runnable websockets_heartbeat_failure;
  public final Runnable websockets_server_heartbeat;
  public final Runnable websockets_uncaught_exception;
  public final Runnable websockets_end_exception;
  public final Runnable redirect_server_heartbeat;

  public final Runnable bad_traffic;
  public final Runnable webhandler_get;
  public final Runnable webhandler_post;
  public final Runnable webhandler_delete;
  public final Runnable webhandler_options;
  public final Runnable webhandler_exception;
  public final Runnable webhandler_found;
  public final Runnable webhandler_notfound;
  public final Runnable webhandler_upload_asset_failure;
  public final Runnable webhandler_failed_cookie_set;
  public final Runnable webhandler_failed_cookie_get;
  public final Runnable webhandler_wta_crash;
  public final Runnable webhandler_options_failure;
  public final Runnable webhandler_healthcheck;
  public final Runnable webhandler_deephealthcheck;
  public final Runnable webhandler_client_download;
  public final Runnable webhandler_set_asset_key;
  public final Runnable webhandler_assets_no_cookie;
  public final Runnable webhandler_assets_invalid_uri;
  public final Runnable webhandler_assets_start;
  public final Runnable webhandler_assets_failed_start;
  public final Runnable webhandler_firewall;
  public final Runnable webhandler_asset_failed;

  public final Runnable websockets_start;
  public final Runnable websockets_end;

  public final CallbackMonitor web_asset_upload;

  public WebMetrics(MetricsFactory factory) {
    this.websockets_active = factory.inflight("websockets_active");
    this.websockets_active_child_connections = factory.inflight("websockets_active_child_connections");
    this.websockets_send_heartbeat = factory.counter("websockets_send_heartbeat");
    this.websockets_heartbeat_failure = factory.counter("websockets_heartbeat_failure");
    this.websockets_server_heartbeat = factory.counter("websockets_server_heartbeat");
    this.websockets_start = factory.counter("websockets_start");
    this.websockets_end = factory.counter("websockets_end");
    this.websockets_uncaught_exception = factory.counter("websockets_uncaught_exception");
    this.websockets_end_exception = factory.counter("websockets_end_exception");
    this.webhandler_client_download = factory.counter("webhandler_client_download");
    this.webhandler_set_asset_key = factory.counter("webhandler_set_asset_key");
    this.webhandler_assets_no_cookie = factory.counter("webhandler_assets_no_cookie");
    this.webhandler_assets_invalid_uri = factory.counter("webhandler_assets_invalid_uri");
    this.webhandler_assets_start = factory.counter("webhandler_assets_start");
    this.webhandler_assets_failed_start = factory.counter("webhandler_assets_failed_start");
    this.webhandler_failed_cookie_set = factory.counter("webhandler_failed_cookie_set");
    this.webhandler_failed_cookie_get = factory.counter("webhandler_failed_cookie_get");
    this.redirect_server_heartbeat = factory.counter("redirect_server_heartbeat");
    this.bad_traffic = factory.counter("webhandler_bad_traffic");
    this.webhandler_get = factory.counter("webhandler_get");
    this.webhandler_post = factory.counter("webhandler_post");
    this.webhandler_delete = factory.counter("webhandler_delete");
    this.webhandler_exception = factory.counter("webhandler_exception");
    this.webhandler_found = factory.counter("webhandler_found");
    this.webhandler_notfound = factory.counter("webhandler_notfound");
    this.webhandler_upload_asset_failure = factory.counter("webhandler_upload_asset_failure");
    this.webhandler_healthcheck = factory.counter("webhandler_healthcheck");
    this.webhandler_deephealthcheck = factory.counter("webhandler_deephealthcheck");
    this.webhandler_firewall = factory.counter("webhandler_firewall");
    this.webhandler_asset_failed = factory.counter("webhandler_asset_failed");
    this.webhandler_wta_crash = factory.counter("webhandler_wta_crash");
    this.webhandler_options = factory.counter("webhandler_options");
    this.webhandler_options_failure = factory.counter("webhandler_options_failure");
    this.web_asset_upload = factory.makeCallbackMonitor("web_asset_upload");
  }
}
