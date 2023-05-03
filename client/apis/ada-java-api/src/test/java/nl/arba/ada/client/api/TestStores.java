package nl.arba.ada.client.api;

import nl.arba.ada.client.api.exceptions.StoreNotCreatedException;
import nl.arba.ada.client.api.security.Everyone;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class TestStores {
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
    public void getStores() throws IOException {
        Assertions.assertEquals(Boolean.FALSE, domain.getStores().isEmpty(), "Geen stores aangetroffen");
    }

    @Test
    public void getStore() throws Exception {
        List <Store> stores = domain.getStores();
        Assertions.assertNotNull(domain.getStore(stores.get(0).getId()), "Store kon niet worden opgevraagd");
    }

    @Test
    public void createStore() throws Exception {
        domain.createStore("test" + System.currentTimeMillis());
    }

    @Test
    public void createStoreForEverybody() throws Exception {
        domain.createStore("test" + System.currentTimeMillis(), new GrantedRight[] {
                GrantedRight.create(Everyone.create(), 1)
        });
    }

    @Test
    public void createStoreNotForMe() throws Exception {
        String storeName = "test" + System.currentTimeMillis();
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
        Assertions.assertTrue(stores.stream().anyMatch(s -> s.getName().equals(storeName)), "Aangemaakte store met rechten voor andere gebruiker niet aangetroffen");
        stores.stream().filter(s -> s.getName().equals(storeName)).findFirst().get().delete();
    }

    @Test
    public void createStoreWithAddons() throws Exception {
        domain.createStore("test" + System.currentTimeMillis(), new GrantedRight[]{}, new String[] {"base"});
    }

}
