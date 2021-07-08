package io.eiren.gui;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;

public class SkeletonConfig extends EJBag {
	
	private final VRServer server;
	private final VRServerGUI gui;
	private Map<String, SkeletonLabel> labels = new HashMap<>();
	
	public SkeletonConfig(VRServer server, VRServerGUI gui) {
		super();
		this.server = server;
		this.gui = gui;

		setAlignmentY(TOP_ALIGNMENT);
		server.humanPoseProcessor.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}
	
	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		java.awt.EventQueue.invokeLater(() -> {
			removeAll();
			
			int row = 0;
			
			add(new TimedResetButton("Reset All", "All"), s(c(1, row, 1), 3, 1));
			row++;
			
			add(new JLabel("Chest"), c(0, row, 1));
			add(new AdjButton("+", "Chest", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Chest"), c(2, row, 1));
			add(new AdjButton("-", "Chest", -0.01f), c(3, row, 1));
			add(new ResetButton("Reset", "Chest"), c(4, row, 1));
			row++;
			
			add(new JLabel("Waist"), c(0, row, 1));
			add(new AdjButton("+", "Waist", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Waist"), c(2, row, 1));
			add(new AdjButton("-", "Waist", -0.01f), c(3, row, 1));
			add(new TimedResetButton("Reset", "Waist"), c(4, row, 1));
			row++;

			add(new JLabel("Hips width"), c(0, row, 1));
			add(new AdjButton("+", "Hips width", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Hips width"), c(2, row, 1));
			add(new AdjButton("-", "Hips width", -0.01f), c(3, row, 1));
			add(new ResetButton("Reset", "Hips width"), c(4, row, 1));
			row++;

			add(new JLabel("Hip length"), c(0, row, 1));
			add(new AdjButton("+", "Hip length", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Hip length"), c(2, row, 1));
			add(new AdjButton("-", "Hip length", -0.01f), c(3, row, 1));
			add(new TimedResetButton("Reset", "Hip length"), c(4, row, 1));
			row++;

			add(new JLabel("Ankle length"), c(0, row, 1));
			add(new AdjButton("+", "Ankle length", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Ankle length"), c(2, row, 1));
			add(new AdjButton("-", "Ankle length", -0.01f), c(3, row, 1));
			add(new TimedResetButton("Reset", "Ankle length"), c(4, row, 1));
			row++;

			add(new JLabel("Foot length"), c(0, row, 1));
			add(new AdjButton("+", "Foot length", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Foot length"), c(2, row, 1));
			add(new AdjButton("-", "Foot length", -0.01f), c(3, row, 1));
			add(new ResetButton("Reset", "Foot length"), c(4, row, 1));
			row++;

			add(new JLabel("Head offset"), c(0, row, 1));
			add(new AdjButton("+", "Head", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Head"), c(2, row, 1));
			add(new AdjButton("-", "Head", -0.01f), c(3, row, 1));
			add(new ResetButton("Reset", "Head"), c(4, row, 1));
			row++;

			add(new JLabel("Neck length"), c(0, row, 1));
			add(new AdjButton("+", "Neck", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Neck"), c(2, row, 1));
			add(new AdjButton("-", "Neck", -0.01f), c(3, row, 1));
			add(new ResetButton("Reset", "Neck"), c(4, row, 1));
			row++;
			
			add(new JLabel("Virtual waist"), c(0, row, 1));
			add(new AdjButton("+", "Virtual waist", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Virtual waist"), c(2, row, 1));
			add(new AdjButton("-", "Virtual waist", -0.01f), c(3, row, 1));
			add(new ResetButton("Reset", "Virtual waist"), c(4, row, 1));
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
	
	private class SkeletonLabel extends JLabel {
		
		public SkeletonLabel(String joint) {
			super(StringUtils.prettyNumber(server.humanPoseProcessor.getSkeletonConfig(joint) * 100, 0));
			labels.put(joint, this);
		}
	}
	
	private class AdjButton extends JButton {
		
		public AdjButton(String text, String joint, float diff) {
			super(text);
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					change(joint, diff);
				}
			});
		}
	}
	
	private class ResetButton extends JButton {
		
		public ResetButton(String text, String joint) {
			super(text);
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					reset(joint);
				}
			});
		}
	}
	
	private class TimedResetButton extends JButton {
		
		public TimedResetButton(String text, String joint) {
			super(text);
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					ButtonTimer.runTimer(TimedResetButton.this, 3, text, () -> reset(joint));
				}
			});
		}
	}
}
