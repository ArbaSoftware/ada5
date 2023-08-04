package nl.arba.ada.client.adaclient.utils;

import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.security.Right;

import java.util.List;
import java.util.stream.Collectors;

public class RightUtils {
    private Domain domain;

    private RightUtils(Domain domain) {
        this.domain = domain;
    }

    public static RightUtils create(Domain domain) {
        return new RightUtils(domain);
    }

    public String getRightNames(int level) {
        List <Right> therights = domain.getRights();
        return therights.stream().filter(r -> (r.getLevel() & level) == r.getLevel()).map(r -> r.getName()).collect(Collectors.joining(","));
    }
}
