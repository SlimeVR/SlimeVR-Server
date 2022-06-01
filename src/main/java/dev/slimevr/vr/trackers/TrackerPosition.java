package dev.slimevr.vr.trackers;

import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import solarxr_protocol.datatypes.BodyPart;


/**
 * Represents a position on the body that a tracker could be placed. Any bone is
 * a valid position.
 *
 * TrackerPosition intentionally lacks a numerical id to avoid breakage.
 */
public enum TrackerPosition {
	// @formatter:off
	HMD("HMD", TrackerRole.HMD, BodyPart.HMD),
	NECK("body:neck", TrackerRole.NECK, BodyPart.NECK),
	CHEST("body:chest", TrackerRole.CHEST, BodyPart.CHEST),
	WAIST("body:waist", Optional.empty(), BodyPart.WAIST),
	HIP("body:hip", TrackerRole.WAIST, BodyPart.HIP),
	LEFT_KNEE("body:left_knee", TrackerRole.LEFT_KNEE, BodyPart.LEFT_KNEE),
	RIGHT_KNEE("body:right_knee", TrackerRole.RIGHT_KNEE, BodyPart.RIGHT_KNEE),
	LEFT_ANKLE("body:left_ankle", Optional.empty(), BodyPart.LEFT_ANKLE),
	RIGHT_ANKLE("body:right_ankle", Optional.empty(), BodyPart.RIGHT_ANKLE),
	LEFT_FOOT("body:left_foot", TrackerRole.LEFT_FOOT, BodyPart.LEFT_FOOT),
	RIGHT_FOOT("body:right_foot", TrackerRole.RIGHT_FOOT, BodyPart.RIGHT_FOOT),
	LEFT_CONTROLLER("body:left_controller", TrackerRole.LEFT_CONTROLLER, BodyPart.LEFT_CONTROLLER),
	RIGHT_CONTROLLER("body:right_controller", TrackerRole.RIGHT_CONTROLLER, BodyPart.RIGHT_CONTROLLER),
	LEFT_FOREARM("body:left_forearm", TrackerRole.LEFT_ELBOW, BodyPart.LEFT_FOREARM),
	RIGHT_FOREARM("body:right_forearm", TrackerRole.RIGHT_ELBOW, BodyPart.RIGHT_FOREARM),
	LEFT_UPPER_ARM("body:left_upperarm", TrackerRole.LEFT_SHOULDER, BodyPart.LEFT_UPPER_ARM),
	RIGHT_UPPER_ARM("body:right_upperarm", TrackerRole.RIGHT_SHOULDER, BodyPart.RIGHT_UPPER_ARM),
	LEFT_HAND("body:left_hand", TrackerRole.LEFT_HAND, BodyPart.LEFT_HAND),
	RIGHT_HAND("body:right_hand", TrackerRole.RIGHT_HAND, BodyPart.RIGHT_HAND),;
	// @formatter:on

	public static final TrackerPosition[] values = values();

	public final String designation;
	public final Optional<TrackerRole> trackerRole;
	/** The associated `BodyPart` */
	public final int bodyPart;

	private TrackerPosition(String designation, TrackerRole nullableTrackerRole, int bodyPart) {
		this(designation, Optional.ofNullable(nullableTrackerRole), bodyPart);
	}

	private TrackerPosition(String designation, Optional<TrackerRole> trackerRole, int bodyPart) {
		this.designation = designation;
		this.trackerRole = trackerRole;
		this.bodyPart = bodyPart;
	}

	/** Indexed by `BodyPart` int value. EFFICIENCY FTW */
	private static final TrackerPosition[] byBodyPart;
	static {
		// Determine maximum value of BodyPart. Kinda hacky, but this will
		// prevent breakage if the max value changes.
		int max = 0;
		for (var field : BodyPart.class.getFields()) {
			if (!Modifier.isStatic(field.getModifiers()) || !field.getType().equals(int.class)) {
				continue;
			}
			try {
				var v = field.getInt(null);
				max = Math.max(max, v);
			} catch (IllegalAccessException e) {
				// unreachable
				java.util.logging.Logger.getGlobal().log(Level.SEVERE, "Reached unreachable code");
			}
		}
		byBodyPart = new TrackerPosition[max + 1];

		for (var tp : TrackerPosition.values) {
			byBodyPart[tp.bodyPart] = tp;
		}
	}

	private static final Map<String, TrackerPosition> byDesignation = new HashMap<>();
	private static final EnumMap<TrackerRole, TrackerPosition> byTrackerRole = new EnumMap<>(
		TrackerRole.class
	);
	static {
		for (TrackerPosition tp : values()) {
			byDesignation.put(tp.designation.toLowerCase(), tp);
			tp.trackerRole.ifPresent((tr) -> byTrackerRole.put(tr, tp));
		}
	}


	/**
	 * Gets the `TrackerPosition` by its string designation.
	 *
	 * @return Returns an optional as not all strings are valid designators.
	 */
	public static Optional<TrackerPosition> getByDesignation(String designation) {
		if (designation == null) {
			return Optional.empty();
		}

		// Support old configs. leg was renamed to knee.
		if (designation.equalsIgnoreCase("body:left_leg"))
			designation = "body:left_knee";
		if (designation.equalsIgnoreCase("body:right_leg"))
			designation = "body:right_knee";


		return Optional.ofNullable(byDesignation.get(designation.toLowerCase()));
	}

	public static Optional<TrackerPosition> getByTrackerRole(TrackerRole role) {
		return Optional.ofNullable(byTrackerRole.get(role));
	}

	public static Optional<TrackerPosition> getByBodyPart(int bodyPart) {
		if (bodyPart < 0 || bodyPart >= TrackerPosition.byBodyPart.length) {
			return Optional.empty();
		}
		return Optional.of(TrackerPosition.byBodyPart[bodyPart]);
	}

}
