package nl.arba.ada.server.cmis.model;

public class Property {
    private String id;
    private String localName;
    private String displayName;
    private String queryName;
    private PropertyType type;
    private Cardinality cardinality;

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

    public static Property createObjectId() {
        Property result = new Property();
        result.id = "cmis:objectId";
        result.localName = "objectId";
        result.displayName = "Object Id";
        result.queryName = "cmis:objectId";
        result.type = PropertyType.ID;
        result.cardinality = Cardinality.SINGLE;
        return result;
    }

    public static Property createBaseTypeId() {
        Property result = new Property();
        result.id = "cmis:baseTypeId";
        result.localName = "baseTypeId";
        result.displayName = "Base Type Id";
        result.queryName = "cmis:baseTypeId";
        result.type = PropertyType.ID;
        result.cardinality = Cardinality.SINGLE;
        return result;
    }

    public static Property createObjectType() {
        Property result = new Property();
        result.id = "cmis:objectType";
        result.localName = "objectType";
        result.displayName = "Object Type";
        result.queryName = "cmis:objectType";
        result.type = PropertyType.ID;
        result.cardinality = Cardinality.SINGLE;
        return result;
    }

}
