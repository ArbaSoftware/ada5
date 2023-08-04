package nl.arba.ada.client.adaclient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import nl.arba.ada.client.adaclient.treeview.AdaClientTreeItem;
import nl.arba.ada.client.adaclient.treeview.ClassTreeItem;
import nl.arba.ada.client.adaclient.treeview.StoreTreeItem;
import nl.arba.ada.client.adaclient.treeview.TreeItemType;
import nl.arba.ada.client.adaclient.utils.AdaUtils;
import nl.arba.ada.client.adaclient.utils.IdName;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.Store;

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

    private ContextMenu classMenu;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initClassMenu();
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
        tvStore.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent contextMenuEvent) {
                Object selectedItem = tvStore.getSelectionModel().getSelectedItem();
                if (selectedItem instanceof AdaClientTreeItem) {
                    if (((AdaClientTreeItem) selectedItem).getType().equals(TreeItemType.CLASS)) {
                        classMenu.show(tvStore, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
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
                AdaClass forClass = (AdaClass) ((ClassTreeItem) tvStore.getSelectionModel().getSelectedItem()).getObject();
                onShowClassProperties(forClass);
            }
        });
        classMenu.getItems().add(properties);
    }

    private void onShowClassProperties(AdaClass target) {
        try {
            Store store = ((StoreTreeItem) tvStore.getRoot()).getStore();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("classproperties.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            loader.setController(new ClassPropertiesController(store.getAdaClass(target.getId())));
            Dialog propertiesDialog = new Dialog();
            propertiesDialog.setTitle(InternationalizationUtils.get("classproperties.title") + " " + target.getName());
            propertiesDialog.getDialogPane().setContent(loader.load());
            propertiesDialog.getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.ok"), ButtonBar.ButtonData.OK_DONE));
            propertiesDialog.getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
            propertiesDialog.show();
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
        addClasses(store, classes);
        tvStore.setRoot(root);
        expandTreeItem(tvStore.getRoot());
    }

    private void addClasses(Store store, TreeItem parent) {
        try {
            AdaClass[] classes = store.getClasses();
            for (AdaClass current : classes) {
                parent.getChildren().add(new ClassTreeItem(current));
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}