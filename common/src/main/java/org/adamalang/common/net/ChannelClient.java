/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.net;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.adamalang.common.Callback;
import org.adamalang.common.gossip.Engine;

import java.util.HashMap;
import java.util.function.Consumer;

/** a single connection for client side */
public class ChannelClient extends ChannelCommon {
  public final String host;
  public final int port;
  private final Lifecycle lifecycle;
  private final HashMap<Integer, Consumer<Boolean>> initiations;
  private final Engine gossipEngine;
  private ChannelHandlerContext context;
  private Runnable unregister;

  public ChannelClient(String host, int port, Lifecycle lifecycle, Engine gossipEngine) {
    super(1, gossipEngine);
    this.host = host;
    this.port = port;
    this.lifecycle = lifecycle;
    this.initiations = new HashMap<>();
    this.gossipEngine = gossipEngine;
    this.unregister = null;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.context = ctx;
    lifecycle.connected(this);
    unregister = gossipEngine.registerClient(this);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf inBuffer = (ByteBuf) msg;
    byte type = inBuffer.readByte();
    int id = inBuffer.readIntLE();
    if (type == 0x10) {
      Consumer<Boolean> callback = initiations.remove(id);
      if (callback != null) {
        callback.accept(true);
      }
    } else {
      routeCommon(type, id, inBuffer, ctx);
    }
    inBuffer.release();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    for (Consumer<Boolean> initiation : initiations.values()) {
      initiation.accept(false);
    }
    initiations.clear();
    lifecycle.disconnected();
    if (unregister != null) {
      unregister.run();
      unregister = null;
    }
  }

  public void close() {
    context.executor().execute(() -> {
      context.close();
    });
  }

  public void gossip() {
    context.executor().execute(() -> {
      int id = makeId();
      Engine.Exchange exchange = gossipEngine.client();
      streams.put(id, exchange);
      ByteBuf buffer = Unpooled.buffer();
      buffer.writeByte(0x11);
      buffer.writeIntLE(id);
      initiations.put(id, (success) -> {
        if (success) {
          exchange.start(new Remote(streams, id, context, () -> {
            flushFromWithinContextExecutor(context);
          }));
        }
      });
      context.write(buffer);
      flushFromWithinContextExecutor(context);
    });
  }

  public void open(ByteStream downstream, Callback<ByteStream> opened) {
    context.executor().execute(() -> {
      int id = makeId();
      streams.put(id, downstream);
      ByteBuf buffer = Unpooled.buffer();
      buffer.writeByte(0x10);
      buffer.writeIntLE(id);
      initiations.put(id, (success) -> {
        if (success) {
          opened.success(new Remote(streams, id, context, () -> {
            flushFromWithinContextExecutor(context);
          }));
        }
      });
      context.write(buffer);
      flushFromWithinContextExecutor(context);
    });
  }
}
