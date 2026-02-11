package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class AutoBoneConfig(
	val cursorIncrement: Int = 2,
	val minDataDistance: Int = 1,
	val maxDataDistance: Int = 1,
	val numEpochs: Int = 50,
	val printEveryNumEpochs: Int = 25,
	val initialAdjustRate: Float = 10.0f,
	val adjustRateDecay: Float = 1.0f,
	val slideErrorFactor: Float = 1.0f,
	val offsetSlideErrorFactor: Float = 0.0f,
	val footHeightOffsetErrorFactor: Float = 0.0f,
	val bodyProportionErrorFactor: Float = 0.05f,
	val heightErrorFactor: Float = 0.0f,
	val positionErrorFactor: Float = 0.0f,
	val positionOffsetErrorFactor: Float = 0.0f,
	val calcInitError: Boolean = false,
	val randomizeFrameOrder: Boolean = true,
	val scaleEachStep: Boolean = true,
	val sampleCount: Int = 1500,
	val sampleRateMs: Long = 20L,
	val saveRecordings: Boolean = false,
	val useSkeletonHeight: Boolean = false,
	val randSeed: Long = 4L,
	val useFrameFiltering: Boolean = false,
	val maxFinalError: Float = 0.03f,
)
