package com.editor.controller;

import com.jcraft.jsch.JSchException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import com.editor.Main;
import com.editor.util.SFTPClient;
import com.editor.util.ShowBox;
import com.editor.util.TranslationTextComponent;
import com.editor.util.json.IUnitJson;
import com.editor.util.json.Json;
import com.editor.util.json.TypeUnit;
import com.editor.util.json.UnitJson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigureConn {
    private static final File settingsFile = new File(Main.homeDir.getAbsolutePath() + File.separator + "connection.json");
    public static String savedHostname;
    public static int savedPort;
    public static String savedUsername;
    public static String savedPassword;
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
    @FXML
    private Label titleLabel;
    @FXML
    private Label hostNameLabel;
    @FXML
    private Label portLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label passwordLabel;

    public static void loadSettingsFromFile() {

        try {
            Json json = new Json(settingsFile);

            List<UnitJson> jsons = json.getUnitsValue();
            for (UnitJson unit : jsons) {
                switch (unit.getName()) {
                    case "hostname":
                        savedHostname = (String) unit.getValue();
                        break;
                    case "port":
                        savedPort = Integer.parseInt((String) unit.getValue());
                        break;
                    case "username":
                        savedUsername = (String) unit.getValue();
                        break;
                    case "password":
                        savedPassword = (String) unit.getValue();
                        break;
                }
            }

        } catch (Exception e) {
            settingsFile.delete();
            try {
                settingsFile.createNewFile();
            } catch (IOException ex) {
                ShowBox.showError(new TranslationTextComponent("error.configurecon.unabletocreatefile", settingsFile.getAbsolutePath()));
            }
        }
    }

    private void setText() {
        saveButton.setText(new TranslationTextComponent("button.save").toString());
        checkButton.setText(new TranslationTextComponent("button.check.connection").toString());
        hostName.setPromptText(new TranslationTextComponent("hostname").toString());
        port.setPromptText(new TranslationTextComponent("port").toString());
        userName.setPromptText(new TranslationTextComponent("username").toString());
        password.setPromptText(new TranslationTextComponent("password").toString());
        titleLabel.setText(new TranslationTextComponent("configure").toString());
        hostNameLabel.setText(new TranslationTextComponent("hostname").toString());
        portLabel.setText(new TranslationTextComponent("port").toString());
        userNameLabel.setText(new TranslationTextComponent("username").toString());
        passwordLabel.setText(new TranslationTextComponent("password").toString());
    }

    public void initialize() {
        setText();
        saveButton.setOnAction(this::eventClickOnSave);
        checkButton.setOnAction(this::eventClickOnCheck);
        port.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                port.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        if (!settingsFile.exists()) {
            try {
                if (settingsFile.createNewFile()) {
                    System.out.println("Создан новый файл коннекта");
                }
            } catch (IOException e) {
                ShowBox.showError(new TranslationTextComponent("error.configurecon.unabletocreatefile", settingsFile.getAbsolutePath()));
                return;
            }
        }
        if (savedUsername != null) {
            port.setText(String.valueOf(savedPort));
            password.setText(savedPassword);
            userName.setText(savedUsername);
            hostName.setText(savedHostname);
        }
    }

    private void saveSettingsToFile() {

        List<UnitJson> json = new ArrayList<UnitJson>() {
            {
                add(new UnitJson("hostname", savedHostname, IUnitJson.TypeValue.STRING));
                add(new UnitJson("port", savedPort, IUnitJson.TypeValue.STRING));
                add(new UnitJson("username", savedUsername, IUnitJson.TypeValue.STRING));
                add(new UnitJson("password", savedPassword, IUnitJson.TypeValue.STRING));
            }
        };
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile))) {
            writer.append(new Json(json, TypeUnit.UNIT).toString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eventClickOnCheck(ActionEvent event) {
        if (!checkInputCorrect()) return;

        SFTPClient client = new SFTPClient(hostName.getText(), Integer.parseInt(port.getText()), userName.getText(), password.getText());
        try {
            client.connect();
            ShowBox.showInfo(new TranslationTextComponent("success"), new TranslationTextComponent("connection.success"));
        } catch (JSchException e) {
            ShowBox.showInfo(new TranslationTextComponent("unsuccess"), new TranslationTextComponent("connection.unsuccessful"));
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
        saveSettingsToFile();
    }


    private boolean checkInputCorrect() {
        if (hostName.getText().isEmpty()) {
            ShowBox.showError(new TranslationTextComponent("error.input.notentered", new TranslationTextComponent("hostname")));
            return false;
        }
        if (port.getText().isEmpty()) {
            ShowBox.showError(new TranslationTextComponent("error.input.notentered", new TranslationTextComponent("port")));
            return false;
        }
        if (userName.getText().isEmpty()) {
            ShowBox.showError(new TranslationTextComponent("error.input.notentered", new TranslationTextComponent("username")));
            return false;
        }
        if (password.getText().isEmpty()) {
            ShowBox.showError(new TranslationTextComponent("error.input.notentered", new TranslationTextComponent("password")));
            return false;
        }


        return true;
    }
}
