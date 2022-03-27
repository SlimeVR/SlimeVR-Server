package dev.slimevr.gui;

import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;
import dev.slimevr.VRServer;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class BodyProportion {
	
	private VRServer server;
	private Map<SkeletonConfigValue, Label> labels = new HashMap<>();
	private int counter = 0;
	
	public BodyProportion(VRServer server) {
		this.server = server;
	}
	
	public void bodyProportionInit(TextFlow name, TextFlow plus, TextFlow label, TextFlow minus, TextFlow reset) {
		
		customizeTextFlow(name, 24.0f, 15);
		customizeTextFlow(plus, 14.0f, 13);
		customizeTextFlow(label, 15.0f, 14);
		customizeTextFlow(minus, 14.0f, 13);
		customizeTextFlow(reset, 14.0f, 13);
		
		for (int i = 0; i < SkeletonConfigValue.values.length; i++) {
			// Get config
			SkeletonConfigValue config = SkeletonConfigValue.values[i];
			boolean isLastLine = i >= (SkeletonConfigValue.values.length - 1);

			// Only use a timer on configs that need time to get into position for
			switch (config) {
			case TORSO:
			case LEGS_LENGTH:
				addBodyPropTimedResetBtn(name, plus, label, minus, reset, SkeletonConfigValue.KNEE_HEIGHT, !isLastLine);
				break;
			default:
				addBodyProp(name, plus, label, minus, reset, SkeletonConfigValue.CHEST, !isLastLine);
				break;
			}
		}
	}
	
	private void addBodyProp(TextFlow name, TextFlow plus, TextFlow label, TextFlow minus, TextFlow reset, SkeletonConfigValue nameStr, boolean lineSeparator) {
		if(lineSeparator)
			name.getChildren().addAll(new Text(nameStr.label), new Text(System.lineSeparator()));
		else
			name.getChildren().add(new Text(nameStr.label));
		
		plus.getChildren().add(new ArithmOpBodyButton("+", nameStr, 0.01f));
		
		if(lineSeparator)
			label.getChildren().addAll(new BodyLabel(nameStr), new Text(System.lineSeparator()));
		else
			label.getChildren().add(new BodyLabel(nameStr));
		
		minus.getChildren().add(new ArithmOpBodyButton("-", nameStr, -0.01f));
		reset.getChildren().add(new ResetBodyButton(nameStr));
	}
	
	private void addBodyPropTimedResetBtn(TextFlow name, TextFlow plus, TextFlow label, TextFlow minus, TextFlow reset, SkeletonConfigValue nameStr, boolean lineSeparator) {
		if(lineSeparator)
			name.getChildren().addAll(new Text(nameStr.label), new Text(System.lineSeparator()));
		else
			name.getChildren().add(new Text(nameStr.label));
		
		plus.getChildren().add(new ArithmOpBodyButton("+", nameStr, 0.01f));
		
		if(lineSeparator)
			label.getChildren().addAll(new BodyLabel(nameStr), new Text(System.lineSeparator()));
		else
			label.getChildren().add(new BodyLabel(nameStr));
		
		minus.getChildren().add(new ArithmOpBodyButton("-", nameStr, -0.01f));
		reset.getChildren().add(new TimedResetBodyButton(nameStr));
	}
	
	private void customizeTextFlow(TextFlow t, float lineSpacing, int topPadding) {
		t.setTextAlignment(TextAlignment.CENTER);
		t.setLineSpacing(lineSpacing);
		t.setPadding(new Insets(topPadding, 0, 0, 0));
	}
	
	private class BodyLabel extends Label {
		
		public BodyLabel(SkeletonConfigValue joint) {
			super(StringUtils.prettyNumber(server.humanPoseProcessor.getSkeletonConfig(joint) * 100, 0));
			setStyle("-fx-text-fill: #f4f4f4;");
			labels.put(joint, this);
		}
	}
	
	private class ArithmOpBodyButton extends Button {
		
		public ArithmOpBodyButton(String text, SkeletonConfigValue joint, float diff) {
			super(text);
			setPrefSize(31, 31);
			setStyle("-fx-font-size : 15px;");
			addEventHandler(MouseEvent.MOUSE_CLICKED, e -> change(joint, diff));
		}
	}
	
	private class ResetBodyButton extends Button {
		
		public ResetBodyButton(SkeletonConfigValue joint) {
			super("Reset");
			setPrefSize(55, 31);
			
			addEventHandler(MouseEvent.MOUSE_CLICKED, e -> reset(joint));
		}
	}
	
	private class TimedResetBodyButton extends Button {
		
		public TimedResetBodyButton(SkeletonConfigValue joint) {
			super("Reset");
			setPrefSize(55, 31);
			
			addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
				setText(String.valueOf(3));
				counter = 2;
				Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), timeLineEvent -> {
					if(counter > 0)
						setText(String.valueOf(counter));
					if(counter == 0) {
						reset(joint);
						setText("Reset");
					}
					counter--;
				}));
				timeline.setCycleCount(3);
				timeline.play();
			});
		}
	}
	
	private void change(SkeletonConfigValue joint, float diff) {
		float current = server.humanPoseProcessor.getSkeletonConfig(joint);
		server.humanPoseProcessor.setSkeletonConfig(joint, current + diff);
		server.saveConfig();
		labels.get(joint).setText(StringUtils.prettyNumber((current + diff) * 100, 0));
	}
	
	public void reset(SkeletonConfigValue joint) {
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
	
	@ThreadSafe
	public void refreshAll() {
		labels.forEach((joint, label) -> {
			label.setText(StringUtils.prettyNumber(server.humanPoseProcessor.getSkeletonConfig(joint) * 100, 0));
		});
	}
}
