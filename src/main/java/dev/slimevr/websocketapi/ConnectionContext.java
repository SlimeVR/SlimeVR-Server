package dev.slimevr.websocketapi;

import slimevr_protocol.data_feed.DataFeedConfigT;

import java.util.ArrayList;
import java.util.List;

public class ConnectionContext {

	private final List<DataFeedConfigT> dataFeedConfigList = new ArrayList<>();

	// I did it in a separate array because it was more convenient than making a parent object of the DataFeedConfigT
	// idk if it should be a concern or not, i think it is fine tbh
	// Futurabeast
	private final List<Long> dataFeedTimers = new ArrayList<>();

	public List<DataFeedConfigT> getDataFeedConfigList() {
		return dataFeedConfigList;
	}

	public List<Long> getDataFeedTimers() {
		return dataFeedTimers;
	}


}
