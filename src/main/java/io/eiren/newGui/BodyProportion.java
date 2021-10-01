package io.eiren.newGui;

import io.eiren.util.StringUtils;
import io.eiren.vr.VRServer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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

	private class BodyLabel extends Label {

		public BodyLabel(String joint) {
			super(StringUtils.prettyNumber(server.humanPoseProcessor.getSkeletonConfig(joint) * 100, 0));
			labels.put(joint, this);
		}
	}

	private class ArithmOpBodyButton extends Button {

		public ArithmOpBodyButton(String text, String joint, float diff) {
			super(text);
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
