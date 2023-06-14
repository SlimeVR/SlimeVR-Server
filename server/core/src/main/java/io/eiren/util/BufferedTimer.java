package io.eiren.util;

import com.jme3.system.NanoTimer;


/**
 * This timer accumulate measured TPF and returns average/min/max FPS value
 */
public class BufferedTimer extends NanoTimer {

	private final float measureInterval;
	private float averageTpf;
	private float averageFps;
	private float averageFrameRenderTime;
	private float sumFrameRenderTime;
	private float sumTpf;
	private float minFpsCurrent;
	private float maxFpsCurrent;
	private float maxFps;
	private float minFps;
	private int count;
	private boolean measured = false;

	/**
	 * Measure average tpf over the provided inverval in seconds
	 * 
	 * @param measureInterval interval to measure averages over
	 */
	public BufferedTimer(float measureInterval) {
		averageFps = 0;
		sumTpf = 0;
		count = 0;
		this.measureInterval = measureInterval;
	}

	public float getAverageFPS() {
		return averageFps;
	}

	public float getMinFPS() {
		return minFps;
	}

	public float getMaxFPS() {
		return maxFps;
	}

	public void addRenderTime(float renderTime) {
		sumFrameRenderTime += renderTime;
	}

	public float getAverageFrameRenderTime() {
		return averageFrameRenderTime;
	}

	public boolean isMeasured() {
		if (measured) {
			measured = false;
			return true;
		}
		return false;
	}

	public TimerSample getCurrentData() {
		return new TimerSample(getFrameRate(), minFps, maxFps, averageFps);
	}

	@Override
	public void update() {
		super.update();
		// Accumulate instant rate
		sumTpf += getTimePerFrame();
		float fps = getFrameRate();
		if (fps < minFpsCurrent)
			minFpsCurrent = fps;
		if (fps > maxFpsCurrent)
			maxFpsCurrent = fps;
		++count;
		// Calculate results once per measure interval
		if (!measured || sumTpf > measureInterval) {
			// Average results
			averageTpf = sumTpf / count;
			averageFps = 1.0f / averageTpf;
			averageFrameRenderTime = sumFrameRenderTime / count;
			minFps = minFpsCurrent;
			maxFps = maxFpsCurrent;
			// Reset counter
			sumTpf = 0;
			sumFrameRenderTime = 0;
			minFpsCurrent = Float.MAX_VALUE;
			maxFpsCurrent = 0;
			count = 0;
			measured = true;
		}
	}

	public static class TimerSample {

		public float fps;
		public float minFps;
		public float maxFps;
		public float averageFps;

		public TimerSample(float fps, float minFps, float maxFps, float averageFps) {
			this.fps = fps;
			this.minFps = minFps;
			this.maxFps = maxFps;
			this.averageFps = averageFps;
		}

		public float getFps() {
			return fps;
		}

		public float getMinFps() {
			return minFps;
		}

		public float getMaxFps() {
			return maxFps;
		}

		public float getAverageFps() {
			return averageFps;
		}
	}
}
