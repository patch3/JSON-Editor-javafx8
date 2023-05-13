package JSON_Editor.controler;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ConfigureConn {
    @FXML
    private AnchorPane configureConnScene;
    @FXML
    private Button saveButton;


    public void initialize() {
        saveButton.setOnAction(this::eventClickOnSave);
    }

    public void eventClickOnSave(Event event) {
        ((Stage) saveButton.getScene().getWindow()).close();
    }
}
