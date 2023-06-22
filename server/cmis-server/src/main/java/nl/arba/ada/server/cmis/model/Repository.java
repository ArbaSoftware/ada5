package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.Store;

import java.util.Date;

public class Repository extends CMISObject {
    private Repository(String id, String name, BaseTypeId basetype, String objecttype, String path) {
        super(id, name, basetype, objecttype, path, "me", new Date(System.currentTimeMillis()), "me", new Date(System.currentTimeMillis()));
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
        return new Repository(store.getId(), store.getName(), BaseTypeId.CMIS_FOLDER, "cmis:folder", "/");
    }
}
