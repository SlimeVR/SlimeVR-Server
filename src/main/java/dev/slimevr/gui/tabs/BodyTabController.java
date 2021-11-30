package dev.slimevr.gui.tabs;

import dev.slimevr.gui.views.SkeletonConfigView;
import dev.slimevr.gui.views.SkeletonDataListView;
import io.eiren.vr.Main;
import io.eiren.vr.VRServer;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class BodyTabController {

	@FXML
	VBox bodyItems;
	@FXML
	VBox skeletonData;

	private VRServer server;

	public BodyTabController() {
		this.server = Main.vrServer;
	}

	@FXML
	public void initialize() {
		initSkeletonConfigList();
		initSkeletonDataList();
	}



	private void initSkeletonConfigList() {
		SkeletonConfigView skeletonConfigView = new SkeletonConfigView(server);
		bodyItems.getChildren().add(skeletonConfigView);
	}

	private void initSkeletonDataList() {
		SkeletonDataListView skeletonDataListView = new SkeletonDataListView(server);
		skeletonData.getChildren().add(skeletonDataListView);
	}
}
