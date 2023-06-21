package com.editor.controller;

import com.editor.util.TranslationTextComponent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class UsersGuide {
    @FXML
    private TextArea textArea;
    @FXML
    private Text titleLabel;
    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() {
        this.showText();
        this.cancelButton.setOnAction(event -> {
            Stage stage = (Stage) textArea.getScene().getWindow();
            stage.close();
        });
    }

    private void showText(){
        this.textArea.setText(new TranslationTextComponent("user_guide.main_text_area").toString());
        this.titleLabel.setText(new TranslationTextComponent("user_guide.title").toString());
        this.cancelButton.setText(new TranslationTextComponent("user_guide.cancel_button").toString());
    }
}
