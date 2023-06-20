package nl.arba.ada.server.cmis.model;

public class PropertyValue {
    private Property property;
    private Object value;

    private PropertyValue(Property property, Object value) {
        this.property = property;
        this.value = value;
    }

    public Property getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }

    public static PropertyValue create(Property property, Object value) {
        return new PropertyValue(property, value);
    }
}
