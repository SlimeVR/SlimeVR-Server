package dev.slimevr.gui.dialogs;

import com.fazecast.jSerialComm.SerialPort;
import dev.slimevr.VRServer;
import io.eiren.util.logging.LogManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class WifiTrackerConfigurationDialog extends AnchorPane implements Initializable {

	private VRServer server;
	private static final Timer timer = new Timer();
	private static String savedSSID = "";
	private static String savedPassword = "";
	SerialPort trackerPort = null;
	TimerTask readTask;

	@FXML
	public AnchorPane parentView;

	@FXML
	private TextArea log;

	@FXML
	private TextField passwdField;

	@FXML
	private Button sendButton;

	@FXML
	private TextField ssidField;

	@FXML
	private Label trackerStatusLabel;

	@FXML
	public Label networkNameLabel;

	@FXML
	public Label networkPasswordLabel;

	public void init(VRServer server, Stage stage)
	{
		this.server = server;
		stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);

		initUi(stage);
	}

	private void initUi(Stage stage) {
		SerialPort[] ports = SerialPort.getCommPorts();
		for(SerialPort port : ports) {
			if(port.getDescriptivePortName().toLowerCase().contains("ch340") || port.getDescriptivePortName().toLowerCase().contains("cp21") || port.getDescriptivePortName().toLowerCase().contains("ch910")) {
				trackerPort = port;
				break;
			}
		}
		if(trackerPort == null) {
			trackerStatusLabel.setText("No trackers connected, connect tracker to USB and reopen window");

			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Platform.runLater(stage::close);
				}
			}, 5000);
		} else {
			initWindowVisibility();
			trackerStatusLabel.setText("Tracker connected to " + trackerPort.getSystemPortName() + " (" + trackerPort.getDescriptivePortName() + ")");

			if(trackerPort.openPort()) {
				trackerPort.setBaudRate(115200);

				Platform.runLater(() -> {
					log.appendText("[OK] Port opened\n");
				});

				readTask = new ReadTask();
				timer.schedule(readTask, 500, 500);
			} else {
				Platform.runLater(() -> {
					LogManager.log.severe("ERROR: Can't open port");
					log.appendText("ERROR: Can't open port");
				});
			}
		}

		//parentView.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
	}

	private void initWindowVisibility() {
		sendButton.setVisible(true);
		log.setVisible(true);
		networkNameLabel.setVisible(true);
		networkPasswordLabel.setVisible(true);
		ssidField.setVisible(true);
		passwdField.setVisible(true);
	}

	private void closeWindowEvent(WindowEvent event) {
		if(trackerPort != null)
			trackerPort.closePort();
		if(readTask != null)
			readTask.cancel();

		LogManager.log.info("Port closed okay");
	}

	@FXML
	public void onSendButtonClicked(ActionEvent actionEvent) {
		send(ssidField.getText(), passwdField.getText());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	protected void send(String ssid, String passwd) {
		savedSSID = ssid;
		savedPassword = passwd;

		OutputStream os = trackerPort.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os);

		try {
			writer.append("SET WIFI \"" + ssid + "\" \"" + passwd + "\"\n");
			writer.flush();
		} catch(IOException e) {
			log.appendText(e.toString() + "\n");
			e.printStackTrace();
		}
	}

	private class ReadTask extends TimerTask {

		final InputStream is;
		final Reader reader;
		StringBuffer sb = new StringBuffer();

		public ReadTask() {
			is = trackerPort.getInputStreamWithSuppressedTimeoutExceptions();
			reader = new InputStreamReader(is);
		}

		@Override
		public void run() {
			try {
				while (reader.ready())
					sb.appendCodePoint(reader.read());
				if (sb.length() > 0) {
					String message = sb.toString();
					Platform.runLater(() -> {
						LogManager.log.info(message);
						log.appendText(message);
					});
				}
				sb.setLength(0);
			} catch (Exception e) {
				Platform.runLater(() -> {
					log.appendText(e.toString() + "\n");
				});
				LogManager.log.severe("Exception while reading from tracker", e);
			}
		}
	}
}
