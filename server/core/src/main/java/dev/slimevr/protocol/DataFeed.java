package dev.slimevr.protocol;

import solarxr_protocol.data_feed.DataFeedConfigT;


public class DataFeed {
	private DataFeedConfigT config;
	private Long timeLastSent;

	public DataFeed(DataFeedConfigT config) {
		this.config = config;
		this.timeLastSent = System.currentTimeMillis();
	}

	public DataFeedConfigT getConfig() {
		return config;
	}

	public Long getTimeLastSent() {
		return timeLastSent;
	}

	public void setTimeLastSent(Long timeLastSent) {
		this.timeLastSent = timeLastSent;
	}
}
