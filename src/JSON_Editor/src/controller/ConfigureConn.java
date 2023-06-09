package src.controller;

import com.jcraft.jsch.JSchException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import src.Main;
import util.SFTPClient;
import util.ShowBox;

import java.io.File;

public class ConfigureConn {
    private static String savedHostname;
    private static int savedPort;
    private static String savedUsername;
    private static String savedPassword;


    @FXML
    private AnchorPane configureConnScene;
    @FXML
    private Button saveButton;
    @FXML
    private Button checkButton;
    @FXML
    private TextField hostName;
    @FXML
    private TextField port;
    @FXML
    private TextField userName;
    @FXML
    private TextField password;

    private File SettingsFile = new File(Main.homeDir.getAbsolutePath());


    public void initialize() {
        saveButton.setOnAction(this::eventClickOnSave);
        checkButton.setOnAction(this::eventClickOnCheck);
        port.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                port.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        loadSettings();
        if (savedUsername != null){
            port.setText(String.valueOf(savedPort));
            password.setText(savedPassword);
            userName.setText(savedUsername);
            hostName.setText(savedHostname);
        }
    }

    private void loadSettings() {

    }

    private void eventClickOnCheck(ActionEvent event) {
        if (!checkInputCorrect()) return;

        SFTPClient client = new SFTPClient(hostName.getText(), Integer.parseInt(port.getText()), userName.getText(), password.getText());
        try {
            client.connect();
            ShowBox.showInfo("Успешно", "Подключение прошло успешно");
        } catch (JSchException e) {
            ShowBox.showInfo("Не успешно", "Подключение не удалось");
            e.printStackTrace();//FIXME УБЕРИ ЭТОТ ВЫВОД, ОН В ЦЕЛОМ НЕ НУЖЕН. ДЕБАГ КРЧ
        } finally {
            if (client.isConnected()) {
                client.disconnect();
            }
        }

    }

    public void eventClickOnSave(Event event) {
        if (!checkInputCorrect()) return;

        savedHostname = hostName.getText();
        savedPort = Integer.parseInt(port.getText());
        savedUsername = userName.getText();
        savedPassword = password.getText();
    }


    private boolean checkInputCorrect(){
        if (hostName.getText().isEmpty()) {
            ShowBox.showError("Вы не ввели имя хоста");
            return false;
        }
        if (port.getText().isEmpty()) {
            ShowBox.showError("Вы не ввели порт");
            return false;
        }
        if (userName.getText().isEmpty()) {
            ShowBox.showError("Вы не ввели имя пользователя");
            return false;
        }
        if (password.getText().isEmpty()) {
            ShowBox.showError("Вы не ввели пароль");
            return false;
        }


        return true;
    }
}
