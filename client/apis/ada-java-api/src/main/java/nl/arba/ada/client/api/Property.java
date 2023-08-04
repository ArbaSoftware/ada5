package nl.arba.ada.client.api;
import nl.arba.ada.client.api.exceptions.InvalidPropertyTypeException;

/**
 * Class representing a property
 */
public class Property {
    private String id;
    private String name;
    private PropertyType type;
    private boolean required;
    private boolean multiple;

    /**
     * Set the unique id of the property
     * @param id The unique id of the property
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the unique id of the property
     * @return The unique id of the property
     */
    public String getId() {
        return id;
    }

    /**
     * Set the name of the property
     * @param name The name of the property
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the property
     * @return The name of the property
     */
    public String getName() {
        return name;
    }

    /**
     * Set the datatype of the property
     * @param type The datatype of the property
     * @see PropertyType
     */
    public void setType(PropertyType type) {
        this.type = type;
    }

    /**
     * Set the datatype of the property
     * @param type The datatype of the property
     * @see PropertyType
     */
    public void setType(String type) {
        try {
            this.type = PropertyType.fromString(type);
        }
        catch (Exception err) {}
    }

    /**
     * Get the datatype of the property
     * @return The datatype of the property
     * @see PropertyType
     */
    public PropertyType getType() {
        return type;
    }

    /**
     * Set if the property is required
     * @param value <code>true</code> - the property is required, <code>false</code> - the property is not required
     */
    public void setRequired(boolean value) {
        this.required = value;
    }

    /**
     * Get if the property is required
     * @return <code>true</code> - the property is required, <code>false</code> - the property is not required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Set if the property can have multiple values
     * @param value <code>true</code> - the property can have multiple values, <code>false</code> - the property can not have multiple values
     */
    public void setMultiple(boolean value) {
        multiple = value;
    }

    /**
     * Get if the property can have multiple values
     * @return <code>true</code> - the property can have multiple values, <code>false</code> - the property can not have multiple values
     */
    public boolean isMultiple() {
        return multiple;
    }

    /**
     * Create a json representation of the property
     * @return A json representation of the property
     */
    public String toJson() {
        return "{" +
                (getId() == null ? "" : "\"id\":\"" + getId() + "\",") +
                "\"name\":\"" + getName() + "\"," +
                "\"type\":\"" + getType().toString() + "\"," +
                "\"required\":" + isRequired() + "," +
                "\"multiple\":" + isMultiple() +
                "}";
    }

    /**
     * Helper method to create a property
     * @param name The name of the property
     * @param type The type of the property
     * @return An instance of the property class
     * @see PropertyType
     */
    public static Property create(String name, PropertyType type) {
        Property result = new Property();
        result.setName(name);
        result.setType(type);
        return result;
    }
}
