package JSON_Editor.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ShowBox {
    public static void ShowError(String textErr) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Произошла ошибка");
        alert.setContentText(textErr);
        //Код ниже позволяет тебе определять, какую кнопку нажал юзер во время ошибки. (Ок или не ок в случае ниже)
        alert.showAndWait() //Вызываем окно
                .filter(response -> response == ButtonType.OK) //Проверяем, какая кнопка была нажата
                .ifPresent(response -> System.out.println("Нажата кнопка ОК")); //Если ок, то выводим в консоль текст "Нажата кнопка ОК"
    }


}
