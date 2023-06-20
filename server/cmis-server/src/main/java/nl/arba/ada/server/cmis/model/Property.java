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

}
