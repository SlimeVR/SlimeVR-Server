package dev.slimevr.inputs

class Input(val rightHand: Boolean, val type: InputType)

enum class InputType {
	DOUBLE_TAP,
	TRIPLE_TAP,
}
