package dev.slimevr.gui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import dev.slimevr.gui.javafx.ConfirmBox;
import dev.slimevr.gui.tabs.BodyTabController;
import dev.slimevr.gui.tabs.LinksTabController;
import dev.slimevr.gui.tabs.MainTabController;
import dev.slimevr.gui.tabs.SettingsTabController;
import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;
import io.eiren.vr.Main;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.TransformNode;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainStageController implements Initializable {

	private Stage stage;
	private VRServer server;
	private FXTrayIcon icon; //required to trigger tray notifications
	private final List<TransformNode> nodes;
	private TrackersListNew trackersList;

	private AutoBoneWindow autoBone;

	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];


	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Tab bodyTab;

	@FXML
	private Button buttonBodyTab;

	@FXML
	private Button buttonLinksTab;

	@FXML
	private Button buttonMainTab;

	@FXML
	private Button buttonSettingsTab;

	@FXML
	private HBox buttonsBox;

	@FXML
	private Tab linksTab;

	@FXML
	private ImageView logoImage;

	@FXML
	private Label logoLabel;

	@FXML
	private AnchorPane mainPane;

	@FXML
	private Tab mainTab;


	@FXML
	private Tab settingsTab;

	@FXML
	private TabPane tabsPane;

	@FXML
	private MainTabController mainTabPageController;

	@FXML
	private BodyTabController bodyTabPageController;

	@FXML
	private SettingsTabController settingsTabController;
	@FXML
	private LinksTabController linksTabController;

	private BodyProportion bodyProportion;

	private int i = 0;
	private int n = 0;

	private double xOffset = 0;
	private double yOffset = 0;

	public MainStageController(Stage stage, FXTrayIcon icon) {
		this.stage = stage;
		this.server = Main.vrServer;
		this.icon = icon;
		this.nodes = new FastList<>();
		this.bodyProportion = new BodyProportion(server);

		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {


		steamVRTrackersSetup();

		bodyProportionInit();

		skeletonDataInit();

	}


	@FXML
	private void closeBtnAction(ActionEvent event) {
		closeProgram();
	}

	@FXML
	private void trayBtnAction(ActionEvent event) {
		//stage.setIconified(true);
		stage.hide();
		icon.showMessage("Slime VR", "Application minimized to tray");
	}

	@FXML
	public void buttonMainTabPressed(ActionEvent actionEvent) {
		changeTab(mainTab);
	}

	@FXML
	public void buttonBodyTabPressed(ActionEvent actionEvent) {
		changeTab(bodyTab);
	}

	@FXML
	public void buttonSettingsTabPressed(ActionEvent actionEvent) {
		changeTab(settingsTab);
	}

	@FXML
	public void buttonLinksTabPressed(ActionEvent actionEvent) {
		changeTab(linksTab);
	}

	private void changeTab(Tab tab)
	{
		tabsPane.getSelectionModel().select(tab);
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


	private void closeProgram() {
		if (ConfirmBox.display("Confirm Exit", "Are you sure you want to exit?")) {
			// TODO add save settings
			Platform.exit();
			System.exit(0);
		}
	}


}
