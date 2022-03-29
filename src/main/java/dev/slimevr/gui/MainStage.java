package dev.slimevr.gui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import dev.slimevr.Main;
import dev.slimevr.gui.util.GUIUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainStage extends Application {

	public FXTrayIcon icon;

	@Override
	public void start(Stage stage) throws IOException {
		// Set up the stage
		stage.setTitle("SlimeVR Server (" + Main.VERSION + ")");

		ObservableList<Image> icons = stage.getIcons();
		icons.add(GUIUtils.getImage("/icon16.png", false));
		icons.add(GUIUtils.getImage("/icon32.png", false));
		icons.add(GUIUtils.getImage("/icon48.png", false));
		icons.add(GUIUtils.getImage("/icon64.png", false));
		icons.add(GUIUtils.getImage("/icon128.png", false));
		icons.add(GUIUtils.getImage("/icon256.png", false));

		// Set up the tray icon
		//if (FXTrayIcon.isSupported()) {
		icon = new FXTrayIcon(stage, getClass().getResource("/icon256.png"));
		icon.show();

		// Set up the scene
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
		MainStageController controller = new MainStageController(stage, icon);

		fxmlLoader.setController(controller);
		fxmlLoader.setResources(ResourceBundle.getBundle("localization_files/LangBundle", new Locale("en", "EN")));

		Scene scene = new Scene(fxmlLoader.load());
		stage.setScene(scene);
		stage.setResizable(true);
		stage.centerOnScreen();

		// stage.initStyle(StageStyle.UNDECORATED);

		// TODO: Maybe temporary? Needs an option to chose whether to minimize to tray or exit
		stage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});

		stage.show();
	}
}
