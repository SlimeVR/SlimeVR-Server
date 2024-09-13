package dev.slimevr.status

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import solarxr_protocol.rpc.StatusDataUnion
import solarxr_protocol.rpc.StatusMessageT
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

class StatusSystem {
	private val listeners: MutableList<StatusListener> = CopyOnWriteArrayList()
	private val statuses: MutableMap<Int, StatusDataUnion> = Int2ObjectOpenHashMap()
	private val prioritizedStatuses: MutableSet<Int> = IntOpenHashSet()
	private val idCounter = AtomicInteger(1)

	fun addListener(listener: StatusListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: StatusListener) {
		listeners.remove(listener)
	}

	// For some reason Int2ObjectOpenHashMap can report a size of -1... isEmpty does not
	// consider this and will not function, and if the size is -1 then it will crash immediately,
	// so please do not touch this workaround!
	@Suppress("ReplaceSizeCheckWithIsNotEmpty")
	fun getStatuses(): Array<StatusMessageT> = if (statuses.size > 0) {
		statuses.map { (id, message) ->
			val status = StatusMessageT()
			status.id = id.toUInt().toLong()
			status.data = message
			status.prioritized = prioritizedStatuses.contains(id)
			status
		}.toTypedArray()
	} else {
		emptyArray()
	}

	/**
	 * @return the ID of the status, 0 is not a valid ID, can be used as replacement of null
	 */
	@JvmName("addStatusInt")
	fun addStatus(statusData: StatusDataUnion, prioritized: Boolean = false): UInt {
		val id = idCounter.getAndUpdate {
			(it.toUInt() + 1u).toInt() // the simple way of making unsigned math
		}
		statuses[id] = statusData
		if (prioritized) {
			prioritizedStatuses.add(id)
		}

		listeners.forEach {
			it.onStatusChanged(id.toUInt(), statusData, prioritized)
		}

		return id.toUInt()
	}

	@JvmName("removeStatusInt")
	fun removeStatus(id: UInt) {
		statuses.remove(id.toInt())
		prioritizedStatuses.remove(id.toInt())

		listeners.forEach {
			it.onStatusRemoved(id)
		}
	}
}

interface StatusListener {
	fun onStatusChanged(id: UInt, message: StatusDataUnion, prioritized: Boolean)
	fun onStatusRemoved(id: UInt)
}
