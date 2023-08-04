package nl.arba.ada.client.adaclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import nl.arba.ada.client.adaclient.dialogs.OkListener;
import nl.arba.ada.client.adaclient.utils.IdName;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.Property;
import nl.arba.ada.client.api.PropertyType;
import nl.arba.ada.client.api.util.JsonUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

public class EditPropertyController implements Initializable {
    private Property targetProperty;
    private AdaClass parentClass;
    @FXML
    private TextField txtName;
    @FXML
    private ComboBox cmbType;
    @FXML
    private CheckBox cbRequired;
    @FXML
    private CheckBox cbMultiple;
    private Dialog dialog;
    private ArrayList <Change> changes;
    private OkListener onOk;
    private boolean addingProperty = false;

    public EditPropertyController(AdaClass targetclass, Property target, OkListener onok) {
        targetProperty = target;
        parentClass = targetclass;
        changes = new ArrayList<>();
        onOk = onok;
    }

    public EditPropertyController(AdaClass parent, OkListener onok) {
        this(parent, null, onok);
        addingProperty = true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!addingProperty)
            txtName.setText(targetProperty.getName());
        txtName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                onChange(new Change("name", t1));
            }
        });
        ArrayList <IdName> types = new ArrayList<>();
        types.add(new IdName(PropertyType.STRING.toString(), InternationalizationUtils.get("property.type.string")));
        types.add(new IdName(PropertyType.INTEGER.toString(), InternationalizationUtils.get("property.type.integer")));
        types.add(new IdName(PropertyType.DATE.toString(), InternationalizationUtils.get("property.type.date")));
        types.add(new IdName(PropertyType.OBJECT.toString(), InternationalizationUtils.get("property.type.object")));
        int selectedIndex = -1;
        if (!addingProperty) {
            for (int index = 0; index < types.size(); index++) {
                if (types.get(index).getId().equals(targetProperty.getType().toString())) {
                    selectedIndex = index;
                    break;
                }
            }
        }
        for (IdName item: types)
            cmbType.getItems().add(item);
        if (!addingProperty)
            cmbType.getSelectionModel().select(selectedIndex);

        cmbType.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                onChange(new Change("type", t1.toString()));
            }
        });
        if (!addingProperty)
            cmbType.setEditable(false);

        if (!addingProperty)
            cbRequired.selectedProperty().set(targetProperty.isRequired());
        cbRequired.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                Change change = new Change("required", t1);
                onChange(change);
            }
        });
        cbRequired.setDisable(!addingProperty);
        if (!addingProperty)
            cbMultiple.setSelected(targetProperty.isMultiple());
        cbMultiple.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                Change change = new Change("multiple", t1);
                onChange(change);
            }
        });
        cbMultiple.setDisable(!addingProperty);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
        disableok(true);
        ((Button) getOk()).setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                onSave();
            }
        });
    }

    private Node getOk() {
        ObservableList<ButtonType> buttontypes= this.dialog.getDialogPane().getButtonTypes();
        Optional <ButtonType> ok = buttontypes.stream().filter(t -> t.getText().equals(InternationalizationUtils.get("dialog.button.ok"))).findFirst();
        return dialog.getDialogPane().lookupButton(ok.get());
    }

    private void disableok(boolean value) {
        getOk().setDisable(value);
    }

    private class Change {
        private String what;
        private Object value;

        public Change(String what, Object newvalue) {
            this.what = what;
            this.value = value;
        }
    }

    public void onChange(Change change) {
        Optional<Change> alreadychanged = changes.stream().filter(c -> c.what == change.what).findFirst();
        if (alreadychanged.isPresent())
            alreadychanged.get().value = change.value;
        else
            changes.add(change);
        if (addingProperty) {
            disableok(txtName.getText().isEmpty() || cmbType.getSelectionModel().getSelectedIndex() < 0);
        }
        else
            disableok(txtName.getText().isEmpty() || txtName.getText().equals(targetProperty.getName()));
    }

    private void onSave() {
        if (addingProperty) {
            try {
                Property newProperty = new Property();
                newProperty.setName(txtName.getText());
                newProperty.setRequired(cbRequired.isSelected());
                newProperty.setMultiple(cbMultiple.isSelected());
                newProperty.setType( ((IdName) cmbType.getSelectionModel().getSelectedItem()).getId());
                parentClass.addProperty(newProperty);
                onOk.onOk();
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }
        else {
            try {
                targetProperty.setName(txtName.getText());
                parentClass.editProperty(targetProperty);
                onOk.onOk();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
    }
}
