package dev.slimevr.autobone;

import java.util.HashMap;
import java.util.Map;


public enum AutoBoneProcessType {
	NONE(0),
	RECORD(1),
	SAVE(2),
	PROCESS(3),
	APPLY(4);

	public final int id;

	private static final Map<Integer, AutoBoneProcessType> byId = new HashMap<>();

	private AutoBoneProcessType(int id) {
		this.id = id;
	}

	public static AutoBoneProcessType getById(int id) {
		return byId.get(id);
	}

	static {
		for (AutoBoneProcessType abpt : values()) {
			byId.put(abpt.id, abpt);
		}
	}
}
