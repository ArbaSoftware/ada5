package nl.arba.ada.client.api;

import nl.arba.ada.client.api.security.Everyone;
import nl.arba.ada.client.api.security.GrantedRight;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestObjects {
    private static Domain domain;
    private static String TEST_URL = "http://192.168.2.74:9601/ada";
    private final static String TEST_USER_1_EMAIL = "test@test.nl";
    private final static String TEST_USER_1_PASSWORD = "test";
    private final static String TEST_USER_2_EMAIL= "dev@arjanbas.nl";
    private final static String TEST_USER_2_PASSWORD = "hemertje";
    private static Store store = null;
    private static AdaClass folder = null;

    @BeforeAll
    public static void before() throws Exception {
        domain = Domain.create(TEST_URL);
        domain.login(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD);
        store = domain.createStore("test" + System.currentTimeMillis());
        folder = new AdaClass();
        folder.setFolderClass(true);
        folder.setDocumentClass(false);
        folder.setName("Folder");
        folder.addProperty(Property.create("Name", PropertyType.STRING));
        folder = domain.addClass(store.getId(), folder);
    }

    @AfterAll
    public static void after() throws Exception {
        store.delete();
    }

    @Test
    public void doTest() throws Exception {
        AdaObject newObject = new AdaObject();
        newObject.setClassid(folder.getId());
        newObject = domain.addObject(store, newObject);
    }

}
