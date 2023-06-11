package nl.arba.ada.client.api;

import nl.arba.ada.client.api.addon.base.Document;
import nl.arba.ada.client.api.exceptions.ClassNotDeletedException;
import nl.arba.ada.client.api.exceptions.ObjectNotCreatedException;
import nl.arba.ada.client.api.exceptions.StoreNotCreatedException;
import nl.arba.ada.client.api.security.Everyone;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.Right;
import nl.arba.ada.client.api.util.StreamUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestDocuments {
    private static Domain domain;
    private static Store testStore;
    private static String TEST_URL = "http://192.168.2.74:9601/ada";
    private final static String TEST_USER_1_EMAIL = "test@test.nl";
    private final static String TEST_USER_1_PASSWORD = "test";
    private final static String TEST_USER_2_EMAIL= "dev@arjanbas.nl";
    private final static String TEST_USER_2_PASSWORD = "hemertje";
    private static Store store;

    @BeforeClass
    public static void before() throws IOException, StoreNotCreatedException {
        domain = Domain.create(TEST_URL);
        domain.login(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD);
        int allowAll = 0;
        for (Right right: domain.getRights()) {
            if (right.isStoreRight()) {
                allowAll += right.getLevel();
            }
        }
        store = domain.createStore("test" + System.currentTimeMillis(), new GrantedRight[] {GrantedRight.create(Everyone.create(), allowAll)}, new String[] { "base"});
    }

    @AfterClass
    public static void after() throws ClassNotDeletedException {
        store.delete();
    }

    @Test
    public void test() throws ObjectNotCreatedException, IOException {
        Document newDoc = new Document();
        newDoc.setTitle("Eerste document");
        newDoc.setContent(Content.create(new File("/home/arjanbas/test.txt"), "text/text", false ));
        newDoc.refresh(store.createObject(newDoc));
        newDoc.checkout();
        newDoc.checkin(Content.create(new File("/home/arjanbas/test.txt"), "text/text", false ));
        System.out.println(newDoc.getMajorVersion() + "." + newDoc.getMinorVersion() + " (" + newDoc.getMimetype() + ")");
        System.out.println(newDoc.getStore().getId() + " / " + newDoc.getId());
        System.out.println(newDoc.isCheckedOut() + " -> " + newDoc.getCheckOutUser().getEmail());
    }
}
