package nl.arba.ada.client.api;

import nl.arba.ada.client.api.security.Everyone;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.Right;
import org.junit.*;

import java.io.IOException;
import java.util.List;

public class TestObjects {
    private static Domain domain;
    private static String TEST_URL = "http://192.168.2.74:9601/ada";
    private final static String TEST_USER_1_EMAIL = "test@test.nl";
    private final static String TEST_USER_1_PASSWORD = "test";
    private final static String TEST_USER_2_EMAIL= "dev@arjanbas.nl";
    private final static String TEST_USER_2_PASSWORD = "hemertje";
    private static Store store = null;
    private static AdaClass folder = null;

    @BeforeClass
    public static void before() throws Exception {
        domain = Domain.create(TEST_URL);
        domain.login(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD);
        List<Right> rights = domain.getRights();
        int allowAll = 0;
        for (Right right: rights) {
            if (right.isClassRight())
                allowAll += right.getLevel();
        }
        store = domain.createStore("test" + System.currentTimeMillis(), new GrantedRight[] {GrantedRight.create(Everyone.create(), allowAll)}, new String[] {"base"});
        folder = store.getAdaClass("Folder");
    }

    @AfterClass
    public static void after() throws Exception {
        store.delete();
    }

    @Test
    public void doTest() throws Exception {
        AdaObject newObject = new AdaObject();
        newObject.setClassid(folder.getId());
        newObject.setStringProperty("Name", "Eerste folder");
        newObject = domain.addObject(store, newObject);

        AdaObject childFolder = new AdaObject();
        childFolder.setClassid(folder.getId());
        childFolder.setStringProperty("Name", "Subfolder");
        childFolder.setObjectProperty("ParentFolder", newObject.getId());
        childFolder = domain.addObject(store, childFolder);
    }

}
