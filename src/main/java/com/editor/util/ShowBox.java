package com.editor.util;

import javafx.scene.control.Alert;

public class ShowBox {

    public static void showInfo(TranslationTextComponent title, TranslationTextComponent textInfo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title.toString());
        alert.setHeaderText("showbox.info.headerText");
        alert.setContentText(textInfo.toString());
        alert.show();
    }

    public static void showError(TranslationTextComponent textErr) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(new TranslationTextComponent("showbox.error.title").toString());
        alert.setHeaderText("showbox.error.headerText");
        alert.setContentText(textErr.toString());
        alert.show();
    }

    public static void showError(String textErr) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(new TranslationTextComponent("showbox.error.title").toString());
        alert.setHeaderText("showbox.error.headerText");
        alert.setContentText(textErr.toString());
        alert.show();
    }

}
