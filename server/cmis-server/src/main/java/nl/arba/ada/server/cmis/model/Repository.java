package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.Store;

import java.util.Date;

public class Repository extends CMISObject {
    private Repository(String id, String name, BaseTypeId basetype, String objecttype, String path, String createdby, Date createdon, String lastmodifier, Date lastmodifiedon) {
        super(id, name, basetype, objecttype, path, createdby, createdon, lastmodifier, lastmodifiedon);
        this.id = id;
        this.name = name;
    }

    private String id;
    private String name;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Repository fromStore(Store store) {
        return new Repository(store.getId(), store.getName(), BaseTypeId.CMIS_FOLDER, "cmis:folder", "/", store.getCreator(),store.getDateCreated(), store.getLastmodifier(), store.getLastModified());
    }
}
