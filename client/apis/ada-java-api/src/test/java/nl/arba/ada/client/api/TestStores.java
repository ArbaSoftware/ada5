package nl.arba.ada.client.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class TestStores {
    private static Domain domain;

    @BeforeAll
    public static void before() throws IOException {
        domain = Domain.create("http://192.168.2.74:9601/ada");
        domain.login("dev@arjanbas.nl", "hemertje");
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

}
