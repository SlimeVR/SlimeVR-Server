package dev.slimevr.tracker

fun udpTrackerHardwareId(deviceHardwareId: String, sensorId: Int): String = "udp:$deviceHardwareId:$sensorId"

fun hidTrackerHardwareId(address: String): String = "hid:$address"

fun driverTrackerHardwareId(hardwareIdentifier: String): String = "driver:$hardwareIdentifier"
