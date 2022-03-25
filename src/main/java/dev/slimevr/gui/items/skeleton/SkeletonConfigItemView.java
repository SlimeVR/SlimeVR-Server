package dev.slimevr.gui.items.skeleton;

import io.eiren.util.StringUtils;
import io.eiren.util.logging.LogManager;
import dev.slimevr.VRServer;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SkeletonConfigItemView extends HBox  implements Initializable{

	@FXML
	private ResourceBundle resources;


	@FXML
	private Button itemMinusButton;

	@FXML
	private Button itemPlusButton;

	@FXML
	private Button itemResetButton;

	@FXML
	private Label itemValue;

	@FXML
	private Label itemTitle;

	private SkeletonConfigValue joint;
	private float value;
	private final VRServer server;
	private final SkeletonConfigItemListener itemListener;
	private ResourceBundle bundle;


	public SkeletonConfigItemView(VRServer server, SkeletonConfigValue joint, SkeletonConfigItemListener itemListener) {

		this.server = server;
		this.itemListener = itemListener;
		this.joint = joint;
		this.value = server.humanPoseProcessor.getSkeletonConfig(joint);

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cells/skeleton/skeletonConfigItemView.fxml"));
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
		LogManager.log.debug("initialize "+joint+" "+bundle.getString(joint.configKey)+" "+value);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		LogManager.log.debug("initialize "+joint+" "+bundle.getString(joint.configKey)+" "+value);
		itemTitle.setText(bundle.getString(joint.configKey));
		setItemValueText(value);
		itemResetButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				reset();
			}
		});

		itemPlusButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				change(0.01f);
			}
		});
		itemMinusButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				change(-0.01f);
			}
		});
	}

	private void change(float diff) {
		itemListener.change(joint, diff);
	}

	private void reset() {
		itemListener.reset(joint);
	}


	private void setItemValueText(float value) {
		itemValue.setText(StringUtils.prettyNumber(value * 100, 0));
	}


	public void refreshJoint(float value) {
		this.value = value;
		setItemValueText(value);
	}




	public interface SkeletonConfigItemListener {
		void change(SkeletonConfigValue joint, float diff);

		void reset(SkeletonConfigValue joint);
	}

	public SkeletonConfigValue getJoint() {
		return joint;
	}

	public float getValue() {
		return value;
	}
}
