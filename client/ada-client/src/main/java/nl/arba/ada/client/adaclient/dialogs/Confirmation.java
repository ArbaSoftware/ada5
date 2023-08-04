package nl.arba.ada.client.adaclient.dialogs;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;

public class Confirmation extends Dialog {
    private String message;
    private String title;

    private boolean result;

    public static Confirmation create(String message, String title) {
        Confirmation result = new Confirmation();
        result.setContentText(message);
        result.setTitle(title);
        ButtonType yes = new ButtonType(InternationalizationUtils.get("confirmation.yes"), ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType(InternationalizationUtils.get("confirmation.no"), ButtonBar.ButtonData.NO);
        result.getDialogPane().getButtonTypes().add(yes);
        result.getDialogPane().getButtonTypes().add(no);

        ((Button) result.getDialogPane().lookupButton(yes)).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                result.setResult(Boolean.TRUE);
            }
        });

        ((Button) result.getDialogPane().lookupButton(no)).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                result.setResult(Boolean.FALSE);
            }
        });
        return result;
    }
}
