package dev.slimevr.gui;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.VRServer;
import dev.slimevr.config.TrackerConfig;
import dev.slimevr.gui.swing.EJBagNoStretch;
import dev.slimevr.gui.swing.EJBoxNoStretch;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.StringUtils;
import io.eiren.util.ann.AWTThread;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;


public class TrackersList extends EJBoxNoStretch {

	private static final long UPDATE_DELAY = 50;
	private final VRServer server;
	private final VRServerGUI gui;
	private final List<TrackerPanel> trackers = new FastList<>();
	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];
	private long lastUpdate = 0;
	private boolean debug = false;

	public TrackersList(VRServer server, VRServerGUI gui) {
		super(BoxLayout.PAGE_AXIS, false, true);
		this.server = server;
		this.gui = gui;

		setAlignmentY(TOP_ALIGNMENT);

		server.addNewTrackerConsumer(this::newTrackerAdded);
	}

	private static int getTrackerSort(Tracker t) {
		Tracker tracker = t.get();
		if (tracker instanceof IMUTracker)
			return 0;
		if (tracker instanceof HMDTracker)
			return 100;
		if (tracker instanceof ComputedTracker)
			return 200;
		return 1000;
	}

	@AWTThread
	public void setDebug(boolean debug) {
		this.debug = debug;
		build();
	}

	@AWTThread
	private void build() {
		removeAll();

		trackers.sort((tr1, tr2) -> getTrackerSort(tr1.t) - getTrackerSort(tr2.t));

		Class<? extends Tracker> currentClass = null;

		EJBoxNoStretch line = null;
		boolean first = true;

		for (TrackerPanel tr : trackers) {
			Tracker t = tr.t.get();
			if (currentClass != t.getClass()) {
				currentClass = t.getClass();
				if (line != null)
					line.add(Box.createHorizontalGlue());
				line = null;
				line = new EJBoxNoStretch(BoxLayout.LINE_AXIS, false, true);
				line.add(Box.createHorizontalGlue());
				JLabel nameLabel;
				line.add(nameLabel = new JLabel(currentClass.getSimpleName()));
				nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
				line.add(Box.createHorizontalGlue());
				add(line);
				line = null;
			}

			if (line == null) {
				line = new EJBoxNoStretch(BoxLayout.LINE_AXIS, false, true);
				add(Box.createVerticalStrut(3));
				add(line);
				first = true;
			} else {
				line.add(Box.createHorizontalStrut(3));
				first = false;
			}
			tr.build();
			line.add(tr);
			if (!first)
				line = null;
		}
		validate();
		gui.refresh();
	}

	@ThreadSafe
	public void updateTrackers() {
		if (lastUpdate + UPDATE_DELAY > System.currentTimeMillis())
			return;
		lastUpdate = System.currentTimeMillis();
		java.awt.EventQueue.invokeLater(() -> {
			for (TrackerPanel tr : trackers) {
				tr.update();
			}
		});
	}

	@ThreadSafe
	public void newTrackerAdded(Tracker t) {
		java.awt.EventQueue.invokeLater(() -> {
			trackers.add(new TrackerPanel(t));
			build();
		});
	}

	private class TrackerPanel extends EJBagNoStretch {

		final Tracker t;
		JLabel position;
		JLabel rotation;
		JLabel status;
		JLabel url;
		JLabel tps;
		JLabel bat;
		JLabel ping;
		JLabel raw;
		JLabel rawMag;
		JLabel calibration;
		JLabel magAccuracy;
		JLabel adj;
		JLabel adjYaw;
		JLabel adjGyro;
		JLabel correction;
		JLabel signalStrength;
		JLabel rotQuat;
		JLabel rotAdj;
		JLabel temperature;
		JLabel accel;

		@AWTThread
		public TrackerPanel(Tracker t) {
			super(false, true);

			this.t = t;
		}

		@SuppressWarnings("unchecked")
		@AWTThread
		public TrackerPanel build() {
			int row = 0;

			Tracker tracker = t.get();
			removeAll();
			JLabel nameLabel;
			add(
				nameLabel = new JLabel(
					t.getCustomName() != null ? t.getCustomName() : t.getDisplayName()
				),
				s(c(0, row, 2, GridBagConstraints.FIRST_LINE_START), 4, 1)
			);
			nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
			row++;

			if (t.userEditable()) {
				TrackerConfig cfg = server.getConfigManager().getVrConfig().getTracker(t);
				JComboBox<String> desSelect;
				add(
					desSelect = new JComboBox<>(),
					s(c(0, row, 2, GridBagConstraints.FIRST_LINE_START), 2, 1)
				);
				desSelect.addItem("NONE");
				for (TrackerPosition p : TrackerPosition.values) {
					desSelect.addItem(p.name());
				}
				if (cfg.getDesignation() != null) {
					TrackerPosition
						.getByDesignation(cfg.getDesignation())
						.ifPresentOrElse(
							trackerPosition -> desSelect.setSelectedItem(trackerPosition.name()),
							() -> desSelect.setSelectedItem("NONE")
						);
				}
				desSelect.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (desSelect.getSelectedItem() == "NONE") {
							t.setBodyPosition(null);
						} else {
							TrackerPosition p = TrackerPosition
								.valueOf(String.valueOf(desSelect.getSelectedItem()));
							t.setBodyPosition(p);
						}
						server.trackerUpdated(t);
					}
				});
				if (tracker instanceof IMUTracker) {
					IMUTracker imu = (IMUTracker) tracker;
					JComboBox<String> mountSelect;
					add(
						mountSelect = new JComboBox<>(),
						s(c(2, row, 2, GridBagConstraints.FIRST_LINE_START), 2, 1)
					);
					for (TrackerMountingRotation p : TrackerMountingRotation.values) {
						mountSelect.addItem(p.name());
					}

					TrackerMountingRotation selected = TrackerMountingRotation
						.fromQuaternion(imu.getMountingRotation());
					mountSelect
						.setSelectedItem(
							Objects
								.requireNonNullElse(selected, TrackerMountingRotation.BACK)
								.name()
						);
					mountSelect.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							TrackerMountingRotation tr = TrackerMountingRotation
								.valueOf(String.valueOf(mountSelect.getSelectedItem()));
							imu.setMountingRotation(tr.quaternion);
							server.trackerUpdated(t);
						}
					});
				}
				row++;
			}

			if (t.getDevice() != null && t.getDevice().getIpAddress() != null) {
				add(new JLabel("URL:"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					url = new JLabel(""),
					c(1, row, 2, GridBagConstraints.FIRST_LINE_START)
				);
				row++;
			}

			if (t.hasRotation())
				add(new JLabel("Rotation"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
			if (t.hasPosition())
				add(new JLabel("Position"), c(1, row, 2, GridBagConstraints.FIRST_LINE_START));
			add(new JLabel("TPS"), c(3, row, 2, GridBagConstraints.FIRST_LINE_START));
			if (tracker instanceof TrackerWithWireless) {
				add(new JLabel("Ping"), c(2, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(new JLabel("Signal"), c(4, row, 2, GridBagConstraints.FIRST_LINE_START));
			}
			row++;
			if (t.hasRotation())
				add(
					rotation = new JLabel("0 0 0"),
					c(0, row, 2, GridBagConstraints.FIRST_LINE_START)
				);
			if (t.hasPosition())
				add(
					position = new JLabel("0 0 0"),
					c(1, row, 2, GridBagConstraints.FIRST_LINE_START)
				);
			if (tracker instanceof TrackerWithWireless) {
				add(ping = new JLabel(""), c(2, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					signalStrength = new JLabel(""),
					c(4, row, 2, GridBagConstraints.FIRST_LINE_START)
				);
			}
			if (tracker instanceof TrackerWithTPS) {
				add(tps = new JLabel("0"), c(3, row, 2, GridBagConstraints.FIRST_LINE_START));
			} else {
				add(new JLabel(""), c(3, row, 2, GridBagConstraints.FIRST_LINE_START));
			}
			row++;
			add(new JLabel("Status:"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
			add(
				status = new JLabel(t.getStatus().toString().toLowerCase()),
				c(1, row, 2, GridBagConstraints.FIRST_LINE_START)
			);
			if (tracker instanceof TrackerWithBattery) {
				add(new JLabel("Battery:"), c(2, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(bat = new JLabel("0"), c(3, row, 2, GridBagConstraints.FIRST_LINE_START));
			}
			row++;
			add(new JLabel("Raw:"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
			add(
				raw = new JLabel("0 0 0"),
				s(c(1, row, 2, GridBagConstraints.FIRST_LINE_START), 3, 1)
			);

			if (debug && tracker instanceof IMUTracker) {
				add(new JLabel("Quat:"), c(2, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(rotQuat = new JLabel("0"), c(3, row, 2, GridBagConstraints.FIRST_LINE_START));
			}
			row++;

			if (debug && tracker instanceof IMUTracker) {
				add(new JLabel("Raw mag:"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					rawMag = new JLabel("0 0 0"),
					s(c(1, row, 2, GridBagConstraints.FIRST_LINE_START), 3, 1)
				);
				add(new JLabel("Gyro fix:"), c(2, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					new JLabel(String.format("0x%8x", tracker.hashCode())),
					s(c(3, row, 2, GridBagConstraints.FIRST_LINE_START), 3, 1)
				);
				row++;
				add(new JLabel("Cal:"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					calibration = new JLabel("0"),
					c(1, row, 2, GridBagConstraints.FIRST_LINE_START)
				);
				add(new JLabel("Mag acc:"), c(2, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					magAccuracy = new JLabel("0°"),
					c(3, row, 2, GridBagConstraints.FIRST_LINE_START)
				);
				row++;
				add(new JLabel("Correction:"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					correction = new JLabel("0 0 0"),
					s(c(1, row, 2, GridBagConstraints.FIRST_LINE_START), 3, 1)
				);
				add(new JLabel("RotAdj:"), c(2, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(rotAdj = new JLabel("0"), c(3, row, 2, GridBagConstraints.FIRST_LINE_START));
				row++;

				add(new JLabel("Accel:"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(accel = new JLabel("0 0 0"), c(1, row, 2, GridBagConstraints.FIRST_LINE_START));
				row++;
			}

			if (debug && t instanceof ReferenceAdjustedTracker) {
				add(new JLabel("Att fix:"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(adj = new JLabel("0 0 0 0"), c(1, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(new JLabel("Yaw Fix:"), c(2, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					adjYaw = new JLabel("0 0 0 0"),
					c(3, row, 2, GridBagConstraints.FIRST_LINE_START)
				);
				row++;
				add(new JLabel("Gyro Fix:"), c(0, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					adjGyro = new JLabel("0 0 0 0"),
					c(1, row, 2, GridBagConstraints.FIRST_LINE_START)
				);
				add(new JLabel("Temp:"), c(2, row, 2, GridBagConstraints.FIRST_LINE_START));
				add(
					temperature = new JLabel("?"),
					c(3, row, 2, GridBagConstraints.FIRST_LINE_START)
				);
			}

			setBorder(BorderFactory.createLineBorder(new Color(0x663399), 2, false));
			TrackersList.this.add(this);
			return this;
		}

		@SuppressWarnings("unchecked")
		@AWTThread
		public void update() {
			if (position == null && rotation == null)
				return;
			Tracker tracker = t.get();
			t.getRotation(q);
			t.getPosition(v);
			q.toAngles(angles);

			if (position != null)
				position
					.setText(
						StringUtils.prettyNumber(v.x, 1)
							+ " "
							+ StringUtils.prettyNumber(v.y, 1)
							+ " "
							+ StringUtils.prettyNumber(v.z, 1)
					);
			if (rotation != null)
				rotation
					.setText(
						StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
							+ " "
							+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
							+ " "
							+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)
					);
			status.setText(t.getStatus().toString().toLowerCase());
			if (t.getDevice() != null && t.getDevice().getIpAddress() != null)
				url.setText("udp:/" + t.getDevice().getIpAddress());

			if (tracker instanceof TrackerWithTPS) {
				tps.setText(StringUtils.prettyNumber(((TrackerWithTPS) tracker).getTPS(), 1));
			}
			if (tracker instanceof TrackerWithBattery) {
				TrackerWithBattery twb = (TrackerWithBattery) tracker;
				float level = twb.getBatteryLevel();
				float voltage = twb.getBatteryVoltage();
				if (level == 0.0f) {
					bat.setText(String.format("%sV", StringUtils.prettyNumber(voltage, 2)));
				} else if (voltage == 0.0f) {
					bat.setText(String.format("%d%%", Math.round(level)));
				} else {
					bat
						.setText(
							String
								.format(
									"%d%% (%sV)",
									Math.round(level),
									StringUtils.prettyNumber(voltage, 2)
								)
						);
				}
			}
			if (t instanceof ReferenceAdjustedTracker) {
				ReferenceAdjustedTracker<Tracker> rat = (ReferenceAdjustedTracker<Tracker>) t;
				if (adj != null) {
					rat.attachmentFix.toAngles(angles);
					adj
						.setText(
							StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)
						);
				}
				if (adjYaw != null) {
					rat.yawFix.toAngles(angles);
					adjYaw
						.setText(
							StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)
						);
				}
				if (adjGyro != null) {
					rat.gyroFix.toAngles(angles);
					adjGyro
						.setText(
							StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)
						);
				}
			}

			if (tracker instanceof TrackerWithWireless) {
				ping.setText(String.valueOf(((TrackerWithWireless) tracker).getPing()));
				int signal = ((TrackerWithWireless) tracker).getSignalStrength();

				if (signal == -1) {
					signalStrength.setText("N/A");
				} else {
					// -40 dBm is excellent, -95 dBm is very poor
					int percentage = (signal - -95) * (100 - 0) / (-40 - -95) + 0;
					percentage = Math.max(Math.min(percentage, 100), 0);
					signalStrength.setText(percentage + "% " + "(" + signal + " dBm" + ")");
				}
			}

			tracker.getRotation(q);
			q.toAngles(angles);
			raw
				.setText(
					StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
						+ " "
						+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
						+ " "
						+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)
				);
			if (tracker instanceof IMUTracker) {
				IMUTracker imu = (IMUTracker) tracker;
				if (rawMag != null) {
					imu.rotMagQuaternion.toAngles(angles);
					rawMag
						.setText(
							StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)
						);
				}
				if (calibration != null)
					calibration.setText(imu.calibrationStatus + " / " + imu.magCalibrationStatus);
				if (magAccuracy != null)
					magAccuracy
						.setText(
							StringUtils
								.prettyNumber(imu.magnetometerAccuracy * FastMath.RAD_TO_DEG, 1)
								+ "°"
						);
				if (correction != null) {
					imu.getCorrection(q);
					q.toAngles(angles);
					correction
						.setText(
							StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)
						);
				}
				if (rotQuat != null) {
					imu.rotQuaternion.toAngles(angles);
					rotQuat
						.setText(
							StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)
						);
				}
				if (rotAdj != null) {
					imu.rotAdjust.toAngles(angles);
					rotAdj
						.setText(
							StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
								+ " "
								+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)
						);
				}
				if (temperature != null) {
					if (imu.temperature == 0.0f) {
						// Can't be exact 0, so no info received
						temperature.setText("?");
					} else {
						temperature.setText(StringUtils.prettyNumber(imu.temperature, 1) + "∘C");
					}
				}
				if (accel != null) {
					accel
						.setText(
							StringUtils.prettyNumber(imu.accelVector.x, 1)
								+ " "
								+ StringUtils.prettyNumber(imu.accelVector.y, 1)
								+ " "
								+ StringUtils.prettyNumber(imu.accelVector.z, 1)
						);
				}
			}
		}
	}
}
