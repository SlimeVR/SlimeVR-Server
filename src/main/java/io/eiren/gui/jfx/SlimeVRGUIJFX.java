package io.eiren.gui.jfx;

import dev.slimevr.Main;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SlimeVRGUIJFX extends Application {
	
	private Stage currentStage;
	
	public SlimeVRGUIJFX() {
		
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		currentStage = stage;
		stage.setTitle("SlimeVR Server");
		stage.setOnCloseRequest((event) -> System.exit(0));
        
        VBox vBox = new VBox();
		
        HBox topRow = new HBox();
        vBox.getChildren().add(topRow);
        
        Label titleLabel = new Label("SlimeVR");
		topRow.getChildren().add(titleLabel);
        
        
		TabPane tabs = new TabPane();
		vBox.getChildren().add(tabs);

		TrackersList tl;
		Tab trackersTab = new Tab("Trackers", tl = new TrackersList(Main.vrServer, this));
		tabs.getTabs().add(trackersTab);
		
		Tab proportionsTab = new Tab("Body proportions", new SkeletonConfigGUI(Main.vrServer, this));
		tabs.getTabs().add(proportionsTab);

		stage.setScene(new Scene(vBox));
		stage.show();
        
        Main.vrServer.addOnTick(tl::updateTrackers);
	}
	
	public void refresh() {
		currentStage.sizeToScene();
	}
}
