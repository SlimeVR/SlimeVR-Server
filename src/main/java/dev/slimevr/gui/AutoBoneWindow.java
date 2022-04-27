package dev.slimevr.gui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

import io.eiren.util.StringUtils;
import io.eiren.util.ann.AWTThread;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;

import javax.swing.event.MouseInputAdapter;

import org.apache.commons.lang3.tuple.Pair;

import dev.slimevr.VRServer;
import dev.slimevr.autobone.AutoBone;
import dev.slimevr.gui.swing.EJBox;
import dev.slimevr.poserecorder.PoseFrames;
import dev.slimevr.poserecorder.PoseFrameIO;
import dev.slimevr.poserecorder.PoseRecorder;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;

public class AutoBoneWindow extends JFrame {

	private EJBox pane;
	
	private final transient VRServer server;
	private final transient SkeletonConfigGUI skeletonConfig;
	private final transient PoseRecorder poseRecorder;
	private final transient AutoBone autoBone;
	
	private transient Thread recordingThread = null;
	private transient Thread saveRecordingThread = null;
	private transient Thread autoBoneThread = null;
	
	private JButton saveRecordingButton;
	private JButton adjustButton;
	private JButton applyButton;
	
	private JLabel processLabel;
	private JLabel lengthsLabel;
	
	public AutoBoneWindow(VRServer server, SkeletonConfigGUI skeletonConfig) {
		super("Skeleton Auto-Configuration");
		
		this.server = server;
		this.skeletonConfig = skeletonConfig;
		this.poseRecorder = new PoseRecorder(server);
		this.autoBone = new AutoBone(server);
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		add(new JScrollPane(pane = new EJBox(BoxLayout.PAGE_AXIS), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		build();
	}
	

	private float processFrames(PoseFrames frames) {
		return autoBone.processFrames(frames, autoBone.calcInitError, autoBone.targetHeight, (epoch) -> {
			processLabel.setText(epoch.toString());
			lengthsLabel.setText(autoBone.getLengthsString());
		});
	}
	
	@AWTThread
	private void build() {
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				setBorder(new EmptyBorder(i(5)));
				add(new JButton("Start Recording") {
					{
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								// Prevent running multiple times
								if(!isEnabled() || recordingThread != null) {
									return;
								}
								
								Thread thread = new Thread() {
									@Override
									public void run() {
										try {
											if(poseRecorder.isReadyToRecord()) {
												setText("Recording...");
												// 1000 samples at 20 ms per sample is 20 seconds
												int sampleCount = server.config.getInt("autobone.sampleCount", 1000);
												long sampleRate = server.config.getLong("autobone.sampleRateMs", 20L);
												Future<PoseFrames> framesFuture = poseRecorder.startFrameRecording(sampleCount, sampleRate);
												PoseFrames frames = framesFuture.get();
												LogManager.log.info("[AutoBone] Done recording!");
												
												saveRecordingButton.setEnabled(true);
												adjustButton.setEnabled(true);
												
												if(server.config.getBoolean("autobone.saveRecordings", false)) {
													setText("Saving...");
													autoBone.saveRecording(frames);
												}
											} else {
												setText("Not Ready...");
												LogManager.log.severe("[AutoBone] Unable to record...");
												Thread.sleep(3000); // Wait for 3 seconds
												return;
											}
										} catch(Exception e) {
											setText("Recording Failed...");
											LogManager.log.severe("[AutoBone] Failed recording!", e);
											try {
												Thread.sleep(3000); // Wait for 3 seconds
											} catch(Exception e1) {
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
					}
				});
				
				add(saveRecordingButton = new JButton("Save Recording") {
					{
						setEnabled(poseRecorder.hasRecording());
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								// Prevent running multiple times
								if(!isEnabled() || saveRecordingThread != null) {
									return;
								}
								
								Thread thread = new Thread() {
									@Override
									public void run() {
										try {
											Future<PoseFrames> framesFuture = poseRecorder.getFramesAsync();
											if(framesFuture != null) {
												setText("Waiting for Recording...");
												PoseFrames frames = framesFuture.get();
												
												if(frames.getTrackerCount() <= 0) {
													throw new IllegalStateException("Recording has no trackers");
												}
												
												if(frames.getMaxFrameCount() <= 0) {
													throw new IllegalStateException("Recording has no frames");
												}
												
												setText("Saving...");
												autoBone.saveRecording(frames);
												
												setText("Recording Saved!");
												try {
													Thread.sleep(3000); // Wait for 3 seconds
												} catch(Exception e1) {
													// Ignore
												}
											} else {
												setText("No Recording...");
												LogManager.log.severe("[AutoBone] Unable to save, no recording was done...");
												try {
													Thread.sleep(3000); // Wait for 3 seconds
												} catch(Exception e1) {
													// Ignore
												}
												return;
											}
										} catch(Exception e) {
											setText("Saving Failed...");
											LogManager.log.severe("[AutoBone] Failed to save recording!", e);
											try {
												Thread.sleep(3000); // Wait for 3 seconds
											} catch(Exception e1) {
												// Ignore
											}
										} finally {
											setText("Save Recording");
											saveRecordingThread = null;
										}
									}
								};
								
								saveRecordingThread = thread;
								thread.start();
							}
						});
					}
				});
				
				add(adjustButton = new JButton("Auto-Adjust") {
					{
						// If there are files to load, enable the button
						setEnabled(poseRecorder.hasRecording() || (AutoBone.getLoadDir().isDirectory() && AutoBone.getLoadDir().list().length > 0));
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								// Prevent running multiple times
								if(!isEnabled() || autoBoneThread != null) {
									return;
								}
								
								Thread thread = new Thread() {
									@Override
									public void run() {
										try {
											setText("Load...");
											List<Pair<String, PoseFrames>> frameRecordings = autoBone.loadRecordings();
											
											if(!frameRecordings.isEmpty()) {
												LogManager.log.info("[AutoBone] Done loading frames!");
											} else {
												Future<PoseFrames> framesFuture = poseRecorder.getFramesAsync();
												if(framesFuture != null) {
													setText("Waiting for Recording...");
													PoseFrames frames = framesFuture.get();
													
													if(frames.getTrackerCount() <= 0) {
														throw new IllegalStateException("Recording has no trackers");
													}
													
													if(frames.getMaxFrameCount() <= 0) {
														throw new IllegalStateException("Recording has no frames");
													}
													
													frameRecordings.add(Pair.of("<Recording>", frames));
												} else {
													setText("No Recordings...");
													LogManager.log.severe("[AutoBone] No recordings found in \"" + AutoBone.getLoadDir().getPath() + "\" and no recording was done...");
													try {
														Thread.sleep(3000); // Wait for 3 seconds
													} catch(Exception e1) {
														// Ignore
													}
													return;
												}
											}
											
											setText("Processing...");
											LogManager.log.info("[AutoBone] Processing frames...");
											FastList<Float> heightPercentError = new FastList<Float>(frameRecordings.size());
											for(Pair<String, PoseFrames> recording : frameRecordings) {
												LogManager.log.info("[AutoBone] Processing frames from \"" + recording.getKey() + "\"...");
												
												heightPercentError.add(processFrames(recording.getValue()));
												LogManager.log.info("[AutoBone] Done processing!");
												applyButton.setEnabled(true);
												
												//#region Stats/Values
												Float neckLength = autoBone.getConfig(SkeletonConfigValue.NECK);
												Float chestDistance = autoBone.getConfig(SkeletonConfigValue.CHEST);
												Float torsoLength = autoBone.getConfig(SkeletonConfigValue.TORSO);
												Float hipWidth = autoBone.getConfig(SkeletonConfigValue.HIPS_WIDTH);
												Float legsLength = autoBone.getConfig(SkeletonConfigValue.LEGS_LENGTH);
												Float kneeHeight = autoBone.getConfig(SkeletonConfigValue.KNEE_HEIGHT);
												
												float neckTorso = neckLength != null && torsoLength != null ? neckLength / torsoLength : 0f;
												float chestTorso = chestDistance != null && torsoLength != null ? chestDistance / torsoLength : 0f;
												float torsoWaist = hipWidth != null && torsoLength != null ? hipWidth / torsoLength : 0f;
												float legTorso = legsLength != null && torsoLength != null ? legsLength / torsoLength : 0f;
												float legBody = legsLength != null && torsoLength != null && neckLength != null ? legsLength / (torsoLength + neckLength) : 0f;
												float kneeLeg = kneeHeight != null && legsLength != null ? kneeHeight / legsLength : 0f;
												
												LogManager.log.info("[AutoBone] Ratios: [{Neck-Torso: " + StringUtils.prettyNumber(neckTorso) + "}, {Chest-Torso: " + StringUtils.prettyNumber(chestTorso) + "}, {Torso-Waist: " + StringUtils.prettyNumber(torsoWaist) + "}, {Leg-Torso: " + StringUtils.prettyNumber(legTorso) + "}, {Leg-Body: " + StringUtils.prettyNumber(legBody) + "}, {Knee-Leg: " + StringUtils.prettyNumber(kneeLeg) + "}]");
												
												String lengthsString = autoBone.getLengthsString();
												LogManager.log.info("[AutoBone] Length values: " + lengthsString);
												lengthsLabel.setText(lengthsString);
											}
											
											if(!heightPercentError.isEmpty()) {
												float mean = 0f;
												for(float val : heightPercentError) {
													mean += val;
												}
												mean /= heightPercentError.size();
												
												float std = 0f;
												for(float val : heightPercentError) {
													float stdVal = val - mean;
													std += stdVal * stdVal;
												}
												std = (float) Math.sqrt(std / heightPercentError.size());
												
												LogManager.log.info("[AutoBone] Average height error: " + StringUtils.prettyNumber(mean, 6) + " (SD " + StringUtils.prettyNumber(std, 6) + ")");
											}
											//#endregion
										} catch(Exception e) {
											setText("Failed...");
											LogManager.log.severe("[AutoBone] Failed adjustment!", e);
											try {
												Thread.sleep(3000); // Wait for 3 seconds
											} catch(Exception e1) {
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
					}
				});
				
				add(applyButton = new JButton("Apply Values") {
					{
						setEnabled(false);
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								if(!isEnabled()) {
									return;
								}

								autoBone.applyConfig();
								// Update GUI values after applying
								skeletonConfig.refreshAll();
							}
						});
					}
				});
			}
		});
		
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				setBorder(new EmptyBorder(i(5)));
				add(processLabel = new JLabel("Processing has not been started..."));
			}
		});
		
		pane.add(new EJBox(BoxLayout.LINE_AXIS) {
			{
				setBorder(new EmptyBorder(i(5)));
				add(lengthsLabel = new JLabel(autoBone.getLengthsString()));
			}
		});
		
		// Pack and display
		pack();
		setLocationRelativeTo(null);
		setVisible(false);
	}
}
