package controller;

import com.sun.istack.internal.Nullable;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import jdk.jfr.internal.tool.Main;
import util.directory.DirectoryElement;
import util.directory.IDirectory;
import util.directory.directory;
import util.json.IUnitJson;
import util.json.Json;
import util.json.UnitJson;
import util.json.ValueUnitsJsonList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


public class Home {
    @FXML
    private AnchorPane scene;
    @FXML
    private MenuItem createLocal;
    @FXML
    private MenuItem configureConn;
    @FXML
    private MenuItem openLocal;
    @FXML
    public TreeView<IDirectory> directoryTreeView;
    @FXML
    public TreeView<IUnitJson> treeView;
    @FXML
    private MenuItem openLocalFolder;

    public Json json;


    @Nullable
    private File workFile;
    //private static final Json gson = new Json();
    @Nullable
    private File workDirectory;

    @FXML
    public void initialize() {
        openLocal.setOnAction(this::eventClickOpen);
        createLocal.setOnAction(this::eventClickCreateLocal);
        configureConn.setOnAction(this::eventClickConfigureConn);
        openLocalFolder.setOnAction(this::eventClickOpenLocalFolder);


        directoryTreeView.setEditable(false);
        directoryTreeView.setShowRoot(true);
        directoryTreeView.setCellFactory(this::createDirectoryTreeView);
        directoryTreeView.setOnMouseClicked(this::onDirectoryTreeViewClick);

        treeView.setEditable(true);
        treeView.setShowRoot(false);
        treeView.setCellFactory(param -> createTextFieldTreeCell());
        treeView.setOnEditCommit(this::onEditCommit);
    }

    private TreeCell<IDirectory> createDirectoryTreeView(TreeView<IDirectory> treeView) {
        return new TreeCell<IDirectory>() {
            @Override
            protected void updateItem(IDirectory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                    setAccessibleText(item.getName());
                }
            }
        };
    }

    private void onDirectoryTreeViewClick(MouseEvent event) {
        if (event.getClickCount() > 2) {
            TreeItem<IDirectory> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                DirectoryElement selectedObject = (DirectoryElement) selectedItem.getValue();
                System.err.println(selectedObject.isJson);
            }
        }
    }


    private void eventClickOpenLocalFolder(Event event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("");

        workDirectory = chooser.showDialog(scene.getScene().getWindow());

        if (workDirectory == null) return;
        directory dir = new directory(workDirectory);
        TreeItem<IDirectory> rootItem = new TreeItem<>(dir);
        for (DirectoryElement directoryElement : dir.elementlist) {
            TreeItem<IDirectory> item = new TreeItem<>(directoryElement);
            rootItem.getChildren().add(item);
        }
        for (DirectoryElement element : dir.jsonFiles) {
            TreeItem<IDirectory> item = new TreeItem<>(element);
            rootItem.getChildren().add(item);
        }
        directoryTreeView.setRoot(rootItem);
        //directoryTreeView.refresh();

    }

    private void eventClickCreateLocal(Event event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("");
        chooser.setInitialFileName("NewFile");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json", "*.json"));
        workFile = chooser.showSaveDialog(scene.getScene().getWindow());

        if (workFile == null) return;

        try {
            if (workFile.createNewFile()) {
                FileWriter write = new FileWriter(workFile);
                write.write("{\n\t\n}");
                write.flush();
                write.close();
            } else {
                if (workFile.exists()){
                    // File already exists
                }
                // File cannot be created
                System.err.println("Unable to create file: " + workFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void eventClickCreateLocal(Event event) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void eventClickOpen(Event event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("");
        chooser.setInitialFileName("NewFile"); //Имя файла, которое по умолчанию устанавливается
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json", "*.json")); //Допустимые расширения файла
        workFile = chooser.showOpenDialog(scene.getScene().getWindow()); //Вызываем диалоговое окно
        if (workFile == null) return;
        try {
            this.json = new Json(workFile);
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

    private void onEditCommit(TreeView.EditEvent<IUnitJson> event) {
        TreeItem<IUnitJson> editedItem = event.getTreeItem();
        event.getNewValue().setValue(event.getOldValue());// на всякий крайний
        int[] intdex = this.json.indexOf(editedItem.getValue());
        IUnitJson object = this.json.get(intdex);

        object = event.getNewValue();
        object.setValue(event.getOldValue());
        editedItem.setValue(object);
        this.json.set(intdex, object);

        treeView.refresh(); // Обновление TreeView
    }

    /*public void showJson(Json json) {
        TreeItem<IUnitJson> rootItem = new TreeItem<>();
        rootItem.setExpanded(true);
        if (json.getType() == TypeUnit.ARRAY_UNIT) {
            List<ArrayUnitJson> unitJson = json.getArrayValue();
            int size = unitJson.size();
            for (int i = 0; i < size; ++i) {
                TreeItem<IUnitJson> item = new TreeItem<>(unitJson.get(i));
                rootItem.getChildren().add(item);
            }
        } else if (json.getType() == TypeUnit.UNIT) {
            List<UnitJson> unitJson = json.getUnitsValue();
            int size = unitJson.size();
            for (int i = 0; i < size; ++i) {
                TreeItem<IUnitJson> item = new TreeItem<>(unitJson.get(i));
                rootItem.getChildren().add(item);
            }
        }
        treeView.setRoot(rootItem);
        //treeView.setShowRoot(false);
    }*/

    public void showJson(Json json) {
        treeView.setRoot(this.recursionShowJson(json.getValue()));
    }

    private TreeItem<IUnitJson> recursionShowJson(List<IUnitJson> unitList) {
        TreeItem<IUnitJson> rootItem = new TreeItem<>();
        rootItem.setExpanded(true);
        if (unitList != null) {
            for (IUnitJson iUnitJson : unitList) {
                TreeItem<IUnitJson> item = new TreeItem<>(iUnitJson);
                if (iUnitJson.getValue() instanceof ValueUnitsJsonList) {
                    item.getChildren().add(recursionShowJson(iUnitJson.getValueList()));
                }
                rootItem.getChildren().add(item);
            }
        }
        return rootItem;
    }

    /*private TreeItem<IUnitJson> createTreeItem(IUnitJson obj) {
        TreeItem<IUnitJson> item = new TreeItem<>(obj);
        if (obj.getTypeValue() == IUnitJson.TypeValue.UNITS_ARRAY) {
            List<IUnitJson> unitsList = obj.getValueList();
            for (IUnitJson childObject : unitsList) {
                TreeItem<IUnitJson> childItem = createTreeItem(childObject);
                item.getChildren().add(childItem);
            }
        }
        return item;
    }*/

    private TextFieldTreeCell<IUnitJson> createTextFieldTreeCell() {
        return new TextFieldTreeCell<>(new StringConverter<IUnitJson>() {
            @Override
            public String toString(IUnitJson object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public IUnitJson fromString(String string) {
                return new UnitJson(string);
            }
        });
    }


}
