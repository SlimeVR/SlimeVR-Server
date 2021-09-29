package io.eiren.newGui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import io.eiren.gui.SkeletonList;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static io.eiren.util.Aligner.FROM_RIGHT;
import static io.eiren.util.Aligner.align;

public class MainStageController implements Initializable {

	private Stage stage;
	//required to trigger tray notifications
	private FXTrayIcon icon;

	private final List<TransformNode> nodes = new FastList<>();
	private VRServer server = Main.vrServer;

	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];

	@FXML
	public MenuBar fxMenuBar;
	@FXML
	public Button closeBTN;
	@FXML
	public Button trayBTN;
	@FXML
	public Button skeletonBTN;
	@FXML
	public Button bodyBTN;
	@FXML
	public AnchorPane bodyPane;
	@FXML
	public Button wifiBTN;


	@FXML
	public TextFlow jointTextFlow;
	@FXML
	public TextFlow xTextFlow;
	@FXML
	public TextFlow yTextFlow;
	@FXML
	public TextFlow zTextFlow;
	@FXML
	public TextFlow pitchTextFlow;
	@FXML
	public TextFlow yawTextFlow;
	@FXML
	public TextFlow rollTextFlow;


	public int i = 0;



	private double xOffset = 0;
	private double yOffset = 0;

	public MainStageController(Stage stage, FXTrayIcon icon) {
		this.stage = stage;
		this.icon = icon;

		server.addSkeletonUpdatedCallback(this::skeletonUpdated);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		fxMenuBar.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
			xOffset = e.getSceneX();
			yOffset = e.getSceneY();
		});

		fxMenuBar.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			stage.setX(event.getScreenX() - xOffset);
			stage.setY(event.getScreenY() - yOffset);
		});

		fxMenuBar.prefWidthProperty().bind(stage.widthProperty());

		closeBTN.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			event.consume();
			closeProgram();
		});

		trayBTN.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			//stage.setIconified(true);
			stage.hide();
			//icon.showMessage("Slime VR", "Application minimized to tray");
		});

		skeletonBTN.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			bodyPane.setVisible(true);
		});

		bodyBTN.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
			bodyPane.setVisible(false);
		});

		wifiBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			new WiFiWindow();
		});




		//final Separator separator = new Separator(Orientation.HORIZONTAL);
		//separator.prefWidthProperty().bind(skeletonTextFlow.widthProperty());
		//separator.setStyle("-fx-background-color: red;");

		jointTextFlow.setLineSpacing(10.0f);
		xTextFlow.setLineSpacing(10.0f);
		yTextFlow.setLineSpacing(10.0f);
		zTextFlow.setLineSpacing(10.0f);
		pitchTextFlow.setLineSpacing(10.0f);
		yawTextFlow.setLineSpacing(10.0f);
		rollTextFlow.setLineSpacing(10.0f);


		jointTextFlow.setTextAlignment(TextAlignment.CENTER);
		xTextFlow.setTextAlignment(TextAlignment.CENTER);
		yTextFlow.setTextAlignment(TextAlignment.CENTER);
		zTextFlow.setTextAlignment(TextAlignment.CENTER);
		pitchTextFlow.setTextAlignment(TextAlignment.CENTER);
		yawTextFlow.setTextAlignment(TextAlignment.CENTER);
		rollTextFlow.setTextAlignment(TextAlignment.CENTER);

		//skeletonTextFlow.setPadding(new Insets(0, 50, 0, 0));




		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
			i++;
			jointTextFlow.getChildren().clear();
			xTextFlow.getChildren().clear();
			yTextFlow.getChildren().clear();
			zTextFlow.getChildren().clear();
			pitchTextFlow.getChildren().clear();
			yawTextFlow.getChildren().clear();
			rollTextFlow.getChildren().clear();

			//skeletonTextFlow.getChildren().add(text1);
			//skeletonTextFlow.getChildren().add(new Text(System.lineSeparator()));
			//skeletonTextFlow.getChildren().add(text2);
			//skeletonTextFlow.getChildren().add(new Text(System.lineSeparator()));
			//skeletonTextFlow.getChildren().add(text3);

			//skeletonTextFlow.getChildren().addAll(new Text("skeletondata "), new Text(String.valueOf(i)), new Text(" test"), new Text(System.lineSeparator()));

			//skeletonTextFlow.getChildren().add(new Text("skeletondata "));
			//skeletonTextFlow.getChildren().add(new Text(align(String.valueOf(i), ' ', 10, FROM_RIGHT)));
			//skeletonTextFlow.getChildren().add(new Text(" test"));
			//skeletonTextFlow.getChildren().add(new Text(System.lineSeparator()));

			//skeletonTextFlow.getChildren().add(new Text("skeletondata " + i));
			//skeletonTextFlow.getChildren().add(new Text(System.lineSeparator()));

			//skeletonTextFlow.getChildren().add(new Text("skeletondata " + i));
			//skeletonTextFlow.getChildren().add(new Text(System.lineSeparator()));


			for (TransformNode n : nodes) {
				update(n);
			}


		}));

		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();


	}

	@ThreadSafe
	public void skeletonUpdated(HumanSkeleton newSkeleton) {
			newSkeleton.getRootNode().depthFirstTraversal((node) -> {
				nodes.add(node);
			});
	}

	public void update(TransformNode n) {

		Text name = new Text(n.getName());
		Text x = new Text();
		Text y = new Text();
		Text z = new Text();
		Text a1 = new Text();
		Text a2 = new Text();
		Text a3 = new Text();
		Text separator = new Text(System.lineSeparator());

		n.worldTransform.getTranslation(v);
		n.worldTransform.getRotation(q);
		q.toAngles(angles);

		x.setText(" " + StringUtils.prettyNumber(v.x, 2) + " ");
		y.setText(StringUtils.prettyNumber(v.y, 2) + " ");
		z.setText(StringUtils.prettyNumber(v.z, 2) + " ");
		a1.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0) + " ");
		a2.setText(StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0) + " ");
		a3.setText(StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));


		jointTextFlow.setLineSpacing(10.0f);
		xTextFlow.setLineSpacing(10.0f);
		yTextFlow.setLineSpacing(10.0f);
		zTextFlow.setLineSpacing(10.0f);
		pitchTextFlow.setLineSpacing(10.0f);
		yawTextFlow.setLineSpacing(10.0f);
		rollTextFlow.setLineSpacing(10.0f);


		jointTextFlow.getChildren().addAll(name, new Text(System.lineSeparator()));
		xTextFlow.getChildren().addAll(x, new Text(System.lineSeparator()));
		yTextFlow.getChildren().addAll(y, new Text(System.lineSeparator()));
		zTextFlow.getChildren().addAll(z, new Text(System.lineSeparator()));
		pitchTextFlow.getChildren().addAll(a1, new Text(System.lineSeparator()));
		yawTextFlow.getChildren().addAll(a2, new Text(System.lineSeparator()));
		rollTextFlow.getChildren().addAll(a3, new Text(System.lineSeparator()));

	}

	public void closeProgram() {
		if (ConfirmBox.display("Confirm Exit", "Are you sure you want to exit?")) {
			// TODO add save settings
			Platform.exit();
			System.exit(0);
		}
	}

}
