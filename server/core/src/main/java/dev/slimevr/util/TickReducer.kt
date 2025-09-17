package dev.slimevr.util

class TickReducer(
	/**
	 * The function to run every tick interval.
	 */
	private val onTick: (delta: Float) -> Unit,
	/**
	 * The tick interval in seconds.
	 */
	var interval: Float,
	/**
	 * The amount of time in seconds that a tick can fire early.
	 */
	var resolution: Float = 0f,
) {
	/**
	 * The amount of time in seconds since the last tick.
	 */
	private var tickDelta = 0f

	/**
	 * The offset in timing for the next frame to approximate a tick rate with low
	 * resolution ticking.
	 */
	private var tickOffset = 0f

	/**
	 * The main ticking function to be run at a fast tick rate. Runs [onTick] at the
	 * specified [interval] and [resolution].
	 * @param delta The time in seconds between the last time [tick] was run and the
	 * current tick.
	 */
	@Synchronized
	fun tick(delta: Float) {
		// Update tick delta from delta
		tickDelta += delta

		// If the next tick time is not within the given resolution
		if (tickDelta + tickOffset + resolution < interval) return

		// Run tick, providing the delta time
		onTick(tickDelta)

		// Reset tick timing including an offset to compensate for inaccuracy
		// Define a maximum for the offset to prevent double ticking
		tickOffset = (tickDelta - interval).coerceAtMost(interval / 2f)
		tickDelta = 0f
	}

	/**
	 * Resets the tick timing.
	 */
	@Synchronized
	fun reset() {
		tickDelta = 0f
		tickOffset = 0f
	}
}
