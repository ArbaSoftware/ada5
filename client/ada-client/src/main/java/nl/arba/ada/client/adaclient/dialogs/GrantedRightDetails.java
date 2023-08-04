package nl.arba.ada.client.adaclient.dialogs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import nl.arba.ada.client.adaclient.App;
import nl.arba.ada.client.adaclient.utils.IdName;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.exceptions.IdentityProviderNotFoundException;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.IdentityProvider;

import java.net.URL;
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

    private GrantedRightDetails(GrantedRight right, Domain domain) {
        super();
        target = right;
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
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static GrantedRightDetails create(GrantedRight right, Domain domain) {
        return new GrantedRightDetails(right, domain);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbGranteeType.getItems().add(InternationalizationUtils.get("granteetype.user"));
        cmbGranteeType.getItems().add(InternationalizationUtils.get("granteetype.role"));
        cmbGranteeType.getItems().add(InternationalizationUtils.get("granteetype.group"));
        cmbGranteeType.getItems().add(InternationalizationUtils.get("granteetype.special"));
        cmbGranteeType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
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
            }
        });
        if (target.getGranteetype().equals("user")) {
            cmbGranteeType.getSelectionModel().select(0);
            cmbIdentityProvider.getSelectionModel().select(target.getUser().getIdentityProvider());
            if (target.getUser() != null)
                lblSearchResult.setText(target.getUser().getDisplayName());
            else
                lblSearchResult.setText(InternationalizationUtils.get("grantedrightsdetails.granteesearch.noneselected"));
        }
        else if (target.getGranteetype().equals("role")) {
            cmbGranteeType.getSelectionModel().select(1);
            cmbGranteeSelect.getItems().clear();
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

        for (IdentityProvider idp: domain.getIdentityProviders()) {
            cmbIdentityProvider.getItems().add(new IdName(idp.getId(), idp.getName()));
        }
        cmbIdentityProvider.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                btnSearch.setDisable(t1.intValue() < 0);
            }
        });

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    SearchUser.create(domain, domain.getIdentityProvider(((IdName) cmbIdentityProvider.getSelectionModel().getSelectedItem()).getId())).showAndWait();
                }
                catch (IdentityProviderNotFoundException nfe) {}
            }
        });
    }
}
