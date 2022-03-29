package dev.slimevr.gui.tabs;

import dev.slimevr.gui.dialogs.AutoConfigurationDialog;
import dev.slimevr.gui.views.SkeletonConfigView;
import dev.slimevr.gui.views.SkeletonDataListView;
import dev.slimevr.Main;
import dev.slimevr.VRServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class BodyTabController {

	@FXML
	VBox bodyItems;
	@FXML
	VBox skeletonData;

	private VRServer server;
	SkeletonConfigView skeletonConfigView;
	SkeletonDataListView skeletonDataListView;

	public BodyTabController() {
		this.server = Main.vrServer;
	}

	@FXML
	public void initialize() {
		initSkeletonConfigList();
		initSkeletonDataList();
	}

	private void initSkeletonConfigList() {
		skeletonConfigView = new SkeletonConfigView(server);
		bodyItems.getChildren().add(skeletonConfigView);
	}

	private void initSkeletonDataList() {
		skeletonDataListView = new SkeletonDataListView(server);
		skeletonData.getChildren().add(skeletonDataListView);
	}

	@FXML
	public void openAutoConfigurationPressed(ActionEvent actionEvent) {
		openAutoConfiguration();
	}

	private void openAutoConfiguration() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dialogs/autoConfigurationDialog.fxml"));
		Stage stage = new Stage();
		//AutoConfigurationDialog autoConfigurationDialog = new AutoConfigurationDialog(server,stage);
		//fxmlLoader.setController(autoConfigurationDialog);
		//fxmlLoader.setController(autoConfigurationDialog);
		fxmlLoader.setResources(ResourceBundle.getBundle("localization_files/LangBundle", new Locale("en", "EN")));

		Scene scene = null;
		try {
			scene = new Scene(fxmlLoader.load());
			AutoConfigurationDialog controller = fxmlLoader.getController();
			controller.init(server, stage, this::updateSkeletonConfig);
			stage.setScene(scene);
			stage.setTitle("Skeleton Auto-Configuration");
			stage.setResizable(false);
			stage.centerOnScreen();
			// stage.initStyle(StageStyle.UNDECORATED);

			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		stage.setTitle("AutoBoneWindow");
		stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon256.png"))));
		stage.setScene(scene);
		stage.setResizable(false);
		stage.centerOnScreen();
		stage.show();
		*/
	}

	private void updateSkeletonConfig() {
		skeletonConfigView.refreshAll();
	}
}
