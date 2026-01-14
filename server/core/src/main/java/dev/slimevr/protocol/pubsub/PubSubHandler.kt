package dev.slimevr.protocol.pubsub

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolAPIServer
import dev.slimevr.protocol.ProtocolHandler
import io.eiren.util.logging.LogManager
import solarxr_protocol.MessageBundle
import solarxr_protocol.pub_sub.Message
import solarxr_protocol.pub_sub.PubSubHeader
import solarxr_protocol.pub_sub.PubSubUnion
import solarxr_protocol.pub_sub.SubscriptionRequest
import solarxr_protocol.pub_sub.Topic
import solarxr_protocol.pub_sub.TopicHandle
import solarxr_protocol.pub_sub.TopicHandleRequest
import solarxr_protocol.pub_sub.TopicId
import solarxr_protocol.pub_sub.TopicIdT
import solarxr_protocol.pub_sub.TopicMapping
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

class PubSubHandler(private val api: ProtocolAPI) : ProtocolHandler<PubSubHeader>() {
	// Two ways maps for faster reading when handling lots of packets
	var topicsHandle: HashMap<HashedTopicId, Int> = HashMap()
	var handleTopics: HashMap<Int, HashedTopicId> = HashMap()

	var nextLocalHandle: AtomicInteger = AtomicInteger()

	init {
		registerPacketListener(PubSubUnion.SubscriptionRequest, ::onSubscriptionRequest)
		registerPacketListener(PubSubUnion.TopicHandleRequest, ::onTopicHandleRequest)
		registerPacketListener(PubSubUnion.Message, ::onTopicMessage)
	}

	private fun getTopicHandle(topicIdT: TopicIdT): Int {
		val hashedTopicId = HashedTopicId(topicIdT)
		var handleT = topicsHandle.get(hashedTopicId)
		// if no handle exists for this topic id we create one and return it
		// anyway
		if (handleT == null) {
			handleT = nextLocalHandle.incrementAndGet()
			topicsHandle[hashedTopicId] = handleT
			handleTopics[handleT] = hashedTopicId
		}

		return handleT
	}

	fun onSubscriptionRequest(conn: GenericConnection, messageHeader: PubSubHeader) {
		val req =
			messageHeader.u(SubscriptionRequest()) as SubscriptionRequest? ?: return

		var subHandle = -1
		if (req.topicType() == Topic.TopicHandle) {
			val handle = req.topic(TopicHandle()) as TopicHandle?
			if (handle != null && handleTopics.containsKey(handle.id())) subHandle = handle.id()
		} else if (req.topicType() == Topic.TopicId) {
			val topicId = req.topic(TopicId()) as TopicId?
			if (topicId != null) subHandle = getTopicHandle(topicId.unpack())
		}

		assert(subHandle != -1)

		val finalSubHandle = subHandle
		val first = conn
			.context
			.subscribedTopics
			.stream()
			.filter { handle: Int -> handle == finalSubHandle }
			.findFirst()
		if (!first.isPresent) {
			conn.context.subscribedTopics.add(finalSubHandle)
		}

		val fbb = FlatBufferBuilder(32)
		val topicIdOffset = TopicId.pack(fbb, handleTopics.get(finalSubHandle)!!.inner)
		val topicHandleOffset = TopicHandle.createTopicHandle(fbb, finalSubHandle)

		val outbound = createMessage(
			fbb,
			PubSubUnion.TopicMapping,
			TopicMapping.createTopicMapping(fbb, topicIdOffset, topicHandleOffset),
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	fun onTopicHandleRequest(conn: GenericConnection, messageHeader: PubSubHeader) {
		val req = messageHeader.u(TopicHandleRequest()) as TopicHandleRequest? ?: return

		val topicRequest = req.unpack()
		val handle = getTopicHandle(topicRequest.id)

		val fbb = FlatBufferBuilder(32)
		val topicIdOffset = TopicId.pack(fbb, topicRequest.id)
		val topicHandleOffset = TopicHandle.createTopicHandle(fbb, handle)

		val outbound = createMessage(
			fbb,
			PubSubUnion.TopicMapping,
			TopicMapping.createTopicMapping(fbb, topicIdOffset, topicHandleOffset),
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	fun onTopicMessage(c: GenericConnection, messageHeader: PubSubHeader) {
		val req = messageHeader.u(Message()) as Message? ?: return

		val messageT = req.unpack()

		var subHandle = 1
		if (messageT.topic.type == Topic.TopicHandle) {
			subHandle = messageT.topic.asTopicHandle().id
		} else if (messageT.topic.type == Topic.TopicId) {
			subHandle = getTopicHandle(messageT.topic.asTopicId())
		}

		assert(subHandle != -1)

		val finalSubHandle = subHandle

		this.api.apiServers.forEach(
			Consumer { server: ProtocolAPIServer ->
				server.apiConnections.forEach { conn: GenericConnection ->
					// Make sure that we are not sending a message to ourselves
					// And check that the receiver has subscribed to the topic
					if (conn.connectionId != c.connectionId &&
						conn.context.subscribedTopics
							.contains(finalSubHandle)
					) {
						val fbb = FlatBufferBuilder(32)
						val outbound = createMessage(
							fbb,
							PubSubUnion.Message,
							Message.pack(fbb, messageT),
						)
						fbb.finish(outbound)
						conn.send(fbb.dataBuffer())
					}
				}
			},
		)
	}

	override fun onMessage(conn: GenericConnection, message: PubSubHeader) {
		val consumer = this.handlers[message.uType().toInt()]
		if (consumer != null) {
			consumer.accept(conn, message)
		} else {
			LogManager
				.info("[ProtocolAPI] Unhandled PubSub packet received id: " + message.uType())
		}
	}

	override fun messagesCount(): Int = PubSubUnion.names.size

	fun createMessage(fbb: FlatBufferBuilder, messageType: Byte, messageOffset: Int): Int {
		val data = IntArray(1)

		data[0] = PubSubHeader.createPubSubHeader(fbb, messageType, messageOffset)

		val messages = MessageBundle.createPubSubMsgsVector(fbb, data)

		MessageBundle.startMessageBundle(fbb)
		MessageBundle.addPubSubMsgs(fbb, messages)
		return MessageBundle.endMessageBundle(fbb)
	}
}
