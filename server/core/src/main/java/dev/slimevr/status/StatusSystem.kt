package dev.slimevr.status

import solarxr_protocol.rpc.StatusDataUnion
import solarxr_protocol.rpc.StatusMessageT
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

class StatusSystem {
	private val listeners: MutableList<StatusListener> = CopyOnWriteArrayList()
	private val statuses: MutableMap<Int, StatusDataUnion> = ConcurrentHashMap()
	private val prioritizedStatuses: MutableSet<Int> = ConcurrentHashMap.newKeySet()
	private val idCounter = AtomicInteger(1)

	fun addListener(listener: StatusListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: StatusListener) {
		listeners.remove(listener)
	}

	fun getStatuses(): Array<StatusMessageT> = statuses.map { (id, message) ->
		val status = StatusMessageT()
		status.id = id.toUInt().toLong()
		status.data = message
		status.prioritized = prioritizedStatuses.contains(id)
		status
	}.toTypedArray()

	fun hasStatusType(dataType: Byte): Boolean = statuses.any {
		it.value.type == dataType
	}
}

interface StatusListener {
	fun onStatusChanged(id: UInt, message: StatusDataUnion, prioritized: Boolean)
	fun onStatusRemoved(id: UInt)
}
