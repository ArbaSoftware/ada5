package nl.arba.ada.client.api;

import nl.arba.ada.client.api.exceptions.ClassNotDeletedException;
import nl.arba.ada.client.api.security.Everyone;
import nl.arba.ada.client.api.security.GrantedRight;
import org.junit.*;

import java.io.IOException;

public class TestClasses {
    private static Domain domain;
    private static String TEST_URL = "http://192.168.2.74:9601/ada";
    private final static String TEST_USER_1_EMAIL = "test@test.nl";
    private final static String TEST_USER_1_PASSWORD = "test";
    private final static String TEST_USER_2_EMAIL= "dev@arjanbas.nl";
    private final static String TEST_USER_2_PASSWORD = "hemertje";
    private static Store store;

    @BeforeClass
    public static void before() throws Exception {
        domain = Domain.create(TEST_URL);
        domain.login(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD);
        try {
            store = domain.createStore("test" + System.currentTimeMillis());
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    @AfterClass
    public static void after() throws ClassNotDeletedException {
        store.delete();
    }

    @Test
    public void test_create_class() throws Exception {
        AdaClass folder = new AdaClass();
        folder.setFolderClass(true);
        folder.setDocumentClass(false);
        folder.setName("Folder");
        GrantedRight right = GrantedRight.create(Everyone.create(), 1);
        folder.addRight(right);
        folder.addProperty(Property.create("Name", PropertyType.STRING));
        folder = store.addClass(folder);

        AdaClass anotherfolder = new AdaClass();
        anotherfolder.setName("AnotherFolder");
        anotherfolder.setParentClass(folder);
        anotherfolder.addRight(right);
        anotherfolder = store.addClass(anotherfolder);
        System.out.println(anotherfolder.getParentClass().getName());
        System.out.println("Store id: " + store.getId());
        System.out.println("Class id: " + anotherfolder.getId());

        AdaClass[] classes = store.getClasses();
    }

}
