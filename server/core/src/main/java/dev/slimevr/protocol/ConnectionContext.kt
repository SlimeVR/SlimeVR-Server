package dev.slimevr.protocol

import dev.slimevr.tracking.trackers.Tracker

class ConnectionContext {
	val dataFeedList: MutableList<DataFeed> = mutableListOf()
	val subscribedTopics: MutableList<Int> = mutableListOf()
	val createdTrackers: MutableList<Tracker> = mutableListOf()

	var useSerial: Boolean = false
	var useProvisioning: Boolean = false
	var useAutoBone: Boolean = false
}
