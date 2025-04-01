package dev.slimevr.reset;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ResetHandler {

	private final List<ResetListener> listeners = new CopyOnWriteArrayList<>();

	public ResetHandler() {

	}

	public void sendStarted(int resetType) {
		this.listeners.forEach((listener) -> listener.onStarted(resetType));
	}

	public void sendFinished(int resetType) {
		this.listeners.forEach((listener) -> listener.onFinished(resetType));
	}

	public void addListener(ResetListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(ResetListener l) {
		listeners.removeIf(listener -> l == listener);
	}
}
