package dev.slimevr.protocol.pubsub

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.ProtocolAPIServer
import dev.slimevr.protocol.ProtocolHandler
import io.eiren.util.logging.LogManager
import solarxr_protocol.MessageBundle
import solarxr_protocol.datatypes.Bytes
import solarxr_protocol.datatypes.StringTable
import solarxr_protocol.pub_sub.KeyValues
import solarxr_protocol.pub_sub.Message
import solarxr_protocol.pub_sub.Payload
import solarxr_protocol.pub_sub.PubSubHeader
import solarxr_protocol.pub_sub.PubSubUnion
import solarxr_protocol.pub_sub.SubscriptionRequest
import solarxr_protocol.pub_sub.Topic
import solarxr_protocol.pub_sub.TopicHandle
import solarxr_protocol.pub_sub.TopicHandleRequest
import solarxr_protocol.pub_sub.TopicId
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

	private fun getTopicHandle(topicIdT: TopicId): Int {
		val hashedTopicId = HashedTopicId(topicIdT)
		var handleT = topicsHandle[hashedTopicId]
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
		if (messageHeader.uType != PubSubUnion.SubscriptionRequest) return
		val req = messageHeader.u(SubscriptionRequest()) as SubscriptionRequest

		var subHandle = -1
		if (req.topicType == Topic.TopicHandle) {
			val handle = req.topic(TopicHandle()) as TopicHandle?
			if (handle != null && handleTopics.containsKey(handle.id.toInt())) subHandle = handle.id.toInt()
		} else if (req.topicType == Topic.TopicId) {
			val topicId = req.topic(TopicId()) as TopicId?
			if (topicId != null) subHandle = getTopicHandle(topicId)
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

		val topic = handleTopics[finalSubHandle]!!.inner
		val topicIdOffset = createTopicId(fbb, topic.organization, topic.appName, topic.topic)
		val topicHandleOffset = TopicHandle.createTopicHandle(fbb, finalSubHandle.toUShort())

		val outbound = createMessageBundle(
			fbb,
			PubSubUnion.TopicMapping,
			TopicMapping.createTopicMapping(fbb, topicIdOffset, topicHandleOffset),
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	fun onTopicHandleRequest(conn: GenericConnection, messageHeader: PubSubHeader) {
		if (messageHeader.uType != PubSubUnion.TopicHandleRequest) return
		val topicRequest = messageHeader.u(TopicHandleRequest()) as TopicHandleRequest

		val topic = topicRequest.id ?: error("missing topicId")
		val handle = getTopicHandle(topic)

		val fbb = FlatBufferBuilder(32)

		val topicIdOffset = createTopicId(fbb, topic.organization, topic.appName, topic.topic)
		val topicHandleOffset = TopicHandle.createTopicHandle(fbb, handle.toUShort())

		val outbound = createMessageBundle(
			fbb,
			PubSubUnion.TopicMapping,
			TopicMapping.createTopicMapping(fbb, topicIdOffset, topicHandleOffset),
		)
		fbb.finish(outbound)
		conn.send(fbb.dataBuffer())
	}

	@OptIn(ExperimentalUnsignedTypes::class)
	fun onTopicMessage(c: GenericConnection, messageHeader: PubSubHeader) {
		if (messageHeader.uType != PubSubUnion.Message) return
		val msg = messageHeader.u(Message()) as Message

		var subHandle = 1
		if (msg.topicType == Topic.TopicHandle) {
			subHandle = (msg.topic(TopicHandle()) as TopicHandle).id.toInt()
		} else if (msg.topicType == Topic.TopicId) {
			subHandle = getTopicHandle(msg.topic(TopicId()) as TopicId)
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

						val topicOffset = when(msg.topicType) {
							Topic.TopicId -> {
								val topic = msg.topic(TopicId()) as TopicId
								createTopicId(fbb, topic.organization, topic.appName, topic.topic)
							}
							Topic.TopicHandle -> {
								val topic = msg.topic(TopicHandle()) as TopicHandle
								TopicHandle.createTopicHandle(fbb, topic.id)
							}
							else -> 0
						}

						val payloadOffset = when(msg.payloadType) {
							Payload.solarxr_protocol_datatypes_StringTable -> {
								val stringTable = msg.payload(StringTable()) as StringTable

								val stringOffset = stringTable.s?.let { fbb.createString(it) } ?: 0
								StringTable.createStringTable(fbb, stringOffset)
							}
							Payload.solarxr_protocol_datatypes_Bytes -> {
								val bytes = msg.payload(Bytes()) as Bytes
								val bytesArray = (0..bytes.bLength).map { index ->
									bytes.b(index).toByte()
								}.toByteArray()

								val bytesOffset = fbb.createByteVector(bytesArray)
								Bytes.createBytes(fbb, bytesOffset)
							}
							Payload.KeyValues -> {
								val keyValues = msg.payload(KeyValues()) as KeyValues

								val keys = (0..keyValues.keysLength).map { index ->
									keyValues.keys(index)
								}.toTypedArray()
								val values = (0..keyValues.valuesLength).map { index ->
									keyValues.values(index)
								}.toTypedArray()

								val keysOffsets = keys.map {
									fbb.createString(it)
								}.toIntArray()
								val valuesOffsets = values.map {
									fbb.createString(it)
								}.toIntArray()

								val keysOffset = KeyValues.createKeysVector(fbb, keysOffsets)
								val valuesOffset = KeyValues.createValuesVector(fbb, valuesOffsets)

								KeyValues.createKeyValues(fbb, keysOffset, valuesOffset)
							}
							else -> 0
						}

						val messageOffset = Message.createMessage(fbb, msg.topicType, topicOffset, msg.payloadType, payloadOffset)
						val outbound = createMessageBundle(
							fbb,
							PubSubUnion.Message,
							messageOffset,
						)
						fbb.finish(outbound)
						conn.send(fbb.dataBuffer())
					}
				}
			},
		)
	}

	override fun onMessage(conn: GenericConnection, message: PubSubHeader) {
		val consumer = this.handlers[message.uType.toInt()]
		if (consumer != null) {
			consumer.accept(conn, message)
		} else {
			LogManager
				.info("[ProtocolAPI] Unhandled PubSub packet received id: " + message.uType)
		}
	}

	override fun messagesCount(): Int = (PubSubUnion.TopicMapping + 1u).toInt()

	fun createMessageBundle(fbb: FlatBufferBuilder, messageType: UByte, messageOffset: Int): Int {
		val data = IntArray(1)

		data[0] = PubSubHeader.createPubSubHeader(fbb, messageType, messageOffset)

		val messages = MessageBundle.createPubSubMsgsVector(fbb, data)

		MessageBundle.startMessageBundle(fbb)
		MessageBundle.addPubSubMsgs(fbb, messages)
		return MessageBundle.endMessageBundle(fbb)
	}

	fun createTopicId(fbb: FlatBufferBuilder, organization: String?, appName: String?, topic: String?): Int {
		val organizationOffset = organization?.let { fbb.createString(it) } ?: 0
		val appNameOffset = appName?.let { fbb.createString(it) } ?: 0
		val topicOffset = topic?.let { fbb.createString(it) } ?: 0

		return TopicId.createTopicId(fbb, organizationOffset, appNameOffset, topicOffset)
	}
}
