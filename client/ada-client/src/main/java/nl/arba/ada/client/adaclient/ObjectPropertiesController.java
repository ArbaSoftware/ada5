package nl.arba.ada.client.adaclient;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import nl.arba.ada.client.adaclient.controls.PropertiesPane;
import nl.arba.ada.client.adaclient.controls.RightsTable;
import nl.arba.ada.client.adaclient.utils.IdName;
import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.AdaObject;
import nl.arba.ada.client.api.security.GrantedRight;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ObjectPropertiesController implements Initializable {
    private AdaObject theObject;
    private Button okButton;
    @FXML
    private Label lblClass;
    @FXML
    private ComboBox cmbClass;
    @FXML
    private ScrollPane propertiesScrollPane;
    @FXML
    private BorderPane rightsPane;
    private PropertiesPane propertiesPane;
    private RightsTable rightsTable;

    public ObjectPropertiesController(AdaObject target) {
        theObject = target;
    }

    public void setOkButton(Button ok) {
        okButton = ok;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            AdaClass[] classes = theObject.getStore().getClasses();
            List<IdName> items = Arrays.asList(classes).stream().map(c -> new IdName(c.getId(), c.getName() )).collect(Collectors.toList());
            items.sort(new Comparator<IdName>() {
                @Override
                public int compare(IdName o1, IdName o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
            for (IdName item: items)
                cmbClass.getItems().add(item);
            if (theObject != null) {
                IdName toSelect = items.stream().filter(i -> i.getId().equals(theObject.getClassId())).findFirst().get();
                cmbClass.getSelectionModel().select(toSelect);
                cmbClass.setDisable(true);
            }
            else {
                cmbClass.setDisable(false);
            }

            propertiesPane = new PropertiesPane(theObject);
            propertiesScrollPane.setContent(propertiesPane);

            rightsTable = new RightsTable(theObject.getRights(), theObject.getStore().getDomain(), theObject.getStore().getDomain().getRights().stream().filter(r -> r.isObjectRight()).collect(Collectors.toList()));
            rightsPane.setCenter(rightsTable);
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    public String getUpdateRequest() throws Exception {
        AdaObject tosave = new AdaObject();
        tosave.setId(theObject.getId());
        GrantedRight[] rights = new GrantedRight[rightsTable.getItems().size()];
        int index = 0;
        for (Object right: rightsTable.getItems()) {
            rights[index] = (GrantedRight) right;
            index++;
        }
        tosave.setProperties(propertiesPane.getPropertyValues());
        return tosave.createAddRequest();
    }
}
