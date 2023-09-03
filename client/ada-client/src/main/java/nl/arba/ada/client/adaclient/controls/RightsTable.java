package nl.arba.ada.client.adaclient.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import nl.arba.ada.client.adaclient.dialogs.GrantedRightDetails;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.adaclient.utils.RightUtils;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.Right;

import java.util.ArrayList;
import java.util.List;

public class RightsTable extends TableView {
    private ArrayList<GrantedRight> rights;
    private ContextMenu cmExisting;
    private ContextMenu cmAdd;
    private GrantedRight toEdit = null;
    private List<Right> availableRights;
    private ArrayList <ChangeListener> changeListeners = new ArrayList<>();
    private TableRow selectedRow = null;

    private Domain domain;
    public RightsTable(List<GrantedRight> rights, Domain domain, List<Right> availablerights) {
        super();
        availableRights = availablerights;
        this.domain =domain;
        this.rights = new ArrayList<>();
        for (GrantedRight right: rights) {
            this.rights.add(right);
        }

        initContextMenus();

        //Create columns
        TableColumn grantee = new TableColumn();
        grantee.setText(InternationalizationUtils.get("rightstable.columns.grantee.header"));
        grantee.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GrantedRight, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<GrantedRight, String> cellDataFeatures) {
                GrantedRight right = (GrantedRight) cellDataFeatures.getValue();
                //System.out.println("Right: " + right.getGrantee());
                if (right.getGrantee() == null) {
                    return new SimpleStringProperty("?");
                }
                else
                    return new SimpleStringProperty(right.getGrantee().getDisplayName());
            }
        });
        getColumns().add(grantee);

        TableColumn type = new TableColumn();
        type.setText(InternationalizationUtils.get("rightstable.columns.type.header"));
        type.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GrantedRight, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<GrantedRight, String> cellDataFeatures) {
                GrantedRight right = (GrantedRight) cellDataFeatures.getValue();
                if (right.getGranteetype() == null)
                    return new SimpleStringProperty("?");
                else if (right.getGranteetype().equals("user"))
                    return new SimpleStringProperty(InternationalizationUtils.get("grantedright.type.user"));
                else if (right.getGranteetype().equals("role"))
                    return new SimpleStringProperty(InternationalizationUtils.get("grantedright.type.role"));
                else if (right.getGranteetype().equals("group"))
                    return new SimpleStringProperty(InternationalizationUtils.get("grantedright.type.group"));
                else if (right.getGranteetype().equals("special"))
                    return new SimpleStringProperty(InternationalizationUtils.get("grantedright.type.special"));
                else
                    return new SimpleStringProperty("?");
            }
        });
        getColumns().add(type);

        RightUtils utils = RightUtils.create(domain);
        TableColumn rightscolumn = new TableColumn();
        rightscolumn.setText(InternationalizationUtils.get("rightstable.columns.rights.header"));
        rightscolumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<GrantedRight, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<GrantedRight, String> cellDataFeatures) {
                return new SimpleStringProperty(utils.getRightNames(cellDataFeatures.getValue().getLevel()));
            }
        });
        getColumns().add(rightscolumn);

        this.setRowFactory(new Callback<TableView, TableRow>() {
            @Override
            public TableRow call(TableView tableView) {
                TableRow newRow = new TableRow();
                newRow.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        selectedRow = newRow;
                    }
                });
                newRow.setOnMouseExited(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        selectedRow = null;
                    }
                });
                return newRow;
            }
        });

        for (GrantedRight right: rights) {
            getItems().add(right);
        }
    }

    private void initContextMenus() {
        RightsTable parent = this;
        EventHandler addEventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GrantedRightDetails details = GrantedRightDetails.create(domain, availableRights);
                details.showAndWait();
                ButtonType result = (ButtonType) details.getResult();
                if (details.isOkButtonType(result)) {
                    parent.getItems().add(details.getGrantedRight());
                    changeListeners.stream().forEach(l -> l.changed(null, null, null));
                }
            }
        };
        cmExisting = new ContextMenu();
        MenuItem edit = new MenuItem(InternationalizationUtils.get("rightstable.contextmenu.edit"));
        edit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                GrantedRightDetails details = GrantedRightDetails.create(toEdit, domain, availableRights);
                details.showAndWait();
                ButtonType result = (ButtonType) details.getResult();
                if (details.isOkButtonType(result)) {
                    parent.getItems().remove(toEdit);
                    parent.getItems().add(details.getGrantedRight());
                    changeListeners.stream().forEach(l -> l.changed(null, null, null));
                }
            }
        });
        cmExisting.getItems().add(edit);
        MenuItem addOnExisting = new MenuItem(InternationalizationUtils.get("rightstable.contextmenu.add"));
        addOnExisting.setOnAction(addEventHandler);
        cmExisting.getItems().add(addOnExisting);

        cmAdd = new ContextMenu();
        MenuItem add = new MenuItem(InternationalizationUtils.get("rightstable.contextmenu.add"));
        add.setOnAction(addEventHandler);
        cmAdd.getItems().add(add);

        this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent e) {
                if (selectedRow == null || selectedRow.itemProperty().getValue() == null)
                    cmAdd.show((Node) e.getSource(), e.getScreenX(), e.getScreenY());
                else {
                    toEdit = (GrantedRight) selectedRow.getItem();
                    cmExisting.show((Node) e.getSource(), e.getScreenX(), e.getScreenY());
                }
            }
        });
    }

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }
}
