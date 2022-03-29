package dev.slimevr.gui.items.skeleton;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import io.eiren.util.StringUtils;
import dev.slimevr.VRServer;
import dev.slimevr.vr.processor.TransformNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SkeletonDataItemView extends HBox  implements Initializable{

	TransformNode node;

	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];

	@FXML
	private ResourceBundle resources;

	@FXML
	private Label itemJointName;

	@FXML
	private Label itemPitch;

	@FXML
	private Label itemRoll;

	@FXML
	private Label itemXcoord;

	@FXML
	private Label itemYaw;

	@FXML
	private Label itemYcoord;

	@FXML
	private Label itemZcoord;

	private final VRServer server;
	private ResourceBundle bundle;

	public SkeletonDataItemView(VRServer server, TransformNode node) {
		this.server = server;
		this.node = node;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cells/skeleton/skeletonDataItemView.fxml"));
		fxmlLoader.setResources(ResourceBundle.getBundle("localization_files/LangBundle", new Locale("en", "EN")));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	public void initialize() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		itemJointName.setText(node.getName());
	}

	public void update() {
		//this.node = node;
		this.node.worldTransform.getTranslation(v);
		this.node.worldTransform.getRotation(q);
		q.toAngles(angles);
		updateUi();
	}

	private void updateUi() {
		itemXcoord.setText(StringUtils.prettyNumber(v.x, 2));
		itemYcoord.setText(StringUtils.prettyNumber(v.y, 2));
		itemZcoord.setText(StringUtils.prettyNumber(v.z, 2));
		itemPitch.setText(StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 0));
		itemYaw.setText(StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 0));
		itemRoll.setText(StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 0));
	}
}
