package nl.arba.ada.client.adaclient;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import nl.arba.ada.client.adaclient.controls.RightsTable;
import nl.arba.ada.client.adaclient.dialogs.EditProperty;
import nl.arba.ada.client.adaclient.dialogs.OkListener;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.Property;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.Right;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClassPropertiesController implements Initializable {
    private AdaClass adaClass;
    @FXML
    private TextField txtName;
    @FXML
    private TableView tableProperties;
    @FXML
    private BorderPane rightsPane;
    private ContextMenu cmProperty;
    private ContextMenu cmAddProperty;
    private Property editProperty;
    private List<Right> classRights;
    private ArrayList<Property> toDelete = new ArrayList<>();
    private boolean save;
    private boolean hasChanges = false;
    private Button okButton;
    private RightsTable rightstable;
    private boolean adding = false;
    private AdaClass parentClass;
    private Domain domain;

    public ClassPropertiesController(AdaClass target) {
        this(target, false);
    }

    public ClassPropertiesController(AdaClass target, boolean adding) {
        this.adding = adding;
        this.adaClass = (adding ? null: target);
        this.parentClass = (adding ? target: null);
        domain = target.getStore().getDomain();
        classRights = target.getStore().getDomain().getRights().stream().filter(r -> r.isClassRight()).collect(Collectors.toList());
        initContextMenus();
    }

    private void initContextMenus() {
        EventHandler<ActionEvent> addHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    save = false;
                    EditProperty dialog = new EditProperty(adaClass, new OkListener() {
                        @Override
                        public void onOk() {
                            save = true;
                        }
                    });
                    dialog.showAndWait();
                    if (save) {
                        tableProperties.getItems().add(dialog.getController().getProperty());
                        okButton.setDisable(false);
                        hasChanges = true;
                    }
                }
                catch (Exception err) {
                    err.printStackTrace();
                }
            }
        };
        cmProperty = new ContextMenu();
        MenuItem properties = new MenuItem(InternationalizationUtils.get("classproperties.properties.contextmenu.edit"));
        properties.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    save = false;
                    EditProperty dialog = new EditProperty(adaClass, editProperty, new OkListener() {
                        @Override
                        public void onOk() {
                            save = true;
                        }
                    });
                    dialog.showAndWait();
                    int index = tableProperties.getItems().indexOf(editProperty);
                    tableProperties.getItems().remove(editProperty);
                    tableProperties.getItems().add(index, dialog.getController().getProperty());
                    okButton.setDisable(false);
                    hasChanges = true;
                }
                catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });
        cmProperty.getItems().add(properties);
        MenuItem adding = new MenuItem(InternationalizationUtils.get("classproperties.properties.contextmenu.add"));
        adding.setOnAction(addHandler);
        cmProperty.getItems().add(adding);
        MenuItem delete = new MenuItem(InternationalizationUtils.get("classproperties.properties.contextmenu.delete"));
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deleteProperty((Property)tableProperties.getSelectionModel().getSelectedItem());
            }
        });
        cmProperty.getItems().add(delete);

        cmAddProperty = new ContextMenu();
        MenuItem addProperty = new MenuItem(InternationalizationUtils.get("classproperties.properties.contextmenu.add"));
        addProperty.setOnAction(addHandler);
        cmAddProperty.getItems().add(addProperty);
    }

    public void setOkButton(Button ok) {
        okButton = ok;
        okButton.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtName.setText(adding ? "": adaClass.getName());
        txtName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                okButton.setDisable(false);
            }
        });
        TableColumn nameColumn = new TableColumn();
        nameColumn.setText(InternationalizationUtils.get("classproperties.tabproperties.column.name"));
        nameColumn.prefWidthProperty().bind(tableProperties.widthProperty().multiply(0.8));
        nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Property, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Property, String> propertyStringCellDataFeatures) {
                return new SimpleStringProperty(propertyStringCellDataFeatures.getValue().getName());
            }
        });

        TableColumn typeColumn = new TableColumn();
        typeColumn.setText(InternationalizationUtils.get("classproperties.tabproperties.column.type"));
        typeColumn.prefWidthProperty().bind(tableProperties.widthProperty().multiply(0.2));
        typeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Property, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Property, String> cellDataFeatures) {
                Property prop = cellDataFeatures.getValue();
                return new SimpleStringProperty(prop.getType().toString());
            }
        });

        tableProperties.getColumns().clear();
        tableProperties.getColumns().add(nameColumn);
        tableProperties.getColumns().add(typeColumn);

        tableProperties.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent contextMenuEvent) {
                if (tableProperties.getSelectionModel().getSelectedIndex() >= 0 && tableProperties.getSelectionModel().getSelectedIndex() < tableProperties.getItems().size()) {
                    editProperty = (Property) tableProperties.getSelectionModel().getSelectedItem();
                    cmProperty.show(tableProperties, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                }
                else {
                    cmAddProperty.show(tableProperties, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
                }
            }
        });
        refreshProperties();

        rightstable = new RightsTable(adding ? new ArrayList<GrantedRight>(): adaClass.getGrantedRights(), domain, classRights);
        rightstable.addChangeListener((ObservableValue observableValue, Object o, Object t1) -> {
            okButton.setDisable(false);
            hasChanges = true;
        });
        rightsPane.setCenter(rightstable);
    }

    private void refreshProperties() {
        if (!adding) {
            try {
                ArrayList <String> parentPropertyIds = new ArrayList<>();
                if (!adding) {

                    for (Property p: domain.getAdaClass(adaClass.getStore(), adaClass.getParentClass().getId()).getProperties()) {
                        parentPropertyIds.add(p.getId());
                    }
                }
                adaClass = adaClass.getStore().getAdaClass(adaClass.getId());
                tableProperties.getItems().clear();
                for (Property property : adaClass.getProperties()) {
                    if (!parentPropertyIds.contains(property.getId()))
                        tableProperties.getItems().add(property);
                }
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private void deleteProperty(Property todelete) {
        toDelete.add(todelete);
        tableProperties.getItems().remove(todelete);
        hasChanges = true;
        okButton.setDisable(false);
    }

    public AdaClass getClassToSave() {
        AdaClass tosave = new AdaClass();
        tosave.setStore(adaClass == null ? parentClass.getStore(): adaClass.getStore());
        tosave.setName(txtName.getText());
        for (Object prop : tableProperties.getItems()) {
            try {
                Property toadd = (Property) prop;
                tosave.addProperty(toadd);
            }
            catch (Exception pnae) {}
        }
        for (Object right: rightstable.getItems()) {
            tosave.addRight((GrantedRight) right);
        }
        if (adaClass != null)
            tosave.setId(adaClass.getId());
        else {
            tosave.setFolderClass(parentClass.isFolderClass());
            tosave.setDocumentClass(parentClass.isDocumentClass());
            tosave.setParentClass(parentClass);
        }
        return tosave;
    }

    public boolean isAdding() {
        return adaClass == null;
    }
}
