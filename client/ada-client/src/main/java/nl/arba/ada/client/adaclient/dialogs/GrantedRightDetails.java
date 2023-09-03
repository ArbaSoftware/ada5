package nl.arba.ada.client.adaclient.dialogs;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import nl.arba.ada.client.adaclient.App;
import nl.arba.ada.client.adaclient.utils.IdName;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.exceptions.IdentityProviderNotFoundException;
import nl.arba.ada.client.api.security.*;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class GrantedRightDetails extends Dialog implements Initializable {
    private GrantedRight target;
    @FXML
    private ComboBox cmbGranteeType;
    @FXML
    private Label lblSearchResult;
    @FXML
    private Button btnSearch;
    @FXML
    private Label lblGranteeSearch;
    @FXML
    private ComboBox cmbGranteeSelect;
    @FXML
    private Label lblIdentityProvider;
    @FXML
    private ComboBox cmbIdentityProvider;
    private Domain domain;
    @FXML
    private TableView tableRights;
    private List<Right> availableRights;
    private Button btnOk;

    private GrantedRightDetails(GrantedRight right, Domain domain, List<Right> availablerights) {
        super();
        availableRights = availablerights;
        target = new GrantedRight();
        if (right != null) {
            if (right.getGrantee() != null) {
                target.setGrantee(right.getGrantee());
                if (right.getGrantee().getIdentityProvider() != null)
                    target.setIdentityproviderid(right.getGrantee().getIdentityProvider().getId());
            }
            target.setGranteetype(right.getGranteetype());
            target.setLevel(right.getLevel());
        }
        this.domain = domain;
        setTitle(InternationalizationUtils.get("grantedrightdetails.title"));
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("grantedrightdetails.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            loader.setController(this);
            getDialogPane().setContent(loader.load());
            ButtonType ok = new ButtonType(InternationalizationUtils.get("grantedrightsdetails.ok"), ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType(InternationalizationUtils.get("grantedrightsdetails.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            getDialogPane().getButtonTypes().add(ok);
            getDialogPane().getButtonTypes().add(cancel);
            btnOk = (Button) getDialogPane().lookupButton(ok);
            if (right == null)
                btnOk.setDisable(true);
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static GrantedRightDetails create(GrantedRight right, Domain domain, List<Right> availablerights) {
        return new GrantedRightDetails(right, domain, availablerights);
    }

    public static GrantedRightDetails create(Domain domain, List<Right> availablerights) {
        return new GrantedRightDetails(null, domain, availablerights);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (IdentityProvider idp: domain.getIdentityProviders()) {
            cmbIdentityProvider.getItems().add(new IdName(idp.getId(), idp.getName()));
        }
        cmbGranteeType.getItems().add(InternationalizationUtils.get("granteetype.user"));
        cmbGranteeType.getItems().add(InternationalizationUtils.get("granteetype.role"));
        cmbGranteeType.getItems().add(InternationalizationUtils.get("granteetype.group"));
        cmbGranteeType.getItems().add(InternationalizationUtils.get("granteetype.special"));
        cmbGranteeType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (t1.intValue() == 0)
                    target.setGranteetype("user");
                else if (t1.intValue() == 1)
                    target.setGranteetype("role");
                else if (t1.intValue() == 2)
                    target.setGranteetype("group");
                else if (t1.intValue() == 3)
                    target.setGranteetype("special");
                if (t1.intValue() == 0 || t1.intValue() == 2) {
                    lblSearchResult.setVisible(true);
                    btnSearch.setDisable(cmbIdentityProvider.getSelectionModel().getSelectedIndex() < 0);
                    btnSearch.setVisible(true);
                    cmbGranteeSelect.setVisible(false);
                    lblGranteeSearch.setText(InternationalizationUtils.get("grantedrightsdetails.granteesearch.label.search"));
                }
                else {
                    lblGranteeSearch.setText(InternationalizationUtils.get("grantedrightsdetails.granteesearch.label.choose"));
                    lblSearchResult.setVisible(false);
                    btnSearch.setVisible(false);
                    cmbGranteeSelect.setVisible(true);
                }

                lblIdentityProvider.setVisible(t1.intValue() == 0 || t1.intValue() == 1 || t1.intValue() == 2);
                cmbIdentityProvider.setVisible(t1.intValue() == 0 || t1.intValue() == 1 || t1.intValue() == 2);

                if (t1.intValue() == 1) {
                    if (cmbIdentityProvider.getSelectionModel().getSelectedIndex() >= 0) {
                        try {
                            IdentityProvider idp = domain.getIdentityProvider(((IdName) cmbIdentityProvider.getSelectionModel().getSelectedItem()).getId());
                            refreshRoles(idp);
                        }
                        catch (Exception err) {
                            err.printStackTrace();
                        }
                    }
                    else
                        cmbGranteeSelect.getItems().clear();
                }
                else if (t1.intValue() == 3) {
                    cmbGranteeSelect.getItems().clear();
                    cmbGranteeSelect.getItems().add("Everyone");
                }
                evaluateOkButton();
            }
        });
        cmbIdentityProvider.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                try {

                    if (cmbGranteeType.getSelectionModel().getSelectedIndex() == 1) {
                        IdentityProvider idp = domain.getIdentityProvider(((IdName) cmbIdentityProvider.getSelectionModel().getSelectedItem()).getId());
                        refreshRoles(idp);
                        target.setIdentityproviderid(idp.getId());
                    }
                }
                catch (Exception err) {
                    err.printStackTrace();
                }
                evaluateOkButton();
            }
        });
        if (target.getGranteetype() == null) {

        }
        else if (target.getGranteetype().equals("user")) {
            cmbGranteeType.getSelectionModel().select(0);
            //cmbIdentityProvider.getSelectionModel().select(target.getGrantee().getIdentityProvider());
            System.out.println("GRANTEE: " + target.getGrantee());
            System.out.println("GRANTEE/IDP: " + target.getGrantee().getIdentityProvider());
            System.out.println("IDP id: " + target.getIdentityProviderId());
            if (target.getGrantee() != null) {
                lblSearchResult.setText(target.getGrantee().getDisplayName());
                if (target.getGrantee().getIdentityProvider() != null) {
                    for (int index = 0; index < cmbIdentityProvider.getItems().size(); index++) {
                        if (((IdName) cmbIdentityProvider.getItems().get(index)).getId().equals(target.getGrantee().getIdentityProvider().getId())) {
                            cmbIdentityProvider.getSelectionModel().select(index);
                        }
                    }
                }
            }
            else
                lblSearchResult.setText(InternationalizationUtils.get("grantedrightsdetails.granteesearch.noneselected"));
        }
        else if (target.getGranteetype().equals("role")) {
            cmbGranteeType.getSelectionModel().select(1);
            for (int index = 0; index < cmbIdentityProvider.getItems().size(); index++) {
                if (((IdName) cmbIdentityProvider.getItems().get(index)).getId().equals(target.getGrantee().getIdentityProvider().getId()))
                    cmbIdentityProvider.getSelectionModel().select(index);
            }
            refreshRoles(target.getGrantee().getIdentityProvider());
            for (int index = 0; index < cmbGranteeSelect.getItems().size(); index++) {
                IdName current = (IdName) cmbGranteeSelect.getItems().get(index);
                if (current.getId().equals(target.getGrantee().getId()))
                    cmbGranteeSelect.getSelectionModel().select(index);
            }
            lblSearchResult.setText(InternationalizationUtils.get("grantedrightsdetails.granteesearch.noneselected"));
        }
        else if (target.getGranteetype().equals("group")) {
            cmbGranteeType.getSelectionModel().select(2);
            lblSearchResult.setText(InternationalizationUtils.get("grantedrightsdetails.granteesearch.noneselected"));
        }
        else if (target.getGranteetype().equals("special")) {
            cmbGranteeType.getSelectionModel().select(3);
            cmbGranteeSelect.getItems().clear();
            cmbGranteeSelect.getItems().add("Everyone");
            cmbGranteeSelect.getSelectionModel().select(0);
            lblSearchResult.setText(InternationalizationUtils.get("grantedrightsdetails.granteesearch.noneselected"));
        }
        else {
            cmbGranteeType.getSelectionModel().select(-1);
            lblSearchResult.setText(InternationalizationUtils.get("grantedrightsdetails.granteesearch.noneselected"));
        }

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    SearchUser su = SearchUser.create(domain, domain.getIdentityProvider(((IdName) cmbIdentityProvider.getSelectionModel().getSelectedItem()).getId()));
                    su.showAndWait();
                    ButtonType result = (ButtonType) su.getResult();
                    if (!result.getButtonData().isCancelButton()) {
                        User selectedUser = su.getSelectedUser();
                        target.setGrantee(selectedUser);
                        target.setGranteeid(selectedUser.getEmail());
                        target.setGranteetype("user");
                        target.setIdentityproviderid( ((IdName) cmbIdentityProvider.getSelectionModel().getSelectedItem()).getId());
                        lblSearchResult.setText(selectedUser.getDisplayName());
                        evaluateOkButton();
                    }
                }
                catch (IdentityProviderNotFoundException nfe) {}
            }
        });
        cmbIdentityProvider.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                btnSearch.setDisable(t1.intValue() < 0);
                lblSearchResult.setText(InternationalizationUtils.get("grantedrightsdetails.granteesearch.noneselected"));
            }
        });
        cmbGranteeSelect.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if (cmbGranteeType.getSelectionModel().getSelectedIndex() == 1) {
                    try {
                        IdentityProvider idp = domain.getIdentityProvider(((IdName) cmbIdentityProvider.getSelectionModel().getSelectedItem()).getId());
                        Role[] roles = domain.getRoles(idp);
                        IdName role = (IdName) cmbGranteeSelect.getSelectionModel().getSelectedItem();
                        Optional<Role> optRole = Arrays.asList(roles).stream().filter(r -> r.getId().equals(role.getId())).findFirst();
                        if (optRole.isPresent())
                            target.setGrantee(optRole.get());
                    }
                    catch (Exception err) {}
                }
                else if (cmbGranteeType.getSelectionModel().getSelectedIndex() == 3) {
                    if (cmbGranteeSelect.getSelectionModel().getSelectedIndex() == 0)
                        target.setGrantee(Everyone.create());
                }
                evaluateOkButton();
            }
        });
        initRightsTable();
    }

    private void initRightsTable() {
        tableRights.getColumns().clear();
        TableColumn setted = new TableColumn();
        setted.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SettableRight, CheckBox>, ObservableValue<CheckBox>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<SettableRight, CheckBox> cellDataFeatures) {
                RightCheckBox checkbox = new RightCheckBox(cellDataFeatures.getValue().getTarget());
                checkbox.setSelected(cellDataFeatures.getValue().isSelected());
                checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                        if (t1)
                            target.setLevel(target.getLevel() + checkbox.targetRight.getLevel());
                        else
                            target.setLevel(target.getLevel() - checkbox.targetRight.getLevel());
                        System.out.println("new level: " + target.getLevel());
                    }
                });
                return new SimpleObjectProperty<CheckBox>(checkbox);
            }
        });
        tableRights.getColumns().add(setted);

        TableColumn right = new TableColumn();
        right.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SettableRight, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures<SettableRight, String> cellDataFeatures) {
                return new SimpleStringProperty(cellDataFeatures.getValue().getTarget().getName());
            }
        });
        tableRights.getColumns().add(right);

        for (Right current: availableRights) {
            SettableRight value = new SettableRight(current, ((target.getLevel() & current.getLevel()) == current.getLevel()));
            tableRights.getItems().add(value);
        }
    }

    private void refreshRoles(IdentityProvider idp) {
        cmbGranteeSelect.getItems().clear();
        if (idp.getType().equals(IdentityProviderType.OAUTH)) {
            try {
                Role[] roles = domain.getRoles(idp);
                for (int index = 0; index < roles.length; index++) {
                    cmbGranteeSelect.getItems().add(new IdName(roles[index].getId(), roles[index].getName()));
                }
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    private class SettableRight {
        private Right target;
        private boolean selected;

        public SettableRight(Right target, boolean selected) {
            this.target = target;
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean value) {
            selected = value;
        }

        public Right getTarget() {
            return target;
        }
    }

    private class RightCheckBox extends CheckBox {
        private Right targetRight;

        public RightCheckBox(Right target) {
            super();
            targetRight = target;
        }

        public Right getTarget() {
            return targetRight;
        }
    }

    private void evaluateOkButton() {
        if (btnOk != null) {
            if (cmbGranteeType.getSelectionModel().getSelectedIndex() == 0) {
                btnOk.setDisable(cmbIdentityProvider.getSelectionModel().getSelectedIndex() < 0 || target.getGrantee() == null || !(target.getGrantee() instanceof User));
            } else if (cmbGranteeType.getSelectionModel().getSelectedIndex() == 1)
                btnOk.setDisable(cmbIdentityProvider.getSelectionModel().getSelectedIndex() < 0 || cmbGranteeSelect.getSelectionModel().getSelectedIndex() < 0);
            else if (cmbGranteeType.getSelectionModel().getSelectedIndex() == 3)
                btnOk.setDisable(cmbGranteeSelect.getSelectionModel().getSelectedIndex() < 0);
            else
                btnOk.setDisable(true);
        }
    }

    public boolean isOkButtonType(ButtonType type) {
        return getDialogPane().lookupButton(type).equals(btnOk);
    }

    public GrantedRight getGrantedRight() {
        return target;
    }
}
