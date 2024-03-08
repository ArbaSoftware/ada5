package nl.arba.ada.client.api;

import nl.arba.ada.client.api.addon.base.Folder;
import nl.arba.ada.client.api.addon.base.RootFolder;
import nl.arba.ada.client.api.exceptions.ClassNotDeletedException;
import nl.arba.ada.client.api.search.PropertyFilter;
import nl.arba.ada.client.api.search.Search;
import nl.arba.ada.client.api.security.Everyone;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.Right;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class TestFolders {
    private static Domain domain;
    private static Store testStore;
    private static String TEST_URL = "http://192.168.2.74:9601/ada";
    private final static String TEST_USER_1_EMAIL = "test@test.nl";
    private final static String TEST_USER_1_PASSWORD = "test";
    private final static String TEST_USER_2_EMAIL= "dev@arjanbas.nl";
    private final static String TEST_USER_2_PASSWORD = "hemertje";
    private static Store store;

    @BeforeClass
    public static void before() throws IOException {
        domain = Domain.create(TEST_URL);
        domain.login(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD);
    }

    @AfterClass
    public static void after() throws ClassNotDeletedException {
        store.delete();
    }

    @Test
    public void test() throws Exception {
        int allowAll = 0;
        for (Right right: domain.getRights()) {
            if (right.isStoreRight()) {
                allowAll += right.getLevel();
            }
        }
        store = domain.createStore("test" + System.currentTimeMillis(), new GrantedRight[] {GrantedRight.create(Everyone.create(), allowAll)}, new String[] { "base"});
        Folder rootFolder = RootFolder.addSubFolder(store, "Eerste folder");
        Folder subFolder = rootFolder.createSubFolder("Subfolder");
        Folder rootFolder2 = RootFolder.addSubFolder(store, "Tweede rootfolder");

        Folder[] subfolders = RootFolder.create(store).getSubFolders();
        Folder[] subfolders2 = rootFolder.getSubFolders();
    }

    @Test
    public void cmis() throws Exception {
        Store cmis = domain.getStore("cmis");
        Folder[] root = cmis.getRootFolders();
        AdaClass folderClass = cmis.getAdaClass("Folder");
        Property parentFolder = folderClass.getProperty("ParentFolder");
        Search search = Search.create(cmis.getAdaClass("Folder"));
        search.addFilter(PropertyFilter.createNullFilter("ParentFolder", parentFolder.getType()));
        search.addFilter(PropertyFilter.createEqualFilter("Name", folderClass.getProperty("Name").getType(), "Testfolder"));
        search.addProperty("Name");
        AdaObject[] objects = domain.search(cmis, search);
        System.out.println(objects);
        /*
        Folder parent = Folder.create(cmis.getObject("792b8acf-48f0-11ee-915b-98f2b3f20cf4"));
        Folder[] subfolders = parent.getSubFolders();
        System.out.println(subfolders.length);

        AdaClass vdclass = cmis.getAdaClass("c70d7912-48d0-11ee-915b-98f2b3f20cf4");
        AdaObject vergaderdossier = new AdaObject();
        vergaderdossier.setClassid(vdclass.getId());
        vergaderdossier.setStringProperty("Name", "Vergaderdossier");
        vergaderdossier.setObjectProperty("ParentFolder", parent.getId());
        System.out.println(vergaderdossier.createAddRequest());
        cmis.createObject(vergaderdossier);
         */
    }
}
