package nl.arba.ada.server.cmis.services;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.Store;
import nl.arba.ada.client.api.exceptions.StoreNotFoundException;
import nl.arba.ada.server.cmis.model.CMISObject;
import nl.arba.ada.server.cmis.model.Repository;
import nl.arba.ada.server.cmis.model.RootFolder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class CMISService extends HttpServlet {
    private String adaApiUrl;
    private Cache cache;

    public CMISService(String apiurl, Cache cache) {
        adaApiUrl = apiurl;
        this.cache = cache;
    }

    public Cache getCache() {
        return cache;
    }

    public boolean verifyAuthorization(String authorizationheader) {
        return true;
    }

    public void notAuthorized(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setIntHeader("Content-Length", 0);
        OutputStream os = response.getOutputStream();
        os.flush();
        os.close();
    }

    public void notFound(HttpServletResponse response) throws IOException {
        response.setStatus(404);
        response.setIntHeader("Content-Length", 0);
        OutputStream os = response.getOutputStream();
        os.flush();
        os.close();
    }

    private Domain getDomain(String authorization) throws IOException {
        if (!getCache().hasSession(authorization))
            getCache().addSession(authorization);
        return getCache().getSession(authorization);
    }

    public Repository[] getRepositories(HttpServletRequest request) throws IOException {
        Domain domain = getDomain(request.getHeader("Authorization"));
        List <Store> stores = domain.getStores();
        Repository[] results = new Repository[stores.size()];
        for (int index = 0; index < results.length; index++)
            results[index] = Repository.fromStore(stores.get(index));
        return results;
    }

    public Repository getRepository(String id, HttpServletRequest request) throws IOException, StoreNotFoundException {
        Domain domain = getDomain(request.getHeader("Authorizization"));
        return Repository.fromStore(domain.getStore(id));
    }

    public CMISObject getRootFolder(String storeid, HttpServletRequest request) throws IOException, StoreNotFoundException {
        Domain domain = getDomain(request.getHeader("Authorization"));
        Store store = domain.getStore(storeid);
        return RootFolder.create(store);
    }
}
