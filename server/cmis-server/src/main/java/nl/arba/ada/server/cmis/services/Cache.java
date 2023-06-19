package nl.arba.ada.server.cmis.services;

import nl.arba.ada.client.api.Domain;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {
    private ConcurrentHashMap <String, Domain> domains = new ConcurrentHashMap<>();
    private String apiUrl;
    private static Cache onlyCache;

    public Cache(String apiurl) {
        apiUrl = apiurl;
    }

    public boolean hasSession(String authorizationheader) {
        return domains.containsKey(authorizationheader);
    }

    public void addSession(String authorizationheader) throws IOException {
        String decoded = new String(Base64.getDecoder().decode(authorizationheader.substring("Basic ".length()).getBytes()));
        String user = decoded.substring(0, decoded.indexOf(':'));
        String password = decoded.substring(decoded.indexOf(":")+1);
        Domain domain = Domain.create(apiUrl);
        domain.login(user, password);
        domains.put(authorizationheader, domain );
    }

    public Domain getSession(String authorizationheader) {
        return domains.get(authorizationheader);
    }
}
