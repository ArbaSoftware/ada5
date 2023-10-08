package nl.arba.ada.client.api;

public enum ObjectRelationTypeObjectType {
    ANY, DOCUMENT, FOLDER;

    public String toString() {
        if (this.equals(DOCUMENT))
            return "document";
        else if (this.equals(FOLDER))
            return "folder";
        else
            return "any";
    }

    public static ObjectRelationTypeObjectType fromString(String value) {
        if (value.equalsIgnoreCase("document"))
            return DOCUMENT;
        else if (value.equalsIgnoreCase("folder"))
            return FOLDER;
        else
            return ANY;
    }
}
