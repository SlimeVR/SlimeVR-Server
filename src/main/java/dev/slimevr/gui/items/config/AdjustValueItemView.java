package dev.slimevr.gui.items.config;

import io.eiren.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class AdjustValueItemView extends HBox implements Initializable {

	public interface AdjustValueItemListener {
		void change(float diff);
		void reset();
	}

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

	private ResourceBundle bundle;

	private String title;
	private float value;
	private float adjAmount;

	private AdjustValueItemListener eventListener;

	public AdjustValueItemView(String title, float value, float adjAmount, AdjustValueItemListener eventListener) {
		this.title = title;
		this.value = value;
		this.adjAmount = adjAmount;

		this.eventListener = eventListener;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cells/config/adjustValueItemView.fxml"));
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

		itemTitle.setText(title);
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
				change(adjAmount);
			}
		});

		itemMinusButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				change(-adjAmount);
			}
		});
	}

	private void change(float diff) {
		eventListener.change(diff);
	}

	private void reset() {
		eventListener.reset();
	}

	private void setItemValueText(float value) {
		itemValue.setText(StringUtils.prettyNumber(value * 100, 0));
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
		setItemValueText(value);
	}
}
