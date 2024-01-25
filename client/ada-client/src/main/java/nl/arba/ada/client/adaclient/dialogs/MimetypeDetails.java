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
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.exceptions.ApiException;

import java.net.URL;
import java.util.ResourceBundle;

public class MimetypeDetails extends Dialog implements Initializable {
    @FXML
    private TextField txtExtension;
    @FXML
    private TextField txtMimetype;
    private Button btnOk;
    private Domain domain;

    public MimetypeDetails(Domain domain) {
        super();
        this.domain = domain;
        setTitle(InternationalizationUtils.get("mimetypedetails.title"));
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("mimetypedetails.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            loader.setController(this);
            getDialogPane().setContent(loader.load());
            ButtonType ok = new ButtonType(InternationalizationUtils.get("mimetypedetails.ok"), ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType(InternationalizationUtils.get("mimetypedetails.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            getDialogPane().getButtonTypes().add(ok);
            getDialogPane().getButtonTypes().add(cancel);
            btnOk = (Button) getDialogPane().lookupButton(ok);
            btnOk.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        domain.addMimetype(txtExtension.getText(), txtMimetype.getText());
                    }
                    catch (ApiException exception) {

                    }
                }
            });
            btnOk.setDisable(true);
        }
        catch (Exception err) {
            err.printStackTrace();
        }

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtExtension.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() > 25)
                    txtExtension.setText(t1.substring(0,25));
                evaluateOk();
            }
        });

        txtMimetype.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if (t1.length() > 255)
                    txtMimetype.setText(t1.substring(0,255));
                evaluateOk();
            }
        });
    }

    private void evaluateOk() {
        btnOk.setDisable(txtExtension.getText().isEmpty() || txtMimetype.getText().isEmpty());
    }
}
