package nl.arba.ada.client.api;

import nl.arba.ada.client.api.addon.AddOn;
import nl.arba.ada.client.api.exceptions.AddOnNotCreatedException;
import nl.arba.ada.client.api.exceptions.ApiException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class TestAddOn {
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
    public void doTest() throws IOException, AddOnNotCreatedException {
        AddOn addon = AddOn.fromJson(getClass().getResourceAsStream("/addons/base.json"));
        domain.addAddOn(addon);
    }

    @Test
    public void test_get_addons() throws ApiException {
        domain.getAddOns();
    }
}
