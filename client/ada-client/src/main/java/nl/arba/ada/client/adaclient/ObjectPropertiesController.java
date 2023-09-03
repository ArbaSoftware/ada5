package nl.arba.ada.client.adaclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import nl.arba.ada.client.api.AdaObject;

public class ObjectPropertiesController {
    private AdaObject theObject;
    private Button okButton;
    @FXML
    private Label lblClass;
    private ComboBox cmbClass;

    public ObjectPropertiesController(AdaObject target) {
        theObject = target;
    }

    public void setOkButton(Button ok) {
        okButton = ok;
    }
}
