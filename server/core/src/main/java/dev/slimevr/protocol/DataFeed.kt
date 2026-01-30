package dev.slimevr.protocol

import solarxr_protocol.data_feed.DataFeedConfig

class DataFeed(val config: DataFeedConfig) {
	var timeLastSent: Long = System.currentTimeMillis()
}
