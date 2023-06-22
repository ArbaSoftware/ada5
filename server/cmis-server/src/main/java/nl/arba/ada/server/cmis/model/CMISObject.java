package nl.arba.ada.server.cmis.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CMISObject {
    private HashMap <String, PropertyValue> propertyValues = new HashMap<>();

    public CMISObject(String id, String name, BaseTypeId basetype, String objecttype, String path, String createdby, Date createdon, String lastmodifiedby, Date lastmodifiedon) {
        setProperty(PropertyValue.create(Property.createObjectId(), id));
        setProperty(PropertyValue.create(Property.createName(), name));
        setProperty(PropertyValue.create(Property.createBaseTypeId(), basetype.getValue()));
        setProperty(PropertyValue.create(Property.createObjectType(), objecttype));
        setProperty(PropertyValue.create(Property.createPath(), path));
        setProperty(PropertyValue.create(Property.createLastModifiedBy(), lastmodifiedby));
        setProperty(PropertyValue.create(Property.createLastModificationDate(), lastmodifiedon));
        setProperty(PropertyValue.create(Property.createdBy(), createdby));
        setProperty(PropertyValue.create(Property.createCreationDate(), createdon));
    }

    public void setProperty(PropertyValue value) {
        propertyValues.put(value.getProperty().getId(), value);
    }

    public List <PropertyValue> getProperties() {
        return propertyValues.values().stream().toList();
    }
}
