package nl.arba.ada.client.adaclient.utils;

import nl.arba.ada.client.api.Domain;

import java.io.IOException;

public class AdaUtils {
    private static Domain domain;

    public static Domain getDomain() throws IOException {
        if (domain == null) {
            domain = Domain.create(System.getProperty("ada.url"));
            domain.login(System.getProperty("oauth.token"));
        }
        return domain;
    }
}
