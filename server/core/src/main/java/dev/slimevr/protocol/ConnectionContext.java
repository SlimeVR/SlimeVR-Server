package dev.slimevr.protocol;

import java.util.ArrayList;
import java.util.List;


public class ConnectionContext {

	private final List<DataFeed> dataFeedList = new ArrayList<>();

	private final List<Integer> subscribedTopics = new ArrayList<>();

	private boolean useSerial = false;

	private boolean useProvisioning = false;
	private boolean useAutoBone = false;

	public List<DataFeed> getDataFeedList() {
		return dataFeedList;
	}

	public List<Integer> getSubscribedTopics() {
		return subscribedTopics;
	}

	public boolean useSerial() {
		return useSerial;
	}

	public void setUseSerial(boolean useSerial) {
		this.useSerial = useSerial;
	}

	public boolean useAutoBone() {
		return useAutoBone;
	}

	public void setUseAutoBone(boolean useAutoBone) {
		this.useAutoBone = useAutoBone;
	}

	public boolean useProvisioning() {
		return useProvisioning;
	}

	public void setUseProvisioning(boolean useProvisioning) {
		this.useProvisioning = useProvisioning;
	}
}
