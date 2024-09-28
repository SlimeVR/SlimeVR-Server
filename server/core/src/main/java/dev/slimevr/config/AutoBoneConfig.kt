package dev.slimevr.config

class AutoBoneConfig {
	var cursorIncrement = 2
	var minDataDistance = 1
	var maxDataDistance = 1
	var numEpochs = 100
	var printEveryNumEpochs = 25
	var initialAdjustRate = 10.0f
	var adjustRateDecay = 1.0f
	var slideErrorFactor = 0.0f
	var offsetSlideErrorFactor = 1.0f
	var footHeightOffsetErrorFactor = 0.0f
	var bodyProportionErrorFactor = 0.25f
	var heightErrorFactor = 0.0f
	var positionErrorFactor = 0.0f
	var positionOffsetErrorFactor = 0.0f
	var calcInitError = false
	var targetHmdHeight = -1f
	var targetFullHeight = -1f
	var randomizeFrameOrder = true
	var scaleEachStep = true
	var sampleCount = 1500
	var sampleRateMs = 20L
	var saveRecordings = false
	var useSkeletonHeight = false
	var randSeed = 4L
	var useFrameFiltering = false
	var maxFinalError = 0.1f
}
