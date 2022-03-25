package dev.slimevr.gui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import dev.slimevr.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainStage extends Application {

	public FXTrayIcon icon;

	@Override
	public void start(Stage stage) throws IOException {

		//if (FXTrayIcon.isSupported()) {
		icon = new FXTrayIcon(stage, getClass().getResource("/icon256.png"));
		icon.show();

		FXMLLoader fxmlLoader = new FXMLLoader(MainStage.class.getResource("/main.fxml"));
		MainStageController controller = new MainStageController(stage, icon);
		fxmlLoader.setController(controller);
		fxmlLoader.setResources(ResourceBundle.getBundle("localization_files/LangBundle", new Locale("en", "EN")));
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("SlimeVR Server (" + Main.VERSION + ")");
		stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon256.png"))));
		stage.setScene(scene);
		stage.setResizable(true);
		stage.centerOnScreen();
		// stage.initStyle(StageStyle.UNDECORATED);

		stage.show();
	}




}
