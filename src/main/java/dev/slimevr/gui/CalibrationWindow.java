package dev.slimevr.gui;

import dev.slimevr.gui.swing.EJBox;
import dev.slimevr.vr.trackers.CalibratingTracker;
import dev.slimevr.vr.trackers.Tracker;
import io.eiren.util.ann.AWTThread;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;


public class CalibrationWindow extends JFrame {

	public final Tracker tracker;
	private JTextArea currentCalibration;
	private JTextArea newCalibration;
	private JButton calibrateButton;

	public CalibrationWindow(Tracker t) {
		super(t.getName() + " calibration");
		this.tracker = t;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS));

		build();
	}

	public void currentCalibrationReceived(String str) {
		java.awt.EventQueue.invokeLater(() -> {
			currentCalibration.setText(str);
			pack();
		});
	}

	public void newCalibrationReceived(String str) {
		java.awt.EventQueue.invokeLater(() -> {
			calibrateButton.setText("Calibrate");
			newCalibration.setText(str);
			pack();
		});
	}

	@AWTThread
	private void build() {
		Container pane = getContentPane();

		pane.add(calibrateButton = new JButton("Calibrate") {
			{
				addMouseListener(new MouseInputAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						calibrateButton.setText("Calibrating...");
						((CalibratingTracker) tracker)
							.startCalibration(CalibrationWindow.this::newCalibrationReceived);
					}
				});
			}
		});

		pane.add(new EJBox(BoxLayout.PAGE_AXIS) {
			{
				setBorder(new EmptyBorder(i(5)));
				add(new JLabel("Current calibration"));
				add(currentCalibration = new JTextArea(10, 25));

				((CalibratingTracker) tracker)
					.requestCalibrationData(CalibrationWindow.this::currentCalibrationReceived);
			}
		});
		pane.add(new EJBox(BoxLayout.PAGE_AXIS) {
			{
				setBorder(new EmptyBorder(i(5)));
				add(new JLabel("New calibration"));
				add(newCalibration = new JTextArea(10, 25));
			}
		});

		// Pack and display
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				toFront();
				repaint();
			}
		});
	}
}
