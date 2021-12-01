package dev.slimevr.gui.tabs;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.gui.AutoBoneWindow;
import dev.slimevr.gui.BodyProportion;
import dev.slimevr.gui.views.TrackersListPane;
import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.Main;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.TransformNode;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.List;

public class MainTabController {


	@FXML
	public VBox trackersListContainer;

	private TrackersListPane trackersListPane = null;



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

		trackersListPane = new TrackersListPane(server);
		trackersListContainer.getChildren().add(trackersListPane);
		server.addOnTick(trackersListPane::updateTrackers);

		/*int currentRow = 0;
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
