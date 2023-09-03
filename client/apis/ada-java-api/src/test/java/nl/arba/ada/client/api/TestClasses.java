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

    @Test
    public void edit_class_property() throws Exception {
        AdaClass folder = new AdaClass();
        folder.setFolderClass(true);
        folder.setDocumentClass(false);
        folder.setName("Folder1");
        GrantedRight right = GrantedRight.create(Everyone.create(), 9);
        folder.addRight(right);
        folder.addProperty(Property.create("Name", PropertyType.STRING));
        folder = store.addClass(folder);

        AdaClass refreshed = store.getAdaClass(folder.getId());
        Property only = refreshed.getProperties()[0];
        only.setName("Changed");
        refreshed.editProperty(only);
        Assert.assertTrue(refreshed.hasProperty("Changed"));
    }

    @Test
    public void add_class_property() throws Exception {
        AdaClass folder = new AdaClass();
        folder.setFolderClass(true);
        folder.setDocumentClass(false);
        folder.setName("Folder1");
        GrantedRight right = GrantedRight.create(Everyone.create(), 9);
        folder.addRight(right);
        folder.addProperty(Property.create("Name", PropertyType.STRING));
        folder = store.addClass(folder);

        AdaClass refreshed = store.getAdaClass(folder.getId());
        Property newProperty = new Property();
        newProperty.setName("Nieuw");
        newProperty.setType(PropertyType.STRING);
        newProperty.setMultiple(true);
        newProperty.setRequired(true);
        refreshed.addProperty(newProperty);

        refreshed = store.getAdaClass(folder.getId());
        Assert.assertTrue(refreshed.hasProperty("Nieuw"));

        refreshed.deleteProperty(refreshed.getProperty("Nieuw"));
        refreshed = store.getAdaClass(folder.getId());
        Assert.assertFalse(refreshed.hasProperty("Nieuw"));
    }

    @Test
    public void get_class() throws Exception{
        Store mystore = domain.getStore("052ebd3e-310f-11ee-915b-98f2b3f20cf4");
        System.out.println(mystore.getName());
        AdaClass clazz = mystore.getAdaClass("055bff90-310f-11ee-915b-98f2b3f20cf4");
        System.out.println(clazz.getName());
    }

    @Test
    public void edit_class_rights() throws Exception {
        AdaClass[] classes = store.getClasses();
        AdaClass clazz = new AdaClass();
        clazz.setName("test");
        System.out.println(clazz.toJson());
        AdaClass refreshed = store.addClass(clazz);
        System.out.println(refreshed.getId());
        AdaClass again = store.getAdaClass(refreshed.getId());
        again.getGrantedRights().get(0).setLevel(142);
        /*
        GrantedRight newright = new GrantedRight();
        newright.setLevel(1);
        newright.setGrantee(Everyone.create());
        again.addRight(newright);
         */

        System.out.println(again.getId());
        //again.update();

        again = store.getAdaClass(again.getId());
        AdaClass updateClass = new AdaClass();
        updateClass.setStore(again.getStore());
        updateClass.setProperties(again.getProperties());
        Property newProperty = new Property();
        newProperty.setName("Toegevoegddoor");
        newProperty.setType(PropertyType.STRING);
        updateClass.addProperty(newProperty);
        updateClass.setId(again.getId());
        updateClass.setGrantedrights(again.getGrantedRights().toArray(new GrantedRight[0]));
        updateClass.update();

    }

    @Test
    public void test_parent_class() throws Exception {
        Store cmis = store.getDomain().getStore("cmis");
        cmis.getClasses();
        AdaClass parentclass = cmis.getAdaClass("3b997608-085f-11ee-905d-98f2b3f20cf4");
        //{"name" : "Vergaderdossier", "documentclass": false,"folderclass": true,"parentclass": "3b997608-085f-11ee-905d-98f2b3f20cf4","rights":[],"properties":[]}
        AdaClass newClass = new AdaClass();
        newClass.setParentClass(parentclass);
        newClass.setName("Vergaderdossier");
        newClass.setFolderClass(true);
        System.out.println(newClass.toJson());
        cmis.addClass(newClass);


    }

}
