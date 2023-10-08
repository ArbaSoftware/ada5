package nl.arba.ada.client.adaclient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import nl.arba.ada.client.adaclient.controls.PropertiesPane;
import nl.arba.ada.client.adaclient.controls.RightsTable;
import nl.arba.ada.client.adaclient.utils.ContentUtils;
import nl.arba.ada.client.adaclient.utils.IdName;
import nl.arba.ada.client.api.*;
import nl.arba.ada.client.api.addon.base.Document;
import nl.arba.ada.client.api.security.GrantedRight;

import java.net.URL;
import java.util.*;
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
    private Store newStore;
    private boolean addingFolder;
    private boolean updating = false;

    public ObjectPropertiesController(AdaObject target) {
        theObject = target;
        updating = true;
    }

    public ObjectPropertiesController(Store store, boolean addingfolder) {
        newStore = store;
        addingFolder = addingfolder;
    }

    public void setOkButton(Button ok) {
        okButton = ok;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            AdaClass[] classes = theObject == null ? newStore.getClasses() : theObject.getStore().getClasses();
            List<IdName> items = Arrays.asList(classes).stream().filter(c -> addingFolder ? c.isFolderClass(): updating? true: !c.isFolderClass()).map(c -> new IdName(c.getId(), c.getName() )).collect(Collectors.toList());
            items.sort(new Comparator<IdName>() {
                @Override
                public int compare(IdName o1, IdName o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
            for (IdName item: items)
                cmbClass.getItems().add(item);
            if (updating) {
                IdName toSelect = items.stream().filter(i -> i.getId().equals(theObject.getClassId())).findFirst().get();
                cmbClass.getSelectionModel().select(toSelect);
                cmbClass.setDisable(true);
            }
            else {
                cmbClass.setDisable(false);
                cmbClass.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<IdName>() {
                    @Override
                    public void changed(ObservableValue observableValue, IdName o, IdName t1) {
                        try {
                            AdaClass newClass = newStore.getAdaClass(t1.getId());
                            propertiesPane.onChangeClass(newClass);
                        }
                        catch (Exception err) {

                        }
                    }
                });
            }

            if (updating)
                propertiesPane = new PropertiesPane(theObject);
            else
                propertiesPane = new PropertiesPane();
            propertiesScrollPane.setContent(propertiesPane);

            rightsTable = new RightsTable(updating? theObject.getRights() : Arrays.asList(new GrantedRight[0]), updating? theObject.getStore().getDomain() : newStore.getDomain(), (updating ? theObject.getStore().getDomain() : newStore.getDomain()).getRights().stream().filter(r -> r.isObjectRight()).collect(Collectors.toList()));
            rightsPane.setCenter(rightsTable);
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    public AdaObject getUpdatedObject() throws Exception {
        AdaObject tosave = new AdaObject();
        tosave.setStore(theObject.getStore());
        tosave.setId(theObject.getId());
        GrantedRight[] rights = new GrantedRight[rightsTable.getItems().size()];
        int index = 0;
        for (Object right: rightsTable.getItems()) {
            rights[index] = (GrantedRight) right;
            index++;
        }
        tosave.setRights(rights);
        for (PropertyValue value: propertiesPane.getPropertyValues()) {
            if (value.getValue() == null)
                tosave.setNullProperty(value.getName());
            else if (value.getValue() instanceof String)
                tosave.setStringProperty(value.getName(), (String) value.getValue());
        }
        return tosave;
    }

    public AdaObject getNewObject() throws Exception {
        AdaClass targetClass = newStore.getAdaClass(((IdName) cmbClass.getSelectionModel().getSelectedItem()).getId());
        AdaObject tosave = null;
        if (targetClass.isDocumentClass() && propertiesPane.hasContentFile()) {
            tosave = new Document();
            ((Document) tosave).setContent(Content.create(propertiesPane.getContentFile(), ContentUtils.getMimetype(propertiesPane.getContentFile()), false));
        }
        else {
            tosave = new AdaObject();
        }
        tosave.setClassid(targetClass.getId());
        tosave.setStore(newStore);
        GrantedRight[] rights = new GrantedRight[rightsTable.getItems().size()];
        int index = 0;
        for (Object right : rightsTable.getItems()) {
            rights[index] = (GrantedRight) right;
            index++;
        }
        tosave.setRights(rights);
        for (PropertyValue value : propertiesPane.getPropertyValues()) {
            if (value.getValue() == null)
                tosave.setNullProperty(value.getName());
            else if (value.getValue() instanceof String)
                tosave.setStringProperty(value.getName(), (String) value.getValue());
        }
        return tosave;
    }

}
