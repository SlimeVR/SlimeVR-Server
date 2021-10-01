package io.eiren.newGui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import io.eiren.gui.WiFiWindow;
import io.eiren.util.StringUtils;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;
import io.eiren.vr.Main;
import io.eiren.vr.VRServer;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.processor.TransformNode;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainStageController implements Initializable {

	private Stage stage;
	private VRServer server = Main.vrServer;
	private FXTrayIcon icon; //required to trigger tray notifications
	private final List<TransformNode> nodes = new FastList<>();

	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];

	@FXML
	private MenuBar fxMenuBar;

	@FXML
	private Button reset;

	@FXML
	private AnchorPane skeletonPane;
	@FXML
	private Button skeletonBTN;
	@FXML
	private Button skeletonArrowBTN;
	@FXML
	private TextFlow jointTextFlow;
	@FXML
	private TextFlow xTextFlow;
	@FXML
	private TextFlow yTextFlow;
	@FXML
	private TextFlow zTextFlow;
	@FXML
	private TextFlow pitchTextFlow;
	@FXML
	private TextFlow yawTextFlow;
	@FXML
	private TextFlow rollTextFlow;

	private BodyProportion bodyProportion = new BodyProportion(server);
//	@FXML
//	private AnchorPane bodyPane;
	@FXML
	private TextFlow bodyNameTextFlow;
	@FXML
	private TextFlow bodyPlusTextFlow;
	@FXML
	private TextFlow bodyLableTextFlow;
	@FXML
	private TextFlow bodyMinusTextFlow;
	@FXML
	private TextFlow bodyResetTextFlow;

	private int i = 0;
	private int n = 0;

	private double xOffset = 0;
	private double yOffset = 0;

	public MainStageController(Stage stage, FXTrayIcon icon) {
		this.stage = stage;
		this.icon = icon;

		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		fxMenuBar.prefWidthProperty().bind(stage.widthProperty());
		fxMenuBar.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			xOffset = e.getSceneX();
			yOffset = e.getSceneY();
		});
		fxMenuBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			stage.setX(event.getScreenX() - xOffset);
			stage.setY(event.getScreenY() - yOffset);
		});

		skeletonDataInit();

		bodyProportion.bodyProportionInit(bodyNameTextFlow, bodyPlusTextFlow,
				bodyLableTextFlow, bodyMinusTextFlow, bodyResetTextFlow);
	}

	@FXML
	private void wifiBtnAction(ActionEvent event) {
		new WiFiWindow();
	}

	@FXML
	private void skeletonBtnAction(ActionEvent event) {
		skeletonPane.setVisible(false);
		skeletonPane.setEffect(null);
	}

	@FXML
	private void skeletonArrowBtnAction(ActionEvent event) {
		skeletonPane.setLayoutX(102);
		skeletonPane.setLayoutY(124);
		skeletonPane.setEffect(new DropShadow());
		skeletonArrowBTN.setVisible(false);
		skeletonBTN.setText("\uD83D\uDFAB");
		skeletonBTN.setPrefSize(31, 31);
		skeletonBTN.setStyle("-fx-font-size : 13.5px");
		skeletonBTN.setLayoutX(364);
		skeletonBTN.setLayoutY(14);
	}

	@FXML
	private void bodyBtnAction(ActionEvent event) {
		skeletonPane.setVisible(true);
		skeletonPane.setLayoutX(528);
		skeletonPane.setLayoutY(124);
		skeletonArrowBTN.setVisible(true);
		skeletonBTN.setText("Body");
		skeletonBTN.setPrefSize(85, 31);
		skeletonBTN.setLayoutX(12);
		skeletonBTN.setLayoutY(14);
		skeletonPane.setEffect(null);
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
	private void reset(ActionEvent event) {
		reset.setText(String.valueOf(3));
		n = 2;
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), timeLineEvent -> {
			if (n > 0) reset.setText(String.valueOf(n));
			if (n == 0){
				server.resetTrackers();
				reset.setText("Reset");
			}
			n--;
		}));
		timeline.setCycleCount(3);
		timeline.play();
	}

	@FXML
	private void fastReset(ActionEvent event) {
		server.resetTrackersYaw();
	}

	private void skeletonDataInit() {
		customizeTextFlow(jointTextFlow);
		customizeTextFlow(xTextFlow);
		customizeTextFlow(yTextFlow);
		customizeTextFlow(zTextFlow);
		customizeTextFlow(pitchTextFlow);
		customizeTextFlow(yawTextFlow);
		customizeTextFlow(rollTextFlow);

		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
			i++;
			jointTextFlow.getChildren().clear();
			xTextFlow.getChildren().clear();
			yTextFlow.getChildren().clear();
			zTextFlow.getChildren().clear();
			pitchTextFlow.getChildren().clear();
			yawTextFlow.getChildren().clear();
			rollTextFlow.getChildren().clear();

			for (TransformNode n : nodes) {
				updateSkeletonData(n);
			}
		}));

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
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

		jointTextFlow.getChildren().addAll(name, new Text(System.lineSeparator()));
		xTextFlow.getChildren().addAll(x, new Text(System.lineSeparator()));
		yTextFlow.getChildren().addAll(y, new Text(System.lineSeparator()));
		zTextFlow.getChildren().addAll(z, new Text(System.lineSeparator()));
		pitchTextFlow.getChildren().addAll(a1, new Text(System.lineSeparator()));
		yawTextFlow.getChildren().addAll(a2, new Text(System.lineSeparator()));
		rollTextFlow.getChildren().addAll(a3, new Text(System.lineSeparator()));
	}

	private void closeProgram() {
		if (ConfirmBox.display("Confirm Exit", "Are you sure you want to exit?")) {
			// TODO add save settings
			Platform.exit();
			System.exit(0);
		}
	}

}
