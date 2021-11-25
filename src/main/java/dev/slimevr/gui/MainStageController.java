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
	private FXTrayIcon icon; //required to trigger tray notifications


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



	public MainStageController(Stage stage, FXTrayIcon icon) {
		this.stage = stage;
		this.icon = icon;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {



		initGui();

	}

	private void initGui() {

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






	private void closeProgram() {
		if (ConfirmBox.display("Confirm Exit", "Are you sure you want to exit?")) {
			// TODO add save settings
			Platform.exit();
			System.exit(0);
		}
	}


}
