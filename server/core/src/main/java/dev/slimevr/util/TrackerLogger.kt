package dev.slimevr.util

import dev.slimevr.SLIMEVR_IDENTIFIER
import dev.slimevr.VRServer
import dev.slimevr.tracking.trackers.Tracker
import io.eiren.util.OperatingSystem
import io.eiren.util.Util
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Consumer

class TrackerLogger : Consumer<Tracker> {

	var saveLogs = true
	private var file: FileWriter? = null
	private var lastSaved = 0L
	val saveInterval = 100

	fun update() {
		if (saveLogs) {
			if (file == null) {
				val dir = OperatingSystem.resolveLogDirectory(SLIMEVR_IDENTIFIER)
					?.toFile()?.absoluteFile
					?: File("").absoluteFile
				file = FileWriter(
					File(
						dir,
						DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(
							LocalDateTime.now()
						) + ".csv"
					)
				)
				writeCSVHeader()
			}
			if(lastSaved + saveInterval < System.currentTimeMillis() && VRServer.instance.allTrackers.isNotEmpty()) {
				lastSaved = System.currentTimeMillis()
				var isFirst = true
				VRServer.instance.allTrackers.forEach {
					if(it.isInternal || it.isComputed)
						return@forEach
					if (!isFirst) {
						file!!.write(';'.code)
					}
					isFirst = false
					file!!.write(
						"\"" + it.position.x + "\";"
							+ "\"" + it.position.y + "\";"
							+ "\"" + it.position.z + "\";"
							+ "\"" + it.getRotation().w + "\";"
							+ "\"" + it.getRotation().x + "\";"
							+ "\"" + it.getRotation().y + "\";"
							+ "\"" + it.getRotation().z + "\";"
							+ "\"" + it.getAcceleration().x + "\";"
							+ "\"" + it.getAcceleration().y + "\";"
							+ "\"" + it.getAcceleration().z + "\";"
							+ "\"" + it.getMagVector().x + "\";"
							+ "\"" + it.getMagVector().y + "\";"
							+ "\"" + it.getMagVector().z + "\";"
							+ "\"" + it.batteryVoltage + "\";"
							+ "\"" + it.batteryLevel + "\";"
							+ "\"" + it.signalStrength + "\";"
							+ "\"" + it.ping + "\";"
							+ "\"" + it.packetsReceived + "\";"
							+ "\"" + it.packetsLost + "\";"
							+ "\"" + it.packetLoss + "\";"
							+ "\"" + it.windowsHit + "\";"
							+ "\"" + it.windowsMiss + "\""
					)
				}
				file!!.write('\n'.code)
				file!!.flush()
			}
		} else {
			if(file != null) {
				saveCSVAndClose()
			}
		}
	}

	private fun saveCSVAndClose() {
		if (file != null) {
			Util.close(file)
			file = null
		}
	}

	private fun writeCSVHeader() {
		var isFirst = true
		VRServer.instance.allTrackers.forEach {
			if(it.isInternal || it.isComputed)
				return@forEach
			if (!isFirst) {
				file!!.write(';'.code)
			}
			isFirst = false
			val tid = it.id.toString()
			file!!.write("\"" + tid + "_x\";"
				+ "\"" + tid + "_y\";"
				+ "\"" + tid + "_z\";"
				+ "\"" + tid + "_qw\";"
				+ "\"" + tid + "_qx\";"
				+ "\"" + tid + "_qy\";"
				+ "\"" + tid + "_qz\";"
				+ "\"" + tid + "_ax\";"
				+ "\"" + tid + "_ay\";"
				+ "\"" + tid + "_az\";"
				+ "\"" + tid + "_mx\";"
				+ "\"" + tid + "_my\";"
				+ "\"" + tid + "_mz\";"
				+ "\"" + tid + "_batv\";"
				+ "\"" + tid + "_batp\";"
				+ "\"" + tid + "_signal\";"
				+ "\"" + tid + "_ping\";"
				+ "\"" + tid + "_p_rcv\";"
				+ "\"" + tid + "_p_lost\";"
				+ "\"" + tid + "_p_loss\";"
				+ "\"" + tid + "_w_hit\";"
				+ "\"" + tid + "_w_miss\"")
		}
		file!!.write('\n'.code)
	}

	override fun accept(t: Tracker) {
		// New tracker appeared, re-create the log file
		saveCSVAndClose()
	}
}
