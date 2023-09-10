package nl.arba.ada.client.adaclient.controls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import nl.arba.ada.client.api.*;
import nl.arba.ada.client.api.exceptions.AdaClassNotFoundException;
import nl.arba.ada.client.api.util.JsonUtils;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class PropertiesPane extends AnchorPane {
    private AdaObject targetObject;
    private HashMap <String, Object> propertyValues = new HashMap<>();
    private HashMap <String, Control> inputControls = new HashMap<>();
    private HashMap <String, CheckBox> nullCheckboxes = new HashMap <> ();
    private HashMap <String, PropertyType> propertyTypes = new HashMap<>();
    private int currentY = 15;
    private double maxLabelWidth = 0d;
    private double maxInputWidth = 0d;
    private ObjectMapper jsonMapper = new ObjectMapper();

    public PropertiesPane(AdaObject target) {
        super();
        this.targetObject = target;
        try {
            AdaClass clazz = targetObject.getStore().getAdaClass(targetObject.getClassId());
            for (Property property: clazz.getProperties()) {
                if (targetObject.getProperties().stream().filter(p -> p.getId().equals(property.getId())).findFirst().isPresent()) {
                    try {
                        addProperty(property.getName(), property.getType(), targetObject.getStringProperty(property.getName()));
                    } catch (Exception err) {
                    }
                }
                else
                    addProperty(property.getName(), property.getType(), null);
            }
        }
        catch (AdaClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }

    private void addProperty(String name, PropertyType type, Object value) {
        if (isTypeSupported(type)) {
            propertyTypes.put(name, type);
            Label propertyLabel = new Label(name);
            propertyLabel.setLayoutY(currentY);
            propertyLabel.setLayoutX(15);
            propertyLabel.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
                @Override
                public void changed(ObservableValue<? extends Bounds> observableValue, Bounds bounds, Bounds t1) {
                    if (t1.getWidth() > maxLabelWidth) {
                        inputControls.values().stream().forEach(i -> i.setLayoutX(15d + t1.getWidth() + 15d));
                        maxLabelWidth = t1.getWidth();
                    }
                }
            });
            getChildren().add(propertyLabel);
            Control inputControl = createInputControl(type, value);
            inputControls.put(name, inputControl);
            inputControl.setLayoutX(105);
            inputControl.setLayoutY(currentY - (inputControl.getLayoutBounds().getHeight() / 2));
            inputControl.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
                @Override
                public void changed(ObservableValue<? extends Bounds> observableValue, Bounds bounds, Bounds t1) {
                    if (t1.getWidth() > maxInputWidth) {
                        nullCheckboxes.values().stream().forEach(c -> c.setLayoutX(15d + maxLabelWidth + t1.getWidth()+25d));
                        maxInputWidth = t1.getWidth();
                    }
                }
            });
            getChildren().add(inputControl);

            CheckBox cbNull = new CheckBox();
            cbNull.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    if (t1)
                        makeInputEmpty(name);
                    else
                        inputControls.get(name).setDisable(false);
                }
            });
            nullCheckboxes.put(name, cbNull);
            cbNull.setLayoutY(currentY - (inputControl.getLayoutBounds().getHeight()) / 2);
            cbNull.setLayoutX(200d);
            cbNull.setSelected(value == null);
            getChildren().add(cbNull);
            currentY += 30;
        }
    }

    private void makeInputEmpty(String property) {
        Control input = inputControls.get(property);
        if (input instanceof TextField)
            ((TextField) input).setText("");
        else if (input instanceof DatePicker)
            ((DatePicker) input).setValue(null);
        input.setDisable(true);
    }

    private boolean isTypeSupported(PropertyType type) {
        return type.equals(PropertyType.STRING) || type.equals(PropertyType.DATE);
    }

    private Control createInputControl(PropertyType type, Object value) {
        if (type.equals(PropertyType.STRING))
            return createTextInputControl((String) value);
        else if (type.equals(PropertyType.DATE))
            return createDateInputControl((Date) value);
        else
            return null;
    }

    private TextField createTextInputControl(String value) {
        TextField control = new TextField();
        control.setText(value);
        return control;
    }

    private DatePicker createDateInputControl(Date value) {
        DatePicker control = new DatePicker();
        if (value != null)
            control.setValue(value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return control;
    }

    public PropertyValue[] getPropertyValues() {
        PropertyValue[] result = new PropertyValue[propertyTypes.size()];
        int index = 0;
        Iterator<String> properties = propertyTypes.keySet().iterator();
        while (properties.hasNext()) {
            String property = properties.next();
            PropertyValue value = new PropertyValue();
            value.setType(propertyTypes.get(property));
            value.setName(property);
            if (nullCheckboxes.get(property).isSelected())
                value.setValue(null);
            else if (propertyTypes.get(property).equals(PropertyType.STRING))
                value.setValue(((TextField) inputControls.get(property)).getText());
            else if (propertyTypes.get(property).equals(PropertyType.DATE))
                value.setValue(Date.from(((DatePicker) inputControls.get(property)).getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            result[index] = value;
            index++;
        }
        return result;
    }

    public String toJson() throws JsonProcessingException {
        String json = "{";
        Iterator<String> properties = inputControls.keySet().iterator();
        while (properties.hasNext()) {
            String property = properties.next();
            json += (json.equals("{") ? "": ",") + "\"" + property + "\":";
            if (nullCheckboxes.get(property).isSelected())
                json += "null";
            else if (propertyTypes.get(property).equals(PropertyType.STRING))
                json+= "\"" + jsonMapper.writeValueAsString(((TextField) inputControls.get(property)).getText()) + "\"";
            else if (propertyTypes.get(property).equals(PropertyType.DATE)) {
                Calendar c = Calendar.getInstance();
                Date dateValue = Date.from(((DatePicker) inputControls.get(property)).getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                c.setTime(dateValue);
                HashMap <String, Integer> dateMap = new HashMap<>();
                dateMap.put("day", c.get(Calendar.DATE));
                dateMap.put("month", c.get(Calendar.MONTH)+1);
                dateMap.put("year", c.get(Calendar.YEAR));
                json += jsonMapper.writeValueAsString(dateMap);
            }
        }
        json += "}";
        return json;
    }
}
