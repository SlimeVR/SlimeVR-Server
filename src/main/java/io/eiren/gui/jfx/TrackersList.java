package io.eiren.gui.jfx;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class TrackersList extends VBox {
	
	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];
	
	private List<TrackerRow> trackers = new FastList<>();
	
	private final VRServer server;
	private final SlimeVRGUIJFX gui;

	public TrackersList(VRServer server, SlimeVRGUIJFX gui) {
		super(BoxLayout.PAGE_AXIS);
		this.server = server;
		this.gui = gui;

		server.addNewTrackerConsumer(this::newTrackerAdded);
		build();
	}

	@AWTThread
	private void build() {
		getChildren().clear();
		
		trackers.sort((tr1, tr2) -> getTrackerSort(tr1.t) - getTrackerSort(tr2.t));
		
		Class<? extends Tracker> currentClass = null;
		
		for(int i = 0; i < trackers.size(); ++i) {
			//getChildren().add(Box.createVerticalStrut(3));
			TrackerRow tr = trackers.get(i);
			Tracker t = tr.t;
			if(currentClass != t.getClass()) {
				currentClass = t.getClass();
				getChildren().add(new Label(currentClass.getSimpleName()));
			}

			tr.build();
		}
		gui.refresh();
	}
	
	AtomicBoolean updateQueued = new AtomicBoolean();
	
	@ThreadSafe
	public void updateTrackers() {
		if(updateQueued.getAndSet(true))
			return;
		javafx.application.Platform.runLater(() -> {
			updateQueued.set(false);
			for(int i = 0; i < trackers.size(); ++i)
				trackers.get(i).update();
		});
	}
	
	@ThreadSafe
	public void newTrackerAdded(Tracker t) {
		javafx.application.Platform.runLater(() -> {
			trackers.add(new TrackerRow(t));
			build();
		});
	}
	
	private class TrackerRow extends GridPane {
		
		final Tracker t;
		Label position;
		Label rotation;
		Label status;
		Label tps;
		Label bat;
		Label ping;
		Label raw;
		
		@AWTThread
		public TrackerRow(Tracker t) {
			super();
			this.t = t;
		}

		@SuppressWarnings("unchecked")
		@AWTThread
		public TrackerRow build() {
			add(new Label(t.getName()), 0, 0, 4, 1);
			if(t.userEditable()) {
				TrackerConfig cfg = server.getTrackerConfig(t);
				ComboBox<String> desSelect;
				add(desSelect = new ComboBox<>(), 0, 1, 2, 1);
				for(TrackerBodyPosition p : TrackerBodyPosition.values) {
					desSelect.getItems().add(p.name());
				}
				if(cfg.designation != null) {
					TrackerBodyPosition p = TrackerBodyPosition.getByDesignation(cfg.designation);
					//if(p != null)
					//	desSelect.setSelectedItem(p.name());
				}
				/*desSelect.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						TrackerBodyPosition p = TrackerBodyPosition.valueOf(String.valueOf(desSelect.getSelectedItem()));
						t.setBodyPosition(p);
						server.trackerUpdated(t);
					}
				});*/
				Tracker realTracker = t;
				if(t instanceof ReferenceAdjustedTracker<?>)
					realTracker = ((ReferenceAdjustedTracker<? extends Tracker>) t).getTracker();
				if(realTracker instanceof IMUTracker) {
					IMUTracker imu = (IMUTracker) realTracker;
					TrackerMountingRotation tr = imu.getMountingRotation();
					ComboBox<String> mountSelect;
					add(mountSelect = new ComboBox<>(), 2, 1, 2, 1);
					for(TrackerMountingRotation p : TrackerMountingRotation.values) {
						mountSelect.getItems().add(p.name());
					}
					//if(tr != null) {
					//	mountSelect.setSelectedItem(tr.name());
					//} else {
					//	mountSelect.setSelectedItem(TrackerMountingRotation.BACK.name());
					//}
					/*mountSelect.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							TrackerMountingRotation tr = TrackerMountingRotation.valueOf(String.valueOf(mountSelect.getSelectedItem()));
							imu.setMountingRotation(tr);
							server.trackerUpdated(t);
						}
					});*/
				}
			}
			add(new Label("Rotation"), 0, 2);
			add(new Label("Postion"), 1, 2);
			add(new Label("Ping"), 2, 2);
			add(new Label("TPS"), 3, 2);
			add(rotation = new Label("0 0 0"), 0, 3);
			add(position = new Label("0 0 0"), 1, 3);
			add(ping = new Label(""), 2, 3);
			if(t instanceof TrackerWithTPS) {
				add(tps = new Label("0"), 3, 3);
			} else {
				add(new Label(""), 3, 3);
			}
			add(new Label("Status:"), 0, 4);
			add(status = new Label(t.getStatus().toString().toLowerCase()), 1, 4);
			add(new Label("Battery:"), 2, 4);
			add(bat = new Label("0"), 3, 4);
			add(new Label("Raw:"), 0, 5);
			add(raw = new Label("0 0 0 0"), 1, 5, 3, 1);

			//setBorder(BorderFactory.createLineBorder(new Color(0x663399), 4, true));
			TrackersList.this.getChildren().add(this);
			return this;
		}

		@SuppressWarnings("unchecked")
		@AWTThread
		public void update() {
			if(position == null)
				return;
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
			
			if(t instanceof TrackerWithTPS) {
				tps.setText(StringUtils.prettyNumber(((TrackerWithTPS) t).getTPS(), 1));
			}
			if(t instanceof TrackerWithBattery)
				bat.setText(StringUtils.prettyNumber(((TrackerWithBattery) t).getBatteryVoltage(), 1));
			Tracker t2 = t;
			if(t instanceof ReferenceAdjustedTracker)
				t2 = ((ReferenceAdjustedTracker<Tracker>) t).getTracker();
			if(t2 instanceof IMUTracker)
				ping.setText(String.valueOf(((IMUTracker) t2).ping));
			t2.getRotation(q);
			raw.setText(StringUtils.prettyNumber(q.getX(), 4)
					+ " " + StringUtils.prettyNumber(q.getY(), 4)
					+ " " + StringUtils.prettyNumber(q.getZ(), 4)
					+ " " + StringUtils.prettyNumber(q.getW(), 4));
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
