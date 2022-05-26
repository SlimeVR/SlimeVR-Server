package dev.slimevr.gui;

import dev.slimevr.VRServer;
import dev.slimevr.gui.swing.ButtonTimer;
import dev.slimevr.gui.swing.EJBagNoStretch;
import dev.slimevr.vr.processor.skeleton.Skeleton;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;


public class SkeletonConfigGUI extends EJBagNoStretch {

	private final VRServer server;
	private final VRServerGUI gui;
	private final AutoBoneWindow autoBone;
	private final Map<SkeletonConfigValue, SkeletonLabel> labels = new HashMap<>();
	private JCheckBox precisionCb;

	public SkeletonConfigGUI(VRServer server, VRServerGUI gui) {
		super(false, true);
		this.server = server;
		this.gui = gui;
		this.autoBone = new AutoBoneWindow(server, this);

		setAlignmentY(TOP_ALIGNMENT);
		server.humanPoseProcessor.addSkeletonUpdatedCallback(this::skeletonUpdated);
		skeletonUpdated(null);
	}

	@ThreadSafe
	public void skeletonUpdated(Skeleton newSkeleton) {
		java.awt.EventQueue.invokeLater(() -> {
			removeAll();

			int row = 0;

			add(new TimedResetButton("Reset All"), s(c(1, row, 2), 3, 1));
			add(new JButton("Auto") {
				{
					addMouseListener(new MouseInputAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							autoBone.setVisible(true);
							autoBone.toFront();
						}
					});
				}
			}, s(c(4, row, 2), 3, 1));

			add(precisionCb = new JCheckBox("Precision adjust"), c(0, row, 2));
			precisionCb.setSelected(false);

			row++;

			for (SkeletonConfigValue config : SkeletonConfigValue.values) {
				add(new JLabel(config.label), c(0, row, 2));
				add(new AdjButton("+", config, false), c(1, row, 2));
				add(new SkeletonLabel(config), c(2, row, 2));
				add(new AdjButton("-", config, true), c(3, row, 2));

				// Only use a timer on configs that need time to get into
				// position for
				switch (config) {
					case TORSO:
					case LEGS_LENGTH:
						add(new TimedResetButton("Reset", config), c(4, row, 2));
						break;
					default:
						add(new ResetButton("Reset", config), c(4, row, 2));
						break;
				}

				row++;
			}

			gui.refresh();
		});
	}

	float proportionsIncrement(Boolean negative) {
		float increment = 0.01f;
		if (negative)
			increment = -0.01f;
		if (precisionCb.isSelected())
			increment /= 2f;
		return increment;
	}

	String getBoneLengthString(SkeletonConfigValue joint) { // Rounded to the
															// nearest 0.5
		return (StringUtils
			.prettyNumber(
				Math.round(server.humanPoseProcessor.getSkeletonConfig(joint) * 200) / 2.0f,
				1
			));
	}

	@ThreadSafe
	public void refreshAll() {
		java.awt.EventQueue.invokeLater(() -> {
			labels.forEach((joint, label) -> {
				label.setText(getBoneLengthString(joint));
			});
		});
	}

	private void change(SkeletonConfigValue joint, float diff) {
		// Update config value
		float current = server.humanPoseProcessor.getSkeletonConfig(joint);
		server.humanPoseProcessor.setSkeletonConfig(joint, current + diff);
		server.humanPoseProcessor.getSkeletonConfig().saveToConfig(server.config);
		server.saveConfig();

		// Update GUI
		labels.get(joint).setText(getBoneLengthString(joint));
	}

	private void reset(SkeletonConfigValue joint) {
		// Update config value
		server.humanPoseProcessor.resetSkeletonConfig(joint);
		server.humanPoseProcessor.getSkeletonConfig().saveToConfig(server.config);
		server.saveConfig();

		// Update GUI
		labels.get(joint).setText(getBoneLengthString(joint));
	}

	private void resetAll() {
		// Update config value
		server.humanPoseProcessor.resetAllSkeletonConfigs();
		server.humanPoseProcessor.getSkeletonConfig().saveToConfig(server.config);
		server.saveConfig();

		// Update GUI
		refreshAll();
	}

	private class SkeletonLabel extends JLabel {

		public SkeletonLabel(SkeletonConfigValue joint) {
			super(getBoneLengthString(joint));
			labels.put(joint, this);
		}
	}

	private class AdjButton extends JButton {

		public AdjButton(String text, SkeletonConfigValue joint, boolean negative) {
			super(text);
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					change(joint, proportionsIncrement(negative));
				}
			});
		}
	}

	private class ResetButton extends JButton {

		public ResetButton(String text, SkeletonConfigValue joint) {
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

		public TimedResetButton(String text, SkeletonConfigValue joint) {
			super(text);
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					ButtonTimer.runTimer(TimedResetButton.this, 3, text, () -> reset(joint));
				}
			});
		}

		public TimedResetButton(String text) {
			super(text);
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					ButtonTimer.runTimer(TimedResetButton.this, 3, text, () -> resetAll());
				}
			});
		}
	}
}
