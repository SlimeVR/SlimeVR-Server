package dev.slimevr.util

import solarxr_protocol.datatypes.TrackerStatus

fun TrackerStatus.isActive() = this == TrackerStatus.OK || this == TrackerStatus.SLEEPING || this == TrackerStatus.TIMED_OUT

/** Active, or occluded and expected back. A present tracker keeps its bone and its body part. */
fun isPresent(status: TrackerStatus) = status.isActive() || status == TrackerStatus.OCCLUDED
