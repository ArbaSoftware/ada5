package nl.arba.ada.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.arba.ada.client.api.exceptions.StoreNotFoundException;
import nl.arba.ada.client.api.util.HttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class representing a Ada domain that can hold multiple stores
 */
public class Domain {
    private String baseUrl;

    private String user;
    private String password;
    private ArrayList<Store> stores;
    private ObjectMapper mapper;

    private Domain(String baseurl) {
        baseUrl = baseurl;
        mapper = new ObjectMapper();
    }

    /**
     * Creating a domain by specifying the url to the rest api
     * @param url Url to the rest api of the domain
     * @return An instance of an ada domain
     */
    public static Domain create(String url) {
        return new Domain(url);
    }

    /**
     * Login to the domain by providing a username and a password
     * @param user The user to login
     * @param password The password th login
     * @throws IOException If the connection to the rest api fails an io exception is throwed
     */
    public void login(String user, String password) throws IOException {
        this.user = user;
        this.password = password;
        Store[] storez = mapper.readValue(HttpUtils.doGet(baseUrl + "/store", user, password), Store[].class);
        stores = new ArrayList<>();
        stores.addAll(Arrays.asList(storez));
    }

    /**
     * Get the available stores in the domain
     * @return A collection of the available stores
     * @see Store
     */
    public List<Store> getStores() {
        return stores;
    }

    /**
     * Get a store
     * @param id The id of the store
     * @return The store with the provided id
     * @see Store
     * @throws StoreNotFoundException
     */
    public Store getStore(String id) throws StoreNotFoundException {
        try {
            InputStream is = HttpUtils.doGet(baseUrl + "/store/" + id, user, password);
            return mapper.readValue(is, Store.class);
        }
        catch (IOException err) {
            throw new StoreNotFoundException();
        }
    }

}
