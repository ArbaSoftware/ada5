package nl.arba.ada.client.api;

import nl.arba.ada.client.api.exceptions.InvalidPropertyTypeException;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Class that holds a property value
 */
public class PropertyValue {
    private String id;
    private String name;
    private PropertyType type;
    private Object value;

    /**
     * Set the id of the property which value is hold by this object
     * @param id The id of the property which value is hold by this object
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the id of property which value is hold by this object
     * @return The id of the property which value is hold by this object
     */
    public String getId() {
        return id;
    }

    /**
     * Set the name of the property which value is hold by this object
     * @param name The name of the property which value is hold by this object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of property which value is hold by this object
     * @return The name of the property which value is hold by this object
     */
    public String getName() {
        return name;
    }

    /**
     * Set the type of property which value is hold by this object
     * @param type The type of the property which value is hold by this object
     * @see PropertyType
     * @throws InvalidPropertyTypeException Throwed when the given type is invalid
     */
    public void setType(String type) throws InvalidPropertyTypeException {
         this.type = PropertyType.fromString(type);
    }

    /**
     * Set the property type
     * @param type The property type
     * @see PropertyType
     */
    public void setType(PropertyType type) {
        this.type = type;
    }

    /**
     * Get the type of property which value is hold by this object
     * @return The type of the property which value is hold by this object
     * @see PropertyType
     */
    public PropertyType getType() {
        return type;
    }

    /**
     * Set the value of the property
     * @param value The value of the property
     */
    public void setValue(Object value) {
        if (value instanceof Map) {
            Map <String, Integer> dateInput = (Map <String, Integer>) value;
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DATE, dateInput.get("day"));
            c.set(Calendar.MONTH, dateInput.get("month")-1);
            c.set(Calendar.YEAR, dateInput.get("year"));
            this.value = c.getTime();
        }
        else
            this.value = value;
    }

    /**
     * Get the value of the property
     * @return The value of the property
     */
    public Object getValue() {
        return value;
    }

    public String toJson() {
        String json = "{";
        String prefix = "";
        if (getId() != null) {
            json += "\"id\":\"" + getId() + "\"";
            prefix = ",";
        }
        if (getName() != null) {
            json += prefix + "\"name\":\"" + getName() + "\"";
            prefix = ",";
        }
        json += prefix + "\"type\":\"" + getType().toString() + "\"";
        prefix = ",";
        if (getValue() != null) {
            json += prefix + "\"value\":";
            if (getType().equals(PropertyType.STRING))
                json += "\"" + getValue() + "\"";
            else if (getType().equals(PropertyType.DATE)) {
                Map<String,Integer> dateValue = (Map<String,Integer>) getValue();
                json += "{\"day\":" + dateValue.get("day") + ",";
                json += "\"month\":" + dateValue.get("month") + ",";
                json += "\"year\":" + dateValue.get("year");
                json += "}";
            }
            else if (getType().equals(PropertyType.OBJECT)) {
                json += "\""+ getValue() + "\"";
            }
            else if (getType().equals(PropertyType.INTEGER))
                json += getValue();
        }
        json += "}";
        return json;
    }
}
