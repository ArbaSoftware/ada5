package nl.arba.ada.server.cmis.model;

public class Property {
    private String id;
    private String localName;
    private String displayName;
    private String queryName;
    private PropertyType type;
    private Cardinality cardinality;
    private boolean required;

    public String getId() {
        return id;
    }

    public String getLocalName() {
        return localName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getQueryName() {
        return queryName;
    }

    public PropertyType getType() {
        return type;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public boolean isRequired() {
        return required;
    }

    public static Property createObjectId() {
        return createProperty("cmis:objectId", "objectId", "Object Id", "cmis:objectId", PropertyType.ID, Cardinality.SINGLE, true);
    }

    public static Property createBaseTypeId() {
        return createProperty("cmis:baseTypeId", "baseTypeId", "Base Type Id", "cmis:baseTypeId", PropertyType.ID, Cardinality.SINGLE, true);
    }

    public static Property createObjectType() {
        return createProperty("cmis:objectTypeId", "objectTypeId", "Object Type", "cmis:objectTypeId", PropertyType.ID, Cardinality.SINGLE, true);
    }

    public static Property createdBy() {
        return createProperty("cmis:createdBy", "createdBy", "Created by", "cmis:createdBy", PropertyType.STRING, Cardinality.SINGLE, false);
    }

    public static Property createLastModifiedBy() {
        return createProperty("cmis:lastModifiedBy", "lastModifiedBy", "Last modified by", "cmis:lastModifiedBy", PropertyType.STRING, Cardinality.SINGLE, false);
    }

    public static Property createCreationDate() {
        return createProperty("cmis:creationDate", "creationDate", "Creation date", "cmis:creationDate", PropertyType.DATETIME, Cardinality.SINGLE, false);
    }

    public static Property createName() {
        return createProperty("cmis:name","name", "Name","cmis:name", PropertyType.STRING, Cardinality.SINGLE, false);
    }
    public static Property createLastModificationDate() {
        return createProperty("cmis:lastModificationDate", "lastModificationDate", "Last modification date", "cmis:lastModificationDate", PropertyType.DATETIME, Cardinality.SINGLE, false);
    }

    public static Property createChangeToken() {
        return createProperty("cmis:changeToken", "changeToken", "Change token", "cmis:changeToken", PropertyType.STRING, Cardinality.SINGLE, false);
    }

    public static Property createPath() {
        return createProperty("cmis:path", "path", "Path", "cmis:path", PropertyType.STRING, Cardinality.SINGLE, true);
    }

    public static Property createProperty(String id, String localname, String displayname, String queryname, PropertyType type, Cardinality cardinality, boolean required) {
        Property result = new Property();
        result.id = id;
        result.localName = localname;
        result.displayName = displayname;
        result.queryName = queryname;
        result.type = type;
        result.cardinality = cardinality;
        result.required = required;
        return result;
    }

}
