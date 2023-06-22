package nl.arba.ada.server.cmis.model;

public enum BaseTypeId {
    CMIS_FOLDER, CMIS_DOCUMENT, CMIS_ITEM;

    public String getValue() {
        if (this.equals(CMIS_FOLDER))
            return "cmis:folder";
        else if (this.equals(CMIS_DOCUMENT))
            return "cmis:document";
        else
            return "cmis:item";
    }
}
