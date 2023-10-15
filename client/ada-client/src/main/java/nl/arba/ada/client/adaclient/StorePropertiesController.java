package nl.arba.ada.client.adaclient;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import nl.arba.ada.client.adaclient.controls.RightsTable;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.addon.AddOn;
import nl.arba.ada.client.api.security.GrantedRight;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StorePropertiesController implements Initializable {
    private Domain domain;
    private Button ok;
    @FXML
    private TextField txtName;
    @FXML
    private ListView lvAddOns;
    private boolean adding;
    @FXML
    private BorderPane rightsPane;
    private RightsTable rightsTable;

    public StorePropertiesController(Domain domain) {
        this.domain = domain;
        adding = true;
    }

    public void setOkButton(Button ok) {
        this.ok = ok;
        if (adding)
            this.ok.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lvAddOns.setCellFactory(CheckBoxListCell.forListView(new Callback<AddOnState, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(AddOnState item) {
                return new SimpleBooleanProperty(item.isSelected());
            }
        }));
        try {
            AddOn[] addons = domain.getAddOns();
            System.out.println("Found " + addons.length + " addons");
            for (AddOn current : addons) {
                lvAddOns.getItems().add(new AddOnState(current));
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        txtName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                ok.setDisable(t1.isEmpty());
            }
        });
        rightsTable = new RightsTable( new ArrayList<GrantedRight>(), domain, domain.getRights().stream().filter(r -> r.isStoreRight()).collect(Collectors.toList()));
        rightsPane.setCenter(rightsTable);
    }

    private class AddOnState {
        private AddOn addon;
        private boolean selected;

        public AddOnState(AddOn addon) {
            this.addon = addon;
            this.selected = false;
        }

        public boolean isSelected() {
            return selected;
        }

        public String toString() {
            return addon.getName();
        }
    }
}
