package dev.slimevr.gui.views;

import dev.slimevr.gui.items.SkeletonConfigItemView;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SkeletonConfigView extends VBox implements Initializable {


	private final VRServer server;
	private Map<String, SkeletonConfigItemView> configItems = new HashMap<>();

	public SkeletonConfigView(VRServer server) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"/gui/skeletonConfigView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		this.server = server;
		populateSkeletonItems();


		//server.humanPoseProcessor.addSkeletonUpdatedCallback(this::skeletonUpdated);
		//skeletonUpdated(null);
	}

	private void populateSkeletonItems() {
		SkeletonConfigItemView.SkeletonConfigItemListener skeletonConfigItemListener = subscribeToSkeletonItems();
		List<JointModel> jointModels = populateJoints();
		for (JointModel jointModel : jointModels) {
			addSkeletonItem(jointModel, skeletonConfigItemListener);
		}

	}

	private void addSkeletonItem(JointModel jointModel, SkeletonConfigItemView.SkeletonConfigItemListener skeletonConfigItemListener) {
		SkeletonConfigItemView skeletonConfigItemView = new SkeletonConfigItemView(server,jointModel.joint,jointModel.jointName,skeletonConfigItemListener);
		configItems.put(jointModel.jointName,skeletonConfigItemView);
		this.getChildren().add(skeletonConfigItemView);
	}

	private List<JointModel> populateJoints() {
		List<JointModel> jointModels = new ArrayList<>();
		jointModels.add(new JointModel("Torso", "Torso length"));
		jointModels.add(new JointModel("Chest", "Chest distance"));
		jointModels.add(new JointModel("Knee height", "Knee height"));
		jointModels.add(new JointModel("Foot length", "Foot length"));
		jointModels.add(new JointModel("Head", "Head offset"));
		jointModels.add(new JointModel("Neck", "Neck length"));
		jointModels.add(new JointModel("Hip offset", "Hip offset"));
		jointModels.add(new JointModel("Foot offset", "Foot offset"));

		return jointModels;
	}


	public void skeletonUpdated(HumanSkeleton newSkeleton) {
		//SkeletonConfigItemView.SkeletonConfigItemListener skeletonConfigItemListener = subscribeToSkeletonItems();
		/*this.getChildren().add(
				new SkeletonConfigItemView(server,"Torso","Torso length",skeletonConfigItemListener)
		);*/


	}

	private SkeletonConfigItemView.SkeletonConfigItemListener subscribeToSkeletonItems() {
		return new SkeletonConfigItemView.SkeletonConfigItemListener() {
			@Override
			public void change(String joint, float diff) {
				LogManager.log.debug("change "+joint+" "+diff);
			}

			@Override
			public void reset(String joint) {

				LogManager.log.debug("reset "+joint);
			}
		};
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}


	private class JointModel {
		private String joint;
		private String jointName;

		public JointModel(String joint, String jointName) {
			this.joint = joint;
			this.jointName = jointName;
		}

		public String getJoint() {
			return joint;
		}

		public String getJointName() {
			return jointName;
		}
	}

}
