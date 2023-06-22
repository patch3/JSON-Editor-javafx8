package com.editor;

import com.editor.config.FilesCfg;
import com.editor.controller.ConfigureConn;
import com.editor.manager.Dirs;
import com.editor.util.FileUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Main extends Application {
    public static final String FILENAME_REGEX = "^[^\\\\/:*?\"<>|]*$"; // допустимое имя файла
    public static final File homeDir = new Dirs(FilesCfg.HOME_DIR).getWorkDir();
    public static final File tempDir = new File(homeDir, "temp");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL url = this.getClass().getResource("/fxml/home.fxml");
        loader.setLocation(url);
        Parent root = loader.load();
        root.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("/css/main.css")).toExternalForm());
        primaryStage.setTitle("JSOM Editor");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinHeight(310);
        primaryStage.setMinWidth(400);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ico/ico.png"))));
        ConfigureConn.loadSettingsFromFile();
        primaryStage.setOnCloseRequest(this::onCloseRequest);
        primaryStage.show();
    }

    /**
     * При закрытии проложения
     */
    private void onCloseRequest(WindowEvent event) {
        FileUtils.deleteFolder(tempDir.getAbsolutePath());
        System.exit(0);
    }
}
