package io.eiren.gui.autobone;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import io.eiren.gui.EJBox;
import io.eiren.gui.SkeletonConfig;
import io.eiren.util.StringUtils;
import io.eiren.util.ann.AWTThread;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.VRServer;
import javax.swing.event.MouseInputAdapter;

import org.apache.commons.lang3.tuple.Pair;

public class AutoBoneWindow extends JFrame {

	private EJBox pane;

	private final VRServer server;
	private final SkeletonConfig skeletonConfig;
	private final PoseRecorder poseRecorder;
	private final AutoBone autoBone;

	private Thread recordingThread = null;
	private Thread autoBoneThread = null;

	private JLabel processLabel;
	private JLabel lengthsLabel;

	public AutoBoneWindow(VRServer server, SkeletonConfig skeletonConfig) {
		super("Skeleton Auto-Configuration");

		this.server = server;
		this.skeletonConfig = skeletonConfig;
		this.poseRecorder = new PoseRecorder(server);
		this.autoBone = new AutoBone(server);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		add(new JScrollPane(pane = new EJBox(BoxLayout.PAGE_AXIS), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		build();
	}

	private String getLengthsString() {
		boolean first = true;
		StringBuilder configInfo = new StringBuilder("");
		for (Entry<String, Float> entry : autoBone.configs.entrySet()) {
			if (!first) {
				configInfo.append(", ");
			} else {
				first = false;
			}

			configInfo.append(entry.getKey() + ": " + StringUtils.prettyNumber(entry.getValue() * 100f, 2));
		}

		return configInfo.toString();
	}

	@AWTThread
	private void build() {
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {{
			setBorder(new EmptyBorder(i(5)));
			add(new JButton("Start Recording") {{
				addMouseListener(new MouseInputAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						// Prevent running multiple times
						if (recordingThread != null) {
							return;
						}

						Thread thread = new Thread() {
							@Override
							public void run() {
								try {
									if (poseRecorder.isReadyToRecord()) {
										setText("Recording...");
										// 1000 samples at 20 ms per sample is 20 seconds
										int sampleCount = server.config.getInt("autobone.sampleCount", 1000);
										long sampleRate = server.config.getLong("autobone.sampleRateMs", 20L);
										Future<PoseFrame[]> framesFuture = poseRecorder.startFrameRecording(sampleCount, sampleRate);
										PoseFrame[] frames = framesFuture.get();
										LogManager.log.info("[AutoBone] Done recording!");

										setText("Saving...");
										if (server.config.getBoolean("autobone.saveRecordings", true)) {
											File saveFolder = new File("Recordings");
											if (saveFolder.isDirectory() || saveFolder.mkdirs()) {
												File saveRecording;
												int recordingIndex = 1;
												do {
													saveRecording = new File(saveFolder, "ABRecording" + recordingIndex++ + ".abf");
												} while (saveRecording.exists());

												LogManager.log.info("[AutoBone] Exporting frames to \"" + saveRecording.getPath() + "\"...");
												if (PoseRecordIO.writeToFile(saveRecording, frames)) {
													LogManager.log.info("[AutoBone] Done exporting! Recording can be found at \"" + saveRecording.getPath() + "\".");
												} else {
													LogManager.log.severe("[AutoBone] Failed to export the recording to \"" + saveRecording.getPath() + "\".");
												}
											} else {
												LogManager.log.severe("[AutoBone] Failed to create the recording directory \"" + saveFolder.getPath() + "\".");
											}
										}
									} else {
										setText("Not Ready...");
										LogManager.log.severe("[AutoBone] Unable to record...");
										Thread.sleep(3000); // Wait for 3 seconds
										return;
									}
								} catch (Exception e) {
									setText("Recording Failed...");
									LogManager.log.severe("[AutoBone] Failed recording!", e);
									try {
										Thread.sleep(3000); // Wait for 3 seconds
									} catch (Exception e1) {
										// Ignore
									}
								} finally {
									setText("Start Recording");
									recordingThread = null;
								}
							}
						};

						recordingThread = thread;
						thread.start();
					}
				});
			}});

			add(new JButton("Auto-Adjust") {{
				addMouseListener(new MouseInputAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						// Prevent running multiple times
						if (autoBoneThread != null) {
							return;
						}

						Thread thread = new Thread() {
							@Override
							public void run() {
								try {
									FastList<Pair<String, PoseFrame[]>> frameRecordings = new FastList<Pair<String, PoseFrame[]>>();

									File loadFolder = new File("LoadRecordings");
									if (loadFolder.isDirectory()) {
										setText("Load...");

										File[] files = loadFolder.listFiles();
										if (files != null) {
											for (File file : files) {
												if (file.isFile() && org.apache.commons.lang3.StringUtils.endsWithIgnoreCase(file.getName(), ".abf")) {
													LogManager.log.info("[AutoBone] Detected recording at \"" + file.getPath() + "\", loading frames...");
													PoseFrame[] frames = PoseRecordIO.readFromFile(file);

													if (frames == null) {
														LogManager.log.severe("Reading frames from \"" + file.getPath() + "\" failed...");
													} else {
														frameRecordings.add(Pair.of(file.getName(), frames));
													}
												}
											}
										}
									}

									if (frameRecordings.size() > 0) {
										LogManager.log.info("[AutoBone] Done loading frames!");
									} else {
										Future<PoseFrame[]> frames = poseRecorder.getFramesAsync();
										if (frames != null) {
											setText("Waiting for Recording...");
											frameRecordings.add(Pair.of("<Recording>", frames.get()));
										} else {
											setText("No Recordings...");
											LogManager.log.severe("[AutoBone] No recordings found in \"" + loadFolder.getPath() + "\" and no recording was done...");
											try {
												Thread.sleep(3000); // Wait for 3 seconds
											} catch (Exception e1) {
												// Ignore
											}
											return;
										}
									}

									setText("Processing...");
									LogManager.log.info("[AutoBone] Processing frames...");
									FastList<Float> heightPercentError = new FastList<Float>(frameRecordings.size());
									for (Pair<String, PoseFrame[]> recording : frameRecordings) {
										LogManager.log.info("[AutoBone] Processing frames from \"" + recording.getKey() + "\"...");
										autoBone.reloadConfigValues();

										autoBone.minDataDistance = server.config.getInt("autobone.minimumDataDistance", autoBone.minDataDistance);
										autoBone.maxDataDistance = server.config.getInt("autobone.maximumDataDistance", autoBone.maxDataDistance);
										autoBone.numEpochs = server.config.getInt("autobone.epochCount", autoBone.numEpochs);
										autoBone.initialAdjustRate = server.config.getFloat("autobone.adjustRate", autoBone.initialAdjustRate);
										autoBone.adjustRateDecay = server.config.getFloat("autobone.adjustRateDecay", autoBone.adjustRateDecay);
										autoBone.slideErrorFactor = server.config.getFloat("autobone.slideErrorFactor", autoBone.slideErrorFactor);
										autoBone.offsetErrorFactor = server.config.getFloat("autobone.offsetErrorFactor", autoBone.offsetErrorFactor);
										autoBone.proportionErrorFactor = server.config.getFloat("autobone.proportionErrorFactor", autoBone.proportionErrorFactor);
										autoBone.heightErrorFactor = server.config.getFloat("autobone.heightErrorFactor", autoBone.heightErrorFactor);

										boolean calcInitError = server.config.getBoolean("autobone.calculateInitialError", true);
										float targetHeight = server.config.getFloat("autobone.manualTargetHeight", -1f);
										heightPercentError.add(autoBone.processFrames(recording.getValue(), calcInitError, targetHeight, (epoch) -> {
											processLabel.setText(epoch.toString());
											lengthsLabel.setText(getLengthsString());
										}));

										LogManager.log.info("[AutoBone] Done processing!");

										//#region Stats/Values
										Float neckLength = autoBone.getConfig("Neck");
										Float chestLength = autoBone.getConfig("Chest");
										Float waistLength = autoBone.getConfig("Waist");
										Float hipWidth = autoBone.getConfig("Hips width");
										Float legsLength = autoBone.getConfig("Legs length");
										Float kneeHeight = autoBone.getConfig("Knee height");

										float neckWaist = neckLength != null && waistLength != null ? neckLength / waistLength : 0f;
										float chestWaist = chestLength != null && waistLength != null ? chestLength / waistLength : 0f;
										float hipWaist = hipWidth != null && waistLength != null ? hipWidth / waistLength : 0f;
										float legWaist = legsLength != null && waistLength != null ? legsLength / waistLength : 0f;
										float legBody = legsLength != null && waistLength != null && neckLength != null ? legsLength / (waistLength + neckLength) : 0f;
										float kneeLeg = kneeHeight != null && legsLength != null ? kneeHeight / legsLength : 0f;

										LogManager.log.info("[AutoBone] Ratios: [{Neck-Waist: " + StringUtils.prettyNumber(neckWaist) +
										"}, {Chest-Waist: " + StringUtils.prettyNumber(chestWaist) +
										"}, {Hip-Waist: " + StringUtils.prettyNumber(hipWaist) +
										"}, {Leg-Waist: " + StringUtils.prettyNumber(legWaist) +
										"}, {Leg-Body: " + StringUtils.prettyNumber(legBody) +
										"}, {Knee-Leg: " + StringUtils.prettyNumber(kneeLeg) + "}]");

										String lengthsString = getLengthsString();
										LogManager.log.info("[AutoBone] Length values: " + lengthsString);
										lengthsLabel.setText(lengthsString);
									}

									if (heightPercentError.size() > 0) {
										float mean = 0f;
										for (float val : heightPercentError) {
											mean += val;
										}
										mean /= heightPercentError.size();

										float std = 0f;
										for (float val : heightPercentError) {
											float stdVal = val - mean;
											std += stdVal * stdVal;
										}
										std = (float)Math.sqrt(std / heightPercentError.size());

										LogManager.log.info("[AutoBone] Average height error: " + StringUtils.prettyNumber(mean, 6) + " (SD " + StringUtils.prettyNumber(std, 6) + ")");
									}
									//#endregion
								} catch (Exception e) {
									setText("Failed...");
									LogManager.log.severe("[AutoBone] Failed adjustment!", e);
									try {
										Thread.sleep(3000); // Wait for 3 seconds
									} catch (Exception e1) {
										// Ignore
									}
								} finally {
									setText("Auto-Adjust");
									autoBoneThread = null;
								}
							}
						};

						autoBoneThread = thread;
						thread.start();
					}
				});
			}});

			add(new JButton("Apply Values") {{
				addMouseListener(new MouseInputAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						autoBone.applyConfig();

						// Update GUI values after applying
						skeletonConfig.refreshAll();
					}
				});
			}});
		}});

		pane.add(new EJBox(BoxLayout.LINE_AXIS) {{
			add(processLabel = new JLabel("Processing has not been started..."));
		}});

		pane.add(new EJBox(BoxLayout.LINE_AXIS) {{
			add(lengthsLabel = new JLabel(getLengthsString()));
		}});

		// Pack and display
		pack();
		setLocationRelativeTo(null);
		setVisible(false);
	}
}
