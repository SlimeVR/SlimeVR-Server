package dev.slimevr.util

enum class Side {
	LEFT,
	RIGHT,
}

val Side.opposite: Side
	get() = when (this) {
		Side.LEFT -> Side.RIGHT
		Side.RIGHT -> Side.LEFT
	}
