package dev.slimevr.tracking.trackers

enum class ResetBodyPose(val id: Int) {

	SKIING(solarxr_protocol.rpc.ResetBodyPose.SKIING),
	SITTING_LEANING_BACK(solarxr_protocol.rpc.ResetBodyPose.SITTING_LEANING_BACK),
	;

	companion object {

		private val byId = ResetBodyPose.entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: Int): ResetBodyPose? = byId[id]
	}
}
