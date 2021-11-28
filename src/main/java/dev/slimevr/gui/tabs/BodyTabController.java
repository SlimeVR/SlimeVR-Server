package dev.slimevr.gui.tabs;

import dev.slimevr.gui.views.SkeletonConfigView;
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
	}

	private void initSkeletonConfigList() {
		SkeletonConfigView skeletonConfigView = new SkeletonConfigView(server);
		bodyItems.getChildren().add(skeletonConfigView);
	}
}
