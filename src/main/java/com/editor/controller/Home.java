package com.editor.controller;


import com.editor.util.FileUtils;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import com.editor.Main;
import com.editor.util.SFTPClient;
import com.editor.util.ShowBox;
import com.editor.util.TranslationTextComponent;
import com.editor.util.directory.Directory;
import com.editor.util.directory.DirectoryElement;
import com.editor.util.directory.IDirectory;
import com.editor.util.json.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.editor.Main.FILENAME_REGEX;


public class Home {
    @FXML
    public TreeView<IDirectory> directoryTreeView;
    @FXML
    public TreeView<IUnitJson> treeView;

    public Json json;
    public boolean jsonIsRemote = false;
    public String remotePath;
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
    private IUnitJson unitJson;
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
    private MenuItem infoThisAppItem;
    @FXML
    private MenuItem usersGuideItem;
    @FXML
    private RadioMenuItem textEquivalentCheck;
    @FXML
    private Label nameLabel;
    @FXML
    private Label valueLabel;

    private TextArea textAreaJson;



    @FXML
    public void initialize() {
        this.setText();

        /* _______Menu________ */
        /* пункт файл */
        this.openLocal.setOnAction(this::eventClickOpen); // открытие файла локально на пк
        this.createLocal.setOnAction(event -> eventClickCreateLocal(event, null)); // создание файла локально
        this.openLocalFolder.setOnAction(this::eventClickOpenLocalFolder); // открытие папки локально
        this.saveFile.setOnAction(this::eventSaveFile); // сохранить файл
        this.exitProg.setOnAction(event -> Platform.exit());
        this.exitFile.setOnAction(event -> {
            clearTreeView(this.treeView);
            this.json = null;
        });
        this.exitFolder.setOnAction(event -> {
            clearTreeView(this.treeView);
            clearTreeView(this.directoryTreeView);
            this.json = null;
        });
        this.saveAs.setOnAction(this::eventSaveAs);
        /* __________________ */

        this.scene.setOnKeyReleased(this::onKeyReleased);
        /* пункт удаленного подключения */
        this.configureConn.setOnAction(this::eventClickConfigureConn); // открытие окна конфигурации удаленного подключения
        this.connectToFolder.setOnAction(this::eventClickOpenRemoteFolder); // подключиться по удалунному соединению к файлу
        this.disconnect.setOnAction(event -> disconnect()); // отключиться
        /* __________________ */

        ToggleGroup group = new ToggleGroup();
        File[] translates = TranslationTextComponent.getTranslateFiles();
        for (File f : translates) {
            RadioMenuItem item = new RadioMenuItem(f.getName());
            item.setOnAction(event -> onChangeLanguage(item));
            item.setToggleGroup(group);
            if (item.getText().equals(TranslationTextComponent.currentTranslation.getName())) {
                group.selectToggle(item);
            }
            language.getItems().add(item);
        }


        this.directoryTreeView.setCellFactory(this::createDirectoryTreeView);
        this.directoryTreeView.setOnMouseClicked(this::onDirectoryTreeViewClick);
        this.directoryTreeView.setOnContextMenuRequested(this::eventDirectoryTreeViewContextMenuRequest);


        this.treeView.setCellFactory(param -> createTextFieldTreeCell());
        this.treeView.setOnEditCommit(this::onEditCommit);
        this.treeView.setOnMouseClicked(this::onMauseClickTreeView);
        this.treeView.setOnContextMenuRequested(this::eventTreeViewContextMenuRequest);

        /* панель изменения элементов json */
        this.saveButton.setOnAction(this::onActionSaveButton);
        this.cancelButton.setOnAction(this::onActionCancelButton);

        /* панель вида */
        this.textEquivalentCheck.setOnAction(this::eventOnActionTextEquivalentCheck);

    }

    private void eventSaveAs(ActionEvent event) {
        if (this.workFile == null) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(new TranslationTextComponent("menu_item.save.as").toString());
        fileChooser.setInitialFileName("NewJson");

        // Установка фильтров для типов файлов
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("Json Files (*.json)", "*.json");
        fileChooser.getExtensionFilters().addAll(jsonFilter, txtFilter);
        fileChooser.setSelectedExtensionFilter(jsonFilter);

        // Открытие диалогового окна "Сохранить как"
        File file = fileChooser.showSaveDialog(scene.getScene().getWindow());

        if (file != null) {
            this.workFile = file;
            this.eventSaveFile(null);
        }

    }

    private void onKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.isControlDown() && keyEvent.getCode().equals(KeyCode.S)){
            this.eventSaveFile(null);
        }
    }

    /**
     * обновить текст на всей странице
     */
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
        infoThisAppItem.setText(new TranslationTextComponent("menu_item.information.about.this.app").toString());
        usersGuideItem.setText(new TranslationTextComponent("menu_item.information.guide").toString());
        language.setText(new TranslationTextComponent("menu.language").toString());
        fileMenu.setText(new TranslationTextComponent("menu.file").toString());
        connectMenu.setText(new TranslationTextComponent("menu.connect").toString());
        infoMenu.setText(new TranslationTextComponent("menu.info").toString());
        viewMenu.setText(new TranslationTextComponent("menu.view").toString());
        nameLabel.setText(new TranslationTextComponent("name").toString());
        nameUnitTextField.setPromptText(new TranslationTextComponent("name").toString());
        valueLabel.setText(new TranslationTextComponent("value").toString());
        textEquivalentCheck.setText(new TranslationTextComponent("menu_item.view.text_equivalent").toString());
        numCheckBox.setText(new TranslationTextComponent("number").toString());
        saveButton.setText(new TranslationTextComponent("button.save").toString());
        cancelButton.setText(new TranslationTextComponent("button.cancel").toString());
    }



    /**
     *  ивент нажатия на кнопку "текстовый эквивалент"
     *  */
    private void eventOnActionTextEquivalentCheck(ActionEvent event) {
        if (this.textEquivalentCheck.isSelected()) { // если при нажатии флажок нажат
            TextArea textArea = new TextArea();
            // при выделении флажка
            if (this.treeView.getRoot() == null || this.treeView.getRoot().getChildren().isEmpty()) {
                if (this.workFile == null) {
                    this.json = null;
                    textArea.setEditable(false);
                }
            } else {
                if (this.json != null) {
                    try {
                        textArea.setText(this.json.toString());
                    } catch (JsonException ex) {
                        ShowBox.showError(new TranslationTextComponent("error.json.change.text", ex.getMessage()));
                    }
                } else {
                    this.json = new Json();
                }
            }

            this.textAreaJson = (TextArea) replace((Pane) this.treeView.getParent(), this.treeView, textArea);
            showJson();
            return;
        }

        try {
            this.json = new Json(this.textAreaJson.getText());
        } catch (JsonException ex) {
            ShowBox.showError(new TranslationTextComponent("error.json.change.text", ex.getMessage()));
        }
        this.treeView = (TreeView<IUnitJson>) replace((Pane)this.treeView.getParent(), this.textAreaJson, this.treeView);
        showJson();
    }


    /**
     * Сменя языка в зависемости от выбранного языка
     * @param item
     */
    private void onChangeLanguage(RadioMenuItem item) {
        File[] translates = TranslationTextComponent.getTranslateFiles();
        for (File f : translates) {
            String name = item.getText();
            if (name.equals(f.getName())) {
                TranslationTextComponent.currentTranslation = f;
                break;
            }
        }
        this.setText();
    }

    /**
     * Создания контекстного меню при правом клике по дереву json
     */
    private void eventTreeViewContextMenuRequest(ContextMenuEvent event) {
        if (this.json == null) return;
        TreeItem<IUnitJson> selectedItem = treeView.getSelectionModel().getSelectedItem();

        ContextMenu menu = new ContextMenu();

        MenuItem deleteMenuItem = new MenuItem(new TranslationTextComponent("delete").toString());
        MenuItem addUnitJsonMenuItem = new MenuItem(new TranslationTextComponent("context_menu.add_unit").toString());
        MenuItem addUnitListJsonMenuItem = new MenuItem(new TranslationTextComponent("context_menu.add_unit_list").toString());
        MenuItem addArrayListMenuItem = new MenuItem(new TranslationTextComponent("context_menu.add_array_list").toString());

        deleteMenuItem.setOnAction(event1 -> eventDeleteJsonUnitOnAction(event1, selectedItem));       // Удалить элемент
        addUnitJsonMenuItem.setOnAction(event1 -> eventAddUnitOnAction(event1, selectedItem));         // Создание unit
        addUnitListJsonMenuItem.setOnAction(event1 -> eventAddUnitListOnAction(event1, selectedItem)); // Создать список units
        addArrayListMenuItem.setOnAction(event1 -> eventAddArrayListOnAction(event1, selectedItem));   // Создать массив


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

    /**
     * Эвент создание элемента в дереве json
     * @param event
     * @param selectedItem элемент дерева по которому нажали
     * @param labelUnit название элемента
     * @param typeUnit тип для хранения(только если это список иначе null)
     */
    private void eventAddEmptyUnit(ActionEvent event, TreeItem<IUnitJson> selectedItem, String labelUnit, TypeUnit typeUnit) {
        if (this.treeView.getRoot() == null || this.treeView.getRoot().getChildren().isEmpty()) {
            labelUnit = (typeUnit == TypeUnit.UNIT) ? "[ArrayList]" : "[UnitList]";
            this.treeView.setRoot(
                    new TreeItem<>(
                            new UnitJson(
                                    labelUnit,
                                    new ValueUnitsJsonList(typeUnit),
                                    IUnitJson.TypeValue.UNITS_ARRAY
            )));
            this.json = new Json(new ArrayList<>(), typeUnit);
            this.treeView.refresh();
            return;
        }

        IUnitJson parentUnit = selectedItem.getValue();
        ValueUnitsJsonList parentValue = (ValueUnitsJsonList) parentUnit.getValue();
        IUnitJson emptyUnit;

        Object value;
        IUnitJson.TypeValue type;
        if (typeUnit != null) {
            value = new ValueUnitsJsonList(typeUnit);
            type = IUnitJson.TypeValue.UNITS_ARRAY;
        } else {
            value = "";
            type = IUnitJson.TypeValue.STRING;
        }

        if (parentValue.getType() == TypeUnit.UNIT) {
            emptyUnit = new UnitJson(new TranslationTextComponent(labelUnit).toString(), value, type);
        } else {
            emptyUnit = new ArrayUnitJson(value, type, parentValue.getValue().size());
        }

        if (selectedItem == treeView.getRoot()) {
            json.getValue().add(emptyUnit);
            treeView.getRoot().getChildren().add(new TreeItem<>(emptyUnit));
            treeView.refresh();
            return;
        }

        if (parentUnit.getTypeValue() != IUnitJson.TypeValue.UNITS_ARRAY) {
            throw new RuntimeException("List expected");
        }

        IUnitJson newParentUnit = Json.newUnit(parentUnit);
        newParentUnit.getValueList().add(emptyUnit);
        json.set(json.indexOf(parentUnit), newParentUnit);

        TreeItem<IUnitJson> treeItem = new TreeItem<>(emptyUnit);
        treeItem.setExpanded(true);
        selectedItem.setValue(newParentUnit);
        selectedItem.getChildren().add(treeItem);
        treeView.refresh();
    }
    /* создание array */
    private void eventAddArrayListOnAction(ActionEvent event, TreeItem<IUnitJson> selectedItem) {
        eventAddEmptyUnit(event, selectedItem, "unit.new_array_list", TypeUnit.ARRAY_UNIT);
    }
    /* создание unit list */
    private void eventAddUnitListOnAction(ActionEvent event, TreeItem<IUnitJson> selectedItem) {
        eventAddEmptyUnit(event, selectedItem, "unit.new_unit_list", TypeUnit.UNIT);
    }
    /* создание unit */
    private void eventAddUnitOnAction(ActionEvent event, TreeItem<IUnitJson> selectedItem) {
        eventAddEmptyUnit(event, selectedItem, "unit.new_unit", null);
    }

    private void eventDeleteJsonUnitOnAction(ActionEvent event, TreeItem<IUnitJson> selectedItem) {
        if (selectedItem == this.treeView.getRoot()) {
            this.json = new Json();
            this.treeView.getRoot().getChildren().clear();
            this.clearTreeView(treeView);
            return;
        }
        IUnitJson selectedUnit = selectedItem.getValue();

        int[] indexs = this.json.indexOf(selectedUnit);

        this.json.delete(indexs);
        selectedItem.getParent().getValue().getValueList().remove(indexs[indexs.length - 1]);
        //this.getParentTreeItem(indexs).getChildren().remove(indexs[indexs.length - 1]);
        this.treeView.refresh(); // Обновление TreeView
    }

    private void clearTreeView(TreeView<?> treeView) {
        this.json = null;
        if (this.textEquivalentCheck.isSelected()) {
            this.textAreaJson.clear();
        } else {
            this.treeView.setRoot(null);
            this.treeView.refresh();
        }
    }

    private void eventDirectoryTreeViewContextMenuRequest(ContextMenuEvent event) {
        if (directoryTreeView.getSelectionModel().isEmpty()) {
            return;
        }
        ContextMenu menu = new ContextMenu();
        if (directoryTreeView.getSelectionModel().getSelectedItem().getValue() instanceof Directory) {
            MenuItem createJson = new MenuItem(new TranslationTextComponent("menu_item.create.json").toString());
            MenuItem createDirectory = new MenuItem(new TranslationTextComponent("menu_item.create.directory").toString());
            createDirectory.setOnAction(this::eventCreateDirectory);
            createJson.setOnAction(event1 -> eventCreateJson(event1, directoryTreeView.getSelectionModel().getSelectedItem().getValue()));
            menu.getItems().setAll(createJson, createDirectory);
            menu.show(scene.getScene().getWindow(), event.getScreenX(), event.getScreenY());
            return;
        }
        DirectoryElement selectedElement = (DirectoryElement) directoryTreeView.getSelectionModel().getSelectedItem().getValue();

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

    /*private TreeItem<IUnitJson> getParentTreeItem(int[] indexes) {
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
    }*/

    private void deleteJsonRemote(String pathToElement) throws SftpException, JSchException {
        SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
        client.connect();
        client.deleteFile(pathToElement);
        client.disconnect();
    }

    private void eventDeleteJson(ActionEvent event) {
        TreeItem<IDirectory> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();
        DirectoryElement selectedElement = (DirectoryElement) selectedItem.getValue();

        if (selectedElement.isRemote()) {
            try {
                deleteJsonRemote(selectedElement.pathToElement());
                selectedItem.getParent().getChildren().remove(selectedItem);
                directoryTreeView.refresh();
            } catch (Exception e) {
                ShowBox.showError(new TranslationTextComponent("error.unabletodelete.file", e.getMessage()));
                e.printStackTrace();
            }
        } else {
            if (new File(selectedElement.pathToElement()).delete()) {
                ShowBox.showInfo(new TranslationTextComponent("success"), new TranslationTextComponent("succes.file.deleted"));
                selectedItem.getParent().getChildren().remove(selectedItem);
                directoryTreeView.refresh();
            } else {
                ShowBox.showError(new TranslationTextComponent("error.unabletodelete.file", selectedElement.pathToElement()));
            }
        }
    }

    private void createRemoteDirectory(String remotePath, String dirName) throws JSchException, SftpException {
        SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
        client.connect();
        client.createDirectory(remotePath + File.separator + dirName);
        Directory directory = client.getDirectory(directoryTreeView.getSelectionModel().getSelectedItem().getValue().pathToElement(), directoryTreeView.getSelectionModel().getSelectedItem().getValue());
        this.fillTree(directoryTreeView.getSelectionModel().getSelectedItem(), client.getDirectory(directoryTreeView.getSelectionModel().getSelectedItem().getValue().pathToElement(), directory));
        client.disconnect();
    }

    private void eventCreateDirectory(ActionEvent event) {
        TreeItem<IDirectory> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();


        IDirectory selectedElement = selectedItem.getValue();

        Stage stage = new Stage();
        AnchorPane pane = new AnchorPane();
        VBox box = new VBox();
        Text text = new Text(new TranslationTextComponent("text.enter", new TranslationTextComponent("dirname")).toString());
        Button ok = new Button(new TranslationTextComponent("ok").toString());
        TextField field = new TextField();
        field.setPromptText(new TranslationTextComponent("dirname").toString());
        if (selectedElement.isRemote()) {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!Pattern.compile(FILENAME_REGEX).matcher(newValue).matches()) {
                    text.setText(oldValue);
                }
            });
            box.setAlignment(Pos.CENTER);
            ok.setOnAction(event1 -> {
                try {
                    createRemoteDirectory(selectedElement.pathToElement(), field.getText());
                } catch (Exception e) {
                    ShowBox.showError(new TranslationTextComponent("error.create.remote.file", e.getMessage()));
                    e.printStackTrace();
                } finally {
                    stage.close();
                    directoryTreeView.refresh();
                }
            });
        } else {
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!Pattern.compile(FILENAME_REGEX).matcher(newValue).matches()) {
                    text.setText(oldValue);
                }
            });
            box.setAlignment(Pos.CENTER);
            ok.setOnAction(event1 -> {
                try {
                    File f = new File( selectedElement.pathToElement() + File.separator + field.getText());
                    f.mkdirs();
                    this.fillTree(directoryTreeView.getSelectionModel().getSelectedItem(), new Directory(f));
                } catch (Exception e) {
                    ShowBox.showError(new TranslationTextComponent("error.create.dir", e.getMessage()));
                    e.printStackTrace();
                } finally {
                    stage.close();
                    directoryTreeView.refresh();
                }
            });

        }
        box.getChildren().setAll(text, field, ok);
        pane.getChildren().add(box);
        stage.setScene(new Scene(pane));
        stage.show();
    }
    private void eventDeleteDirectory(ActionEvent event) {
        TreeItem<IDirectory> selectedItem = directoryTreeView.getSelectionModel().getSelectedItem();
        DirectoryElement selectedItemValue = (DirectoryElement) selectedItem.getValue();
        if (selectedItemValue.isRemote()){
            SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
            try {
                client.connect();
                client.deleteDirectory(selectedItemValue.pathToElement());
                directoryTreeView.getSelectionModel().getSelectedItem().getParent().getChildren().remove(directoryTreeView.getSelectionModel().getSelectedItem());
            } catch (Exception e) {
                ShowBox.showError(new TranslationTextComponent("error.delete.remote.dir", e.getMessage()));
            } finally {
                client.disconnect();
                directoryTreeView.refresh();
            }
        }else {
            File f = new File(selectedItemValue.pathToElement());
            FileUtils.deleteFolder(f.getAbsolutePath());
            ShowBox.showInfo(new TranslationTextComponent("showbox.info.headertext"), new TranslationTextComponent("succes.file.deleted"));
            directoryTreeView.getSelectionModel().getSelectedItem().getParent().getChildren().remove(directoryTreeView.getSelectionModel().getSelectedItem());

        }

    }

    private void createRemoteJsonFile(String remotePath, String fileName) throws JSchException, SftpException {
        SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
        client.connect();
        client.createFile(remotePath + File.separator + fileName + ".json");
        Directory directory = client.getDirectory(directoryTreeView.getSelectionModel().getSelectedItem().getValue().pathToElement(), directoryTreeView.getSelectionModel().getSelectedItem().getValue());
        this.fillTree(directoryTreeView.getSelectionModel().getSelectedItem(), client.getDirectory(directoryTreeView.getSelectionModel().getSelectedItem().getValue().pathToElement(), directory));
        client.disconnect();
    }

    private void eventCreateJson(ActionEvent event, IDirectory idir) {
        if (idir.isRemote()) {
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
                    createRemoteJsonFile(idir.pathToElement(), field.getText());
                    ShowBox.showInfo(new TranslationTextComponent("success"), new TranslationTextComponent("success.file.created"));
                } catch (Exception e) {
                    ShowBox.showError(new TranslationTextComponent("error.create.remote.file", e.getMessage()));
                    e.printStackTrace();
                } finally {
                    stage.close();
                    directoryTreeView.refresh();
                }

            });
            box.getChildren().setAll(text, field, ok);
            pane.getChildren().add(box);

            stage.setScene(new Scene(pane));
            stage.show();
        } else {
            eventClickCreateLocal(null, new File(idir.pathToElement()));
            this.fillTree(directoryTreeView.getSelectionModel().getSelectedItem(), new Directory((DirectoryElement) directoryTreeView.getSelectionModel().getSelectedItem().getValue()));

        }
    }

    private void eventClickOpenRemoteFolder(Event event) {
        SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
        try {
            client.connect();
            Directory dir = client.getDirectory("/", null);
            openDirectory(dir);
        } catch (Exception e) {
            ShowBox.showError(new TranslationTextComponent("error.connection.unsuccessful", e.getMessage()));
        } finally {
            if (client.isConnected()) {
                client.disconnect();
            }
        }
    }

    private void openDirectory(Directory dir) {
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
            if (selectedItem == null) {
                return;
            }
            if (selectedItem.getValue() instanceof Directory) {
                return;
            }
            DirectoryElement selectedObject = (DirectoryElement) selectedItem.getValue();

            if (selectedObject.isRemote()) {
                SFTPClient client = new SFTPClient(ConfigureConn.savedHostname, ConfigureConn.savedPort, ConfigureConn.savedUsername, ConfigureConn.savedPassword);
                if (selectedObject.isJson) {
                    try {
                        client.connect();
                        File file = client.downloadFile(selectedObject.pathToElement(), Main.tempDir + File.separator + selectedObject.getName());
                        this.json = new Json(file);
                        showJson();
                        jsonIsRemote = true;
                        remotePath = selectedObject.pathToElement();
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
                        directory = client.getDirectory(selectedObject.pathToElement(), selectedObject);
                    } catch (Exception e) {
                        ShowBox.showError(new TranslationTextComponent("error.connection.unsuccessful", e.getMessage()));
                        e.printStackTrace();
                        return;
                    } finally {
                        if (client.isConnected()) {
                            client.disconnect();
                        }
                    }
                    this.fillTree(selectedItem, directory);
                }
                return;
            }
            if (selectedObject.isJson) {
                try {
                    this.json = new Json(new File(selectedObject.pathToElement()));
                    showJson();
                    workFile = new File(selectedObject.pathToElement());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Directory directory = new Directory(selectedObject);
                this.fillTree(selectedItem, directory);
            }
        }
    }

    private void fillTree(TreeItem<IDirectory> selectedItem, Directory directory){
        selectedItem.getChildren().clear();
        for (IDirectory element : directory.elementlist) {
            selectedItem.getChildren().add(new TreeItem<>(element));
        }
        for (IDirectory element : directory.jsonFiles) {
            selectedItem.getChildren().add(new TreeItem<>(element));
        }
        selectedItem.setExpanded(true);
        directoryTreeView.refresh();
    }



    private void eventSaveFile(ActionEvent event) {
        if (this.json == null) return;

        if (this.textEquivalentCheck.isSelected()) {
            if (this.textAreaJson.getText().isEmpty()) {
                return;
            }
            try {
                this.json = new Json(textAreaJson.getText());
            } catch (JsonException e) {
                ShowBox.showError(new TranslationTextComponent("error.json.load", e.getMessage()));
            }
        }

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
            }else {
                clearTreeView(treeView);
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
            this.showJson();
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
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/configure_conn.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (root == null) {
            return;
        }
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Двойной щелчек по TreeItem
     * @param event
     */
    private void onEditCommit(TreeView.EditEvent<IUnitJson> event) {
        TreeItem<IUnitJson> editedItem = event.getTreeItem();
        if (
            editedItem == null ||
            editedItem == this.treeView.getRoot() ||
            editedItem.getValue().getTypeUnit() == TypeUnit.ARRAY_UNIT
        ) {
            assert editedItem != null;
            editedItem.setValue(event.getOldValue());
            return;
        }

        event.getNewValue().setValue(event.getOldValue());
        int[] index = this.json.indexOf(editedItem.getValue());
        IUnitJson object = event.getNewValue();
        object.setValue(event.getNewValue());
        editedItem.setValue(object);
        this.json.set(index, object);

        treeView.refresh(); // Обновление TreeView
    }

    private void eventClickOpenLocalFolder(Event event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("");

        workDirectory = chooser.showDialog(scene.getScene().getWindow());

        if (workDirectory == null) return;
        Directory dir = new Directory(workDirectory);
        openDirectory(dir);
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


    public void showJson() {
        disableChangePanel(true);
        if (this.json == null) {
            return;
        }

        if (this.textEquivalentCheck.isSelected()) {
            this.textAreaJson.setEditable(true);
            this.textAreaJson.setText(this.json.toString());
            return;
        }

        List<IUnitJson> unitList = this.json.getValue();
        UnitJson rootUnit;
        if (this.json.getType() == TypeUnit.UNIT) {
            rootUnit = new UnitJson(
                    "[UnitList]",
                    new ValueUnitsJsonList(TypeUnit.UNIT),
                    IUnitJson.TypeValue.UNITS_ARRAY
            );
        } else if (this.json.getType() == TypeUnit.ARRAY_UNIT) {
            rootUnit = new UnitJson(
                    "[ArrayList]",
                    new ValueUnitsJsonList(TypeUnit.ARRAY_UNIT),
                    IUnitJson.TypeValue.UNITS_ARRAY
            );
        } else {
            return;
        }
        TreeItem<IUnitJson> rootItem = new TreeItem<>(rootUnit);
        rootItem.setExpanded(true);
        if (unitList != null) {
            for (IUnitJson iUnitJson : unitList) {
                if (iUnitJson.getValue() instanceof ValueUnitsJsonList) {
                    rootItem.getChildren().add(recursionShowJson(iUnitJson));
                }
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
                }
            }
        }
        return item;
    }



    private void onMauseClickTreeView(MouseEvent event) {
        if (event.getClickCount() == 1) { // Обработка одиночного щелчка
            TreeItem<IUnitJson> selectedItem = this.treeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;

            // если это корневой элемент
            if (selectedItem == treeView.getRoot()) {
                this.disableChangePanel(true);

                if (this.json.getType() == TypeUnit.UNIT) {
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

    private void onActionSaveButton(ActionEvent event) {
        IUnitJson newUnit = this.unitJson;
        newUnit.setName(this.nameUnitTextField.getText());

        if (this.numCheckBox.isSelected()) {
            newUnit.setValue(this.valueUnitTextArea.getText(), IUnitJson.TypeValue.NUMBER);
        } else {
            newUnit.setValue(this.valueUnitTextArea.getText(), IUnitJson.TypeValue.STRING);
        }

        this.json.set(json.indexOf(this.unitJson), newUnit);
        this.unitJson = newUnit;

        treeView.refresh(); // Обновление TreeView
    }

    private void disconnect() {
        for (SFTPClient client : SFTPClient.connections) {
            client.disconnect();
        }
        clearTreeView(directoryTreeView);
        clearTreeView(treeView);
        jsonIsRemote = false;
        remotePath = "";
    }


    public static Node replace(Pane parent, Node whatReplace, Node replaceTo) {
        if (parent != null && whatReplace != null && replaceTo != null) {
            int index = parent.getChildren().indexOf(whatReplace);
            if (index != -1) {
                // Заменяем ноду и устанавливаем ее на место старой ноды
                replaceTo.setLayoutX(whatReplace.getLayoutX());
                replaceTo.setLayoutY(whatReplace.getLayoutY());
                replaceTo.setTranslateX(whatReplace.getTranslateX());
                replaceTo.setTranslateY(whatReplace.getTranslateY());
                replaceTo.setRotate(whatReplace.getRotate());
                replaceTo.setScaleX(whatReplace.getScaleX());
                replaceTo.setScaleY(whatReplace.getScaleY());
                replaceTo.setVisible(true);
                parent.getChildren().set(index, replaceTo);
                return replaceTo;
            }
        }
        return null;
    }
}
