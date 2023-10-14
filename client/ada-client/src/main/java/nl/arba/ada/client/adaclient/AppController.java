package nl.arba.ada.client.adaclient;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import nl.arba.ada.client.adaclient.dialogs.Confirmation;
import nl.arba.ada.client.adaclient.treeview.*;
import nl.arba.ada.client.adaclient.utils.AdaUtils;
import nl.arba.ada.client.adaclient.utils.IdName;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.AdaObject;
import nl.arba.ada.client.api.Store;
import nl.arba.ada.client.api.addon.base.Document;
import nl.arba.ada.client.api.addon.base.Folder;
import nl.arba.ada.client.api.exceptions.AdaClassNotFoundException;
import nl.arba.ada.client.api.exceptions.NoSearchResultsException;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    private Stage stage;
    public AppController(Stage stage) {
        this.stage = stage;
    }
    @FXML
    private BorderPane main;
    @FXML
    private BorderPane topPane;
    @FXML
    private ComboBox cmbStores;
    @FXML
    private TreeView tvStore;

    private MenuBar mainMenu;

    private TreeItem selectedTreeViewItem;
    @FXML
    private TableView tableFolderContent;
    private TableRow tableFolderContentSelectedRow = null;

    private ContextMenu classMenu;
    private ContextMenu folderMenu;
    private ContextMenu newObjectMenu;
    private ContextMenu objectMenu;
    private HashMap<String, AdaClass> classCache = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initClassMenu();
        initFolderMenu();
        initObjectMenus();
        mainMenu = new MenuBar();
        Menu fileMenu = new Menu(InternationalizationUtils.get("menu.file"));
        MenuItem exit = new MenuItem(InternationalizationUtils.get("menu.file.exit"));
        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        fileMenu.getItems().add(exit);
        mainMenu.getMenus().add(fileMenu);
        topPane.setTop(mainMenu);

        try {
            List <Store> stores = AdaUtils.getDomain().getStores();
            for (Store store: stores)
                cmbStores.getItems().add(new IdName(store.getId(), store.getName()));
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        cmbStores.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    IdName selectedItem = (IdName) cmbStores.getSelectionModel().getSelectedItem();
                    onStoreChanged(AdaUtils.getDomain().getStore(selectedItem.getId()));
                }
                catch (Exception err) {}
            }
        });
        tvStore.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {
            @Override
            public void changed(ObservableValue observableValue, TreeItem o, TreeItem t1) {
                selectedTreeViewItem = t1;
                if (selectedTreeViewItem instanceof FolderTreeItem) {
                    showFolderContent(((FolderTreeItem) selectedTreeViewItem).getFolder());
                }
            }
        });
        tvStore.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent contextMenuEvent) {
                Object selectedItem = tvStore.getSelectionModel().getSelectedItem();
                if (selectedItem instanceof AdaClientTreeItem) {
                    if (((AdaClientTreeItem) selectedItem).getType().equals(TreeItemType.CLASS)) {
                        classMenu.show(tvStore, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                    }
                    else if (((AdaClientTreeItem) selectedItem).getType().equals(TreeItemType.FOLDER)) {
                        folderMenu.show(tvStore, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                    }
                }
            }
        });

        tableFolderContent.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent contextMenuEvent) {
                if (tvStore.getSelectionModel().getSelectedItem() != null && tvStore.getSelectionModel().getSelectedItem() instanceof FolderTreeItem) {
                    if (tableFolderContentSelectedRow == null)
                        newObjectMenu.show(tableFolderContent, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                    else {
                        objectMenu.setUserData(tableFolderContentSelectedRow.getItem());
                        objectMenu.show(tableFolderContent, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                    }
                }
            }
        });

        TableColumn name = new TableColumn();
        name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<AdaObject, String>, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<AdaObject, String> cellDataFeatures) {
                AdaClass objectClass = getObjectClass(cellDataFeatures.getValue());
                if (objectClass.isDocumentClass()) {
                    try {
                        return new SimpleStringProperty(cellDataFeatures.getValue().getStringProperty(Document.DOCUMENT_TITLE));
                    }
                    catch (Exception err) {}
                }
                return new SimpleStringProperty("?");
            }
        });
        name.setText("Name");
        name.prefWidthProperty().bind(tableFolderContent.widthProperty().multiply(0.8));
        tableFolderContent.getColumns().clear();
        tableFolderContent.getColumns().add(name);

        tableFolderContent.setRowFactory(new Callback<TableView, TableRow>() {
            @Override
            public TableRow call(TableView tableView) {
                TableRow newRow = new TableRow();
                newRow.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (newRow.getItem() == null) {
                            tableFolderContentSelectedRow = null;
                        }
                        else
                            tableFolderContentSelectedRow = newRow;
                    }
                });
                newRow.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        tableFolderContentSelectedRow = null;
                    }
                });
                return newRow;
            }
        });

        tableFolderContent.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                if (dragEvent.getGestureSource() != tableFolderContent && (tvStore.getSelectionModel().getSelectedItem() instanceof FolderTreeItem)) {
                    dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                dragEvent.consume();
            }
        });

        tableFolderContent.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                Dragboard db = dragEvent.getDragboard();
                if (db.hasFiles()) {
                    List <File> files = db.getFiles();
                    dragEvent.setDropCompleted(true);

                    FolderTreeItem treeItem = (FolderTreeItem) tvStore.getSelectionModel().getSelectedItem();
                    onAddDroppedDocument(treeItem, files.get(0));
                }
                else {
                    dragEvent.setDropCompleted(false);
                }
                dragEvent.consume();
            }
        });
    }

    private void initObjectMenus() {
        newObjectMenu = new ContextMenu();
        MenuItem add = new MenuItem(InternationalizationUtils.get("objecttable.contextmenu.addobject"));
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                onAddObject((FolderTreeItem) tvStore.getSelectionModel().getSelectedItem());
            }
        });
        newObjectMenu.getItems().add(add);

        objectMenu = new ContextMenu();
        MenuItem properties = new MenuItem(InternationalizationUtils.get("objecttable.contextmenu.properties"));
        properties.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                onShowObjectProperties((AdaClientTreeItem) tvStore.getSelectionModel().getSelectedItem(), (AdaObject) objectMenu.getUserData());
            }
        });
        objectMenu.getItems().add(properties);
    }

    private void initClassMenu() {
        classMenu = new ContextMenu();
        MenuItem properties = new MenuItem(InternationalizationUtils.get("treeview.class.contextmenu.properties"));
        properties.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                AdaClass classFromTree = (AdaClass) ((ClassTreeItem) tvStore.getSelectionModel().getSelectedItem()).getObject();
                onShowClassProperties(classFromTree);
            }
        });
        classMenu.getItems().add(properties);
        MenuItem add = new MenuItem(InternationalizationUtils.get("treeview.class.contextmenu.add"));
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                AdaClass parentClass = (AdaClass) ((ClassTreeItem) tvStore.getSelectionModel().getSelectedItem()).getObject();
                onShowAddClass(parentClass, selectedTreeViewItem);
            }
        });
        classMenu.getItems().add(add);
    }

    private void initFolderMenu() {
        folderMenu = new ContextMenu();
        MenuItem properties = new MenuItem(InternationalizationUtils.get("treeview.class.contextmenu.properties"));
        properties.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FolderTreeItem treeItem = (FolderTreeItem)tvStore.getSelectionModel().getSelectedItem();
                Folder folderFromTree = treeItem.getFolder();
                onShowObjectProperties(treeItem, folderFromTree);
            }
        });
        folderMenu.getItems().add(properties);

        MenuItem addSubFolder = new MenuItem(InternationalizationUtils.get("treeview.class.contextmenu.add.subfolder"));
        addSubFolder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FolderTreeItem parent = (FolderTreeItem)tvStore.getSelectionModel().getSelectedItem();
                onAddSubFolder(parent);
            }
        });
        folderMenu.getItems().add(addSubFolder);
    }

    private void onShowClassProperties(AdaClass target) {
        try {
            Store store = ((StoreTreeItem) tvStore.getRoot()).getStore();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("classproperties.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            ClassPropertiesController controller = new ClassPropertiesController(store.getAdaClass(target.getId()));
            loader.setController(controller);
            Dialog propertiesDialog = new Dialog();
            propertiesDialog.setTitle(InternationalizationUtils.get("classproperties.title") + " " + target.getName());
            propertiesDialog.getDialogPane().setContent(loader.load());
            ButtonType ok =new ButtonType(InternationalizationUtils.get("dialog.button.ok"), ButtonBar.ButtonData.OK_DONE);
            propertiesDialog.getDialogPane().getButtonTypes().add(ok);
            controller.setOkButton((Button) propertiesDialog.getDialogPane().lookupButton(ok));
            propertiesDialog.getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
            propertiesDialog.showAndWait();

            if (propertiesDialog.getResult().equals(ok)) {
                AdaClass tosave = controller.getClassToSave();
                Confirmation c = Confirmation.create(InternationalizationUtils.get("confirmation.save.class"), InternationalizationUtils.get("confirmation.delete.property.title"));
                c.showAndWait();
                if (c.getResult().equals(Boolean.TRUE)) {
                    try {
                        tosave.update();
                    }
                    catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void onAddSubFolder(FolderTreeItem parent) {
        try {
            Store store = ((StoreTreeItem) tvStore.getRoot()).getStore();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("objectproperties.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            ObjectPropertiesController controller = new ObjectPropertiesController(store, true);
            loader.setController(controller);
            Dialog propertiesDialog = new Dialog();
            propertiesDialog.setTitle(InternationalizationUtils.get("objectproperties.add.folder.title"));
            propertiesDialog.getDialogPane().setContent(loader.load());
            ButtonType ok =new ButtonType(InternationalizationUtils.get("dialog.button.ok"), ButtonBar.ButtonData.OK_DONE);
            propertiesDialog.getDialogPane().getButtonTypes().add(ok);
            controller.setOkButton((Button) propertiesDialog.getDialogPane().lookupButton(ok));
            propertiesDialog.getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
            propertiesDialog.showAndWait();

            if (propertiesDialog.getResult().equals(ok)) {
                AdaObject toadd = controller.getNewObject();
                toadd.setObjectProperty(Folder.PARENT_FOLDER, parent.getFolder().getId());
                AdaObject addedObject = store.createObject(toadd);
                Folder newFolder = Folder.create(addedObject);
                parent.getChildren().add(new FolderTreeItem(newFolder));
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void onAddDroppedDocument(FolderTreeItem parent, File droppedfile) {
        try {
            Store store = ((StoreTreeItem) tvStore.getRoot()).getStore();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("objectproperties.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            ObjectPropertiesController controller = new ObjectPropertiesController(store, droppedfile);
            loader.setController(controller);
            Dialog propertiesDialog = new Dialog();
            propertiesDialog.setTitle(InternationalizationUtils.get("objectproperties.add.folder.title"));
            propertiesDialog.getDialogPane().setContent(loader.load());
            ButtonType ok =new ButtonType(InternationalizationUtils.get("dialog.button.ok"), ButtonBar.ButtonData.OK_DONE);
            propertiesDialog.getDialogPane().getButtonTypes().add(ok);
            controller.setOkButton((Button) propertiesDialog.getDialogPane().lookupButton(ok));
            propertiesDialog.getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
            propertiesDialog.showAndWait();

            if (propertiesDialog.getResult().equals(ok)) {
                AdaObject toadd = controller.getNewObject();
                AdaObject addedObject = store.createObject(toadd);
                parent.getFolder().addChild(addedObject);
                showFolderContent(parent.getFolder());
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void onAddObject(FolderTreeItem parent) {
        try {
            Store store = ((StoreTreeItem) tvStore.getRoot()).getStore();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("objectproperties.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            ObjectPropertiesController controller = new ObjectPropertiesController(store, false);
            loader.setController(controller);
            Dialog propertiesDialog = new Dialog();
            propertiesDialog.setTitle(InternationalizationUtils.get("objectproperties.add.folder.title"));
            propertiesDialog.getDialogPane().setContent(loader.load());
            ButtonType ok =new ButtonType(InternationalizationUtils.get("dialog.button.ok"), ButtonBar.ButtonData.OK_DONE);
            propertiesDialog.getDialogPane().getButtonTypes().add(ok);
            controller.setOkButton((Button) propertiesDialog.getDialogPane().lookupButton(ok));
            propertiesDialog.getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
            propertiesDialog.showAndWait();

            if (propertiesDialog.getResult().equals(ok)) {
                AdaObject toadd = controller.getNewObject();
                AdaObject addedObject = store.createObject(toadd);
                parent.getFolder().addChild(addedObject);
                showFolderContent(parent.getFolder());
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void onShowObjectProperties(AdaClientTreeItem treeitem, AdaObject target) {
        try {
            Store store = ((StoreTreeItem) tvStore.getRoot()).getStore();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("objectproperties.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            ObjectPropertiesController controller = new ObjectPropertiesController(store.getObject(target.getId()));
            loader.setController(controller);
            Dialog propertiesDialog = new Dialog();
            if (target instanceof Folder)
                propertiesDialog.setTitle(InternationalizationUtils.get("objectproperties.title") + " " + ((Folder) target).getName());
            propertiesDialog.getDialogPane().setContent(loader.load());
            ButtonType ok =new ButtonType(InternationalizationUtils.get("dialog.button.ok"), ButtonBar.ButtonData.OK_DONE);
            propertiesDialog.getDialogPane().getButtonTypes().add(ok);
            controller.setOkButton((Button) propertiesDialog.getDialogPane().lookupButton(ok));
            propertiesDialog.getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
            propertiesDialog.showAndWait();

            if (propertiesDialog.getResult().equals(ok)) {
                AdaObject refreshed = controller.getUpdatedObject().update();
                if (treeitem instanceof FolderTreeItem) {
                    treeitem.valueProperty().setValue(refreshed.getStringProperty("Name"));
                }
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void onShowAddClass(AdaClass parentclass, TreeItem parent) {
        try {
            Store store = ((StoreTreeItem) tvStore.getRoot()).getStore();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("classproperties.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            ClassPropertiesController controller = new ClassPropertiesController(store.getAdaClass(parentclass.getId()), true);
            loader.setController(controller);
            Dialog propertiesDialog = new Dialog();
            propertiesDialog.setTitle(InternationalizationUtils.get("classproperties.title.adding"));
            propertiesDialog.getDialogPane().setContent(loader.load());
            ButtonType ok =new ButtonType(InternationalizationUtils.get("dialog.button.ok"), ButtonBar.ButtonData.OK_DONE);
            propertiesDialog.getDialogPane().getButtonTypes().add(ok);
            controller.setOkButton((Button) propertiesDialog.getDialogPane().lookupButton(ok));
            propertiesDialog.getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
            propertiesDialog.showAndWait();

            if (propertiesDialog.getResult().equals(ok)) {
                AdaClass tosave = controller.getClassToSave();
                if (controller.isAdding()) {
                    AdaClass newClass = tosave.getStore().addClass(tosave);
                    ClassTreeItem newChild = new ClassTreeItem(newClass);
                    parent.getChildren().add(newChild);
                }
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void expandTreeItem(TreeItem toexpand) {
        toexpand.setExpanded(true);
        for (Object child: toexpand.getChildren())
            expandTreeItem((TreeItem) child);
    }

    private void onStoreChanged(Store store) {
        TreeItem root = new StoreTreeItem(store);
        TreeItem datamodel = new TreeItem(InternationalizationUtils.get("treeview.datamodel"));
        root.getChildren().add(datamodel);
        TreeItem classes = new TreeItem(InternationalizationUtils.get("treeview.datamodel.classes"));
        datamodel.getChildren().add(classes);
        try {
            addClasses(store.getClasses(), classes, null);
        }
        catch (AdaClassNotFoundException e) {}

        TreeItem browse = new BrowseTreeItem();
        try {
            Folder[] rootFolders = store.getRootFolders();
            for (Folder current: rootFolders)
                browse.getChildren().add(new FolderTreeItem(current));
        }
        catch (NoSearchResultsException nsre) {}

        root.getChildren().add(browse);
        tvStore.setRoot(root);

        tvStore.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {
            @Override
            public void changed(ObservableValue observableValue, TreeItem o, TreeItem t1) {
                if (t1 != null && t1 instanceof FolderTreeItem)
                    ((FolderTreeItem) t1).onSelection();
            }
        });
        expandTreeItem(tvStore.getRoot());
    }

    private void addClasses(AdaClass[] classes, TreeItem parent, String parentclassid) {
        try {
            for (AdaClass current : classes) {
                if ((current.getParentClass() == null && parentclassid == null) || (current.getParentClass() != null && current.getParentClass().getId().equals(parentclassid))) {
                    ClassTreeItem newChild = new ClassTreeItem(current);
                    addClasses(classes, newChild, current.getId());
                    parent.getChildren().add(newChild);
                }
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void showFolderContent(Folder folder) {
        try {
            AdaObject[] children = folder.getChildren();
            System.out.println("Found " + children.length + " children");
            tableFolderContent.getItems().clear();
            tableFolderContent.getItems().addAll(children);
        }
        catch (Exception err) {
            err.printStackTrace();
        }

    }

    private AdaClass getObjectClass(AdaObject object) {
        if (!classCache.containsKey(object.getClassId())) {
            try {
                classCache.put(object.getClassId(), object.getStore().getAdaClass(object.getClassId()));
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }
        return classCache.get(object.getClassId());
    }
}