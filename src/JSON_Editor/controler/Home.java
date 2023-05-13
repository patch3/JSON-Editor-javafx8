package JSON_Editor.controler;


import JSON_Editor.Main;
import com.sun.istack.internal.Nullable;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class Home {
    @FXML
    private AnchorPane scene1;
    @FXML
    private MenuItem createLocal, configureConn, openLocal;
    @FXML
    private TextArea textArea;
    @Nullable
    private File workFile;


    public void initialize() {
        createLocal.setOnAction(this::eventClickCreateLocal);
        openLocal.setOnAction(this::eventClickOpen);
        configureConn.setOnAction(this::eventClickConfigureConn);
    }

    private void eventClickCreateLocal(Event event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("");
        chooser.setInitialFileName("NewFile"); //Имя файла, которое по умолчанию устанавливается
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json", "*.json")); //Допустимые расширения файла
        workFile = chooser.showSaveDialog(createLocal.getParentPopup()); //Вызываем диалоговое окно
        if (workFile == null) return; //Если диалоговое окно было закрыто, то ретурнаем
        try (FileWriter write = new FileWriter(workFile)) {
            write.write("{\n\t\n}");
            write.flush();
            workFile.createNewFile(); //Создаем файл
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void eventClickOpen(Event event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("");
        chooser.setInitialFileName("NewFile"); //Имя файла, которое по умолчанию устанавливается
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json", "*.json")); //Допустимые расширения файла
        workFile = chooser.showOpenDialog(createLocal.getParentPopup()); //Вызываем диалоговое окно
        if (workFile == null) return;
        try (FileReader reader = new FileReader(workFile)) {
            textArea.clear();
            while (reader.ready()) {
                textArea.appendText(String.valueOf((char) reader.read()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eventClickConfigureConn(Event event) {
        Stage stage = new Stage();
        stage.setTitle("Configure");
        Parent root = null;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("fxml\\configure_conn.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (root == null) {
            return;
        }
        stage.setScene(new Scene(root));
        stage.show();
    }
}
