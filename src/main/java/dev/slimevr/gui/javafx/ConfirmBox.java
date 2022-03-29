package dev.slimevr.gui.javafx;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {

	static boolean answer;

	//TODO add checkbox for don't ask again
	public static boolean display(String title, String message) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle(title);
		stage.setMinWidth(250);
		Label label = new Label();
		label.setText(message);

		Button yes = new Button("Yes");
		Button no = new Button("No");

		yes.setOnAction(e -> {
			answer = true;
			stage.close();
		});

		no.setOnAction(e -> {
			answer = false;
			stage.close();
		});

		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, yes, no);
		layout.setAlignment(Pos.CENTER);
		stage.setScene(new Scene(layout));
		stage.showAndWait();

		return answer;
	}
}
