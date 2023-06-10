package src.controller;


import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sun.istack.internal.Nullable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import src.Main;
import src.util.SFTPClient;
import src.util.ShowBox;
import src.util.TranslationTextComponent;
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
import java.util.regex.Pattern;

import static src.Main.FILENAME_REGEX;


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
    private MenuItem saveFile;
    @FXML
    private MenuItem disconnect;
    @FXML
    private MenuItem openLocalFolder;
    @FXML
    private MenuItem connectToFolder;
    @FXML
    private MenuItem saveAs;
    @FXML
    private MenuItem exitFolder;
    @FXML
    private MenuItem exitFile;
    @FXML
    private MenuItem exitProg;

    @FXML
    public TreeView<IDirectory> directoryTreeView;
    @FXML
    public TreeView<IUnitJson> treeView;

    public Json json;

    @FXML
    private TextField nameUnitTextField;
    @FXML
    private TextArea valueUnitTextArea;
    @FXML
    private CheckBox numCheckBox;
    @FXML
    private Button saveButton;

    @FXML
    private Menu language;
    @FXML
    private Button cancelButton;
    public boolean jsonIsRemote = false;
    private IUnitJson unitJson;
    public String remotePath;


    @Nullable
    private File workFile;
    @Nullable
    private File workDirectory;
    @FXML
    private MenuButton fileMenu;
    @FXML
    private MenuButton connectMenu;
    @FXML
    private MenuButton infoMenu;
    @FXML
    private MenuButton viewMenu;


    @FXML
    public void initialize() {
        this.setText();
        
        
        this.openLocal.setOnAction(this::eventClickOpen);
        this.createLocal.setOnAction(event -> eventClickCreateLocal(event, null));
        this.configureConn.setOnAction(this::eventClickConfigureConn);
        this.openLocalFolder.setOnAction(this::eventClickOpenLocalFolder);
        this.connectToFolder.setOnAction(this::eventClickOpenRemoteFolder);
        this.saveFile.setOnAction(this::eventSaveFile);
        this.disconnect.setOnAction(event -> disconnect());

        ToggleGroup group = new ToggleGroup();
        for (File f : TranslationTextComponent.translates){
            RadioMenuItem item = new RadioMenuItem(f.getName());
            item.setOnAction(event -> onChangeLanguage(item));
            item.setToggleGroup(group);
            if (item.getText().equals(TranslationTextComponent.currentTranslation.getName())){
                group.selectToggle(item);
            }
            language.getItems().add(item);
        }


        this.directoryTreeView.setCellFactory(this::createDirectoryTreeView);
        this.directoryTreeView.setOnMouseClicked(this::onDirectoryTreeViewClick);
        this.directoryTreeView.setOnContextMenuRequested(this::eventDirectoryTreeViewContextMenuRequest);

        //this.treeView.setEditable(true);
        //this.treeView.setShowRoot(false);
        this.treeView.setCellFactory(param -> createTextFieldTreeCell());
        this.treeView.setOnEditCommit(this::onEditCommit);
        this.treeView.setOnMouseClicked(this::onMauseClickTreeView);
        this.treeView.setOnContextMenuRequested(this::eventTreeViewContextMenuRequest);

        /* панель изменения элементов json */
        this.saveButton.setOnAction(this::onActionSaveButton);
        this.cancelButton.setOnAction(this::onActionCancelButton);
    }

    private void setText() {
        createLocal.setText(new TranslationTextComponent("menu_item.create.json").toString());
        saveFile.setText(new TranslationTextComponent("menu_item.save.file").toString());
        disconnect.setText(new TranslationTextComponent("menu_item.disconnect").toString());
        openLocalFolder.setText(new TranslationTextComponent("menu_item.open.local.folder").toString());
        connectToFolder.setText(new TranslationTextComponent("menu_item.open.remote.folder").toString());
        saveAs.setText(new TranslationTextComponent("menu_item.save.as").toString());
        exitFolder.setText(new TranslationTextComponent("menu_item.exit.folder").toString());
        exitFile.setText(new TranslationTextComponent("menu_item.exit.file").toString());
        exitProg.setText(new TranslationTextComponent("menu_item.exit.global").toString());
        openLocal.setText(new TranslationTextComponent("menu_item.open.local").toString());
        configureConn.setText(new TranslationTextComponent("menu_item.configure").toString());
        language.setText(new TranslationTextComponent("menu.language").toString());
        fileMenu.setText(new TranslationTextComponent("menu.file").toString());
        connectMenu.setText(new TranslationTextComponent("menu.connect").toString());
        infoMenu.setText(new TranslationTextComponent("menu.info").toString());
        viewMenu.setText(new TranslationTextComponent("menu.view").toString());
    }

    private void onChangeLanguage(RadioMenuItem item) {
        for (File f : TranslationTextComponent.translates){
            String name = item.getText();
            if (name.equals(f.getName())){
                TranslationTextComponent.currentTranslation = f;
            }
        }
        setText();
    }



    private void eventTreeViewContextMenuRequest(ContextMenuEvent event) {
        TreeItem<IUnitJson> selectedItem = treeView.getSelectionModel().getSelectedItem();

        ContextMenu menu = new ContextMenu();

        MenuItem deleteMenuItem          = new MenuItem(new TranslationTextComponent("delete").toString());
        MenuItem addUnitJsonMenuItem     = new MenuItem(new TranslationTextComponent("context_menu.add_unit").toString());
        MenuItem addUnitListJsonMenuItem = new MenuItem(new TranslationTextComponent("context_menu.add_unit_list").toString());
        MenuItem addArrayListMenuItem    = new MenuItem(new TranslationTextComponent("context_menu.add_array_list").toString());

        deleteMenuItem.setOnAction(event1 -> eventDeleteJsonUnitOnAction(event1, selectedItem));
        addUnitJsonMenuItem.setOnAction(event1 -> eventAddUnitOnAction(event1, selectedItem));
        addUnitListJsonMenuItem.setOnAction(event1 -> eventAddUnitListOnAction(event1, selectedItem));
        addArrayListMenuItem.setOnAction(event1 -> eventAddArrayListOnAction(event1, selectedItem));


        if (treeView.getRoot() == null) {
            menu.getItems().setAll(addUnitListJsonMenuItem, addArrayListMenuItem);
        } else {
            IUnitJson selectedUnit = selectedItem.getValue();
            if (selectedUnit.getTypeValue() == IUnitJson.TypeValue.UNITS_ARRAY) {
                menu.getItems().setAll(
                        deleteMenuItem,
                        new SeparatorMenuItem(),
                        addUnitJsonMenuItem,
                        addUnitListJsonMenuItem,
                        addArrayListMenuItem
                );
            } else {
                menu.getItems().setAll(deleteMenuItem);
            }
        }

        menu.show(scene.getScene().getWindow(), event.getScreenX(), event.getScreenY());
    }

    private void eventAddArrayListOnAction(ActionEvent event1, TreeItem<IUnitJson> selectedItem) {
        IUnitJson parentUnit = selectedItem.getValue();
        if (parentUnit.getTypeValue() != IUnitJson.TypeValue.UNITS_ARRAY) {
            throw new RuntimeException("List expected");
        }
        IUnitJson emptyUnit;
        if (((ValueUnitsJsonList)parentUnit.getValue()).getType() == TypeUnit.UNIT) {
            emptyUnit = new UnitJson(
                    new TranslationTextComponent("unit.new_unit").toString(),
                    new ValueUnitsJsonList(TypeUnit.UNIT),
                    IUnitJson.TypeValue.UNITS_ARRAY
            );
        } else {
            emptyUnit = new UnitJson(
                    String.format("[%s]",parentUnit.getValueList().size()),
                    new ValueUnitsJsonList(TypeUnit.UNIT),
                    IUnitJson.TypeValue.UNITS_ARRAY
            );
        }
        IUnitJson newParentUnit = parentUnit;
        newParentUnit.getValueList().add(emptyUnit);
        this.json.set(this.json.indexOf(parentUnit), newParentUnit);
        parentUnit.getValueList().add(emptyUnit);

        TreeItem<IUnitJson> treeItem = new TreeItem<>(emptyUnit);

        selectedItem.setValue(newParentUnit);
        selectedItem.getChildren().add(treeItem);
        this.treeView.refresh();
    }

    private void eventAddUnitListOnAction(ActionEvent event1, TreeItem<IUnitJson> selectedItem) {
        IUnitJson parentUnit = selectedItem.getValue();
        if (parentUnit.getTypeValue() != IUnitJson.TypeValue.UNITS_ARRAY) {
            throw new RuntimeException("List expected");
        }
        IUnitJson emptyUnit;
        if (((ValueUnitsJsonList)parentUnit.getValue()).getType() == TypeUnit.UNIT) {
            emptyUnit = new UnitJson(
                    new TranslationTextComponent("unit.new_unit").toString(),
                    new ValueUnitsJsonList(TypeUnit.UNIT),
                    IUnitJson.TypeValue.UNITS_ARRAY
            );
        } else {
            emptyUnit = new UnitJson(
                    String.format("[%s]",parentUnit.getValueList().size()),
                    new ValueUnitsJsonList(TypeUnit.UNIT),
                    IUnitJson.TypeValue.UNITS_ARRAY
            );
        }
        IUnitJson newParentUnit = parentUnit;
        newParentUnit.getValueList().add(emptyUnit);
        this.json.set(this.json.indexOf(parentUnit), newParentUnit);
        parentUnit.getValueList().add(emptyUnit);

        TreeItem<IUnitJson> treeItem = new TreeItem<>(emptyUnit);

        selectedItem.setValue(newParentUnit);
        selectedItem.getChildren().add(treeItem);
        this.treeView.refresh();

    }

    private void eventAddUnitOnAction(ActionEvent event, TreeItem<IUnitJson> selectedItem) {
        IUnitJson emptyUnit;
        IUnitJson parentUnit = selectedItem.getValue();
        if (((ValueUnitsJsonList)parentUnit.getValue()).getType() == TypeUnit.UNIT) {
            emptyUnit = new UnitJson(new TranslationTextComponent("unit.new_unit").toString(), "", IUnitJson.TypeValue.STRING);
        } else {
            emptyUnit = new UnitJson(String.format("[%s]",parentUnit.getValueList().size()), "", IUnitJson.TypeValue.STRING);
        }

        if (selectedItem == this.treeView.getRoot()) {

        }


        if (parentUnit.getTypeValue() != IUnitJson.TypeValue.UNITS_ARRAY) {
            throw new RuntimeException("List expected");
        }

        IUnitJson newParentUnit = parentUnit;
        newParentUnit.getValueList().add(emptyUnit);
        this.json.set(this.json.indexOf(parentUnit), newParentUnit);

        TreeItem<IUnitJson> treeItem = new TreeItem<>(emptyUnit);

        selectedItem.setValue(newParentUnit);
        selectedItem.getChildren().add(treeItem);
        this.treeView.refresh();
    }



    private void eventDeleteJsonUnitOnAction(ActionEvent event, TreeItem<IUnitJson> selectedItem){
        if (selectedItem == this.treeView.getRoot()) {
            this.json = null;
            this.treeView.getRoot().getChildren().clear();
            this.clearTreeView(treeView);
            return;
        }
        IUnitJson selectedUnit = selectedItem.getValue();

        int[] indexs = this.json.indexOf(selectedUnit);

        this.json.delete(indexs);
        this.getParentTreeItem(indexs).getChildren().remove(indexs[indexs.length - 1]);
        treeView.refresh(); // Обновление TreeView
    }



    private void clearTreeView(TreeView<?> treeView){
        treeView.setRoot(null);
        treeView.refresh();
    }

    private TreeItem<IUnitJson> getParentTreeItem(int[] indexes) {
        TreeItem<IUnitJson> tempElementItem = this.treeView.getTreeItem(0);
        ObservableList<TreeItem<IUnitJson>> treeItemList = tempElementItem.getChildren();
        for (int i = 0; i < indexes.length; ++i) {
            int index = indexes[i];
            if (index >= 0 && index < treeItemList.size()) {
                if (i == indexes.length - 1) {
                    return tempElementItem;
                }
                tempElementItem = treeItemList.get(index);
                 if (tempElementItem.getChildren() != null) {
                    treeItemList = tempElementItem.getChildren();
                } else {
                    throw new RuntimeException("Child list is null");
                }
            } else {
                throw new RuntimeException("Invalid index");
            }
        }
        throw new RuntimeException("Unable to get parent");
    }


    private void eventDirectoryTreeViewContextMenuRequest(ContextMenuEvent event) {
        if (directoryTreeView.getSelectionModel().isEmpty()) {
            return;
        }
        if (directoryTreeView.getSelectionModel().getSelectedItem().getValue() instanceof Directory) {
            return;
        }
        DirectoryElement selectedElement = (DirectoryElement) directoryTreeView.getSelectionModel().getSelectedItem().getValue();

        ContextMenu menu = new ContextMenu();


        SeparatorMenuItem separator = new SeparatorMenuItem();

        if (selectedElement.isJson) {
            MenuItem deleteJson = new MenuItem(new TranslationTextComponent("menu_item.delete.json").toString());
            deleteJson.setOnAction(this::eventDeleteJson);
            menu.getItems().setAll(deleteJson);
        } else {
            MenuItem createJson = new MenuItem(new TranslationTextComponent("menu_item.create.json").toString());
            MenuItem createDirectory = new MenuItem(new TranslationTextComponent("menu_item.create.directory").toString());
            MenuItem deleteDir = new MenuItem(new TranslationTextComponent("menu_item.delete.directory").toString());

            deleteDir.setOnAction(this::eventDeleteDirectory);

            createDirectory.setOnAction(this::eventCreateDirectory);
            createJson.setOnAction(event1 -> eventCreateJson(event1, selectedElement));
            menu.getItems().setAll(createJson, createDirectory, separator, deleteDir);
        }
        menu.show(scene.getScene().getWindow(), event.getScreenX(), event.getScreenY());
    }
    private void eventDeleteJson(ActionEvent event){
        TreeItem<IDirectory> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();
        DirectoryElement selectedElement = (DirectoryElement) selectedItem.getValue();

        if (selectedElement.isRemote){
            try {
                deleteJsonRemote(selectedElement.pathToElement);
                selectedItem.getParent().getChildren().remove(selectedItem);
                directoryTreeView.refresh();
            }catch (Exception e){
                ShowBox.showError(new TranslationTextComponent("error.unabletodelete.file", e.getMessage()));
                e.printStackTrace();
            }
        }else {
            if (new File(selectedElement.pathToElement).delete()){
                ShowBox.showInfo(new TranslationTextComponent("success"), new TranslationTextComponent("succes.file.deleted"));
                selectedItem.getParent().getChildren().remove(selectedItem);
                directoryTreeView.refresh();
            }else {
                ShowBox.showError(new TranslationTextComponent("error.unabletodelete.file", selectedElement.pathToElement));
            }
        }
    }

    private void deleteJsonRemote(String pathToElement) throws SftpException, JSchException {
        SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
        client.connect();
        client.deleteFile(pathToElement);
        client.disconnect();
    }


    private void eventCreateDirectory(ActionEvent event){
        TreeItem<IDirectory> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();
        DirectoryElement selectedElement = (DirectoryElement) selectedItem.getValue();

        if (selectedElement.isRemote){
            Stage stage = new Stage();
            AnchorPane pane = new AnchorPane();
            VBox box = new VBox();
            Text text = new Text(new TranslationTextComponent("text.enter", new TranslationTextComponent("dirname")).toString());
            Button ok = new Button(new TranslationTextComponent("ok").toString());
            TextField field = new TextField();
            field.setPromptText(new TranslationTextComponent("dirname").toString());
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!Pattern.compile(FILENAME_REGEX).matcher(newValue).matches()) {
                    text.setText(oldValue);
                }
            });
            box.setAlignment(Pos.CENTER);
            ok.setOnAction(event1 -> {
                try {
                    createRemoteDirectory(selectedElement.pathToElement, field.getText());
                    System.err.println(selectedElement.pathToElement.replace("\\", "/") + File.separator + field.getText() + " : " + directoryTreeView.getSelectionModel().getSelectedItem().getValue());
                    directoryTreeView.getSelectionModel().getSelectedItem().getChildren().add(new TreeItem<>(
                            new DirectoryElement(
                                    selectedElement.pathToElement.replace("\\", "/") + File.separator + field.getText(),
                                    directoryTreeView.getSelectionModel().getSelectedItem().getValue(),
                                    false
                            )));
                }catch (Exception e){
                    ShowBox.showError(new TranslationTextComponent("error.create.remote.file", e.getMessage()));
                    e.printStackTrace();
                }finally {
                    stage.close();
                    directoryTreeView.refresh();
                }

            });
            box.getChildren().setAll(text, field, ok);
            pane.getChildren().add(box);

            stage.setScene(new Scene(pane));
            stage.show();
        }else {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setInitialDirectory(new File(selectedElement.pathToElement));
            try {
                chooser.showDialog(scene.getScene().getWindow()).createNewFile();
            } catch (IOException e) {
                ShowBox.showError(new TranslationTextComponent("error.create.remote.dir", e.getMessage()));
                e.printStackTrace();
            }
        }
    }

    private void createRemoteDirectory(String remotePath, String dirName) throws JSchException, SftpException {
        SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
        client.connect();
        client.createDirectory(remotePath + File.separator + dirName);
        client.disconnect();
    }

    private void eventDeleteDirectory(ActionEvent event){
        TreeItem<IDirectory> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();
        DirectoryElement selectedItemValue = (DirectoryElement) selectedItem.getValue();

        SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
        try {
            client.connect();
            client.deleteDirectory(selectedItemValue.pathToElement);
            directoryTreeView.getSelectionModel().getSelectedItem().getParent().getChildren().remove(directoryTreeView.getSelectionModel().getSelectedItem());
        }catch (Exception e){
            ShowBox.showError(new TranslationTextComponent("error.delete.remote.dir", e.getMessage()));
        }finally {
            client.disconnect();
            directoryTreeView.refresh();
        }
    }

    private void eventCreateJson(ActionEvent event, DirectoryElement element) {
        if (element.isRemote) {
            Stage stage = new Stage();
            AnchorPane pane = new AnchorPane();
            VBox box = new VBox();
            Text text = new Text(new TranslationTextComponent("text.enter", new TranslationTextComponent("filename")) + " (" + new TranslationTextComponent("text.withoutextention" + ")"));
            Button ok = new Button(new TranslationTextComponent("ok").toString());
            TextField field = new TextField();
            field.setPromptText(new TranslationTextComponent("filename").toString());
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!Pattern.compile(FILENAME_REGEX).matcher(newValue).matches()) {
                    text.setText(oldValue);
                }
            });
            box.setAlignment(Pos.CENTER);
            ok.setOnAction(event1 -> {
                try {
                    createRemoteJsonFile(element.pathToElement, field.getText());
                    directoryTreeView.getSelectionModel().getSelectedItem().getChildren().add(new TreeItem<>(
                            new DirectoryElement(
                                    element.pathToElement.replace("\\", "/") + File.separator + field.getText() + ".json",
                                    directoryTreeView.getSelectionModel().getSelectedItem().getValue(),
                                    true
                             )));
                    ShowBox.showInfo(new TranslationTextComponent("success"), new TranslationTextComponent("success.file.created"));
                }catch (Exception e){
                    ShowBox.showError(new TranslationTextComponent("error.create.remote.file", e.getMessage()));
                    e.printStackTrace();
                }finally {
                    stage.close();
                    directoryTreeView.refresh();
                }

            });
            box.getChildren().setAll(text, field, ok);
            pane.getChildren().add(box);

            stage.setScene(new Scene(pane));
            stage.show();
        } else {
            eventClickCreateLocal(null, new File(element.pathToElement));
        }
    }

    private void createRemoteJsonFile(String remotePath, String fileName) throws JSchException, SftpException {
        SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
        client.connect();
        client.createFile(remotePath + File.separator + fileName + ".json");
        client.disconnect();
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
                ShowBox.showError(new TranslationTextComponent("error.load.remote.file", e.getMessage()));
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
            rootItem.setExpanded(true);
            directoryTreeView.setRoot(rootItem);
        } catch (Exception e) {
            ShowBox.showError(new TranslationTextComponent("error.connection.unsuccessful", e.getMessage()));
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
                            ShowBox.showError(new TranslationTextComponent("error.connection.unsuccessful", e.getMessage()));
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
                            directory = client.getDirectory(selectedObject.pathToElement, selectedObject);
                        } catch (Exception e) {
                            ShowBox.showError(new TranslationTextComponent("error.connection.unsuccessful", e.getMessage()));
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

    private void eventClickCreateLocal(Event event, @Nullable File initialPath) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("");
        chooser.setInitialFileName("NewFile");
        if (initialPath != null) {
            chooser.setInitialDirectory(initialPath);
        }
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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
        stage.setTitle(new TranslationTextComponent("configure").toString());
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
        UnitJson rootUnit;
        if (json.getType() == TypeUnit.UNIT){
            rootUnit = new UnitJson("[UnitList]", null, IUnitJson.TypeValue.UNITS_ARRAY);
        } else {
            rootUnit = new UnitJson("[ArrayList]", null, IUnitJson.TypeValue.UNITS_ARRAY);
        }
        TreeItem<IUnitJson> rootItem = new TreeItem<>(rootUnit);
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

            // если это корневой элемент
            if (selectedItem == treeView.getRoot()){
                this.disableChangePanel(true);

                if (this.json.getType() == TypeUnit.UNIT){
                    this.nameUnitTextField.setText("[UnitList]");
                } else {
                    this.nameUnitTextField.setText("[ArrayList]");
                }
                return;
            }

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
        }
    }

    private void onActionSaveButton(ActionEvent event) {
        if (this.nameUnitTextField.getText().isEmpty()) {
            ShowBox.showError(new TranslationTextComponent("error.input.notentered", new TranslationTextComponent("name")));
            return;
        } else if (this.valueUnitTextArea.getText().isEmpty()) {
            ShowBox.showError(new TranslationTextComponent("error.input.notentered", new TranslationTextComponent("value")));
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

    private void disconnect(){
        for (SFTPClient client : SFTPClient.connections){
            client.disconnect();
        }
        clearTreeView(directoryTreeView);
        clearTreeView(treeView);
    }



}
