package nl.arba.ada.client.api;

import nl.arba.ada.client.api.exceptions.InvalidPropertyTypeException;

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
     */
    public void setType(String type) throws InvalidPropertyTypeException {
         this.type = PropertyType.fromString(type);
    }

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
        this.value = value;
    }

    /**
     * Get the value of the property
     * @return The value of the property
     */
    public Object getValue() {
        return value;
    }
}
