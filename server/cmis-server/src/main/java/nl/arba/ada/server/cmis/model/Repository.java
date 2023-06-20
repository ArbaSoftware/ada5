package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.Store;

public class Repository extends CMISObject {
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
        Repository result = new Repository();
        result.setId(store.getId());
        result.setName(store.getName());
        result.setObjectId("root");
        return result;
    }
}
