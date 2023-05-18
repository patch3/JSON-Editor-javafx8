package JSON_Editor.controler;


import JSON_Editor.Main;
import JSON_Editor.util.json.Json;
import JSON_Editor.util.json.ValueUnitsJson;
import JSON_Editor.util.json.ArrayUnitJson;
import JSON_Editor.util.json.UnitJson;

import com.sun.istack.internal.Nullable;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.List;


public class Home {
    @FXML
    private AnchorPane scene1;
    @FXML
    private MenuItem createLocal, configureConn, openLocal;
    @FXML
    private TextArea textArea;
    @FXML
    public Pane panePlacement;



    public Json json;


    @Nullable
    private File workFile;
    //private static final Json gson = new Json();
    


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

        try {
            Json json = new Json(workFile);
            this.showJson(json);
        } catch (IOException e) {
            // TODO Auto-generated catch block
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


    public void showJson(Json json) {
        List<?> unitJson;
        if (json.getTypeValue() == ValueUnitsJson.TypeValue.ARRAY){
            unitJson = json.getArrayValue();
        } else if (json.getTypeValue() == ValueUnitsJson.TypeValue.UNITS) { 
            unitJson = json.getUnitsValue();
            int size = unitJson.size();
            for (int i = 0; i < size; i++) {
                Button bts = new Button();
                bts.setText("");
                panePlacement.getChildren().add(bts);
            }
        }

        
    }
}