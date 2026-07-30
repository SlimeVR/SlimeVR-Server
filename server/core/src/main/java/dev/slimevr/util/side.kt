package dev.slimevr.util

/**
 * Used to represent the side of a bone.
 */
enum class Side {
    LEFT,
    RIGHT,
}

val Side.opposite: Side
    get() = when (this) {
        Side.LEFT -> Side.RIGHT
        Side.RIGHT -> Side.LEFT
    }
