package dev.slimevr.gui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseEvent;
import io.eiren.util.ann.AWTThread;
import dev.slimevr.VRServer;
import dev.slimevr.vr.MountingCalibration;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.Tracker;
import javax.swing.event.MouseInputAdapter;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

import dev.slimevr.gui.swing.ButtonTimer;
import dev.slimevr.gui.swing.EJBox;

public class MountingCalibrationWindow extends JFrame {
	
	private EJBox pane;
	
	private final MountingCalibration mountingCalibration;
	
	private transient IMUTracker imu;
	private transient Tracker t;
	private JLabel mountingValue;
	private JButton dynamicMountingButton;
	private Quaternion standingOrientation;
	private boolean calibrating;
	
	public MountingCalibrationWindow(VRServer server, IMUTracker imuTracker, Tracker tracker) {
		super("Mounting Calibration " + tracker.getDescriptiveName());
		
		mountingCalibration = server.mountingCalibration;
		imu = imuTracker;
		t = tracker;
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		add(new JScrollPane(pane = new EJBox(BoxLayout.PAGE_AXIS), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		build();
	}
	
	@AWTThread
	private void build() {
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				setBorder(new EmptyBorder(0, 175, 10, 175));
			}
		});
		
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				add(mountingValue = new JLabel("Mounting = " + Math.round(Math.toDegrees(imu.getMountingRotation()))));
			}
		});
		
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				setBorder(new EmptyBorder(i(5)));
			}
		});
		
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				add(new JLabel("Dynamic calibration:"));
			}
		});
		
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				add(dynamicMountingButton = new JButton("Calibrate") {
					{
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								if(!calibrating) { // Prevents running multiple times at the same time.
									calibrating = true;
									dynamicCalibrate();
								}
							}
						});
					}
				});
			}
		});

		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				setBorder(new EmptyBorder(i(5)));
			}
		});
		
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				add(new JLabel("Manual calibration:"));
			}
		});
		
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				add(new JButton("Front") {
					{
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								mountingValue.setText("Mounting = 180");
								mountingCalibration.setIMUMountingRotation((float) (Math.PI), imu, t);
							}
						});
					}
				});
				add(new JButton("Left") {
					{
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								mountingValue.setText("Mounting = 90");
								mountingCalibration.setIMUMountingRotation((float) (FastMath.HALF_PI), imu, t);
							}
						});
					}
				});
				add(new JButton("Right") {
					{
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								mountingValue.setText("Mounting = -90");
								mountingCalibration.setIMUMountingRotation((float) (-FastMath.HALF_PI), imu, t);
							}
						});
					}
				});
				add(new JButton("Back") {
					{
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								mountingValue.setText("Mounting = 0");
								mountingCalibration.setIMUMountingRotation(0f, imu, t);
							}
						});
					}
				});
			}
		});
		
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				setBorder(new EmptyBorder(i(5)));
			}
		});
		
		// Pack and display
		pack();
		setLocationRelativeTo(null);
		setVisible(false);
	}
	
	private void dynamicCalibrate() { // Called when the Calibrate button is pressed for a tracker
		standingOrientation = imu.rotQuaternion.clone();
		ButtonTimer.runTimer(dynamicMountingButton, 3, "Calibrate", this::finishDynamicCalibration);
	}
	
	private void finishDynamicCalibration() {
		mountingCalibration.CalibrateTracker(standingOrientation, imu, t);
		mountingValue.setText("Mounting = " + Math.round(Math.toDegrees(imu.getMountingRotation())));
		calibrating = false;
	}
}
