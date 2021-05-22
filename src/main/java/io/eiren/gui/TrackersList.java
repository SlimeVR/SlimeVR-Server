package io.eiren.gui;

import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.util.StringUtils;
import io.eiren.util.ann.AWTThread;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.AdjustedTracker;
import io.eiren.vr.trackers.CalibratingTracker;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.IMUTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerConfig;
import io.eiren.vr.trackers.TrackerWithBattery;
import io.eiren.vr.trackers.TrackerWithTPS;

public class TrackersList extends EJBag {
	
	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];
	
	private List<TrackerRow> trackers = new FastList<>();
	
	private final VRServer server;
	private final VRServerGUI gui;

	public TrackersList(VRServer server, VRServerGUI gui) {
		super();
		this.server = server;
		this.gui = gui;

		setAlignmentY(TOP_ALIGNMENT);
		
		
		server.addNewTrackerConsumer(this::newTrackerAdded);
	}

	@AWTThread
	private void build() {
		removeAll();
		
		add(new JLabel("Tracker"), c(0, 0, 2));
		add(new JLabel("Designation"), c(1, 0, 2));
		add(new JLabel("X"), c(2, 0, 2));
		add(new JLabel("Y"), c(3, 0, 2));
		add(new JLabel("Z"), c(4, 0, 2));
		add(new JLabel("Pitch"), c(5, 0, 2));
		add(new JLabel("Yaw"), c(6, 0, 2));
		add(new JLabel("Roll"), c(7, 0, 2));
		add(new JLabel("Status"), c(8, 0, 2));
		add(new JLabel("TPS"), c(9, 0, 2));
		add(new JLabel("Bat"), c(10, 0, 2));
		add(new JLabel("Ping"), c(11, 0, 2));
		
		
		trackers.sort((tr1, tr2) -> getTrackerSort(tr1.t) - getTrackerSort(tr2.t));
		
		Class<? extends Tracker> currentClass = null;
		
		int n = 1;
		
		for(int i = 0; i < trackers.size(); ++i) {
			TrackerRow tr = trackers.get(i);
			Tracker t = tr.t;
			if(currentClass != t.getClass()) {
				currentClass = t.getClass();
				add(new JLabel(currentClass.getSimpleName()), c(0, n, 5, GridBagConstraints.CENTER));
				n++;
			}

			tr.build(n);
			TrackerConfig cfg = server.getTrackerConfig(t);
			
			if(cfg.designation != null)
				add(new JLabel(cfg.designation), c(1, n, 2));
			/*if(t instanceof CalibratingTracker) {
				add(new JButton("Calibrate") {{
					addMouseListener(new MouseInputAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							new CalibrationWindow(t);
						}
					});
				}}, c(12, n, 2));
			}*/
			n += tr.getSize();
		}
		gui.refresh();
	}
	
	@VRServerThread
	public void updateTrackers() {
		java.awt.EventQueue.invokeLater(() -> {
			for(int i = 0; i < trackers.size(); ++i)
				trackers.get(i).update();
		});
	}
	
	@ThreadSafe
	public void newTrackerAdded(Tracker t) {
		java.awt.EventQueue.invokeLater(() -> {
			if(t instanceof IMUTracker)
				trackers.add(new IMUTrackerRow((IMUTracker) t));
			else
				trackers.add(new TrackerRow(t));
			build();
		});
	}
	
	private class TrackerRow {
		
		final Tracker t;
		JLabel x;
		JLabel y;
		JLabel z;
		JLabel a1;
		JLabel a2;
		JLabel a3;
		JLabel status;
		JLabel tps;
		JLabel bat;
		JLabel ping;
		
		@AWTThread
		public TrackerRow(Tracker t) {
			this.t = t;
		}

		@AWTThread
		public TrackerRow build(int n) {
			add(new JLabel(t.getName()), c(0, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(x = new JLabel("0"), c(2, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(y = new JLabel("0"), c(3, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(z = new JLabel("0"), c(4, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(a1 = new JLabel("0"), c(5, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(a2 = new JLabel("0"), c(6, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(a3 = new JLabel("0"), c(7, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(status = new JLabel(t.getStatus().toString()), c(8, n, 2, GridBagConstraints.FIRST_LINE_START));
			if(t instanceof TrackerWithTPS) {
				add(tps = new JLabel("0"), c(9, n, 2, GridBagConstraints.FIRST_LINE_START));
			} else {
				add(new JLabel(""), c(9, n, 2, GridBagConstraints.FIRST_LINE_START));
			}
			add(bat = new JLabel("0"), c(10, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(ping = new JLabel(""), c(11, n, 2, GridBagConstraints.FIRST_LINE_START));
			return this;
		}

		@AWTThread
		public void update() {
			if(x == null)
				return;
			t.getRotation(q);
			t.getPosition(v);
			q.toAngles(angles);
			
			x.setText(StringUtils.prettyNumber(v.x, 2));
			y.setText(StringUtils.prettyNumber(v.y, 2));
			z.setText(StringUtils.prettyNumber(v.z, 2));
			a1.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0));
			a2.setText(StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0));
			a3.setText(StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
			status.setText(t.getStatus().toString());
			
			if(t instanceof TrackerWithTPS) {
				tps.setText(StringUtils.prettyNumber(((TrackerWithTPS) t).getTPS(), 1));
			}
			if(t instanceof TrackerWithBattery)
				bat.setText(StringUtils.prettyNumber(((TrackerWithBattery) t).getBatteryVoltage(), 1));
			if(t instanceof IMUTracker)
				ping.setText(String.valueOf(((IMUTracker) t).ping));
		}
		
		public int getSize() {
			return 1;
		}
	}
	
	private class IMUTrackerRow extends TrackerRow {
		
		IMUTracker it;
		JLabel mx;
		JLabel my;
		JLabel mz;
		JLabel ax;
		JLabel ay;
		JLabel az;
		
		public IMUTrackerRow(IMUTracker t) {
			super(t);
			it = t;
		}

		@Override
		public TrackerRow build(int n) {
			super.build(n);
			/*n++;
			add(new JLabel("Mag / Accel:"), c(0, n, 2, GridBagConstraints.FIRST_LINE_END));
			add(mx = new JLabel("0"), c(2, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(my = new JLabel("0"), c(3, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(mz = new JLabel("0"), c(4, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(ax = new JLabel("0"), c(5, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(ay = new JLabel("0"), c(6, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(az = new JLabel("0"), c(7, n, 2, GridBagConstraints.FIRST_LINE_START));*/
			return this;
		}
		
		@Override
		public void update() {
			super.update();
			/*q.lookAt(it.magVector, Vector3f.UNIT_Y);
			q.toAngles(angles);
			mx.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0));
			my.setText(StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0));
			mz.setText(StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
			q.lookAt(it.accelVector, Vector3f.UNIT_Y);
			q.toAngles(angles);
			ax.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0));
			ay.setText(StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0));
			az.setText(StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));*/
		}
		
		@Override
		public int getSize() {
			return super.getSize() + 1;
		}
	}
	
	private static int getTrackerSort(Tracker t) {
		if(t instanceof HMDTracker)
			return 0;
		if(t instanceof ComputedTracker)
			return 1;
		if(t instanceof IMUTracker)
			return 2;
		if(t instanceof AdjustedTracker)
			return 5;
		return 1000;
	}
}
