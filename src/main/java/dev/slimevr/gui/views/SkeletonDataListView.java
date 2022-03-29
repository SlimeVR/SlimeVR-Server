package dev.slimevr.gui.views;

import dev.slimevr.gui.items.skeleton.SkeletonDataItemView;
import dev.slimevr.gui.items.skeleton.SkeletonDataTitleItemView;
import dev.slimevr.VRServer;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.processor.TransformNode;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SkeletonDataListView extends VBox implements Initializable {

	private static final long UPDATE_DELAY = 50;
	private final VRServer server;
	private Map<String, SkeletonDataItemView> nodes = new HashMap<>();
	private long lastUpdate = 0;

	public SkeletonDataListView(VRServer server) {
		this.server = server;
		populateSkeletonItems();

		// server.addSkeletonUpdatedCallback(this::skeletonUpdated);
		server.addOnTick(this::updateBones);
	}

	private void updateBones() {
		if (lastUpdate + UPDATE_DELAY > System.currentTimeMillis())
			return;
		
		lastUpdate = System.currentTimeMillis();
		Platform.runLater(() -> {
			nodes.forEach((key, value) -> value.update());
		});
	}

	private void skeletonUpdated(HumanSkeleton humanSkeleton) {
	}

	private void populateSkeletonItems() {
		this.getChildren().add(new SkeletonDataTitleItemView());
		server.humanPoseProcessor.getSkeleton().getRootNode().depthFirstTraversal(this::addSkeletonDataItem);
	}

	private void addSkeletonDataItem(TransformNode node) {
		SkeletonDataItemView skeletonDataItemView = new SkeletonDataItemView(server, node);
		nodes.put(node.getName(), skeletonDataItemView);
		this.getChildren().add(skeletonDataItemView);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
}
