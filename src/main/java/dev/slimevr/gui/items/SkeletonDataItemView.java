package dev.slimevr.gui.items;

import io.eiren.util.StringUtils;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.VRServer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SkeletonDataItemView extends HBox  implements Initializable{

	@FXML
	private ResourceBundle resources;


	@FXML
	private Text itemJointName;

	@FXML
	private Text itemPitch;

	@FXML
	private Text itemRoll;

	@FXML
	private Text itemXcoord;

	@FXML
	private Text itemYaw;

	@FXML
	private Text itemYcoord;

	@FXML
	private Text itemZcoord;

	private final VRServer server;
	private ResourceBundle bundle;


	public SkeletonDataItemView(VRServer server) {

		this.server = server;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cells/skeletonDataItemView.fxml"));
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

	}

}
