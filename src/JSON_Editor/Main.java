import JSON_Editor.manager.Dirs;
import config.FilesCfg;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class Main extends Application {

    public final static File homeDir = new Dirs(FilesCfg.HOME_DIR).getWorkDir();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(
                        Main.class.getResource("fxml/home.fxml")
                )
        );
        root.getStylesheets().add(
                Objects.requireNonNull(
                        Main.class.getResource(
                                "css/main.css"
                        )
                ).toExternalForm()
        );
        primaryStage.setTitle("JSOM Editor");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
