package dev.slimevr.protocol

class ConnectionContext {
	val dataFeedList: MutableList<DataFeed> = mutableListOf()
	val subscribedTopics: MutableList<Int> = mutableListOf()

	var useSerial: Boolean = false
	var useProvisioning: Boolean = false
	var useAutoBone: Boolean = false
}
