package dev.slimevr.gui.tabs;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import dev.slimevr.gui.dialogs.WifiTrackerConfigurationDialog;
import dev.slimevr.gui.views.TrackersListPane;
import io.eiren.util.logging.LogManager;
import dev.slimevr.Main;
import dev.slimevr.VRServer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainTabController {

	@FXML
	public TrackersListPane trackersListPane;
	public ComboBox<String> testbox;

	private Stage stage;
	private VRServer server;
	private FXTrayIcon icon; //required to trigger tray notifications

	public MainTabController() {
		this.server = Main.vrServer;
	}

	@FXML
	public void initialize() {
		initGui();
		steamVRTrackersSetup();
	}

	private void initGui() {
		LogManager.log.debug("init main tab");

		//trackersListPane = new TrackersListPane(server);
		trackersListPane.init(server);
		server.addOnTick(trackersListPane::updateTrackers);

		/*
		int currentRow = 0;
		int currentColumn = 0;
		List<String> testList = new ArrayList<String>();
		testList.add("akjsjdiajsd");
		testList.add("415410202");
		testList.add("41541541");
		testList.add("akjsjdiajsd");
		testList.add("415410202");
		testList.add("41541541");
		testList.add("akjsjdiajsd");
		testList.add("415410202");
		testList.add("41541541");
		testList.add("akjsjdiajsd");
		testList.add("415410202");
		testList.add("41541541");
		List<TrackerPanelCell> trackerPanelCells = new ArrayList<>();
		for (String item:testList) {
			if(currentColumn>1)
			{
				currentColumn = 0;
				currentRow++;
			}
			TrackerPanelCell trackerPanelCell = new TrackerPanelCell();
			trackerPanelCell.setInfo(item);
			trackerPanelCells.add(trackerPanelCell);
			gridPane.add(trackerPanelCell.getTrackerContainer(), currentColumn, currentRow);
			currentColumn++;
		}

		trackerPanelCells.get(2).setInfo("2165165");
		*/
	}

	@FXML
	void wifiClicked(ActionEvent event) {
		openWifiDialog();
	}

	private void openWifiDialog() {
		LogManager.log.debug("Opening WiFi Settings dialog");
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/dialogs/trackerConfigurationDialog.fxml"));
		Stage stage = new Stage();
		fxmlLoader.setResources(ResourceBundle.getBundle("localization_files/LangBundle", new Locale("en", "EN")));
		Scene scene = null;
		try {
			scene = new Scene(fxmlLoader.load());
			WifiTrackerConfigurationDialog controller = fxmlLoader.getController();
			controller.init(server,stage);
			stage.setScene(scene);
			stage.setTitle("WiFi Settings");
			stage.setResizable(false);
			stage.centerOnScreen();
			stage.show();
		} catch (IOException e) {
			LogManager.log.severe("Exception while opening WiFi Settings dialog", e);
		}
	}

	public void steamVRTrackersSetup() {
		/*steamVRComboBox.getItems().addAll("Waist", "Waist + Legs", "Waist + Legs + Chest", "Waist + Legs + Knees", "Waist + Legs + Chest + Knees");

		switch(server.config.getInt("virtualtrackers", 3)) {
		case 1:
			steamVRComboBox.getSelectionModel().select(0);
			break;
		case 3:
			steamVRComboBox.getSelectionModel().select(1);
			break;
		case 4:
			steamVRComboBox.getSelectionModel().select(2);
			break;
		case 5:
			steamVRComboBox.getSelectionModel().select(3);
			break;
		case 6:
			steamVRComboBox.getSelectionModel().select(4);
			break;
		}

		steamVRComboBox.setOnAction(e -> {
			switch(steamVRComboBox.getSelectionModel().getSelectedIndex()) {
			case 0:
				server.config.setProperty("virtualtrackers", 1);
				break;
			case 1:
				server.config.setProperty("virtualtrackers", 3);
				break;
			case 2:
				server.config.setProperty("virtualtrackers", 4);
				break;
			case 3:
				server.config.setProperty("virtualtrackers", 5);
				break;
			case 4:
				server.config.setProperty("virtualtrackers", 6);
				break;
			}
			server.saveConfig();
		});*/
	}
}
