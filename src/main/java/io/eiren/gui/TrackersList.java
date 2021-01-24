package io.eiren.gui;

import java.awt.GridBagConstraints;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BoxLayout;
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
import io.eiren.vr.trackers.CalibratingTracker;
import io.eiren.vr.trackers.IMUTracker;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerConfig;

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
		
		server.addNewTrackerConsumer(this::newTrackerAdded);
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
			final int n = trackers.size();
			TrackerConfig cfg = server.getTrackerConfig(t);
			
			trackers.add(new TrackerRow(t, n + 1));
			if(cfg.designation != null)
				add(new JLabel(cfg.designation), c(1, n + 1, 2));
			if(t instanceof CalibratingTracker) {
				add(new JButton("Calibrate") {{
					addMouseListener(new MouseInputAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							new CalibrationWindow(t);
						}
					});
				}}, c(10, n + 1, 2));
			}
			
			gui.refresh();
		});
	}
	
	private class TrackerRow {
		
		Tracker t;
		JLabel x;
		JLabel y;
		JLabel z;
		JLabel a1;
		JLabel a2;
		JLabel a3;
		JLabel status;
		JLabel tps;
		
		@AWTThread
		public TrackerRow(Tracker t, int n) {
			this.t = t;
			add(new JLabel(t.getName()), c(0, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(x = new JLabel("0"), c(2, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(y = new JLabel("0"), c(3, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(z = new JLabel("0"), c(4, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(a1 = new JLabel("0"), c(5, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(a2 = new JLabel("0"), c(6, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(a3 = new JLabel("0"), c(7, n, 2, GridBagConstraints.FIRST_LINE_START));
			add(status = new JLabel(t.getStatus().toString()), c(8, n, 2, GridBagConstraints.FIRST_LINE_START));
			if(t instanceof IMUTracker) {
				add(tps = new JLabel("0"), c(9, n, 2, GridBagConstraints.FIRST_LINE_START));
			}
		}

		@AWTThread
		public void update() {
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
			
			if(t instanceof IMUTracker) {
				tps.setText(StringUtils.prettyNumber(((IMUTracker) t).getTPS(), 1));
			}
		}
	}
}
