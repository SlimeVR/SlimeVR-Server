package dev.slimevr.protocol;

import com.google.flatbuffers.FlatBufferBuilder;
import io.eiren.util.logging.LogManager;
import solarxr_protocol.MessageBundle;
import solarxr_protocol.pub_sub.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;


public class PubSubHandler extends ProtocolHandler<PubSubHeader> {

	private final ProtocolAPI api;

	// Two ways maps for faster reading when handling lots of packets
	public HashMap<HashedTopicId, Integer> topicsHandle = new HashMap<>();
	public HashMap<Integer, HashedTopicId> handleTopics = new HashMap<>();

	public AtomicInteger nextLocalHandle = new AtomicInteger();

	public PubSubHandler(ProtocolAPI api) {
		super();
		this.api = api;

		registerPacketListener(PubSubUnion.SubscriptionRequest, this::onSubscriptionRequest);
		registerPacketListener(PubSubUnion.TopicHandleRequest, this::onTopicHandleRequest);
		registerPacketListener(PubSubUnion.Message, this::onTopicMessage);
	}

	private int getTopicHandle(TopicIdT topicIdT) {
		HashedTopicId hashedTopicId = new HashedTopicId(topicIdT);
		Integer handleT = topicsHandle.get(hashedTopicId);
		// if no handle exists for this topic id we create one and return it
		// anyway
		if (handleT == null) {
			handleT = nextLocalHandle.incrementAndGet();
			topicsHandle.put(hashedTopicId, handleT);
			handleTopics.put(handleT, hashedTopicId);
		}

		return handleT;
	}

	public void onSubscriptionRequest(GenericConnection conn, PubSubHeader messageHeader) {
		SubscriptionRequest req = (SubscriptionRequest) messageHeader.u(new SubscriptionRequest());

		if (req == null)
			return;

		int subHandle = -1;
		if (req.topicType() == Topic.TopicHandle) {
			TopicHandle handle = (TopicHandle) req.topic(new TopicHandle());
			if (handle != null && handleTopics.containsKey(handle.id()))
				subHandle = handle.id();
		} else if (req.topicType() == Topic.TopicId) {
			TopicId topicId = (TopicId) req.topic(new TopicId());
			if (topicId != null)
				subHandle = getTopicHandle(topicId.unpack());
		}

		assert subHandle != -1;

		final int finalSubHandle = subHandle;
		Optional<Integer> first = conn
			.getContext()
			.getSubscribedTopics()
			.stream()
			.filter((handle) -> handle == finalSubHandle)
			.findFirst();
		if (first.isEmpty()) {
			conn.getContext().getSubscribedTopics().add(finalSubHandle);
		}


		FlatBufferBuilder fbb = new FlatBufferBuilder(32);
		int topicIdOffset = TopicId.pack(fbb, handleTopics.get(finalSubHandle).getInner());
		int topicHandleOffset = TopicHandle.createTopicHandle(fbb, finalSubHandle);

		int outbound = createMessage(
			fbb,
			PubSubUnion.TopicMapping,
			TopicMapping.createTopicMapping(fbb, topicIdOffset, topicHandleOffset)
		);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}

	public void onTopicHandleRequest(GenericConnection conn, PubSubHeader messageHeader) {

		TopicHandleRequest req = (TopicHandleRequest) messageHeader.u(new TopicHandleRequest());

		if (req == null)
			return;

		TopicHandleRequestT topicRequest = req.unpack();
		int handle = getTopicHandle(topicRequest.getId());

		FlatBufferBuilder fbb = new FlatBufferBuilder(32);
		int topicIdOffset = TopicId.pack(fbb, topicRequest.getId());
		int topicHandleOffset = TopicHandle.createTopicHandle(fbb, handle);

		int outbound = createMessage(
			fbb,
			PubSubUnion.TopicMapping,
			TopicMapping.createTopicMapping(fbb, topicIdOffset, topicHandleOffset)
		);
		fbb.finish(outbound);
		conn.send(fbb.dataBuffer());
	}


	public void onTopicMessage(GenericConnection c, PubSubHeader messageHeader) {
		Message req = (Message) messageHeader.u(new Message());

		if (req == null)
			return;

		MessageT messageT = req.unpack();

		int subHandle = 1;
		if (messageT.getTopic().getType() == Topic.TopicHandle) {
			subHandle = messageT.getTopic().asTopicHandle().getId();
		} else if (messageT.getTopic().getType() == Topic.TopicId) {
			subHandle = getTopicHandle(messageT.getTopic().asTopicId());
		}


		assert subHandle != -1;

		int finalSubHandle = subHandle;

		this.api.getAPIServers().forEach((server) -> {
			server.getAPIConnections().forEach((conn) -> {
				// Make sure that we are not sending a message to ourselves
				// And check that the receiver has subscribed to the topic
				if (
					!conn.getConnectionId().equals(c.getConnectionId())
						&& conn.getContext().getSubscribedTopics().contains(finalSubHandle)
				) {
					FlatBufferBuilder fbb = new FlatBufferBuilder(32);
					int outbound = createMessage(
						fbb,
						PubSubUnion.Message,
						Message.pack(fbb, messageT)
					);
					fbb.finish(outbound);
					conn.send(fbb.dataBuffer());
				}
			});
		});
	}

	@Override
	public void onMessage(GenericConnection conn, PubSubHeader message) {
		BiConsumer<GenericConnection, PubSubHeader> consumer = this.handlers[message.uType()];
		if (consumer != null)
			consumer.accept(conn, message);
		else
			LogManager
				.info("[ProtocolAPI] Unhandled PubSub packet received id: " + message.uType());
	}

	@Override
	public int messagesCount() {
		return PubSubUnion.names.length;
	}

	public int createMessage(FlatBufferBuilder fbb, byte messageType, int messageOffset) {
		int[] data = new int[1];

		data[0] = PubSubHeader.createPubSubHeader(fbb, messageType, messageOffset);

		int messages = MessageBundle.createPubSubMsgsVector(fbb, data);

		MessageBundle.startMessageBundle(fbb);
		MessageBundle.addPubSubMsgs(fbb, messages);
		return MessageBundle.endMessageBundle(fbb);
	}
}
