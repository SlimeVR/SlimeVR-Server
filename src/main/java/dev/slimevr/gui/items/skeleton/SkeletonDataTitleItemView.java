package dev.slimevr.gui.items.skeleton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class SkeletonDataTitleItemView extends HBox  implements Initializable{

	@FXML
	private ResourceBundle resources;

	private ResourceBundle bundle;

	public SkeletonDataTitleItemView() {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cells/skeleton/skeletonDataTitleItemView.fxml"));
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
