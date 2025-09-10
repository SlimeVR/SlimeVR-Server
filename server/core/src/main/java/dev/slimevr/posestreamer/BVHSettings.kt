package dev.slimevr.posestreamer

class BVHSettings {
	var offsetScale: Float = 100f
		private set
	var positionScale: Float = 100f
		private set

	constructor()

	constructor(source: BVHSettings) {
		this.offsetScale = source.offsetScale
		this.positionScale = source.positionScale
	}

	fun setOffsetScale(offsetScale: Float): BVHSettings {
		this.offsetScale = offsetScale
		return this
	}

	fun setPositionScale(positionScale: Float): BVHSettings {
		this.positionScale = positionScale
		return this
	}

	companion object {
		val DEFAULT: BVHSettings = BVHSettings()
		val BLENDER: BVHSettings = BVHSettings(DEFAULT)
			.setOffsetScale(1f)
			.setPositionScale(1f)
	}
}
