package dev.slimevr.reset;

public interface ResetListener {

	void onStarted(int resetType);

	void onFinished(int resetType);
}
