package nl.arba.ada.server.cmis.model;

import java.util.HashMap;
import java.util.List;

public class CMISObject {
    private HashMap <String, PropertyValue> propertyValues = new HashMap<>();

    public void setObjectId(String id) {
        setProperty(PropertyValue.create(Property.createObjectId(), id));
    }

    public void setBaseTypeId(String basetypeid) {
        setProperty(PropertyValue.create(Property.createBaseTypeId(), basetypeid));
    }

    public void setObjectType(String type) {
        setProperty(PropertyValue.create(Property.createObjectType(), type));
    }

    public void setProperty(PropertyValue value) {
        propertyValues.put(value.getProperty().getId(), value);
    }

    public List <PropertyValue> getProperties() {
        return propertyValues.values().stream().toList();
    }
}
