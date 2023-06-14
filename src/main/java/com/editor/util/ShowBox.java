package com.editor.util;

import javafx.scene.control.Alert;

public class ShowBox {
    /*public static boolean showQuestion(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setContentText(textInfo);
        //Код ниже позволяет тебе определять, какую кнопку нажал юзер во время ошибки. (Ок или не ок в случае ниже)
        alert.showAndWait() //Вызываем окно
                .filter(response -> response == ButtonType.OK) //Проверяем, какая кнопка была нажата
                .ifPresent(response -> System.out.println("Нажата кнопка ОК")); //Если ок, то выводим в консоль текст "Нажата кнопка ОК"
    }*/

    public static void showInfo(TranslationTextComponent title, TranslationTextComponent textInfo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title.toString());
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


}
