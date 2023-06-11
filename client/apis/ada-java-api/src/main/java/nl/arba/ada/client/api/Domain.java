package nl.arba.ada.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.arba.ada.client.api.addon.AddOn;
import nl.arba.ada.client.api.exceptions.*;
import nl.arba.ada.client.api.search.Search;
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
import java.util.Optional;
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
        InputStream is = null;
        try {
            is = HttpUtils.doPost(baseUrl + "/store/" + storeid + "/class", newclass.toJson(), user, password);
            String classId = StreamUtils.streamToString(is);
            is = HttpUtils.doGet(baseUrl + "/store/" + storeid + "/class/" + classId, user, password);
            return mapper.readValue(is, AdaClass.class);
        }
        catch (IOException io) {
            try {
                System.out.println(StreamUtils.streamToString(is));
            }
            catch (IOException io2) {}
            throw new ClassNotCreatedException();
        }
    }

    /**
     * Get the class in a store
     * @param store The store that holds the class
     * @param name The name of the class
     * @return The class with the given name in the given store
     * @throws AdaClassNotFoundException Throwed when the class is not found
     */
    public AdaClass getAdaClass(Store store, String name) throws AdaClassNotFoundException {
        try {
            InputStream is = HttpUtils.doGet(baseUrl + "/store/" + store.getId() + "/class/" + name, user, password);
            return mapper.readValue(is, AdaClass.class);
        }
        catch (IOException io) {
            throw new AdaClassNotFoundException();
        }
    }

    public AdaClass[] getAdaClasses(Store store) throws AdaClassNotFoundException {
        try {
            InputStream is = HttpUtils.doGet(baseUrl + "/store/" + store.getId() + "/class", user, password);
            return mapper.readValue(is, AdaClass[].class);
        }
        catch (IOException io) {
            throw new AdaClassNotFoundException();
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
            HttpUtils.doPost(baseUrl + "/addon", definition.toJson(), user, password);
            return definition;
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new AddOnNotCreatedException();
        }
    }

    /**
     * Updates an addon in the domain
     * @param definition The (new) definition of the addon
     * @throws AddOnNotUpdatedException Throwed when the update of the addon fails
     * @see AddOn
     */
    public void updateAddOn(AddOn definition) throws AddOnNotUpdatedException {
        try {
            InputStream is = HttpUtils.doPut(baseUrl + "/addon", definition.toJson(), user, password);
        }
        catch (IOException io) {
            io.printStackTrace();
            throw new AddOnNotUpdatedException();
        }
    }

    /**
     * Add an object to a store
     * @param store The store to add the object to
     * @param toadd The object to add
     * @return The added object
     * @throws ObjectNotCreatedException Throwed when the creation of the object fails
     * @see AdaObject
     * @see Store
     */
    public AdaObject addObject(Store store, AdaObject toadd) throws ObjectNotCreatedException {
        try {
            InputStream is = HttpUtils.doPost(baseUrl + "/store/" + store.getId() + "/class/" + toadd.getClassId() + "/object", toadd.createAddRequest(), user, password);
            String objectId = StreamUtils.streamToString(is);
            return store.getObject(objectId);
        }
        catch (Exception err) {
            err.printStackTrace();
            throw new ObjectNotCreatedException();
        }
    }

    /**
     * Get an object in the given store with the given id
     * @param store The store in which the object is stored
     * @param id The id of the object
     * @return The object
     * @throws ObjectNotFoundException Throwed when the object is not found
     */
    public AdaObject getObject(Store store, String id) throws ObjectNotFoundException {
        String jsonresponse = "";
        try {
            InputStream is = HttpUtils.doGet(baseUrl + "/store/" + store.getId() + "/object/" + id, user, password);
            jsonresponse = StreamUtils.streamToString(is);
            AdaObject result = mapper.readValue(jsonresponse, AdaObject.class);
            result.setStore(store);
            return result;
        }
        catch (IOException io) {
            System.out.println(jsonresponse);
            io.printStackTrace();
            throw new ObjectNotFoundException();
        }
    }

    /**
     * Get the rights that are defined in the domain
     * @return An array of all the defined rights
     */
    public List <Right> getRights() {
        return rights;
    }

    /**
     * Executes a search
     * @param store The store to execute the search in
     * @param search The search to execute
     * @return The results as an array of objects
     * @throws NoSearchResultsException Throwed when the execution of the search fails
     * @see Search
     * @see Store
     * @see NoSearchResultsException
     */
    public AdaObject[] search(Store store, Search search) throws NoSearchResultsException {
        String jsonresponse = "";
        try {
            InputStream is = HttpUtils.doPost(baseUrl + "/store/" + store.getId() + "/search", search.toJson(), user, password);
            jsonresponse = StreamUtils.streamToString(is);
            AdaObject[] results = mapper.readValue(jsonresponse, AdaObject[].class);
            for (AdaObject result: results)
                result.setStore(store);
            return results;
        }
        catch (IOException io) {
            System.out.println(jsonresponse);
            throw new NoSearchResultsException();
        }
    }

    public InputStream getContent(Store store, String docid) {
        try {
            return HttpUtils.doGet(baseUrl + "/store/" + store.getId() + "/object/" + docid + "/content/current", user, password);
        }
        catch (IOException io) {

        }
        return null;
    }

    public boolean checkout(Store store, String docid) {
        try {
            HttpUtils.doGet(baseUrl + "/store/" + store.getId() + "/object/" + docid + "/checkout", user, password);
            return true;
        }
        catch (IOException io) {
            return false;
        }
    }

    public IdentityProvider getIdentityProvider(String id) throws IdentityProviderNotFoundException {
        Optional<IdentityProvider> optProvider = identityProviders.stream().filter(p -> p.getId().equals(id)).findFirst();
        if (optProvider.isPresent())
            return optProvider.get();
        else
            throw new IdentityProviderNotFoundException();
    }

    public boolean checkin(String storeid, String objectid, Content content) {
        try {
            HttpUtils.doPost(baseUrl + "/store/" + storeid + "/object/" + objectid + "/checkin", content.toJson(), user, password);
            return true;
        }
        catch (IOException io) {
            return false;
        }
    }
}
