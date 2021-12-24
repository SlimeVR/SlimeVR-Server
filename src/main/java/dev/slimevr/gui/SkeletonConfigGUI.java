package dev.slimevr.gui;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import dev.slimevr.VRServer;
import dev.slimevr.gui.swing.ButtonTimer;
import dev.slimevr.gui.swing.EJBagNoStretch;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;

public class SkeletonConfigGUI extends EJBagNoStretch {

	private final VRServer server;
	private final VRServerGUI gui;
	private final AutoBoneWindow autoBone;
	private Map<SkeletonConfigValue, SkeletonLabel> labels = new HashMap<>();

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
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		java.awt.EventQueue.invokeLater(() -> {
			removeAll();

			int row = 0;

			/**
			add(new JCheckBox("Extended pelvis model") {{
				addItemListener(new ItemListener() {
				    @Override
				    public void itemStateChanged(ItemEvent e) {
				        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
				        	if(newSkeleton != null && newSkeleton instanceof HumanSkeletonWithLegs) {
				        		HumanSkeletonWithLegs hswl = (HumanSkeletonWithLegs) newSkeleton;
				        		hswl.setSkeletonConfigBoolean("Extended pelvis model", true);
				        	}
				        } else {
				        	if(newSkeleton != null && newSkeleton instanceof HumanSkeletonWithLegs) {
				        		HumanSkeletonWithLegs hswl = (HumanSkeletonWithLegs) newSkeleton;
				        		hswl.setSkeletonConfigBoolean("Extended pelvis model", false);
				        	}
				        }
				    }
				});
				if(newSkeleton != null && newSkeleton instanceof HumanSkeletonWithLegs) {
	        		HumanSkeletonWithLegs hswl = (HumanSkeletonWithLegs) newSkeleton;
	        		setSelected(hswl.getSkeletonConfigBoolean("Extended pelvis model"));
				}
			}}, s(c(0, row, 2), 3, 1));
			row++;
			//*/
			/*
			add(new JCheckBox("Extended knee model") {{
				addItemListener(new ItemListener() {
				    @Override
				    public void itemStateChanged(ItemEvent e) {
				        if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
				        	if(newSkeleton != null && newSkeleton instanceof HumanSkeletonWithLegs) {
				        		HumanSkeletonWithLegs hswl = (HumanSkeletonWithLegs) newSkeleton;
				        		hswl.setSkeletonConfigBoolean("Extended knee model", true);
				        	}
				        } else {
				        	if(newSkeleton != null && newSkeleton instanceof HumanSkeletonWithLegs) {
				        		HumanSkeletonWithLegs hswl = (HumanSkeletonWithLegs) newSkeleton;
				        		hswl.setSkeletonConfigBoolean("Extended knee model", false);
				        	}
				        }
				    }
				});
				if(newSkeleton != null && newSkeleton instanceof HumanSkeletonWithLegs) {
	        		HumanSkeletonWithLegs hswl = (HumanSkeletonWithLegs) newSkeleton;
	        		setSelected(hswl.getSkeletonConfigBoolean("Extended knee model"));
				}
			}}, s(c(0, row, 2), 3, 1));
			row++;
			//*/

			add(new TimedResetButton("Reset All"), s(c(1, row, 2), 3, 1));
			add(new JButton("Auto") {{
				addMouseListener(new MouseInputAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						autoBone.setVisible(true);
						autoBone.toFront();
					}
				});
			}}, s(c(4, row, 2), 3, 1));
			row++;

			for (SkeletonConfigValue config : SkeletonConfigValue.values) {
				add(new JLabel(config.label), c(0, row, 2));
				add(new AdjButton("+", config, 0.01f), c(1, row, 2));
				add(new SkeletonLabel(config), c(2, row, 2));
				add(new AdjButton("-", config, -0.01f), c(3, row, 2));

				// Only use a timer on configs that need time to get into position for
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

	@ThreadSafe
	public void refreshAll() {
		java.awt.EventQueue.invokeLater(() -> {
			labels.forEach((joint, label) -> {
				label.setText(StringUtils.prettyNumber(server.humanPoseProcessor.getSkeletonConfig(joint) * 100, 0));
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
		labels.get(joint).setText(StringUtils.prettyNumber((current + diff) * 100, 0));
	}

	private void reset(SkeletonConfigValue joint) {
		// Update config value
		server.humanPoseProcessor.resetSkeletonConfig(joint);
		server.humanPoseProcessor.getSkeletonConfig().saveToConfig(server.config);
		server.saveConfig();

		// Update GUI
		float current = server.humanPoseProcessor.getSkeletonConfig(joint);
		labels.get(joint).setText(StringUtils.prettyNumber((current) * 100, 0));
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
			super(StringUtils.prettyNumber(server.humanPoseProcessor.getSkeletonConfig(joint) * 100, 0));
			labels.put(joint, this);
		}
	}

	private class AdjButton extends JButton {

		public AdjButton(String text, SkeletonConfigValue joint, float diff) {
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
