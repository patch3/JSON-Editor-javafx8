package src.controller;


import com.sun.istack.internal.Nullable;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import src.Main;
import src.util.SFTPClient;
import src.util.ShowBox;
import src.util.directory.Directory;
import src.util.directory.DirectoryElement;
import src.util.directory.IDirectory;
import src.util.json.*;

import java.io.BufferedWriter;
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
    @FXML
    private MenuItem openFolderRemote;
    @FXML
    private MenuItem openRemote;
    public boolean jsonIsRemote = false;
    public String remotePath;
    @FXML
    private MenuItem saveFile;
    @FXML
    private TextField nameUnitTextField;
    @FXML
    private TextArea valueUnitTextArea;
    @FXML
    private CheckBox numCheckBox;
    @FXML
    private Button saveButton;


    public Json json;
    @FXML
    private Button cancelButton;
    private IUnitJson unitJson;


    @Nullable
    private File workFile;
    //private static final Json gson = new Json();
    @Nullable
    private File workDirectory;

    @FXML
    public void initialize() {
        this.openLocal.setOnAction(this::eventClickOpen);
        this.createLocal.setOnAction(this::eventClickCreateLocal);
        this.configureConn.setOnAction(this::eventClickConfigureConn);
        this.openLocalFolder.setOnAction(this::eventClickOpenLocalFolder);
        this.openFolderRemote.setOnAction(this::eventClickOpenRemoteFolder);
        this.saveFile.setOnAction(this::eventSaveFile);

        //this.directoryTreeView.setEditable(false);
        //this.directoryTreeView.setShowRoot(true);
        this.directoryTreeView.setCellFactory(this::createDirectoryTreeView);
        this.directoryTreeView.setOnMouseClicked(this::onDirectoryTreeViewClick);

        //this.treeView.setEditable(true);
        //this.treeView.setShowRoot(false);
        this.treeView.setCellFactory(param -> createTextFieldTreeCell());
        this.treeView.setOnEditCommit(this::onEditCommit);
        this.treeView.setOnMouseClicked(this::onMauseClickTreeView);


        this.saveButton.setOnAction(this::onActionSaveButton);
        this.cancelButton.setOnAction(this::onActionCancelButton);


    }

    private void eventSaveFile(ActionEvent event) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(workFile))) {
            System.err.println(json.toString());
            writer.write(json.toString());
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (jsonIsRemote) {
            SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
            try {
                client.connect();
                System.err.println(remotePath + " : " + workFile.getAbsolutePath());
                client.uploadFile(remotePath.substring(1), workFile.getAbsolutePath());
                workFile.delete();

            } catch (Exception e) {
                ShowBox.showError("Ошибка при загрузке файла на сервер: " + e.getMessage());
                e.printStackTrace();
            } finally {
                client.disconnect();
                jsonIsRemote = false;
                remotePath = null;
            }
        }
    }
    private void eventClickOpenRemoteFolder(Event event) {
        SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
        try {
            client.connect();
            Directory dir = client.getDirectory("/", null);
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
        } catch (Exception e) {
            ShowBox.showError("Произошла ошибка при подключении: " + e.getMessage());
        } finally {
            if (client.isConnected()) {
                client.disconnect();
            }
        }


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
        if (event.getClickCount() >= 2) {
            TreeItem<IDirectory> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if (selectedItem.getValue() instanceof Directory) return;
                DirectoryElement selectedObject = (DirectoryElement) selectedItem.getValue();
                if (selectedObject.isRemote) {
                    SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
                    if (selectedObject.isJson) {
                        try {
                            client.connect();
                            File file = client.downloadFile(selectedObject.pathToElement, Main.tempDir + File.separator + selectedObject.getName());
                            this.json = new Json(file);
                            showJson(this.json);
                            jsonIsRemote = true;
                            remotePath = selectedObject.pathToElement;
                            workFile = file;
                        } catch (Exception e) {
                            ShowBox.showError("Ошибка при подключении: " + e.getMessage());
                            e.printStackTrace();
                        } finally {
                            if (client.isConnected()) {
                                client.disconnect();
                            }
                        }
                    } else {
                        Directory directory;
                        try {
                            client.connect();
                            directory = client.getDirectory(selectedObject.pathToElement, null);
                        } catch (Exception e) {
                            ShowBox.showError("Ошибка при подключении: " + e.getMessage());
                            e.printStackTrace();
                            return;
                        } finally {
                            if (client.isConnected()) {
                                client.disconnect();
                            }
                        }
                        selectedItem.getChildren().clear();
                        for (IDirectory element : directory.elementlist) {
                            selectedItem.getChildren().add(new TreeItem<>(element));
                        }
                        for (IDirectory element : directory.jsonFiles) {
                            selectedItem.getChildren().add(new TreeItem<>(element));
                        }
                        selectedItem.setExpanded(true);
                    }
                    return;
                }
                if (selectedObject.isJson) {
                    try {
                        this.json = new Json(new File(selectedObject.pathToElement));
                        showJson(this.json);
                        workFile = new File(selectedObject.pathToElement);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Directory directory = new Directory(selectedObject);
                    selectedItem.getChildren().clear();
                    for (IDirectory element : directory.elementlist) {
                        selectedItem.getChildren().add(new TreeItem<>(element));
                    }
                    for (IDirectory element : directory.jsonFiles) {
                        selectedItem.getChildren().add(new TreeItem<>(element));
                    }
                    selectedItem.setExpanded(true);
                }
            }
        }
    }


    private void eventClickOpenLocalFolder(Event event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("");

        workDirectory = chooser.showDialog(scene.getScene().getWindow());

        if (workDirectory == null) return;
        Directory dir = new Directory(workDirectory, null);
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
                if (workFile.exists()) {
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
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../fxml/configure_conn.fxml")));
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
        event.getNewValue().setValue(event.getOldValue());
        int[] index = this.json.indexOf(editedItem.getValue());
        IUnitJson object = event.getNewValue();
        object.setValue(event.getNewValue());
        editedItem.setValue(object);
        this.json.set(index, object);

        treeView.refresh(); // Обновление TreeView
    }



    public void showJson(Json json) {
        List<IUnitJson> unitList = json.getValue();
        TreeItem<IUnitJson> rootItem = new TreeItem<>();
        rootItem.setExpanded(true);
        if (unitList != null) {
            for (IUnitJson iUnitJson : unitList) {
                TreeItem<IUnitJson> item = new TreeItem<>(iUnitJson);
                if (iUnitJson.getValue() instanceof ValueUnitsJsonList) {
                    item.getChildren().add(recursionShowJson(iUnitJson));
                }
                rootItem.getChildren().add(item);
            }
        }

        treeView.setRoot(rootItem);
    }

    private TreeItem<IUnitJson> recursionShowJson(IUnitJson obj) {
        TreeItem<IUnitJson> item = new TreeItem<>(obj);
        List<IUnitJson> valueList = obj.getValueList();
        if (valueList != null) {
            for (IUnitJson iUnitJson : valueList) {
                if (iUnitJson.getValue() instanceof ValueUnitsJsonList) {
                    item.getChildren().add(recursionShowJson(iUnitJson));
                } else {
                    TreeItem<IUnitJson> childItem = new TreeItem<>(iUnitJson);
                    item.getChildren().add(childItem);
                }
            }
        }
        return item;
    }

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


    private void onMauseClickTreeView(MouseEvent event) {
        if (event.getClickCount() == 1) { // Обработка одиночного щелчка
            TreeItem<IUnitJson> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;
            this.unitJson = selectedItem.getValue();

            this.disableChangePanel(false);

            this.nameUnitTextField.setText(this.unitJson.getName());
            this.nameUnitTextField.setDisable(this.unitJson.getTypeUnit() == TypeUnit.ARRAY_UNIT);

            if (this.unitJson.getValue() instanceof ValueUnitsJsonList) {
                this.valueUnitTextArea.setDisable(false);
                this.numCheckBox.setDisable(true);
                this.numCheckBox.setSelected(false);
                if (((ValueUnitsJsonList) this.unitJson.getValue()).getType() == TypeUnit.UNIT) {
                    this.valueUnitTextArea.setText("[UnitList]");
                } else {
                    this.valueUnitTextArea.setText("[ArrayList]");
                }
            } else {
                this.valueUnitTextArea.setDisable(false);

                if (this.unitJson.getTypeValue() == IUnitJson.TypeValue.STRING) {
                    this.valueUnitTextArea.setText((String) this.unitJson.getValue());
                    this.numCheckBox.setSelected(false);
                } else {
                    this.valueUnitTextArea.setText(((AbstractElementJson) this.unitJson).getNumStringValue());
                    this.numCheckBox.setSelected(true);
                }
            }

            // Ваш код обработки события при нажатии на элемент
            System.out.println("Вы нажали на элемент: " + selectedItem.getValue());
        }
    }

    private void onActionSaveButton(ActionEvent event) {
        if (this.nameUnitTextField.getText().isEmpty()) {
            ShowBox.showError("Не допускается пустое поле имени");
            return;
        } else if (this.valueUnitTextArea.getText().isEmpty()) {
            ShowBox.showError("Не допускается пустое поле значения");
            return;
        }
        IUnitJson newUnit = this.unitJson;
        newUnit.setName(this.nameUnitTextField.getText());

        if (this.numCheckBox.isSelected()) {
            newUnit.setValue(this.valueUnitTextArea.getText(), IUnitJson.TypeValue.NUMBER);
        } else {
            newUnit.setValue(this.valueUnitTextArea.getText(), IUnitJson.TypeValue.STRING);
        }

        json.set(json.indexOf(this.unitJson), newUnit);
        this.unitJson = newUnit;

        treeView.refresh(); // Обновление TreeView
    }

    private void onActionCancelButton(ActionEvent event) {
        this.unitJson = null;
        this.nameUnitTextField.clear();
        this.valueUnitTextArea.clear();
        this.numCheckBox.setSelected(false);
        this.disableChangePanel(true);
    }


    private void disableChangePanel(boolean enable) {
        this.nameUnitTextField.setDisable(enable);
        this.valueUnitTextArea.setDisable(enable);
        this.numCheckBox.setDisable(enable);
        this.saveButton.setDisable(enable);
        this.cancelButton.setDisable(enable);
    }
}
