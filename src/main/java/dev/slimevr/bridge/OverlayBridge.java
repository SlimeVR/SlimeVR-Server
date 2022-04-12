package dev.slimevr.bridge;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.google.flatbuffers.FlatBufferBuilder;

import dev.slimevr.vr.trackers.ShareableTracker;
import io.eiren.util.logging.LogManager;
import overlay_protocol.InboundPacket;

/** Stateful context attached to a connection. */
final class ConnectionContext {
    Logger log;
    FlatBufferBuilder fbb = new FlatBufferBuilder();
    // TODO: If we need a queue, put it here.

    public ConnectionContext(WebSocket conn) {
        this.log = Logger.getLogger("[OverlayBridge:" + conn.getRemoteSocketAddress().toString() + "]");
    }
}

public final class OverlayBridge extends WebSocketServer implements Bridge {
    public static final int PORT = 7331;


    public OverlayBridge() {
        super(new InetSocketAddress(PORT));
    }

    // ---- Bridge overrides ----

    @Override
    public void dataRead() {
        FlatBufferBuilder fbb = new FlatBufferBuilder();

        int bonesVectorOffset = fbb.createVectorOfTables(new int[]{});

        InboundPacket.createInboundPacket(fbb, bonesVectorOffset);

        int inboundPacketOffset = InboundPacket.endInboundPacket(fbb);

        fbb.finish(inboundPacketOffset);

        ByteBuffer buf = fbb.dataBuffer();

        System.out.printf("%x", buf);
    }

    @Override
    public void dataWrite() {
        // TODO
    }

    @Override
    public void addSharedTracker(ShareableTracker tracker) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeSharedTracker(ShareableTracker tracker) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startBridge() {
        super.start();
    }

    // ---- WebSocketServer overrides ----

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        var ctx = new ConnectionContext(conn);
        conn.setAttachment(ctx);
        ctx.log.info("Connected");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        ConnectionContext ctx = conn.getAttachment();
        ctx.log.info("Disconnected. Reason: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        ConnectionContext ctx = conn.getAttachment();
        ctx.log.fine("Message recv: " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ConnectionContext ctx = conn.getAttachment();
        ctx.log.warning("Error: " + ex.toString());

    }

    @Override
    public void onStart() {
        LogManager.log.debug("Starting OverlayBridge");
    }
}
