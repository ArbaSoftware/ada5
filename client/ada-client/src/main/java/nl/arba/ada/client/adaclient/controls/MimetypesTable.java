package nl.arba.ada.client.adaclient.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import nl.arba.ada.client.adaclient.dialogs.GrantedRightDetails;
import nl.arba.ada.client.adaclient.dialogs.MimetypeDetails;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.Mimetype;
import nl.arba.ada.client.api.security.GrantedRight;

public class MimetypesTable extends TableView {
    private ContextMenu cmExisting;
    private TableRow selectedRow = null;
    private Mimetype toEdit = null;
    private ContextMenu cmAdd;
    private Domain domain;
    public MimetypesTable(Domain domain) {
        this.domain = domain;
        Mimetype[] mimetypes = new Mimetype[0];
        try {
            mimetypes = domain.getMimetypes();
        }
        catch (Exception err) {}
        TableColumn mimetype = new TableColumn();
        mimetype.setText(InternationalizationUtils.get("mimetypestable.columns.mimetype.header"));
        mimetype.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Mimetype, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Mimetype, String> cellDataFeatures) {
                Mimetype mimetype = cellDataFeatures.getValue();
                return new SimpleStringProperty(mimetype.getMimetype());
            }
        });
        getColumns().add(mimetype);

        initContextMenus();

        TableColumn extension = new TableColumn();
        extension.setText(InternationalizationUtils.get("mimetypestable.columns.extension.header"));
        extension.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Mimetype, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<Mimetype, String> cellDataFeatures) {
                Mimetype mimetype = cellDataFeatures.getValue();
                return new SimpleStringProperty(mimetype.getExtension());
            }
        });
        getColumns().add(extension);

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

        for (Mimetype current: mimetypes) {
            getItems().add(current);
        }
    }

    private void initContextMenus() {
        MimetypesTable parent = this;
        cmExisting = new ContextMenu();
        MenuItem addOnExisting = new MenuItem(InternationalizationUtils.get("mimetypestable.contextmenu.add"));
        addOnExisting.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                MimetypeDetails addDialog = new MimetypeDetails(domain);
                addDialog.showAndWait();
            }
        });
        cmExisting.getItems().add(addOnExisting);

        MenuItem delete = new MenuItem(InternationalizationUtils.get("mimetypestable.contextmenu.delete"));
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("Delete");
            }
        });
        cmExisting.getItems().add(delete);

        cmAdd = new ContextMenu();
        MenuItem add = new MenuItem(InternationalizationUtils.get("rightstable.contextmenu.add"));

        this.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent e) {
                if (selectedRow == null || selectedRow.itemProperty().getValue() == null)
                    cmAdd.show((Node) e.getSource(), e.getScreenX(), e.getScreenY());
                else {
                    toEdit = (Mimetype) selectedRow.getItem();
                    cmExisting.show((Node) e.getSource(), e.getScreenX(), e.getScreenY());
                }
            }
        });
    }

}
