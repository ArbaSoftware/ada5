package nl.arba.ada.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.arba.ada.client.api.addon.AddOn;
import nl.arba.ada.client.api.exceptions.*;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.IdentityProvider;
import nl.arba.ada.client.api.security.Right;
import nl.arba.ada.client.api.security.User;
import nl.arba.ada.client.api.util.HttpUtils;
import nl.arba.ada.client.api.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a Ada domain that can hold multiple stores
 */
public class Domain {
    private String baseUrl;

    private String user;
    private String password;
    private ArrayList<Store> stores;
    private ArrayList<Right> rights;
    private ArrayList<IdentityProvider> identityProviders;
    private ObjectMapper mapper;
    private User currentUser;

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
        Right[] rightz = mapper.readValue(HttpUtils.doGet(baseUrl + "/security/right", user, password), Right[].class);
        rights = new ArrayList<>();
        rights.addAll(Arrays.asList(rightz));
        Store[] storez = mapper.readValue(HttpUtils.doGet(baseUrl + "/store", user, password), Store[].class);
        for (Store store: storez)
            store.setDomain(this);
        stores = new ArrayList<>();
        stores.addAll(Arrays.asList(storez));
        IdentityProvider[] providerz = mapper.readValue(HttpUtils.doGet(baseUrl + "/security/identityprovider", user, password), IdentityProvider[].class);
        identityProviders = new ArrayList<>();
        identityProviders.addAll(Arrays.asList(providerz));
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
     * @throws  StoreNotFoundException When the store is not found
     */
    public Store getStore(String id) throws StoreNotFoundException {
        try {
            InputStream is = HttpUtils.doGet(baseUrl + "/store/" + id, user, password);
            Store result = mapper.readValue(is, Store.class);
            result.setDomain(this);
            return result;
        }
        catch (IOException err) {
            throw new StoreNotFoundException();
        }
    }

    /**
     * Create a store
     * @param name The name of the new store
     * @return The added store
     * @throws StoreNotCreatedException If creation fails
     */
    public Store createStore(String name) throws StoreNotCreatedException {
        return createStore(name, new GrantedRight[]{});
    }

    /**
     * Create a store
     * @param name The name of the new store
     * @param grantedrights The rights that applies to the new store
     * @return The added store
     * @throws StoreNotCreatedException When creation fails
     */
    public Store createStore(String name, GrantedRight[] grantedrights) throws StoreNotCreatedException{
        return createStore(name, grantedrights, new String[0]);
    }

    /**
     * Create a store
     * @param name The name of the new store
     * @param grantedrights The rights that applies to the new store
     * @param addons The addons to apply
     * @return The added store
     * @throws StoreNotCreatedException When creation fails
     */
    public Store createStore(String name, GrantedRight[] grantedrights, String[] addons) throws StoreNotCreatedException {
        try {
            String json = "{\"name\":\"" + name + "\"";
            json += ",\"grantedrights\":[";
            if (grantedrights.length > 0) {
                for (GrantedRight right: grantedrights) {
                    json += (json.endsWith("[") ? "": ",");
                    if (right.getGrantee() instanceof User) {
                        User user = (User) right.getGrantee();
                        json += "{\"grantee\":\"" + (user.getId() == null ? user.getEmail(): user.getId()) + "\",\"identityprovider\":" + (right.getGrantee().getIdentityProvider() == null ? "null" : ("\"" + right.getGrantee().getIdentityProvider().getId() + "\"")) + ",\"level\":" + right.getLevel() + "}";
                    }
                    else
                        json += "{\"grantee\":\"" + right.getGrantee().getId() + "\",\"identityprovider\":" + (right.getGrantee().getIdentityProvider() == null ? "null" : ("\"" + right.getGrantee().getIdentityProvider().getId() + "\"")) + ",\"level\":" + right.getLevel() + "}";
                }
            }
            json += "],";
            json += "\"addons\":[";
            json += Arrays.asList(addons).stream().map(a -> "\"" + a + "\"").collect(Collectors.joining(","));
            json += "]";
            json += "}";
            InputStream is = HttpUtils.doPost(baseUrl + "/store", json, user, password);
            String storeId = StreamUtils.streamToString(is);
            return getStore(storeId);
        }
        catch (IOException err) {
            err.printStackTrace();
            throw new StoreNotCreatedException();
        }
        catch (StoreNotFoundException snfe) {
            throw new StoreNotCreatedException();
        }
    }

    /**
     * Delete a store from the domain
     * @param id The id of the store
     * @throws StoreNoteDeletedException When the deletion fails
     * @throws InsufficientRightsException When the user has no rights to delete the store
     */
    public void deleteStore(String id) throws StoreNoteDeletedException, InsufficientRightsException {
        try {
            boolean result = HttpUtils.doDelete(baseUrl + "/store/" + id, user,password);
            if (!result)
                throw new StoreNoteDeletedException();
        }
        catch (IOException io) {
            throw new StoreNoteDeletedException();
        }
    }

    /**
     * Add a class to the store
     * @param storeid The id of the store
     * @param newclass The class to add
     * @return The added class
     * @throws ClassNotCreatedException When the createion fails
     */
    public AdaClass addClass(String storeid, AdaClass newclass) throws ClassNotCreatedException {
        try {
            InputStream is = HttpUtils.doPost(baseUrl + "/store/" + storeid + "/class", newclass.toJson(), user, password);
            String classId = StreamUtils.streamToString(is);
            is = HttpUtils.doGet(baseUrl + "/store/" + storeid + "/class/" + classId, user, password);
            return mapper.readValue(is, AdaClass.class);
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new ClassNotCreatedException();
        }
    }

    /**
     * Add an addon to the domain
     * @param definition The definition of the addon
     * @return The added addon
     * @throws AddOnNotCreatedException When the creation of the addon failed
     */
    public AddOn addAddOn(AddOn definition) throws AddOnNotCreatedException{
        try {
            InputStream is = HttpUtils.doPost(baseUrl + "/addon", definition.toJson(), user, password);
            System.out.println(StreamUtils.streamToString(is));
            return definition;
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new AddOnNotCreatedException();
        }
    }

}
