package nl.arba.ada.client.adaclient;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import nl.arba.ada.client.adaclient.controls.MimetypesTable;
import nl.arba.ada.client.adaclient.controls.RightsTable;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.Mimetype;
import nl.arba.ada.client.api.security.GrantedRight;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DomainPropertiesController implements Initializable {
    @FXML
    private BorderPane rightsPane;
    @FXML
    private BorderPane mimetypesPane;
    private RightsTable rightsTable;

    private MimetypesTable mimetypesTable;
    private Domain domain;

    public DomainPropertiesController(Domain domain) {
        this.domain = domain;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            rightsTable = new RightsTable(Arrays.asList(domain.getDomainRights()), domain, domain.getRights().stream().filter(r -> r.isDomainRight()).collect(Collectors.toList()));
            rightsPane.setCenter(rightsTable);

            mimetypesTable = new MimetypesTable(domain.getMimetypes());
            mimetypesPane.setCenter(mimetypesTable);
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}
