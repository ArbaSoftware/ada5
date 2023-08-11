package nl.arba.ada.client.adaclient;

import javafx.beans.property.SimpleStringProperty;
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
import nl.arba.ada.client.adaclient.dialogs.Confirmation;
import nl.arba.ada.client.adaclient.dialogs.EditProperty;
import nl.arba.ada.client.adaclient.dialogs.OkListener;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.Property;
import nl.arba.ada.client.api.security.Right;

import java.net.URL;
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

    public ClassPropertiesController(AdaClass target) {
        this.adaClass = target;
        classRights = adaClass.getStore().getDomain().getRights().stream().filter(r -> r.isClassRight()).collect(Collectors.toList());
        initContextMenus();
    }

    private void initContextMenus() {
        EventHandler<ActionEvent> addHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    EditProperty dialog = new EditProperty(adaClass, new OkListener() {
                        @Override
                        public void onOk() {
                            refreshProperties();
                        }
                    });
                    dialog.show();
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
                    EditProperty dialog = new EditProperty(adaClass, editProperty, new OkListener() {
                        @Override
                        public void onOk() {
                            refreshProperties();
                        }
                    });
                    dialog.show();
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtName.setText(adaClass.getName());
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

        RightsTable rightstable = new RightsTable(adaClass.getGrantedRights(), adaClass.getStore().getDomain(), classRights);
        rightsPane.setCenter(rightstable);
    }

    private void refreshProperties() {
        try {
            adaClass = adaClass.getStore().getAdaClass(adaClass.getId());
            tableProperties.getItems().clear();
            for (Property property : adaClass.getProperties()) {
                tableProperties.getItems().add(property);
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void deleteProperty(Property todelete) {
        Confirmation c = Confirmation.create(InternationalizationUtils.get("confirmation.delete.property"), InternationalizationUtils.get("confirmation.delete.property.title"));
        c.showAndWait();
        if (c.getResult().equals(Boolean.TRUE)) {
            try {
                adaClass.deleteProperty(todelete);
                refreshProperties();
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
