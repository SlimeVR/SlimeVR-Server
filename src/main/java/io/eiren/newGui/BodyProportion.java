package io.eiren.newGui;

import io.eiren.util.StringUtils;
import io.eiren.vr.VRServer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class BodyProportion {

	private VRServer server;
	private Map<String, Label> labels = new HashMap();
	private int counter = 0;

	public BodyProportion(VRServer server) {
		this.server = server;
	}

	public void bodyProportionInit(TextFlow name, TextFlow plus, TextFlow lable, TextFlow minus, TextFlow reset) {

		name.setTextAlignment(TextAlignment.CENTER);
		name.setLineSpacing(24.0f);
		name.setPadding(new Insets(15, 0, 0, 0));

		name.getChildren().addAll(new Text("Chest"), new Text(System.lineSeparator()),
				new Text("Waist"), new Text(System.lineSeparator()),
				new Text("Hips width"), new Text(System.lineSeparator()),
				new Text("Legs length"), new Text(System.lineSeparator()),
				new Text("Knee height"), new Text(System.lineSeparator()),
				new Text("Foot length"), new Text(System.lineSeparator()),
				new Text("Head offset"), new Text(System.lineSeparator()),
				new Text("Neck length"), new Text(System.lineSeparator()),
				new Text("Virtual waist"));


		plus.setPadding(new Insets(13, 0, 0, 0));
		plus.setTextAlignment(TextAlignment.CENTER);
		plus.setLineSpacing(14.0f);

		plus.getChildren().addAll(new ArithmOpBodyButton("+", "Chest", 0.01f),
				new ArithmOpBodyButton("+", "Waist", 0.01f),
				new ArithmOpBodyButton("+", "Hips width", 0.01f),
				new ArithmOpBodyButton("+", "Legs length", 0.01f),
				new ArithmOpBodyButton("+", "Knee height", 0.01f),
				new ArithmOpBodyButton("+", "Foot length", 0.01f),
				new ArithmOpBodyButton("+", "Head offset", 0.01f),
				new ArithmOpBodyButton("+", "Neck length", 0.01f),
				new ArithmOpBodyButton("+", "Virtual waist", 0.01f));
	}

	private void customizeTextFlow(TextFlow t) {
		t.setTextAlignment(TextAlignment.CENTER);
		t.setLineSpacing(24.0f);
		t.setPadding(new Insets(15, 0, 0, 0));
	}

	private class BodyLabel extends Label {

		public BodyLabel(String joint) {
			super(StringUtils.prettyNumber(server.humanPoseProcessor.getSkeletonConfig(joint) * 100, 0));
			labels.put(joint, this);
		}
	}

	private class ArithmOpBodyButton extends Button {

		public ArithmOpBodyButton(String text, String joint, float diff) {
			super(text);
			setPrefSize(31, 31);
			setStyle("-fx-font-size : 15px;");
			addEventHandler(MouseEvent.MOUSE_CLICKED, e -> change(joint, diff));
		}
	}

	private class ResetBodyButton extends Button {

		public ResetBodyButton(String text, String joint) {
			super(text);
			addEventHandler(MouseEvent.MOUSE_CLICKED, e -> reset(joint));
		}
	}

	private class TimedResetBodyButton extends Button {

		public TimedResetBodyButton(String text, String joint) {
			super(text);

			addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
				setText(String.valueOf(3));
				counter = 2;
				Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), timeLineEvent -> {
					if (counter > 0) setText(String.valueOf(counter));
					if (counter == 0) {
						reset(joint);
						setText(text);
					}
					counter--;
				}));
				timeline.setCycleCount(3);
				timeline.play();
			});
		}
	}

	private void change(String joint, float diff) {
		float current = server.humanPoseProcessor.getSkeletonConfig(joint);
		server.humanPoseProcessor.setSkeletonConfig(joint, current + diff);
		server.saveConfig();
		labels.get(joint).setText(StringUtils.prettyNumber((current + diff) * 100, 0));
	}

	private void reset(String joint) {
		server.humanPoseProcessor.resetSkeletonConfig(joint);
		server.saveConfig();
		if(!"All".equals(joint)) {
			float current = server.humanPoseProcessor.getSkeletonConfig(joint);
			labels.get(joint).setText(StringUtils.prettyNumber((current) * 100, 0));
		} else {
			labels.forEach((jnt, label) -> {
				float current = server.humanPoseProcessor.getSkeletonConfig(jnt);
				label.setText(StringUtils.prettyNumber((current) * 100, 0));
			});
		}
	}

}
