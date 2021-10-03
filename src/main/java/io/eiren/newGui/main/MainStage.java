package io.eiren.newGui.main;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import io.eiren.vr.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

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
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("SlimeVR Server (" + Main.VERSION + ")");
		stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icon256.png"))));
        stage.setScene(scene);
		stage.setResizable(false);
		stage.centerOnScreen();
		stage.initStyle(StageStyle.UNDECORATED);

        stage.show();
    }

}
