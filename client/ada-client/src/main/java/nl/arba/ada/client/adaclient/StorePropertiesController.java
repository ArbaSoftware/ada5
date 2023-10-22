package nl.arba.ada.client.adaclient;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import nl.arba.ada.client.adaclient.controls.RightsTable;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.Store;
import nl.arba.ada.client.api.addon.AddOn;
import nl.arba.ada.client.api.security.GrantedRight;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StorePropertiesController implements Initializable {
    @FXML
    private Label lblAddOns;
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
    private Store store;

    public StorePropertiesController(Domain domain) {
        this.domain = domain;
        adding = true;
    }

    public StorePropertiesController(Domain domain, Store store) {
        this.domain = domain;
        this.store = store;
        adding = false;
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
                SimpleBooleanProperty prop = new SimpleBooleanProperty(item.isSelected());
                prop.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                        item.setSelected(t1);
                    }
                });
                return prop;
            }
        }));
        try {
            AddOn[] addons = domain.getAddOns();
            for (AddOn current : addons) {
                lvAddOns.getItems().add(new AddOnState(current));
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        if (store != null) {
            txtName.setText(store.getName());
            lvAddOns.setVisible(false);
            lblAddOns.setVisible(false);
        }
        txtName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                ok.setDisable(t1.isEmpty());
            }
        });
        rightsTable = new RightsTable( store == null ? new ArrayList<GrantedRight>() : store.getRights(), domain, domain.getRights().stream().filter(r -> r.isStoreRight()).collect(Collectors.toList()));
        rightsPane.setCenter(rightsTable);
    }

    public Store getStore() {
        Store store = new Store();
        store.setDomain(domain);
        store.setName(txtName.getText());
        return store;
    }

    public GrantedRight[] getRights() {
        GrantedRight[] result = new GrantedRight[rightsTable.getItems().size()];
        for (int index = 0; index < result.length; index++) {
            result[index] = (GrantedRight) rightsTable.getItems().get(index);
        }
        return result;
    }

    public String[] getAddOns() {
        ArrayList <String> states = new ArrayList<>();
        for (Object current: lvAddOns.getItems()) {
            AddOnState currentState = (AddOnState) current;
            if (currentState.isSelected())
                states.add(currentState.getId());
        }
        return states.toArray(new String[0]);
    }

    private class AddOnState {
        private AddOn addon;
        private boolean selected;
        private String id;

        public AddOnState(AddOn addon) {
            this.addon = addon;
            this.selected = false;
            this.id = addon.getId();
        }

        public void setSelected(boolean value) {
            this.selected = value;
        }

        public String getId() {
            return id;
        }

        public boolean isSelected() {
            return selected;
        }

        public String toString() {
            return addon.getName();
        }
    }
}
