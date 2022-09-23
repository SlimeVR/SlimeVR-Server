package dev.slimevr.gui;

import com.fazecast.jSerialComm.SerialPort;
import dev.slimevr.gui.swing.EJBox;
import dev.slimevr.serial.SerialListener;
import io.eiren.util.ann.AWTThread;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class WiFiWindow extends JFrame implements SerialListener {

	private static String savedSSID = "";
	private static String savedPassword = "";
	private final VRServerGUI gui;
	private JScrollPane scroll;
	JTextField ssidField;
	JPasswordField passwdField;
	JTextArea log;

	public WiFiWindow(VRServerGUI gui) {
		super("WiFi Settings");
		this.gui = gui;

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.LINE_AXIS));

		this.gui.server.getSerialHandler().addListener(this);

		build();
	}

	@AWTThread
	private void build() {
		if (!this.gui.server.getSerialHandler().openSerial()) {
			JOptionPane
				.showMessageDialog(
					null,
					"Unable to open a serial connection. Check that your drivers are installed and nothing is using the serial port already (like Cura or VScode or another slimeVR server)",
					"SlimeVR: Serial connection error",
					JOptionPane.ERROR_MESSAGE
				);
		}
	}

	@Override
	@AWTThread
	public void onSerialConnected(SerialPort port) {
		Container pane = getContentPane();
		pane.add(new EJBox(BoxLayout.PAGE_AXIS) {
			{
				add(
					new JLabel(
						"Tracker connected to "
							+ port.getSystemPortName()
							+ " ("
							+ port.getDescriptivePortName()
							+ ")"
					)
				);
				add(new EJBox(BoxLayout.LINE_AXIS) {
					{
						add(new JButton("Reboot Tracker") {
							{
								addMouseListener(new MouseInputAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {
										gui.server.getSerialHandler().rebootRequest();
									}
								});
							}
						});
						add(new JButton("INFO") {
							{
								addMouseListener(new MouseInputAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {
										gui.server.getSerialHandler().infoRequest();
									}
								});
							}
						});
						add(new JButton("Factory Reset") {
							{
								addMouseListener(new MouseInputAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {
										gui.server.getSerialHandler().factoryResetRequest();
									}
								});
							}
						});
					}
				});

				add(
					scroll = new JScrollPane(
						log = new JTextArea(10, 20),
						ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
					)
				);
				log.setLineWrap(true);
				scroll.setAutoscrolls(true);
				add(new JLabel("Enter WiFi credentials:"));
				add(new EJBox(BoxLayout.LINE_AXIS) {
					{
						add(new JLabel("Network name:"));
						add(ssidField = new JTextField(savedSSID));
					}
				});
				add(new EJBox(BoxLayout.LINE_AXIS) {
					{
						add(new JLabel("Network password:"));
						passwdField = new JPasswordField(savedPassword);
						passwdField.setEchoChar('\u25cf');
						add(passwdField);
						add(new JCheckBox("Show Password") {
							{
								addMouseListener(new MouseInputAdapter() {
									@Override
									public void mouseClicked(MouseEvent e) {
										if (isSelected())
											passwdField.setEchoChar((char) 0);
										else
											passwdField.setEchoChar('\u25cf');
									}
								});
							}
						});
					}
				});
				add(new JButton("Send") {
					{
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								savedSSID = ssidField.getText();
								savedPassword = new String(passwdField.getPassword());
								gui.server.getSerialHandler().setWifi(savedSSID, savedPassword);
							}
						});
					}
				});
			}
		});

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
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final WiFiWindow window = this;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				gui.server.getSerialHandler().closeSerial();
				dispose();
				gui.server.getSerialHandler().removeListener(window);
			}
		});
	}

	@Override
	@AWTThread
	public void onSerialDisconnected() {
		if (this.log == null)
			return;
		log.append("[SERVER] Serial port disconnected\n");
	}

	@Override
	@AWTThread
	public void onSerialLog(String str) {
		if (this.log == null)
			return;
		log.append(str);
		// log.setAutoscrolls(true);
		if (scroll != null) {
			JScrollBar vertical = scroll.getVerticalScrollBar();
			if ((vertical.getValue() + str.length() + 200) > vertical.getMaximum()) {
				vertical.setValue(vertical.getMaximum());
			}
		}

	}
}
