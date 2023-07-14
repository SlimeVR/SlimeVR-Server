package dev.slimevr.autobone

enum class AutoBoneProcessType(val id: Int) {
	NONE(0), RECORD(1), SAVE(2), PROCESS(3), APPLY(4);

	companion object {
		fun getById(id: Int): AutoBoneProcessType? = byId[id]
	}
}

private val byId = AutoBoneProcessType.values().associateBy { it.id }
