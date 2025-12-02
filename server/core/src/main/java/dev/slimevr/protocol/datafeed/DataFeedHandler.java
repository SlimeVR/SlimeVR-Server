package dev.slimevr.protocol.datafeed;

import com.google.flatbuffers.FlatBufferBuilder;
import dev.slimevr.protocol.DataFeed;
import dev.slimevr.protocol.GenericConnection;
import dev.slimevr.protocol.ProtocolAPI;
import dev.slimevr.protocol.ProtocolHandler;
import dev.slimevr.tracking.trackers.Tracker;
import io.eiren.util.logging.LogManager;
import solarxr_protocol.MessageBundle;
import solarxr_protocol.data_feed.*;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


public class DataFeedHandler extends ProtocolHandler<DataFeedMessageHeader> {
	private final ProtocolAPI api;

	public DataFeedHandler(ProtocolAPI api) {
		this.api = api;

		registerPacketListener(DataFeedMessage.StartDataFeed, this::onStartDataFeed);
		registerPacketListener(DataFeedMessage.PollDataFeed, this::onPollDataFeedRequest);

		this.api.server.addOnTick(this::sendDataFeedUpdate);
	}

	private void onStartDataFeed(GenericConnection conn, DataFeedMessageHeader header) {
		StartDataFeed req = (StartDataFeed) header.message(new StartDataFeed());
		if (req == null)
			return;
		int dataFeeds = req.dataFeedsLength();

		List<DataFeed> feedList = conn.getContext().getDataFeedList();
		synchronized (feedList) {
			feedList.clear();
			for (int i = 0; i < dataFeeds; i++) {
				// Using the object api here because we
				// need to copy from the buffer, anyway let's
				// do it from here and send the reference to an arraylist
				DataFeedConfigT config = req.dataFeeds(i).unpack();
				feedList.add(new DataFeed(config));
			}
		}
	}

	private void onPollDataFeedRequest(
		GenericConnection conn,
		DataFeedMessageHeader messageHeader
	) {

		PollDataFeed req = (PollDataFeed) messageHeader.message(new PollDataFeed());
		if (req == null)
			return;

		FlatBufferBuilder fbb = new FlatBufferBuilder(300);

		int messageOffset = this.buildDatafeed(fbb, req.config().unpack(), 0);

		DataFeedMessageHeader.startDataFeedMessageHeader(fbb);
		DataFeedMessageHeader.addMessage(fbb, messageOffset);
		DataFeedMessageHeader.addMessageType(fbb, DataFeedMessage.DataFeedUpdate);
		int headerOffset = DataFeedMessageHeader.endDataFeedMessageHeader(fbb);

		MessageBundle.startDataFeedMsgsVector(fbb, 1);
		MessageBundle.addDataFeedMsgs(fbb, headerOffset);
		int datafeedMessagesOffset = fbb.endVector();

		int packet = createMessage(fbb, datafeedMessagesOffset);
		fbb.finish(packet);
		conn.send(fbb.dataBuffer());
	}

	public int buildDatafeed(FlatBufferBuilder fbb, DataFeedConfigT config, int index) {
		int devicesOffset = DataFeedBuilder
			.createDevicesData(
				fbb,
				config.getDataMask(),
				this.api.server.deviceManager
					.getDevices()
			);
		// Synthetic tracker is computed tracker apparently
		int trackersOffset = DataFeedBuilder
			.createSyntheticTrackersData(
				fbb,
				config.getSyntheticTrackersMask(),
				this.api.server
					.getAllTrackers()
					.stream()
					.filter(Tracker::isComputed)
					.collect(Collectors.toList())
			);

		var h = this.api.server.humanPoseManager;
		int bonesOffset = DataFeedBuilder
			.createBonesData(
				fbb,
				config.getBoneMask(),
				h.getAllBones()
			);

		int stayAlignedPoseOffset = 0;
		if (config.getStayAlignedPoseMask()) {
			stayAlignedPoseOffset = DataFeedBuilderKotlin.INSTANCE
				.createStayAlignedPose(fbb, this.api.server.humanPoseManager.skeleton);
		}

		int serverGuardsOffset = 0;
		if (config.getServerGuardsMask()) {
			serverGuardsOffset = DataFeedBuilderKotlin.INSTANCE.createServerGuard(fbb, this.api.server.getServerGuards());
		}

		return DataFeedUpdate
			.createDataFeedUpdate(
				fbb,
				devicesOffset,
				trackersOffset,
				bonesOffset,
				stayAlignedPoseOffset,
				index,
				serverGuardsOffset
			);
	}

	public void sendDataFeedUpdate() {
		long currTime = System.currentTimeMillis();

		this.api.getAPIServers().forEach((server) -> server.getAPIConnections().forEach((conn) -> {
			FlatBufferBuilder fbb = null;

			List<DataFeed> feedList = conn.getContext().getDataFeedList();
			synchronized (feedList) {
				int configsCount = feedList.size();

				int[] data = new int[configsCount];

				for (int index = 0; index < configsCount; index++) {
					DataFeed feed = feedList.get(index);
					Long lastTimeSent = feed.getTimeLastSent();
					DataFeedConfigT configT = feed.getConfig();
					if (currTime - lastTimeSent > configT.getMinimumTimeSinceLast()) {
						if (fbb == null) {
							// That way we create a buffer only when needed
							fbb = new FlatBufferBuilder(300);
						}

						int messageOffset = this.buildDatafeed(fbb, configT, index);

						DataFeedMessageHeader.startDataFeedMessageHeader(fbb);
						DataFeedMessageHeader.addMessage(fbb, messageOffset);
						DataFeedMessageHeader.addMessageType(fbb, DataFeedMessage.DataFeedUpdate);
						data[index] = DataFeedMessageHeader.endDataFeedMessageHeader(fbb);

						feed.setTimeLastSent(currTime);
						int messages = MessageBundle.createDataFeedMsgsVector(fbb, data);
						int packet = createMessage(fbb, messages);
						fbb.finish(packet);
						conn.send(fbb.dataBuffer());
					}
				}
			}
		}));
	}

	@Override
	public void onMessage(GenericConnection conn, DataFeedMessageHeader message) {
		BiConsumer<GenericConnection, DataFeedMessageHeader> consumer = this.handlers[message
			.messageType()];
		if (consumer != null)
			consumer.accept(conn, message);
		else
			LogManager
				.info(
					"[ProtocolAPI] Unhandled Datafeed packet received id: " + message.messageType()
				);
	}

	@Override
	public int messagesCount() {
		return DataFeedMessage.names.length;
	}

	public int createMessage(FlatBufferBuilder fbb, int datafeedMessagesOffset) {
		MessageBundle.startMessageBundle(fbb);
		if (datafeedMessagesOffset > -1)
			MessageBundle.addDataFeedMsgs(fbb, datafeedMessagesOffset);
		return MessageBundle.endMessageBundle(fbb);
	}
}
