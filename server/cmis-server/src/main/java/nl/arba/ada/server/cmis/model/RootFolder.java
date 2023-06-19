package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.Store;

public class RootFolder extends CMISObject {
    private RootFolder(Store store) {

    }

    public static RootFolder create(Store store) {
        return new RootFolder(store);
    }
}
