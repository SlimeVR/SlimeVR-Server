package dev.slimevr.protocol.pubsub

import solarxr_protocol.pub_sub.TopicIdT

// This class is so the HashMap referencing the TopicId as key works
// it needs a unique hashcode based on the topicId and also an equals function
// because equals hashcode does not mean equals strings
class HashedTopicId(val inner: TopicIdT) {
	private val hashcode: Int = (
		(
			inner.appName +
				"." +
				inner.organization +
				"." +
				inner.topic
			)
		).hashCode()

	override fun hashCode(): Int = hashcode

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		val that = other as HashedTopicId
		return inner.organization == that.inner.organization &&
			inner.appName == that.inner.appName &&
			inner.topic == that.inner.topic
	}
}
