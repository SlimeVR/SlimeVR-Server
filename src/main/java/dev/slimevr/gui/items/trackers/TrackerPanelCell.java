package dev.slimevr.gui.items.trackers;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import io.eiren.util.StringUtils;
import io.eiren.util.logging.LogManager;
import dev.slimevr.VRServer;
import dev.slimevr.gui.util.GUIUtils;
import dev.slimevr.vr.trackers.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class TrackerPanelCell extends AnchorPane {


	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];

	Image activeImage = GUIUtils.getImage("/active_slime.png");
	Image deactivatedImage = GUIUtils.getImage("/deactived_slime.png");

	@FXML
	private ResourceBundle resources;

	@FXML
	private Label bat;

	@FXML
	private Label nameLabel;

	@FXML
	private Label ping;

	@FXML
	private Label position;

	@FXML
	private Label rotation;

	@FXML
	private Label status;

	@FXML
	private Label tps;

	@FXML
	private ComboBox<String> desSelect;

	@FXML
	private ComboBox<String> mountSelect;
	
	@FXML
	private ImageView connectIMG;


/*	@FXML
	private URL location;

	@FXML
	private Text nameLabel;

	@FXML
	private Text rawMag;

	@FXML
	private Text adj;

	@FXML
	private Text adjYaw;

	@FXML
	private Text bat;

	@FXML
	private Text calibration;

	@FXML
	private Text correction;

	@FXML
	private Text magAccuracy;

	@FXML
	private Text ping;

	@FXML
	private Text position;

	@FXML
	private Text raw;

	@FXML
	private Text rotation;

	@FXML
	private Text status;

	@FXML
	private Text tps;*/

	@FXML
	private AnchorPane trackerContainer;

	private VRServer server;


	public final Tracker t;

	public TrackerPanelCell(Tracker tracker, VRServer server) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cells/trackers/trackerCell.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		this.server = server;
		this.t = tracker;
	}

	@FXML
	void initialize() {
	}

	public TrackerPanelCell build() {

		LogManager.log.debug("TrackerPanelCell build() " + t.getTrackerId());

		Tracker realTracker = t;
		if (t instanceof ReferenceAdjustedTracker)
			realTracker = ((ReferenceAdjustedTracker<? extends Tracker>) t).getTracker();
		nameLabel.setText(t.getDescriptiveName());

		if (t.userEditable()) {
			TrackerConfig cfg = server.getTrackerConfig(t);
			if (realTracker instanceof IMUTracker) {
				IMUTracker imu = (IMUTracker) realTracker;
				TrackerMountingRotation tr = imu.getMountingRotation();
			}
		}

		if (t.hasRotation()) {
			rotation.setVisible(true);
			rotation.setText("0 0 0");
		}

		if (t.hasPosition()) {
			position.setVisible(true);
			position.setText("0 0 0");
		}

		if (realTracker instanceof TrackerWithTPS) {
			tps.setVisible(true);
			tps.setText("0");
		} else {
			tps.setVisible(false);
		}

		status.setText(t.getStatus().toString().toLowerCase(Locale.ROOT));
		if (status.getText().equals("ok")) {
			connectIMG.setImage(activeImage);
		} else {
			connectIMG.setImage(deactivatedImage);
		}

		if (realTracker instanceof TrackerWithBattery) {
			bat.setVisible(true);
			bat.setText("0");
		}

		if (t.userEditable()) {
			TrackerConfig cfg = server.getTrackerConfig(t);
			for (TrackerPosition p : TrackerPosition.values) {
				LogManager.log.debug(" " + p.name());
				desSelect.getItems().add(p.name());
			}

			if (cfg.designation != null) {
				TrackerPosition p = TrackerPosition.getByDesignation(cfg.designation);
				if (p != null) desSelect.setValue(p.name());
			}
			desSelect.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					TrackerPosition p = TrackerPosition.valueOf(String.valueOf(desSelect.getValue()));
					t.setBodyPosition(p);
					server.trackerUpdated(t);
				}
			});

		} else {
			desSelect.setVisible(false);
		}

		if (realTracker instanceof IMUTracker) {
			IMUTracker imu = (IMUTracker) realTracker;
			TrackerMountingRotation tr = imu.getMountingRotation();
			for (TrackerMountingRotation p : TrackerMountingRotation.values) {
				mountSelect.getItems().add(p.name());
			}
			if (tr != null) {
				mountSelect.setValue(tr.name());
			} else {
				mountSelect.setValue(TrackerMountingRotation.BACK.name());
			}

			desSelect.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					TrackerMountingRotation tr = TrackerMountingRotation.valueOf(String.valueOf(mountSelect.getValue()));
					imu.setMountingRotation(tr);
					server.trackerUpdated(t);
				}
			});

		} else mountSelect.setVisible(false);

		//raw.setText("0 0 0");

		return this;
	}

	public void update() {
		Tracker realTracker = t;
		if (t instanceof ReferenceAdjustedTracker)
			realTracker = ((ReferenceAdjustedTracker<? extends Tracker>) t).getTracker();
		
		t.getRotation(q);
		t.getPosition(v);
		q.toAngles(angles);

		position.setText(String.format("%s %s %s", StringUtils.prettyNumber(v.x, 1),
				StringUtils.prettyNumber(v.y, 1),
				StringUtils.prettyNumber(v.z, 1)));
		rotation.setText(String.format("%s %s %s", StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0),
				StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0),
				StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0)));
		status.setText(t.getStatus().toString().toLowerCase());

		if (status.getText().equals("ok")) {
			connectIMG.setImage(activeImage);
		} else {
			connectIMG.setImage(deactivatedImage);
		}

		if (realTracker instanceof TrackerWithTPS) {
			tps.setText(StringUtils.prettyNumber(((TrackerWithTPS) realTracker).getTPS(), 1));
		}

		if (realTracker instanceof TrackerWithBattery) {
			bat.setText(StringUtils.prettyNumber(((TrackerWithBattery) realTracker).getBatteryVoltage(), 1));
		}

		/*
		if (t instanceof ReferenceAdjustedTracker) {
			((ReferenceAdjustedTracker<Tracker>) t).attachmentFix.toAngles(angles);
			adj.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
			((ReferenceAdjustedTracker<Tracker>) t).yawFix.toAngles(angles);
			adjYaw.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
		}
		*/
		if (realTracker instanceof IMUTracker) {
			ping.setText(String.valueOf(((IMUTracker) realTracker).ping));
		}

		q.toAngles(angles);
		/*
		raw.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
				+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
				+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
		if (realTracker instanceof IMUTracker) {
			((IMUTracker) realTracker).rotMagQuaternion.toAngles(angles);
			rawMag.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
			calibration.setText(((IMUTracker) realTracker).calibrationStatus + " / " + ((IMUTracker) realTracker).magCalibrationStatus);
			magAccuracy.setText(StringUtils.prettyNumber(((IMUTracker) realTracker).magnetometerAccuracy * FastMath.RAD_TO_DEG, 1) + "°");
			((IMUTracker) realTracker).getCorrection(q);
			q.toAngles(angles);
			correction.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0)
					+ " " + StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
		}
		*/
	}

	public void setInfo(String testText) {
	}

	public Pane getTrackerContainer() {
		return trackerContainer;
	}

	private static int getTrackerSort(Tracker t) {
		if (t instanceof ReferenceAdjustedTracker)
			t = ((ReferenceAdjustedTracker<?>) t).getTracker();
		if (t instanceof IMUTracker)
			return 0;
		if (t instanceof HMDTracker)
			return 100;
		if (t instanceof ComputedTracker)
			return 200;
		return 1000;
	}
}
