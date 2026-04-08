package dev.slimevr.desktop.platform.windows;

import dev.slimevr.VRServer;
import dev.slimevr.bridge.BridgeThread;
import dev.slimevr.protocol.GenericConnection;
import dev.slimevr.protocol.ProtocolAPI;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.nio.ByteBuffer;


public class WindowsNamedPipeRpcBridge implements WindowsNamedPipe.PipeMessageReader,
	dev.slimevr.bridge.Bridge, dev.slimevr.protocol.ProtocolAPIServer, Runnable, AutoCloseable {
	private static final int maxConnections = 16;
	private final Thread runnerThread = new Thread(this, "Named pipe RPC thread");
	private final String pipePath;
	private final ProtocolAPI protocolAPI;
	private WindowsNamedPipe pipe;
	private FastList<WindowsNamedPipeRpcConnection> connections = new FastList(maxConnections);

	public WindowsNamedPipeRpcBridge(VRServer server, String pipePath) {
		this.pipePath = pipePath;
		this.protocolAPI = server.protocolAPI;

		server.protocolAPI.registerAPIServer(this);
	}

	@Override
	@BridgeThread
	public void run() {
		try {
			this.pipe = new WindowsNamedPipe(pipePath, maxConnections);
			FastList<WindowsNamedPipeRpcConnection> removedConnections = new FastList(
				maxConnections
			);
			while (true) {
				WindowsNamedPipe.PipeConnection newConnection = this.pipe.tryAccept();
				if (newConnection != null) {
					this.connections.add(new WindowsNamedPipeRpcConnection(newConnection));
				}

				for (WindowsNamedPipeRpcConnection connection : this.connections) {
					if (connection.getState() == PipeState.OPEN) {
						connection.update(this);
					}

					if (connection.getState() == PipeState.ERROR) {
						connection.disconnect();
						removedConnections.add(connection);
					}
				}

				this.connections.removeAll(removedConnections);
				removedConnections.clear();
				Thread.sleep(2);
			}
		} catch (IOException e) {
			LogManager.severe("[SolarXR RPC Bridge] Exception in runner thread", e);
		} catch (InterruptedException e) {
			// Do nothing.
		}
	}

	@Override
	@BridgeThread
	public void onMessage(WindowsNamedPipe.PipeConnection conn, ByteBuffer buf) {
		for (WindowsNamedPipeRpcConnection connection : this.connections) {
			if (connection.getConnection() == conn) {
				this.protocolAPI.onMessage(connection, buf);
				return;
			}
		}

		LogManager.severe("[SolarXR RPC Bridge] Got message from connection we don't know about");
	}

	@Override
	@VRServerThread
	public void dataRead() {
	}

	@Override
	@VRServerThread
	public void dataWrite() {
	}

	@Override
	@VRServerThread
	public void addSharedTracker(Tracker tracker) {
	}

	@Override
	@VRServerThread
	public void removeSharedTracker(Tracker tracker) {
	}

	@Override
	@VRServerThread
	public void startBridge() {
		this.runnerThread.start();
	}

	@Override
	@VRServerThread
	public void stopBridge() {
		this.runnerThread.interrupt();
	}

	@Override
	public void close() {
		this.connections.clear();
		if (this.pipe != null)
			this.pipe.close();
	}

	@Override
	public boolean isConnected() {
		return !this.connections.isEmpty();
	}

	@Override
	public java.util.stream.Stream<GenericConnection> getApiConnections() {
		return this.connections.stream().map(conn -> (GenericConnection) conn);
	}
}

