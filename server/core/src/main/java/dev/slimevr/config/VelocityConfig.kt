package dev.slimevr.config

/**
 * Allows to enable/disable sending of optional derived velocity data via Protobuf.
 * Enables Natural Locomotion Support
 * May create overprediction in certain titles causing excessive jitter when moving upper body.
 */
class VelocityConfig {
	var sendDerivedVelocity: Boolean = false // Disables derived velocity for all trackers. Driver zeroes out velocity if nothing is returned in protobuf message.
}
