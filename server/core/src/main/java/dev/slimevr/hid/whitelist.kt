package dev.slimevr.hid

data class HidProductRule(val vendorId: Int, val productId: Int, val productMask: Int = 0xFFFF)

private val RECEIVER_PRODUCT_RULES = listOf(
	HidProductRule(0x1209, 0x7690), // SlimeVR receiver
	HidProductRule(0x4E76, 0xD200, 0xFF00), // Gestures Inc. D2XX
)
private val TRACKER_PRODUCT_RULES = listOf(
	HidProductRule(0x1209, 0x7692) // SlimeVR tracker direct
)

private fun matchesRule(rule: HidProductRule, vid: Int, pid: Int) = vid == rule.vendorId && (pid and rule.productMask) == rule.productId
fun isCompatibleHidReceiver(vid: Int, pid: Int) = RECEIVER_PRODUCT_RULES.any { rule ->
	matchesRule(rule, vid, pid)
}
fun isCompatibleHidTracker(vid: Int, pid: Int) = TRACKER_PRODUCT_RULES.any { rule ->
	matchesRule(rule, vid, pid)
}

fun isCompatibleHidDevice(vid: Int, pid: Int) = isCompatibleHidReceiver(vid, pid) || isCompatibleHidTracker(vid, pid)
