package dev.slimevr.util

import solarxr_protocol.datatypes.TrackerStatus

fun TrackerStatus.isActive() = this == TrackerStatus.OK || this == TrackerStatus.SLEEPING || this == TrackerStatus.TIMED_OUT
