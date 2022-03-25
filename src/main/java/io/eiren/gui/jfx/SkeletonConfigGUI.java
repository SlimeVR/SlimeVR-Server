package io.eiren.gui.jfx;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;


import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;
import dev.slimevr.VRServer;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class SkeletonConfigGUI extends GridPane {
	
	private final VRServer server;
	private final SlimeVRGUIJFX gui;
	private Map<SkeletonConfigValue, SkeletonLabel> labels = new HashMap<>();
	
	public SkeletonConfigGUI(VRServer server, SlimeVRGUIJFX gui) {
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
			
			add(new TimedResetButton("Reset All"), 1, row, 3, 1);

			row++;
			
			for (SkeletonConfigValue config : SkeletonConfigValue.values) {
				add(new Label(config.label), 0, row);
				add(new AdjButton("+", config, false), 1, row);
				add(new SkeletonLabel(config), 2, row);
				add(new AdjButton("-", config, true), 3, row);

				// Only use a timer on configs that need time to get into position for
				switch (config) {
				case TORSO:
				case LEGS_LENGTH:
					add(new TimedResetButton("Reset", config), 4, row);
					break;
				default:
					add(new ResetButton("Reset", config), 4, row);
					break;
				}

				row++;
			}
			
			gui.refresh();
		});
	}
	
	private void change(SkeletonConfigValue joint, float diff) {
		float current = server.humanPoseProcessor.getSkeletonConfig(joint);
		server.humanPoseProcessor.setSkeletonConfig(joint, current + diff);
		server.saveConfig();
		labels.get(joint).setText(StringUtils.prettyNumber((current + diff) * 100, 0));
	}
	
	private void reset(SkeletonConfigValue joint) {
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

		public SkeletonLabel(SkeletonConfigValue joint) {
			super(StringUtils.prettyNumber(Math.round(server.humanPoseProcessor.getSkeletonConfig(joint) * 200) / 2.0f, 1));
			labels.put(joint, this);
		}
	}

	private class AdjButton extends Button {

		public AdjButton(String text, SkeletonConfigValue joint, boolean negative) {
			super(text);
			/*
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					change(joint, proportionsIncrement(negative));
				}
			});
			*/
		}
	}

	private class ResetButton extends Button {

		public ResetButton(String text, SkeletonConfigValue joint) {
			super(text);
			/*
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					reset(joint);
				}
			});
			*/
		}
	}

	private class TimedResetButton extends Button {

		public TimedResetButton(String text, SkeletonConfigValue joint) {
			super(text);
			/*
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					ButtonTimer.runTimer(TimedResetButton.this, 3, text, () -> reset(joint));
				}
			});
			*/
		}

		public TimedResetButton(String text) {
			super(text);
			/*
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					ButtonTimer.runTimer(TimedResetButton.this, 3, text, () -> resetAll());
				}
			});
			*/
		}
	}
}
