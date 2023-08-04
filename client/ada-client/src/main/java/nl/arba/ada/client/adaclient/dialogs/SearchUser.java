package nl.arba.ada.client.adaclient.dialogs;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import nl.arba.ada.client.adaclient.App;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.security.IdentityProvider;
import nl.arba.ada.client.api.security.User;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchUser extends Dialog implements Initializable {
    private IdentityProvider idp;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnSearch;
    @FXML
    private ListView lvSearchResults;
    private ButtonType ok;
    private ButtonType cancel;

    private Domain domain;

    private SearchUser(Domain domain, IdentityProvider idp) {
        this.idp = idp;
        this.domain = domain;
        init();
    }

    private void init() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("searchuser.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            loader.setController(this);
            getDialogPane().setContent(loader.load());
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static SearchUser create(Domain domain, IdentityProvider idp) {
        return new SearchUser(domain, idp);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ok = new ButtonType(InternationalizationUtils.get("searchuser.ok"), ButtonBar.ButtonData.OK_DONE);
        cancel = new ButtonType(InternationalizationUtils.get("searchuser.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(ok);
        getDialogPane().getButtonTypes().add(cancel);
        getDialogPane().lookupButton(ok).setDisable(true);

        txtSearch.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER && !txtSearch.getText().isEmpty())
                    doSearch();
            }
        });

        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doSearch();
            }
        });
    }

    private void doSearch() {
        if (!txtSearch.getText().isEmpty()) {
            try {
                User[] users = domain.searchUsers(idp, txtSearch.getText());
                getDialogPane().lookupButton(ok).setDisable(users.length != 1);
                for (User user: users) {
                    lvSearchResults.getItems().add(user.getDisplayName());
                }
            }
            catch (Exception err) {
                getDialogPane().lookupButton(ok).setDisable(true);
            }
        }
    }
}
