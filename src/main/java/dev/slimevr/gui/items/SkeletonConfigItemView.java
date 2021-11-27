package dev.slimevr.gui.items;

import io.eiren.util.StringUtils;
import io.eiren.vr.VRServer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SkeletonConfigItemView extends HBox {

	@FXML
	private ResourceBundle resources;


	@FXML
	private Button itemMinusButton;

	@FXML
	private Button itemPlusButton;

	@FXML
	private Button itemResetButton;

	@FXML
	private Text itemValue;

	@FXML
	private Text itemTitle;

	private String joint;
	private String jointName;

	private float value;
	private final VRServer server;
	private final SkeletonConfigItemListener itemListener;


	public SkeletonConfigItemView(VRServer server, String joint, String jointName, SkeletonConfigItemListener itemListener) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cells/skeletonConfigItemView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.server = server;
		this.itemListener = itemListener;
		this.joint = joint;
		this.jointName = jointName;
		this.value = server.humanPoseProcessor.getSkeletonConfig(joint) * 100;

	}

	@FXML
	void initialize() {
		itemTitle.setText(jointName);
		setItemValueText(value);
		itemResetButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				reset();
			}
		});

		itemPlusButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				change(0.01f);
			}
		});
		itemMinusButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				change(-0.01f);
			}
		});


	}

	/*private void change(String joint, float diff) {
		float current = server.humanPoseProcessor.getSkeletonConfig(joint);
		server.humanPoseProcessor.setSkeletonConfig(joint, current + diff);
		server.saveConfig();
		// labels.get(joint).setText(StringUtils.prettyNumber((current + diff) * 100, 0));
	}

	private void reset(String joint) {
		server.humanPoseProcessor.resetSkeletonConfig(joint);
		server.saveConfig();
		*//*if(!"All".equals(joint)) {
			float current = server.humanPoseProcessor.getSkeletonConfig(joint);
			// labels.get(joint).setText(StringUtils.prettyNumber((current) * 100, 0));
		} else {
			labels.forEach((jnt, label) -> {
				float current = server.humanPoseProcessor.getSkeletonConfig(jnt);
				label.setText(StringUtils.prettyNumber((current) * 100, 0));
			});
		}*//*
	}*/

	private void change(float diff) {
		itemListener.change(joint, diff);
	}

	private void reset() {
		itemListener.reset(joint);
	}


	private void setItemValueText(float value) {
		itemValue.setText(StringUtils.prettyNumber(value * 100, 0));
	}


	public void refreshJoint(float value) {
		this.value = value;
		setItemValueText(value);
	}


	public interface SkeletonConfigItemListener {
		void change(String joint, float diff);

		void reset(String joint);
	}

	public String getJoint() {
		return joint;
	}

	public float getValue() {
		return value;
	}
}
