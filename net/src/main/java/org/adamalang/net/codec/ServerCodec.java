/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.common.codec.Helper;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.codec.ServerMessage.ReplicaData;
import org.adamalang.net.codec.ServerMessage.DirectSendResponse;
import org.adamalang.net.codec.ServerMessage.QueryResult;
import org.adamalang.net.codec.ServerMessage.AuthResponse;
import org.adamalang.net.codec.ServerMessage.WebResponseNet;
import org.adamalang.net.codec.ServerMessage.InventoryHeartbeat;
import org.adamalang.net.codec.ServerMessage.HeatPayload;
import org.adamalang.net.codec.ServerMessage.StreamSeqResponse;
import org.adamalang.net.codec.ServerMessage.StreamAskAttachmentResponse;
import org.adamalang.net.codec.ServerMessage.StreamError;
import org.adamalang.net.codec.ServerMessage.StreamData;
import org.adamalang.net.codec.ServerMessage.StreamStatus;
import org.adamalang.net.codec.ServerMessage.ScanDeploymentResponse;
import org.adamalang.net.codec.ServerMessage.ReflectResponse;
import org.adamalang.net.codec.ServerMessage.DeleteResponse;
import org.adamalang.net.codec.ServerMessage.CreateResponse;
import org.adamalang.net.codec.ServerMessage.ProbeCommandResponse;
import org.adamalang.net.codec.ServerMessage.FindResponse;
import org.adamalang.net.codec.ServerMessage.PingResponse;

public class ServerCodec {

  public static abstract class StreamQuery implements ByteStream {
    public abstract void handle(QueryResult payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 2001:
          handle(readBody_2001(buf, new QueryResult()));
          return;
      }
    }
  }

  public static interface HandlerQuery {
    public void handle(QueryResult payload);
  }

  public static void route(ByteBuf buf, HandlerQuery handler) {
    switch (buf.readIntLE()) {
      case 2001:
        handler.handle(readBody_2001(buf, new QueryResult()));
        return;
    }
  }


  public static abstract class StreamCreation implements ByteStream {
    public abstract void handle(CreateResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 12524:
          handle(readBody_12524(buf, new CreateResponse()));
          return;
      }
    }
  }

  public static interface HandlerCreation {
    public void handle(CreateResponse payload);
  }

  public static void route(ByteBuf buf, HandlerCreation handler) {
    switch (buf.readIntLE()) {
      case 12524:
        handler.handle(readBody_12524(buf, new CreateResponse()));
        return;
    }
  }


  public static abstract class StreamFinder implements ByteStream {
    public abstract void handle(FindResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 9001:
          handle(readBody_9001(buf, new FindResponse()));
          return;
      }
    }
  }

  public static interface HandlerFinder {
    public void handle(FindResponse payload);
  }

  public static void route(ByteBuf buf, HandlerFinder handler) {
    switch (buf.readIntLE()) {
      case 9001:
        handler.handle(readBody_9001(buf, new FindResponse()));
        return;
    }
  }


  public static abstract class StreamDeployment implements ByteStream {
    public abstract void handle(ScanDeploymentResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 8922:
          handle(readBody_8922(buf, new ScanDeploymentResponse()));
          return;
      }
    }
  }

  public static interface HandlerDeployment {
    public void handle(ScanDeploymentResponse payload);
  }

  public static void route(ByteBuf buf, HandlerDeployment handler) {
    switch (buf.readIntLE()) {
      case 8922:
        handler.handle(readBody_8922(buf, new ScanDeploymentResponse()));
        return;
    }
  }


  public static abstract class StreamReflection implements ByteStream {
    public abstract void handle(ReflectResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 6736:
          handle(readBody_6736(buf, new ReflectResponse()));
          return;
      }
    }
  }

  public static interface HandlerReflection {
    public void handle(ReflectResponse payload);
  }

  public static void route(ByteBuf buf, HandlerReflection handler) {
    switch (buf.readIntLE()) {
      case 6736:
        handler.handle(readBody_6736(buf, new ReflectResponse()));
        return;
    }
  }


  public static abstract class StreamInfo implements ByteStream {
    public abstract void handle(InventoryHeartbeat payload);

    public abstract void handle(HeatPayload payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 7232:
          handle(readBody_7232(buf, new InventoryHeartbeat()));
          return;
        case 5122:
          handle(readBody_5122(buf, new HeatPayload()));
          return;
      }
    }
  }

  public static interface HandlerInfo {
    public void handle(InventoryHeartbeat payload);
    public void handle(HeatPayload payload);
  }

  public static void route(ByteBuf buf, HandlerInfo handler) {
    switch (buf.readIntLE()) {
      case 7232:
        handler.handle(readBody_7232(buf, new InventoryHeartbeat()));
        return;
      case 5122:
        handler.handle(readBody_5122(buf, new HeatPayload()));
        return;
    }
  }


  public static abstract class StreamDocument implements ByteStream {
    public abstract void handle(StreamSeqResponse payload);

    public abstract void handle(StreamAskAttachmentResponse payload);

    public abstract void handle(StreamError payload);

    public abstract void handle(StreamData payload);

    public abstract void handle(StreamStatus payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 1632:
          handle(readBody_1632(buf, new StreamSeqResponse()));
          return;
        case 15546:
          handle(readBody_15546(buf, new StreamAskAttachmentResponse()));
          return;
        case 19546:
          handle(readBody_19546(buf, new StreamError()));
          return;
        case 10546:
          handle(readBody_10546(buf, new StreamData()));
          return;
        case 12546:
          handle(readBody_12546(buf, new StreamStatus()));
          return;
      }
    }
  }

  public static interface HandlerDocument {
    public void handle(StreamSeqResponse payload);
    public void handle(StreamAskAttachmentResponse payload);
    public void handle(StreamError payload);
    public void handle(StreamData payload);
    public void handle(StreamStatus payload);
  }

  public static void route(ByteBuf buf, HandlerDocument handler) {
    switch (buf.readIntLE()) {
      case 1632:
        handler.handle(readBody_1632(buf, new StreamSeqResponse()));
        return;
      case 15546:
        handler.handle(readBody_15546(buf, new StreamAskAttachmentResponse()));
        return;
      case 19546:
        handler.handle(readBody_19546(buf, new StreamError()));
        return;
      case 10546:
        handler.handle(readBody_10546(buf, new StreamData()));
        return;
      case 12546:
        handler.handle(readBody_12546(buf, new StreamStatus()));
        return;
    }
  }


  public static abstract class StreamProbe implements ByteStream {
    public abstract void handle(ProbeCommandResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 1018:
          handle(readBody_1018(buf, new ProbeCommandResponse()));
          return;
      }
    }
  }

  public static interface HandlerProbe {
    public void handle(ProbeCommandResponse payload);
  }

  public static void route(ByteBuf buf, HandlerProbe handler) {
    switch (buf.readIntLE()) {
      case 1018:
        handler.handle(readBody_1018(buf, new ProbeCommandResponse()));
        return;
    }
  }


  public static abstract class StreamReplica implements ByteStream {
    public abstract void handle(ReplicaData payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 10548:
          handle(readBody_10548(buf, new ReplicaData()));
          return;
      }
    }
  }

  public static interface HandlerReplica {
    public void handle(ReplicaData payload);
  }

  public static void route(ByteBuf buf, HandlerReplica handler) {
    switch (buf.readIntLE()) {
      case 10548:
        handler.handle(readBody_10548(buf, new ReplicaData()));
        return;
    }
  }


  public static abstract class StreamDirect implements ByteStream {
    public abstract void handle(DirectSendResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 1783:
          handle(readBody_1783(buf, new DirectSendResponse()));
          return;
      }
    }
  }

  public static interface HandlerDirect {
    public void handle(DirectSendResponse payload);
  }

  public static void route(ByteBuf buf, HandlerDirect handler) {
    switch (buf.readIntLE()) {
      case 1783:
        handler.handle(readBody_1783(buf, new DirectSendResponse()));
        return;
    }
  }


  public static abstract class StreamWeb implements ByteStream {
    public abstract void handle(WebResponseNet payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 1721:
          handle(readBody_1721(buf, new WebResponseNet()));
          return;
      }
    }
  }

  public static interface HandlerWeb {
    public void handle(WebResponseNet payload);
  }

  public static void route(ByteBuf buf, HandlerWeb handler) {
    switch (buf.readIntLE()) {
      case 1721:
        handler.handle(readBody_1721(buf, new WebResponseNet()));
        return;
    }
  }


  public static abstract class StreamAuth implements ByteStream {
    public abstract void handle(AuthResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 2123:
          handle(readBody_2123(buf, new AuthResponse()));
          return;
      }
    }
  }

  public static interface HandlerAuth {
    public void handle(AuthResponse payload);
  }

  public static void route(ByteBuf buf, HandlerAuth handler) {
    switch (buf.readIntLE()) {
      case 2123:
        handler.handle(readBody_2123(buf, new AuthResponse()));
        return;
    }
  }


  public static abstract class StreamPing implements ByteStream {
    public abstract void handle(PingResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 24322:
          handle(readBody_24322(buf, new PingResponse()));
          return;
      }
    }
  }

  public static interface HandlerPing {
    public void handle(PingResponse payload);
  }

  public static void route(ByteBuf buf, HandlerPing handler) {
    switch (buf.readIntLE()) {
      case 24322:
        handler.handle(readBody_24322(buf, new PingResponse()));
        return;
    }
  }


  public static abstract class StreamDeletion implements ByteStream {
    public abstract void handle(DeleteResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 12526:
          handle(readBody_12526(buf, new DeleteResponse()));
          return;
      }
    }
  }

  public static interface HandlerDeletion {
    public void handle(DeleteResponse payload);
  }

  public static void route(ByteBuf buf, HandlerDeletion handler) {
    switch (buf.readIntLE()) {
      case 12526:
        handler.handle(readBody_12526(buf, new DeleteResponse()));
        return;
    }
  }


  public static ReplicaData read_ReplicaData(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 10548:
        return readBody_10548(buf, new ReplicaData());
    }
    return null;
  }


  private static ReplicaData readBody_10548(ByteBuf buf, ReplicaData o) {
    o.reset = buf.readBoolean();
    o.change = Helper.readString(buf);
    return o;
  }

  public static DirectSendResponse read_DirectSendResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1783:
        return readBody_1783(buf, new DirectSendResponse());
    }
    return null;
  }


  private static DirectSendResponse readBody_1783(ByteBuf buf, DirectSendResponse o) {
    o.seq = buf.readIntLE();
    return o;
  }

  public static QueryResult read_QueryResult(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 2001:
        return readBody_2001(buf, new QueryResult());
    }
    return null;
  }


  private static QueryResult readBody_2001(ByteBuf buf, QueryResult o) {
    o.result = Helper.readString(buf);
    return o;
  }

  public static AuthResponse read_AuthResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 2123:
        return readBody_2123(buf, new AuthResponse());
    }
    return null;
  }


  private static AuthResponse readBody_2123(ByteBuf buf, AuthResponse o) {
    o.agent = Helper.readString(buf);
    return o;
  }

  public static WebResponseNet read_WebResponseNet(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1721:
        return readBody_1721(buf, new WebResponseNet());
    }
    return null;
  }


  private static WebResponseNet readBody_1721(ByteBuf buf, WebResponseNet o) {
    o.contentType = Helper.readString(buf);
    o.body = Helper.readString(buf);
    o.assetId = Helper.readString(buf);
    o.assetName = Helper.readString(buf);
    o.assetSize = buf.readLongLE();
    o.assetMD5 = Helper.readString(buf);
    o.assetSHA384 = Helper.readString(buf);
    o.cors = buf.readBoolean();
    o.cache_ttl_seconds = buf.readIntLE();
    o.asset_transform = Helper.readString(buf);
    return o;
  }

  public static InventoryHeartbeat read_InventoryHeartbeat(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 7232:
        return readBody_7232(buf, new InventoryHeartbeat());
    }
    return null;
  }


  private static InventoryHeartbeat readBody_7232(ByteBuf buf, InventoryHeartbeat o) {
    o.spaces = Helper.readStringArray(buf);
    return o;
  }

  public static HeatPayload read_HeatPayload(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 5122:
        return readBody_5122(buf, new HeatPayload());
    }
    return null;
  }


  private static HeatPayload readBody_5122(ByteBuf buf, HeatPayload o) {
    o.cpu = buf.readDoubleLE();
    o.mem = buf.readDoubleLE();
    return o;
  }

  public static StreamSeqResponse read_StreamSeqResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1632:
        return readBody_1632(buf, new StreamSeqResponse());
    }
    return null;
  }


  private static StreamSeqResponse readBody_1632(ByteBuf buf, StreamSeqResponse o) {
    o.op = buf.readIntLE();
    o.seq = buf.readIntLE();
    return o;
  }

  public static StreamAskAttachmentResponse read_StreamAskAttachmentResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 15546:
        return readBody_15546(buf, new StreamAskAttachmentResponse());
    }
    return null;
  }


  private static StreamAskAttachmentResponse readBody_15546(ByteBuf buf, StreamAskAttachmentResponse o) {
    o.op = buf.readIntLE();
    o.allowed = buf.readBoolean();
    return o;
  }

  public static StreamError read_StreamError(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 19546:
        return readBody_19546(buf, new StreamError());
    }
    return null;
  }


  private static StreamError readBody_19546(ByteBuf buf, StreamError o) {
    o.op = buf.readIntLE();
    o.code = buf.readIntLE();
    return o;
  }

  public static StreamData read_StreamData(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 10546:
        return readBody_10546(buf, new StreamData());
    }
    return null;
  }


  private static StreamData readBody_10546(ByteBuf buf, StreamData o) {
    o.delta = Helper.readString(buf);
    return o;
  }

  public static StreamStatus read_StreamStatus(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12546:
        return readBody_12546(buf, new StreamStatus());
    }
    return null;
  }


  private static StreamStatus readBody_12546(ByteBuf buf, StreamStatus o) {
    o.code = buf.readIntLE();
    return o;
  }

  public static ScanDeploymentResponse read_ScanDeploymentResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 8922:
        return readBody_8922(buf, new ScanDeploymentResponse());
    }
    return null;
  }


  private static ScanDeploymentResponse readBody_8922(ByteBuf buf, ScanDeploymentResponse o) {
    return o;
  }

  public static ReflectResponse read_ReflectResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 6736:
        return readBody_6736(buf, new ReflectResponse());
    }
    return null;
  }


  private static ReflectResponse readBody_6736(ByteBuf buf, ReflectResponse o) {
    o.schema = Helper.readString(buf);
    return o;
  }

  public static DeleteResponse read_DeleteResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12526:
        return readBody_12526(buf, new DeleteResponse());
    }
    return null;
  }


  private static DeleteResponse readBody_12526(ByteBuf buf, DeleteResponse o) {
    return o;
  }

  public static CreateResponse read_CreateResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12524:
        return readBody_12524(buf, new CreateResponse());
    }
    return null;
  }


  private static CreateResponse readBody_12524(ByteBuf buf, CreateResponse o) {
    return o;
  }

  public static ProbeCommandResponse read_ProbeCommandResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1018:
        return readBody_1018(buf, new ProbeCommandResponse());
    }
    return null;
  }


  private static ProbeCommandResponse readBody_1018(ByteBuf buf, ProbeCommandResponse o) {
    o.json = Helper.readString(buf);
    o.errors = Helper.readStringArray(buf);
    return o;
  }

  public static FindResponse read_FindResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 9001:
        return readBody_9001(buf, new FindResponse());
    }
    return null;
  }


  private static FindResponse readBody_9001(ByteBuf buf, FindResponse o) {
    o.id = buf.readLongLE();
    o.location = buf.readIntLE();
    o.archive = Helper.readString(buf);
    o.region = Helper.readString(buf);
    o.machine = Helper.readString(buf);
    o.deleted = buf.readBoolean();
    return o;
  }

  public static PingResponse read_PingResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 24322:
        return readBody_24322(buf, new PingResponse());
    }
    return null;
  }


  private static PingResponse readBody_24322(ByteBuf buf, PingResponse o) {
    return o;
  }

  public static void write(ByteBuf buf, ReplicaData o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(10548);
    buf.writeBoolean(o.reset);
    Helper.writeString(buf, o.change);;
  }

  public static void write(ByteBuf buf, DirectSendResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1783);
    buf.writeIntLE(o.seq);
  }

  public static void write(ByteBuf buf, QueryResult o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(2001);
    Helper.writeString(buf, o.result);;
  }

  public static void write(ByteBuf buf, AuthResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(2123);
    Helper.writeString(buf, o.agent);;
  }

  public static void write(ByteBuf buf, WebResponseNet o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1721);
    Helper.writeString(buf, o.contentType);;
    Helper.writeString(buf, o.body);;
    Helper.writeString(buf, o.assetId);;
    Helper.writeString(buf, o.assetName);;
    buf.writeLongLE(o.assetSize);
    Helper.writeString(buf, o.assetMD5);;
    Helper.writeString(buf, o.assetSHA384);;
    buf.writeBoolean(o.cors);
    buf.writeIntLE(o.cache_ttl_seconds);
    Helper.writeString(buf, o.asset_transform);;
  }

  public static void write(ByteBuf buf, InventoryHeartbeat o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(7232);
    Helper.writeStringArray(buf, o.spaces);;
  }

  public static void write(ByteBuf buf, HeatPayload o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(5122);
    buf.writeDoubleLE(o.cpu);
    buf.writeDoubleLE(o.mem);
  }

  public static void write(ByteBuf buf, StreamSeqResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1632);
    buf.writeIntLE(o.op);
    buf.writeIntLE(o.seq);
  }

  public static void write(ByteBuf buf, StreamAskAttachmentResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(15546);
    buf.writeIntLE(o.op);
    buf.writeBoolean(o.allowed);
  }

  public static void write(ByteBuf buf, StreamError o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(19546);
    buf.writeIntLE(o.op);
    buf.writeIntLE(o.code);
  }

  public static void write(ByteBuf buf, StreamData o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(10546);
    Helper.writeString(buf, o.delta);;
  }

  public static void write(ByteBuf buf, StreamStatus o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12546);
    buf.writeIntLE(o.code);
  }

  public static void write(ByteBuf buf, ScanDeploymentResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(8922);
  }

  public static void write(ByteBuf buf, ReflectResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(6736);
    Helper.writeString(buf, o.schema);;
  }

  public static void write(ByteBuf buf, DeleteResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12526);
  }

  public static void write(ByteBuf buf, CreateResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12524);
  }

  public static void write(ByteBuf buf, ProbeCommandResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1018);
    Helper.writeString(buf, o.json);;
    Helper.writeStringArray(buf, o.errors);;
  }

  public static void write(ByteBuf buf, FindResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(9001);
    buf.writeLongLE(o.id);
    buf.writeIntLE(o.location);
    Helper.writeString(buf, o.archive);;
    Helper.writeString(buf, o.region);;
    Helper.writeString(buf, o.machine);;
    buf.writeBoolean(o.deleted);
  }

  public static void write(ByteBuf buf, PingResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(24322);
  }
}
