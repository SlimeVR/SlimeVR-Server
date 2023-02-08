package dev.slimevr.tracking.trackers;

import solarxr_protocol.datatypes.BodyPart;

import java.lang.reflect.Modifier;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;


/**
 * Represents a position on the body that a tracker could be placed. Any bone is
 * a valid position.
 *
 * TrackerPosition intentionally lacks a numerical id to avoid breakage.
 */
public enum TrackerPosition {
	// @formatter:off
	HMD(1, "HMD", TrackerRole.HMD, BodyPart.HEAD),
	NECK(2, "body:neck", TrackerRole.NECK, BodyPart.NECK),
	CHEST(3, "body:chest", TrackerRole.CHEST, BodyPart.CHEST),
	WAIST(4, "body:waist", Optional.empty(), BodyPart.WAIST),
	HIP(5, "body:hip", TrackerRole.WAIST, BodyPart.HIP),
	LEFT_UPPER_LEG(6, "body:left_upper_leg", TrackerRole.LEFT_KNEE, BodyPart.LEFT_UPPER_LEG),
	RIGHT_UPPER_LEG(7, "body:right_upper_leg", TrackerRole.RIGHT_KNEE, BodyPart.RIGHT_UPPER_LEG),
	LEFT_LOWER_LEG(8, "body:left_lower_leg", Optional.empty(), BodyPart.LEFT_LOWER_LEG),
	RIGHT_LOWER_LEG(9, "body:right_lower_leg", Optional.empty(), BodyPart.RIGHT_LOWER_LEG),
	LEFT_FOOT(10, "body:left_foot", TrackerRole.LEFT_FOOT, BodyPart.LEFT_FOOT),
	RIGHT_FOOT(11, "body:right_foot", TrackerRole.RIGHT_FOOT, BodyPart.RIGHT_FOOT),
	LEFT_CONTROLLER(12, "body:left_controller", TrackerRole.LEFT_CONTROLLER, BodyPart.LEFT_CONTROLLER),
	RIGHT_CONTROLLER(13, "body:right_controller", TrackerRole.RIGHT_CONTROLLER, BodyPart.RIGHT_CONTROLLER),
	LEFT_LOWER_ARM(14, "body:left_lower_arm", TrackerRole.LEFT_ELBOW, BodyPart.LEFT_LOWER_ARM),
	RIGHT_LOWER_ARM(15, "body:right_lower_arm", TrackerRole.RIGHT_ELBOW, BodyPart.RIGHT_LOWER_ARM),
	LEFT_UPPER_ARM(16, "body:left_upper_arm", TrackerRole.LEFT_SHOULDER, BodyPart.LEFT_UPPER_ARM),
	RIGHT_UPPER_ARM(17, "body:right_upper_arm", TrackerRole.RIGHT_SHOULDER, BodyPart.RIGHT_UPPER_ARM),
	LEFT_HAND(18, "body:left_hand", TrackerRole.LEFT_HAND, BodyPart.LEFT_HAND),
	RIGHT_HAND(19, "body:right_hand", TrackerRole.RIGHT_HAND, BodyPart.RIGHT_HAND),
	LEFT_SHOULDER(20, "body:left_shoulder", TrackerRole.LEFT_SHOULDER, BodyPart.LEFT_SHOULDER),
	RIGHT_SHOULDER(21, "body:right_shoulder", TrackerRole.RIGHT_SHOULDER, BodyPart.RIGHT_SHOULDER);
	// @formatter:on

	public static final TrackerPosition[] values = values();

	public final int id;
	public final String designation;
	public final Optional<TrackerRole> trackerRole;
	/** The associated `BodyPart` */
	public final int bodyPart;

	TrackerPosition(int id, String designation, TrackerRole nullableTrackerRole, int bodyPart) {
		this(id, designation, Optional.ofNullable(nullableTrackerRole), bodyPart);
	}

	TrackerPosition(int id, String designation, Optional<TrackerRole> trackerRole, int bodyPart) {
		this.id = id;
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

		// Support old configs.
		if (
			designation.equalsIgnoreCase("body:left_leg")
				|| designation.equalsIgnoreCase("body:left_knee")
		) {
			designation = "body:left_upper_leg";
		} else if (
			designation.equalsIgnoreCase("body:right_leg")
				|| designation.equalsIgnoreCase("body:right_knee")
		) {
			designation = "body:right_upper_leg";
		} else if (designation.equalsIgnoreCase("body:left_ankle")) {
			designation = "body:left_lower_leg";
		} else if (designation.equalsIgnoreCase("body:right_ankle")) {
			designation = "body:right_lower_leg";
		} else if (designation.equalsIgnoreCase("body:left_forearm")) {
			designation = "body:left_lower_arm";
		} else if (designation.equalsIgnoreCase("body:right_forearm")) {
			designation = "body:right_lower_arm";
		} else if (designation.equalsIgnoreCase("body:left_upperarm")) {
			designation = "body:left_upper_arm";
		} else if (designation.equalsIgnoreCase("body:right_upperarm")) {
			designation = "body:right_upper_arm";
		}

		return Optional.ofNullable(byDesignation.get(designation.toLowerCase()));
	}

	public static Optional<TrackerPosition> getByTrackerRole(TrackerRole role) {
		return Optional.ofNullable(byTrackerRole.get(role));
	}

	public static Optional<TrackerPosition> getByBodyPart(int bodyPart) {
		if (bodyPart <= 0 || bodyPart >= TrackerPosition.byBodyPart.length) {
			return Optional.empty();
		}
		return Optional.of(TrackerPosition.byBodyPart[bodyPart]);
	}

}
