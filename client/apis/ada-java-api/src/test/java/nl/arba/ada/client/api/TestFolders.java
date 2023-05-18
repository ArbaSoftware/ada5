package nl.arba.ada.client.api;

import nl.arba.ada.client.api.addon.AddOn;
import nl.arba.ada.client.api.security.Everyone;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.Right;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class TestFolders {
    private static Domain domain;
    private static Store testStore;
    private static String TEST_URL = "http://192.168.2.74:9601/ada";
    private final static String TEST_USER_1_EMAIL = "test@test.nl";
    private final static String TEST_USER_1_PASSWORD = "test";
    private final static String TEST_USER_2_EMAIL= "dev@arjanbas.nl";
    private final static String TEST_USER_2_PASSWORD = "hemertje";

    @BeforeAll
    public static void before() throws Exception {
        domain = Domain.create(TEST_URL);
        domain.login(TEST_USER_1_EMAIL, TEST_USER_1_PASSWORD);
        int allowAll = 0;
        for (Right right: domain.getRights()) {
            if (right.isStoreRight()) {
                allowAll += right.getLevel();
            }
        }
        domain.createStore("test" + System.currentTimeMillis(), new GrantedRight[] {GrantedRight.create(Everyone.create(), allowAll)}, new String[] { "base"});
        AddOn addon = AddOn.fromJson(TestFolders.class.getResourceAsStream("/addons/base.json"));
        domain.updateAddOn(addon);
    }

    @Test
    public void test() {

    }

}
