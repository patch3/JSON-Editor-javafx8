package controler;


import com.sun.istack.internal.Nullable;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.jfr.internal.tool.Main;
import util.json.Json;

import java.io.File;
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
    @FXML
    public TreeView<String> treeView;

    public Json json;


    @Nullable
    private File workFile;
    //private static final Json gson = new Json();
    


    public void initialize() {
        treeView.setShowRoot(false);

        createLocal.setOnAction(this::eventClickCreateLocal);
        //openLocal.setOnAction(this::eventClickOpen);


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


    /*private void eventClickOpen(Event event) {
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
    }*/

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


    /*public void showJson(Json json) {
        TreeItem<String> rootItem = new TreeItem<>("Multi-Dimensional Array");
        rootItem.setExpanded(true);
        if (json.getType() == TypeUnit.ARRAY_UNIT) {
            List<ArrayUnitJson> unitJson = json.getArrayValue();
        } else if (json.getType() == TypeUnit.UNIT) {
            List<UnitJson> unitJson = json.getUnitsValue();
            int size = unitJson.size();
            for (int i = 0; i < size; ++i) {
                TreeItem<String> item = new TreeItem<>(unitJson.get(i).name);
                rootItem.getChildren().add(item);

            }
        }
         treeView.setRoot(rootItem);
        //treeView.setShowRoot(false);
    }*/

    /*private TreeItem<IUnitJson> createTreeItem(IUnitJson obj) {
        TreeItem<IUnitJson> item = new TreeItem<>(obj);
        List<IUnitJson> unitsList;
        if (obj.get)
        for (IUnitJson childObject : unitsList) {
            TreeItem<IUnitJson> childItem = createTreeItem(childObject);
            item.getChildren().add(childItem);
        }
        return item;
    }*/

}