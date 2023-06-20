package com.editor;

import com.editor.config.FilesCfg;
import com.editor.controller.ConfigureConn;
import com.editor.manager.Dirs;
import com.editor.util.FileUtils;
import com.editor.util.TranslationTextComponent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.util.Objects;

public class Main extends Application {
    public static final String FILENAME_REGEX = "^[^\\\\/:*?\"<>|]*$"; // допустимое имя файла
    public final static File homeDir = new Dirs(FilesCfg.HOME_DIR).getWorkDir();
    public final static File tempDir = new File(homeDir.getAbsolutePath() + File.separator + "temp");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        getClass().getResource("/fxml/home.fxml")
                )
        );
        root.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource(
                                "/css/main.css"
                        )
                ).toExternalForm()
        );
        primaryStage.setTitle("JSOM Editor");


        primaryStage.setScene(new Scene(root));

        primaryStage.setMinHeight(310);
        primaryStage.setMinWidth(400);

        ConfigureConn.loadSettingsFromFile();
        primaryStage.setOnCloseRequest(this::onCloseRequest);
        primaryStage.show();
    }

    /**
     * При закрытии проложения
     */
    private void onCloseRequest(WindowEvent event) {
        FileUtils.deleteFolder(tempDir.getAbsolutePath());
    }

}
