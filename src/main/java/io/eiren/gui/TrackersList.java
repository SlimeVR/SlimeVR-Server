package io.eiren.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.StringUtils;
import io.eiren.util.ann.AWTThread;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.TrackerBodyPosition;
import io.eiren.vr.trackers.ReferenceAdjustedTracker;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.IMUTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerConfig;
import io.eiren.vr.trackers.TrackerMountingRotation;
import io.eiren.vr.trackers.TrackerWithBattery;
import io.eiren.vr.trackers.TrackerWithTPS;

public class TrackersList extends EJBox {
	
	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];
	
	private List<TrackerRow> trackers = new FastList<>();
	
	private final VRServer server;
	private final VRServerGUI gui;

	public TrackersList(VRServer server, VRServerGUI gui) {
		super(BoxLayout.PAGE_AXIS);
		this.server = server;
		this.gui = gui;

		setAlignmentY(TOP_ALIGNMENT);
		
		server.addNewTrackerConsumer(this::newTrackerAdded);
	}

	@AWTThread
	private void build() {
		removeAll();
		
		trackers.sort((tr1, tr2) -> getTrackerSort(tr1.t) - getTrackerSort(tr2.t));
		
		Class<? extends Tracker> currentClass = null;
		
		for(int i = 0; i < trackers.size(); ++i) {
			add(Box.createVerticalStrut(3));
			TrackerRow tr = trackers.get(i);
			Tracker t = tr.t;
			if(currentClass != t.getClass()) {
				currentClass = t.getClass();
				add(new JLabel(currentClass.getSimpleName()));
			}

			tr.build();
		}
		validate();
		gui.refresh();
	}
	
	@ThreadSafe
	public void updateTrackers() {
		java.awt.EventQueue.invokeLater(() -> {
			for(int i = 0; i < trackers.size(); ++i)
				trackers.get(i).update();
		});
	}
	
	@ThreadSafe
	public void newTrackerAdded(Tracker t) {
		java.awt.EventQueue.invokeLater(() -> {
			trackers.add(new TrackerRow(t));
			build();
		});
	}
	
	private class TrackerRow extends EJBag {
		
		final Tracker t;
		JLabel position;
		JLabel rotation;
		JLabel status;
		JLabel tps;
		JLabel bat;
		JLabel ping;
		JLabel raw;
		JLabel rawMag;
		JLabel calibration;
		JLabel magAccuracy;
		JLabel adj;
		JLabel adjYaw;
		
		@AWTThread
		public TrackerRow(Tracker t) {
			super();
			this.t = t;
		}

		@SuppressWarnings("unchecked")
		@AWTThread
		public TrackerRow build() {
			int row = 0;
			
			Tracker realTracker = t;
			if(t instanceof ReferenceAdjustedTracker)
				realTracker = ((ReferenceAdjustedTracker<? extends Tracker>) t).getTracker();
			removeAll();
			add(new JLabel(t.getName()), s(c(0, row, 0, GridBagConstraints.FIRST_LINE_START), 4, 1));
			row++;
			
			if(t.userEditable()) {
				TrackerConfig cfg = server.getTrackerConfig(t);
				JComboBox<String> desSelect;
				add(desSelect = new JComboBox<>(), s(c(0, row, 0, GridBagConstraints.FIRST_LINE_START), 2, 1));
				for(TrackerBodyPosition p : TrackerBodyPosition.values) {
					desSelect.addItem(p.name());
				}
				if(cfg.designation != null) {
					TrackerBodyPosition p = TrackerBodyPosition.getByDesignation(cfg.designation);
					if(p != null)
						desSelect.setSelectedItem(p.name());
				}
				desSelect.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						TrackerBodyPosition p = TrackerBodyPosition.valueOf(String.valueOf(desSelect.getSelectedItem()));
						t.setBodyPosition(p);
						server.trackerUpdated(t);
					}
				});
				if(realTracker instanceof IMUTracker) {
					IMUTracker imu = (IMUTracker) realTracker;
					TrackerMountingRotation tr = imu.getMountingRotation();
					JComboBox<String> mountSelect;
					add(mountSelect = new JComboBox<>(), s(c(2, row, 0, GridBagConstraints.FIRST_LINE_START), 2, 1));
					for(TrackerMountingRotation p : TrackerMountingRotation.values) {
						mountSelect.addItem(p.name());
					}
					if(tr != null) {
						mountSelect.setSelectedItem(tr.name());
					} else {
						mountSelect.setSelectedItem(TrackerMountingRotation.BACK.name());
					}
					mountSelect.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							TrackerMountingRotation tr = TrackerMountingRotation.valueOf(String.valueOf(mountSelect.getSelectedItem()));
							imu.setMountingRotation(tr);
							server.trackerUpdated(t);
						}
					});
				}
				row++;
			}
			add(new JLabel("Rotation"), c(0, row, 0, GridBagConstraints.FIRST_LINE_START));
			add(new JLabel("Postion"), c(1, row, 0, GridBagConstraints.FIRST_LINE_START));
			add(new JLabel("Ping"), c(2, row, 0, GridBagConstraints.FIRST_LINE_START));
			add(new JLabel("TPS"), c(3, row, 0, GridBagConstraints.FIRST_LINE_START));
			row++;
			add(rotation = new JLabel("0 0 0"), c(0, row, 0, GridBagConstraints.FIRST_LINE_START));
			add(position = new JLabel("0 0 0"), c(1, row, 0, GridBagConstraints.FIRST_LINE_START));
			add(ping = new JLabel(""), c(2, row, 0, GridBagConstraints.FIRST_LINE_START));
			if(realTracker instanceof TrackerWithTPS) {
				add(tps = new JLabel("0"), c(3, row, 0, GridBagConstraints.FIRST_LINE_START));
			} else {
				add(new JLabel(""), c(3, row, 0, GridBagConstraints.FIRST_LINE_START));
			}
			row++;
			add(new JLabel("Status:"), c(0, row, 0, GridBagConstraints.FIRST_LINE_START));
			add(status = new JLabel(t.getStatus().toString().toLowerCase()), c(1, row, 0, GridBagConstraints.FIRST_LINE_START));
			add(new JLabel("Battery:"), c(2, row, 0, GridBagConstraints.FIRST_LINE_START));
			add(bat = new JLabel("0"), c(3, row, 0, GridBagConstraints.FIRST_LINE_START));
			row++;
			add(new JLabel("Raw:"), c(0, row, 0, GridBagConstraints.FIRST_LINE_START));
			add(raw = new JLabel("0 0 0"), s(c(1, row, 0, GridBagConstraints.FIRST_LINE_START), 3, 1));
			row++;
			if(realTracker instanceof IMUTracker) {
				add(new JLabel("Raw mag:"), c(0, row, 0, GridBagConstraints.FIRST_LINE_START));
				add(rawMag = new JLabel("0 0 0"), s(c(1, row, 0, GridBagConstraints.FIRST_LINE_START), 3, 1));
				row++;
				add(new JLabel("Cal:"), c(0, row, 0, GridBagConstraints.FIRST_LINE_START));
				add(calibration = new JLabel("0"), c(1, row, 0, GridBagConstraints.FIRST_LINE_START));
				add(new JLabel("Mag acc:"), c(2, row, 0, GridBagConstraints.FIRST_LINE_START));
				add(magAccuracy = new JLabel("0°"), c(3, row, 0, GridBagConstraints.FIRST_LINE_START));
				row++;
			}
			
			if(t instanceof ReferenceAdjustedTracker) {	
				add(new JLabel("Adj:"), c(0, row, 0, GridBagConstraints.FIRST_LINE_START));
				add(adj = new JLabel("0 0 0 0"), c(1, row, 0, GridBagConstraints.FIRST_LINE_START));
				add(new JLabel("AdjY:"), c(2, row, 0, GridBagConstraints.FIRST_LINE_START));
				add(adjYaw = new JLabel("0 0 0 0"), c(3, row, 0, GridBagConstraints.FIRST_LINE_START));
			}

			setBorder(BorderFactory.createLineBorder(new Color(0x663399), 4, true));
			TrackersList.this.add(this);
			return this;
		}

		@SuppressWarnings("unchecked")
		@AWTThread
		public void update() {
			if(position == null)
				return;
			Tracker realTracker = t;
			if(t instanceof ReferenceAdjustedTracker)
				realTracker = ((ReferenceAdjustedTracker<? extends Tracker>) t).getTracker();
			t.getRotation(q);
			t.getPosition(v);
			q.toAngles(angles);
			
			position.setText(StringUtils.prettyNumber(v.x, 1)
					+ " " + StringUtils.prettyNumber(v.y, 1)
					+ " " + StringUtils.prettyNumber(v.z, 1));
			rotation.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
			status.setText(t.getStatus().toString().toLowerCase());
			
			if(realTracker instanceof TrackerWithTPS) {
				tps.setText(StringUtils.prettyNumber(((TrackerWithTPS) realTracker).getTPS(), 1));
			}
			if(realTracker instanceof TrackerWithBattery)
				bat.setText(StringUtils.prettyNumber(((TrackerWithBattery) realTracker).getBatteryVoltage(), 1));
			if(t instanceof ReferenceAdjustedTracker) {
				((ReferenceAdjustedTracker<Tracker>) t).attachmentFix.toAngles(angles);
				adj.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
						+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
						+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
				((ReferenceAdjustedTracker<Tracker>) t).yawFix.toAngles(angles);
				adjYaw.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
						+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
						+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
			}
			if(realTracker instanceof IMUTracker) {
				ping.setText(String.valueOf(((IMUTracker) realTracker).ping));
			}
			realTracker.getRotation(q);
			q.toAngles(angles);
			raw.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
			if(realTracker instanceof IMUTracker) {
				((IMUTracker) realTracker).rotMagQuaternion.toAngles(angles);
				rawMag.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
						+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
						+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
				calibration.setText(((IMUTracker) realTracker).calibrationStatus + " / " + ((IMUTracker) realTracker).magCalibrationStatus);
				magAccuracy.setText(StringUtils.prettyNumber(((IMUTracker) realTracker).magnetometerAccuracy * FastMath.RAD_TO_DEG, 1) + "°");
			}
		}
	}
	
	private static int getTrackerSort(Tracker t) {
		if(t instanceof HMDTracker)
			return 0;
		if(t instanceof ComputedTracker)
			return 1;
		if(t instanceof IMUTracker)
			return 2;
		if(t instanceof ReferenceAdjustedTracker)
			return 5;
		return 1000;
	}
}
