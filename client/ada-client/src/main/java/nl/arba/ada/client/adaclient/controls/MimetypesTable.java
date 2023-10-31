package nl.arba.ada.client.adaclient.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.Mimetype;
import nl.arba.ada.client.api.security.GrantedRight;

public class MimetypesTable extends TableView {
    public MimetypesTable(Mimetype[] mimetypes) {
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

        for (Mimetype current: mimetypes) {
            getItems().add(current);
        }
    }
}
