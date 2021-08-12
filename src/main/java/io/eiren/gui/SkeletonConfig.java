package io.eiren.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import io.eiren.gui.autobone.AutoBone;
import io.eiren.gui.autobone.PoseFrame;
import io.eiren.gui.autobone.PoseRecordIO;
import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeletonWithLegs;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.util.logging.LogManager;

public class SkeletonConfig extends EJBag {

	private final VRServer server;
	private final VRServerGUI gui;
	private final AutoBone autoBone;
	private Map<String, SkeletonLabel> labels = new HashMap<>();

	public SkeletonConfig(VRServer server, VRServerGUI gui) {
		super();
		this.server = server;
		this.gui = gui;
		this.autoBone = new AutoBone(server);

		setAlignmentY(TOP_ALIGNMENT);
		server.humanPoseProcessor.addSkeletonUpdatedCallback(this::skeletonUpdated);
		skeletonUpdated(null);
	}

	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		java.awt.EventQueue.invokeLater(() -> {
			removeAll();

			int row = 0;

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
			}}, s(c(0, row, 1), 3, 1));
			row++;

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
			}}, s(c(0, row, 1), 3, 1));
			row++;
			//*/

			add(new TimedResetButton("Reset All", "All"), s(c(1, row, 1), 3, 1));
			add(new JButton("Auto") {{
				addMouseListener(new MouseInputAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						Thread thread = new Thread() {
							@Override
							public void run() {
								try {
									File saveRecording = new File("ABRecording.abf");
									File loadRecording = new File("ABRecording_Load.abf");

									if (loadRecording.exists()) {
										setText("Load");
										LogManager.log.info("[AutoBone] Detected recording at \"" + loadRecording.getPath() + "\", loading frames...");
										PoseFrame[] frames = PoseRecordIO.readFromFile(loadRecording);

										if (frames == null) {
											throw new NullPointerException("Reading frames from \"" + loadRecording.getPath() + "\" failed...");
										}

										autoBone.setFrames(frames);

										setText("Wait");
										LogManager.log.info("[AutoBone] Done loading frames! Processing frames...");
									} else {
										setText("Move");
										autoBone.startFrameRecording(250, 60);

										while (autoBone.isRecording()) {
											Thread.sleep(10);
										}

										setText("Wait");
										LogManager.log.info("[AutoBone] Done recording! Exporting frames to \"" + saveRecording.getPath() + "\"...");
										PoseRecordIO.writeToFile(saveRecording, autoBone.getFrames());

										LogManager.log.info("[AutoBone] Done exporting! Processing frames...");
									}

									autoBone.processFrames();
									LogManager.log.info("[AutoBone] Done processing!");

									boolean first = true;
									StringBuilder configInfo = new StringBuilder("[");

									for (Entry<String, Float> entry : autoBone.configs.entrySet()) {
										if (!first) {
											configInfo.append(", ");
										} else {
											first = false;
										}

										configInfo.append("{" + entry.getKey() + ": " + StringUtils.prettyNumber(entry.getValue()) + "}");
									}

									configInfo.append(']');

									LogManager.log.info("[AutoBone] Length values: " + configInfo.toString());
								} catch (Exception e1) {
									LogManager.log.severe("[AutoBone] Failed adjustment!", e1);
								} finally {
									setText("Auto");
								}
							}
						};

						thread.start();
					}
				});
			}}, s(c(4, row, 1), 3, 1));
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

			add(new JLabel("Legs length"), c(0, row, 1));
			add(new AdjButton("+", "Legs length", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Legs length"), c(2, row, 1));
			add(new AdjButton("-", "Legs length", -0.01f), c(3, row, 1));
			add(new TimedResetButton("Reset", "Legs length"), c(4, row, 1));
			row++;

			add(new JLabel("Knee height"), c(0, row, 1));
			add(new AdjButton("+", "Knee height", 0.01f), c(1, row, 1));
			add(new SkeletonLabel("Knee height"), c(2, row, 1));
			add(new AdjButton("-", "Knee height", -0.01f), c(3, row, 1));
			add(new TimedResetButton("Reset", "Knee height"), c(4, row, 1));
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

	@ThreadSafe
	public void refreshAll() {
		java.awt.EventQueue.invokeLater(() -> {
			labels.forEach((joint, label) -> {
				label.setText(StringUtils.prettyNumber(server.humanPoseProcessor.getSkeletonConfig(joint) * 100, 0));
			});
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
