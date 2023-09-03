package nl.arba.ada.client.adaclient;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.arba.ada.client.adaclient.dialogs.Confirmation;
import nl.arba.ada.client.adaclient.treeview.*;
import nl.arba.ada.client.adaclient.utils.AdaUtils;
import nl.arba.ada.client.adaclient.utils.IdName;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.AdaObject;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.Store;
import nl.arba.ada.client.api.addon.base.Folder;
import nl.arba.ada.client.api.exceptions.AdaClassNotFoundException;
import nl.arba.ada.client.api.exceptions.NoSearchResultsException;

import java.net.URL;
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

    private ContextMenu classMenu;
    private ContextMenu folderMenu;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initClassMenu();
        initFolderMenu();
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
                Folder folderFromTree = ((FolderTreeItem) tvStore.getSelectionModel().getSelectedItem()).getFolder();
                onShowObjectProperties(folderFromTree);
            }
        });
        folderMenu.getItems().add(properties);
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

    private void onShowObjectProperties(AdaObject target) {
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
}