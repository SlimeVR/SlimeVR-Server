package dev.slimevr.websocketapi;

import slimevr_protocol.data_feed.DataFeedConfigT;

import java.util.ArrayList;
import java.util.List;

public class ConnectionContext {

	private final List<DataFeedConfigT> dataFeedConfigList = new ArrayList<>();

	public List<DataFeedConfigT> getDataFeedConfigList() {
		return dataFeedConfigList;
	}

}
