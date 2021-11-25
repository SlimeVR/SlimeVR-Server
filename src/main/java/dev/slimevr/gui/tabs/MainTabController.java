package dev.slimevr.gui.tabs;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.gui.AutoBoneWindow;
import dev.slimevr.gui.BodyProportion;
import dev.slimevr.gui.TrackersListPane;
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



	private Stage stage;
	private VRServer server;
	private FXTrayIcon icon; //required to trigger tray notifications
	private final List<TransformNode> nodes;

	private AutoBoneWindow autoBone;

	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];


	private BodyProportion bodyProportion;


	public MainTabController() {
		this.server = Main.vrServer;
		this.nodes = new FastList<>();
		this.bodyProportion = new BodyProportion(server);

		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}


	@FXML
	public void initialize() {

		initGui();

		steamVRTrackersSetup();

		bodyProportionInit();

		skeletonDataInit();
	}

	private void initGui() {
		LogManager.log.debug("init main tab");

		trackersListContainer.getChildren().add(new TrackersListPane(server));

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


	private void skeletonDataInit() {
		/*customizeTextFlow(jointTextFlow);
		customizeTextFlow(xTextFlow);
		customizeTextFlow(yTextFlow);
		customizeTextFlow(zTextFlow);
		customizeTextFlow(pitchTextFlow);
		customizeTextFlow(yawTextFlow);
		customizeTextFlow(rollTextFlow);

		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
			jointTextFlow.getChildren().clear();
			xTextFlow.getChildren().clear();
			yTextFlow.getChildren().clear();
			zTextFlow.getChildren().clear();
			pitchTextFlow.getChildren().clear();
			yawTextFlow.getChildren().clear();
			rollTextFlow.getChildren().clear();

			for(TransformNode n : nodes) {
				updateSkeletonData(n);
			}
		}));

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();*/
	}

	@ThreadSafe
	private void skeletonUpdated(HumanSkeleton newSkeleton) {
		newSkeleton.getRootNode().depthFirstTraversal((node) -> {
			nodes.add(node);
		});
	}


	private void customizeTextFlow(TextFlow t) {
		t.setTextAlignment(TextAlignment.CENTER);
		t.setLineSpacing(10.0f);
		//t.setPadding(new Insets(0, 50, 0, 0));
	}

	private void updateSkeletonData(TransformNode n) {

		Text name = new Text(n.getName());
		Text x = new Text();
		Text y = new Text();
		Text z = new Text();
		Text a1 = new Text();
		Text a2 = new Text();
		Text a3 = new Text();

		n.worldTransform.getTranslation(v);
		n.worldTransform.getRotation(q);
		q.toAngles(angles);

		x.setText(" " + StringUtils.prettyNumber(v.x, 2) + " ");
		y.setText(StringUtils.prettyNumber(v.y, 2) + " ");
		z.setText(StringUtils.prettyNumber(v.z, 2) + " ");
		a1.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0) + " ");
		a2.setText(StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0) + " ");
		a3.setText(StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));

		//final Separator separator = new Separator(Orientation.HORIZONTAL);
		//separator.prefWidthProperty().bind(skeletonTextFlow.widthProperty());
		//separator.setStyle("-fx-background-color: red;");

		/*jointTextFlow.getChildren().addAll(name, new Text(System.lineSeparator()));
		xTextFlow.getChildren().addAll(x, new Text(System.lineSeparator()));
		yTextFlow.getChildren().addAll(y, new Text(System.lineSeparator()));
		zTextFlow.getChildren().addAll(z, new Text(System.lineSeparator()));
		pitchTextFlow.getChildren().addAll(a1, new Text(System.lineSeparator()));
		yawTextFlow.getChildren().addAll(a2, new Text(System.lineSeparator()));
		rollTextFlow.getChildren().addAll(a3, new Text(System.lineSeparator()));*/
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

	public void bodyProportionInit() {
		/*bodyProportion.bodyProportionInit(bodyNameTextFlow, bodyPlusTextFlow, bodyLableTextFlow, bodyMinusTextFlow, bodyResetTextFlow);

		bodyResetAll.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			bodyResetAll.setText(String.valueOf(3));
			i = 2;
			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), timeLineEvent -> {
				if(i > 0)
					bodyResetAll.setText(String.valueOf(i));
				if(i == 0) {
					bodyProportion.reset("All");
					bodyResetAll.setText("Reset All");
				}
				i--;
			}));
			timeline.setCycleCount(3);
			timeline.play();
		});

		bodyAuto.setOnAction(event -> {
			autoBone = new AutoBoneWindow(server, bodyProportion);
			autoBone.setVisible(true);
			autoBone.toFront();
		});*/
	}
}
