package dev.slimevr.posestreamer

class BVHSettings {
	var offsetScale: Float = 100f
		private set
	var positionScale: Float = 100f
		private set
	private var writeEndNodes = false

	constructor()

	constructor(source: BVHSettings) {
		this.offsetScale = source.offsetScale
		this.positionScale = source.positionScale
		this.writeEndNodes = source.writeEndNodes
	}

	fun setOffsetScale(offsetScale: Float): BVHSettings {
		this.offsetScale = offsetScale
		return this
	}

	fun setPositionScale(positionScale: Float): BVHSettings {
		this.positionScale = positionScale
		return this
	}

	fun shouldWriteEndNodes(): Boolean = writeEndNodes

	fun setWriteEndNodes(writeEndNodes: Boolean): BVHSettings {
		this.writeEndNodes = writeEndNodes
		return this
	}

	companion object {
		val DEFAULT: BVHSettings = BVHSettings()
		val BLENDER: BVHSettings = BVHSettings(DEFAULT)
			.setOffsetScale(1f)
			.setPositionScale(1f)
	}
}
