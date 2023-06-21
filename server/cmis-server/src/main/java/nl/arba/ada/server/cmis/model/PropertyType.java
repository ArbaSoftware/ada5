package nl.arba.ada.server.cmis.model;

public enum PropertyType {
    ID, STRING, DATETIME;

    public String getValue() {
        if (this.equals(ID))
            return "id";
        else if (this.equals(DATETIME))
            return "datetime";
        else
            return "string";
    }
}
