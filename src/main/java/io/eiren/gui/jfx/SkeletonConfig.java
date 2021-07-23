package io.eiren.gui.jfx;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;


import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class SkeletonConfig extends GridPane {
	
	private final VRServer server;
	private final SlimeVRGUIJFX gui;
	private Map<String, SkeletonLabel> labels = new HashMap<>();
	
	public SkeletonConfig(VRServer server, SlimeVRGUIJFX gui) {
		super();
		this.server = server;
		this.gui = gui;

		server.humanPoseProcessor.addSkeletonUpdatedCallback(this::skeletonUpdated);
		skeletonUpdated(null);
		
	}
	
	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		javafx.application.Platform.runLater(() -> {
			getChildren().clear();
			
			int row = 0;
			
			add(new TimedResetButton("Reset All", "All"), 1, row, 3, 1);
			row++;
			
			add(new Label("Chest"), 0, row);
			add(new AdjButton("+", "Chest", 0.01f), 1, row);
			add(new SkeletonLabel("Chest"), 2, row);
			add(new AdjButton("-", "Chest", -0.01f), 3, row);
			add(new ResetButton("Reset", "Chest"), 4, row);
			row++;
			
			add(new Label("Waist"), 0, row);
			add(new AdjButton("+", "Waist", 0.01f), 1, row);
			add(new SkeletonLabel("Waist"), 2, row);
			add(new AdjButton("-", "Waist", -0.01f), 3, row);
			add(new TimedResetButton("Reset", "Waist"), 4, row);
			row++;

			add(new Label("Hips width"), 0, row);
			add(new AdjButton("+", "Hips width", 0.01f), 1, row);
			add(new SkeletonLabel("Hips width"), 2, row);
			add(new AdjButton("-", "Hips width", -0.01f), 3, row);
			add(new ResetButton("Reset", "Hips width"), 4, row);
			row++;

			add(new Label("Legs length"), 0, row);
			add(new AdjButton("+", "Legs length", 0.01f), 1, row);
			add(new SkeletonLabel("Legs length"), 2, row);
			add(new AdjButton("-", "Legs length", -0.01f), 3, row);
			add(new TimedResetButton("Reset", "Legs length"), 4, row);
			row++;

			add(new Label("Knee height"), 0, row);
			add(new AdjButton("+", "Knee height", 0.01f), 1, row);
			add(new SkeletonLabel("Knee height"), 2, row);
			add(new AdjButton("-", "Knee height", -0.01f), 3, row);
			add(new TimedResetButton("Reset", "Knee height"), 4, row);
			row++;

			add(new Label("Foot length"), 0, row);
			add(new AdjButton("+", "Foot length", 0.01f), 1, row);
			add(new SkeletonLabel("Foot length"), 2, row);
			add(new AdjButton("-", "Foot length", -0.01f), 3, row);
			add(new ResetButton("Reset", "Foot length"), 4, row);
			row++;

			add(new Label("Head offset"), 0, row);
			add(new AdjButton("+", "Head", 0.01f), 1, row);
			add(new SkeletonLabel("Head"), 2, row);
			add(new AdjButton("-", "Head", -0.01f), 3, row);
			add(new ResetButton("Reset", "Head"), 4, row);
			row++;

			add(new Label("Neck length"), 0, row);
			add(new AdjButton("+", "Neck", 0.01f), 1, row);
			add(new SkeletonLabel("Neck"), 2, row);
			add(new AdjButton("-", "Neck", -0.01f), 3, row);
			add(new ResetButton("Reset", "Neck"), 4, row);
			row++;
			
			add(new Label("Virtual waist"), 0, row);
			add(new AdjButton("+", "Virtual waist", 0.01f), 1, row);
			add(new SkeletonLabel("Virtual waist"), 2, row);
			add(new AdjButton("-", "Virtual waist", -0.01f), 3, row);
			add(new ResetButton("Reset", "Virtual waist"), 4, row);
			row++;
			
			gui.refresh();
		});
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
	
	private class SkeletonLabel extends Label {
		
		public SkeletonLabel(String joint) {
			super(StringUtils.prettyNumber(server.humanPoseProcessor.getSkeletonConfig(joint) * 100, 0));
			labels.put(joint, this);
		}
	}
	
	private class AdjButton extends Button {
		
		public AdjButton(String text, String joint, float diff) {
			super(text);
			/*addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					change(joint, diff);
				}
			});*/
		}
	}
	
	private class ResetButton extends Button {
		
		public ResetButton(String text, String joint) {
			super(text);
			/*addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					reset(joint);
				}
			});*/
		}
	}
	
	private class TimedResetButton extends Button {
		
		public TimedResetButton(String text, String joint) {
			super(text);
			/*addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					ButtonTimer.runTimer(TimedResetButton.this, 3, text, () -> reset(joint));
				}
			});*/
		}
	}
}
