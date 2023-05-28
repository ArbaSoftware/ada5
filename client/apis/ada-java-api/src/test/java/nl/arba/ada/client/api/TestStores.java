package nl.arba.ada.client.api;

import nl.arba.ada.client.api.exceptions.StoreNotCreatedException;
import nl.arba.ada.client.api.security.Everyone;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.Right;
import nl.arba.ada.client.api.security.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class TestStores {
    private static Domain domain;
    private static String TEST_URL = "http://192.168.2.74:9601/ada";
    private final static String TEST_USER_1_EMAIL = "test@test.nl";
    private final static String TEST_USER_1_PASSWORD = "test";
    private final static String TEST_USER_2_EMAIL= "dev@arjanbas.nl";
    private final static String TEST_USER_2_PASSWORD = "hemertje";

    @BeforeClass
    public static void before() throws IOException {
        domain = Domain.create(TEST_URL);
        domain.login(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD);
    }

    @Test
    public void createStore() throws Exception {
        Store store = null;
        try {
            store = domain.createStore("test" + System.currentTimeMillis());
        }
        finally {
            if (store != null)
                store.delete();
        }
    }

    @Test
    public void createStoreForEverybody() throws Exception {
        Store store = null;
        try {
            int allrightslevel = 0;
            for (Right right: domain.getRights()) {
                if (right.isStoreRight())
                    allrightslevel+= right.getLevel();
            }
            store = domain.createStore("test" + System.currentTimeMillis(), new GrantedRight[]{
                    GrantedRight.create(Everyone.create(), allrightslevel)
            });
        }
        finally {
            if (store != null)
                store.delete();
        }
    }

    @Test
    public void createStoreNotForMe() throws Exception {
        String storeName = "test" + System.currentTimeMillis();
        System.out.println(storeName);
        User otherUser = new User();
        otherUser.setEmail(TEST_USER_2_EMAIL);
        try {
            domain.createStore(storeName, new GrantedRight[]{
                    GrantedRight.create(otherUser, 5)
            });
        }
        catch (StoreNotCreatedException snce) {
            //Expected
        }
        Domain otheruserDomain = Domain.create(TEST_URL);
        otheruserDomain.login(TEST_USER_2_EMAIL, TEST_USER_2_PASSWORD);
        List <Store> stores = otheruserDomain.getStores();
        Assert.assertTrue("Aangemaakte store met rechten voor andere gebruiker niet aangetroffen", stores.stream().anyMatch(s -> s.getName().equals(storeName)));
        stores.stream().filter(s -> s.getName().equals(storeName)).findFirst().get().delete();
    }

    @Test
    public void createStoreWithAddons() throws Exception {
        Store store = null;
        try {
            store = domain.createStore("test" + System.currentTimeMillis(), new GrantedRight[]{}, new String[]{"base"});
        }
        finally {
            store.delete();
        }
    }

}
