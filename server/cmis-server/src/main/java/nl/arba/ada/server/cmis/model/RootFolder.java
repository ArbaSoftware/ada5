package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.Store;

import java.util.Date;

public class RootFolder extends CMISObject {
    private RootFolder(Store store) {
        super("root", "root", BaseTypeId.CMIS_FOLDER, "cmis:folder", "/", "me", new Date(System.currentTimeMillis()), "me", new Date(System.currentTimeMillis()));
    }

    public static RootFolder create(Store store) {
        return new RootFolder(store);
    }
}
