package dev.slimevr.protocol

import solarxr_protocol.data_feed.DataFeedConfigT

class DataFeed(val config: DataFeedConfigT) {
	var timeLastSent: Long = System.currentTimeMillis()
}
