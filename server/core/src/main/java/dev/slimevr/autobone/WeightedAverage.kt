package dev.slimevr.autobone

class WeightedAverage {
	var value: Float = 0f
		private set
	var weight: Float = 0f
		private set

	fun average() = if (weight > 0f) {
		value / weight
	} else {
		0f
	}

	fun addValue(value: Float, weight: Float) {
		this.value += value * weight
		this.weight += weight
	}

	fun subtractValue(value: Float, weight: Float) {
		this.value -= value * weight
		this.weight -= weight
	}

	fun reset() {
		value = 0f
		weight = 0f
	}
}
