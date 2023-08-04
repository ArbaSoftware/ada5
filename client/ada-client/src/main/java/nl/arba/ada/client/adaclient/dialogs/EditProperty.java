package nl.arba.ada.client.adaclient.dialogs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.MouseEvent;
import nl.arba.ada.client.adaclient.App;
import nl.arba.ada.client.adaclient.EditPropertyController;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.Property;

import java.util.EventListener;

public class EditProperty extends Dialog {
    private AdaClass parentClass;
    private Property property;
    private OkListener onOk;
    private boolean addingProperty = false;

    public EditProperty(AdaClass parent, Property property, OkListener onok) {
        parentClass = parent;
        this.property = property;
        onOk = onok;
        init();
    }

    public EditProperty(AdaClass parent, OkListener onok) {
        parentClass = parent;
        addingProperty = true;
        onOk = onok;
        init();
    }

    private void init() {
        try {
            if (addingProperty)
                setTitle(InternationalizationUtils.get("classproperties.editproperties.dialog.title.adding"));
            else
                setTitle(InternationalizationUtils.get("classproperties.editproperties.dialog.title") + " " + property.getName());
            getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.ok"), ButtonBar.ButtonData.OK_DONE));
            getDialogPane().getButtonTypes().add(new ButtonType(InternationalizationUtils.get("dialog.button.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE));
            EditPropertyController controller = null;
            if (addingProperty)
                controller = new EditPropertyController(parentClass, onOk);
            else
                controller = new EditPropertyController(parentClass, property, onOk);
            controller.setDialog(this);
            FXMLLoader loader = new FXMLLoader(App.class.getResource("editProperty.fxml"));
            loader.setResources(InternationalizationUtils.getResources());
            loader.setController(controller);
            getDialogPane().setContent(loader.load());
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}
