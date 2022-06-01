package dev.slimevr.gui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseEvent;
import java.util.EnumMap;

import io.eiren.util.ann.AWTThread;

import javax.swing.event.MouseInputAdapter;

import dev.slimevr.VRServer;
import dev.slimevr.autobone.AutoBoneListener;
import dev.slimevr.autobone.AutoBoneProcessType;
import dev.slimevr.autobone.AutoBone.Epoch;
import dev.slimevr.gui.swing.EJBox;
import dev.slimevr.poserecorder.PoseFrames;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;


public class AutoBoneWindow extends JFrame implements AutoBoneListener {

	private EJBox pane;

	private final transient VRServer server;
	private final transient SkeletonConfigGUI skeletonConfig;

	private JButton saveRecordingButton;
	private JButton applyButton;

	private JLabel processLabel;
	private JLabel lengthsLabel;

	public AutoBoneWindow(VRServer server, SkeletonConfigGUI skeletonConfig) {
		super("Skeleton Auto-Configuration");

		this.server = server;
		this.skeletonConfig = skeletonConfig;

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		add(
			new JScrollPane(
				pane = new EJBox(BoxLayout.PAGE_AXIS),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
			)
		);

		server.getAutoBoneHandler().addListener(this);

		build();
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
								if (!isEnabled()) {
									return;
								}

								server.getAutoBoneHandler().startRecording();
							}
						});
					}
				});

				add(saveRecordingButton = new JButton("Save Recording") {
					{
						setEnabled(false);
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								if (!isEnabled()) {
									return;
								}

								server.getAutoBoneHandler().saveRecording();
							}
						});
					}
				});

				add(new JButton("Auto-Adjust") {
					{
						addMouseListener(new MouseInputAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								if (!isEnabled()) {
									return;
								}

								server.getAutoBoneHandler().processRecording();
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
								if (!isEnabled()) {
									return;
								}

								server.getAutoBoneHandler().applyValues();
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
				add(lengthsLabel = new JLabel("No config changes"));
			}
		});

		// Pack and display
		pack();
		setLocationRelativeTo(null);
		setVisible(false);
	}

	@Override
	public void onAutoBoneProcessStatus(
		AutoBoneProcessType processType,
		String message,
		long current,
		long total,
		boolean completed,
		boolean success
	) {
		if (message != null) {
			if (total == 0) {
				processLabel.setText(String.format("%s: %s", processType.name(), message));
			} else {
				processLabel
					.setText(
						String
							.format(
								"%s (%d/%d) [%.2f%%]: %s",
								processType.name(),
								current,
								total,
								(current / (double) total) * 100.0,
								message
							)
					);
			}
		} else {
			if (total != 0) {
				processLabel
					.setText(
						String
							.format(
								"%s (%d/%d) [%.2f%%]",
								processType.name(),
								current,
								total,
								(current / (double) total) * 100.0
							)
					);
			}
		}
	}

	@Override
	public void onAutoBoneRecordingEnd(PoseFrames recording) {
		saveRecordingButton.setEnabled(true);
	}

	@Override
	public void onAutoBoneEpoch(Epoch epoch) {
		processLabel
			.setText(
				String
					.format(
						"PROCESS: Epoch %d/%d (%.2f%%) Error: %.4f",
						epoch.epoch,
						epoch.totalEpochs,
						(epoch.epoch / (double) epoch.totalEpochs) * 100.0,
						epoch.epochError
					)
			);
		lengthsLabel.setText(server.getAutoBoneHandler().getLengthsString());
	}

	@Override
	public void onAutoBoneEnd(EnumMap<SkeletonConfigValue, Float> configValues) {
		applyButton.setEnabled(true);
	}
}
