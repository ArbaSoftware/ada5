package nl.arba.ada.client.api;

import nl.arba.ada.client.api.security.Everyone;
import nl.arba.ada.client.api.security.GrantedRight;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestClasses {
    private static Domain domain;
    private static String TEST_URL = "http://192.168.2.74:9601/ada";
    private final static String TEST_USER_1_EMAIL = "test@test.nl";
    private final static String TEST_USER_1_PASSWORD = "test";
    private final static String TEST_USER_2_EMAIL= "dev@arjanbas.nl";
    private final static String TEST_USER_2_PASSWORD = "hemertje";

    @BeforeAll
    public static void before() throws IOException {
        domain = Domain.create(TEST_URL);
        domain.login(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD);
    }

    @Test
    public void test_create_class() throws Exception {
        Store newStore = domain.createStore("test" + System.currentTimeMillis());
        AdaClass folder = new AdaClass();
        folder.setFolderClass(true);
        folder.setDocumentClass(false);
        folder.setName("Folder");
        GrantedRight right = GrantedRight.create(Everyone.create(), 1);
        folder.addRight(right);
        folder.addProperty(Property.create("Name", PropertyType.TEXT));
        newStore.addClass(folder);
    }

}
