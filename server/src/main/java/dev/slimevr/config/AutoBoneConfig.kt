package dev.slimevr.config

class AutoBoneConfig {
	var cursorIncrement = 2
	var minDataDistance = 1
	var maxDataDistance = 1
	var numEpochs = 100
	var printEveryNumEpochs = 25
	var initialAdjustRate = 10f
	var adjustRateMultiplier = 0.995f
	var slideErrorFactor = 0.0f
	var offsetSlideErrorFactor = 2.0f
	var footHeightOffsetErrorFactor = 0.0f
	var bodyProportionErrorFactor = 0.825f
	var heightErrorFactor = 0.0f
	var positionErrorFactor = 0.0f
	var positionOffsetErrorFactor = 0.0f
	var calcInitError = false
	var targetHeight = -1f
	var randomizeFrameOrder = true
	var scaleEachStep = true
	var sampleCount = 1000
	var sampleRateMs: Long = 20
	var saveRecordings = false
}
