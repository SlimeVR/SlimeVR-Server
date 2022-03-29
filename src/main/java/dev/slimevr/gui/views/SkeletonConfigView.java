package dev.slimevr.gui.views;

import io.eiren.util.logging.LogManager;
import dev.slimevr.VRServer;
import dev.slimevr.gui.items.config.AdjustValueItemView;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.processor.skeleton.SkeletonConfig;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class SkeletonConfigView extends VBox implements Initializable {

	private ResourceBundle bundle;

	private final VRServer server;
	private Map<SkeletonConfigValue, AdjustValueItemView> configItems = new HashMap<>();

	public SkeletonConfigView(VRServer server) {
		/*
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/skeletonConfigView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		*/

		bundle = ResourceBundle.getBundle("localization_files/LangBundle", new Locale("en", "EN"));

		this.server = server;
		populateSkeletonItems();

		server.humanPoseProcessor.addSkeletonUpdatedCallback(this::skeletonUpdated);
		//skeletonUpdated(null);
	}

	private void populateSkeletonItems() {
		for (SkeletonConfigValue value : SkeletonConfigValue.values) {
			addSkeletonItem(value);
		}
	}

	private void addSkeletonItem(SkeletonConfigValue joint) {
		String title = bundle.getString(joint.configKey);
		float curValue = server.humanPoseProcessor.getSkeletonConfig(joint);
		AdjustValueItemView.AdjustValueItemListener adjustItemListener = subscribeToAdjustItem(joint);
		
		AdjustValueItemView skeletonConfigItemView = new AdjustValueItemView(title, curValue, 0.01f, adjustItemListener);

		configItems.put(joint, skeletonConfigItemView);
		this.getChildren().add(skeletonConfigItemView);
	}

	public void refreshAll() {
		configItems.forEach((joint, skeletonConfigItemView) ->
				skeletonConfigItemView.setValue(server.humanPoseProcessor.getSkeletonConfig(joint))
				);
	}

	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		SkeletonConfig skeletonConfig = newSkeleton.getSkeletonConfig();
		for (SkeletonConfigValue value : SkeletonConfigValue.values) {
			updateSkeletonConfigItem(value, skeletonConfig.getConfig(value));
		}
	}

	private AdjustValueItemView.AdjustValueItemListener subscribeToAdjustItem(SkeletonConfigValue joint) {
		return new AdjustValueItemView.AdjustValueItemListener() {
			@Override
			public void change(float diff) {
				LogManager.log.debug("change " + joint + " " + diff);
				changeJointValue(joint, diff);
			}

			@Override
			public void reset() {
				LogManager.log.debug("reset " + joint);
				resetJoint(joint);
			}
		};
	}

	private void resetJoint(SkeletonConfigValue joint) {
		server.humanPoseProcessor.resetSkeletonConfig(joint);
		server.saveConfig();
		float current = server.humanPoseProcessor.getSkeletonConfig(joint);
		updateSkeletonConfigItem(joint, current);
	}

	private void changeJointValue(SkeletonConfigValue joint, float diff) {
		float current = server.humanPoseProcessor.getSkeletonConfig(joint);
		float newVal = current + diff;

		server.humanPoseProcessor.setSkeletonConfig(joint, newVal);
		server.saveConfig();
		updateSkeletonConfigItem(joint, newVal);
	}


	private void updateSkeletonConfigItem(SkeletonConfigValue joint, float value) {
		configItems.get(joint).setValue(value);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
}
