package nl.arba.ada.client.api;

import nl.arba.ada.client.api.addon.base.Folder;
import nl.arba.ada.client.api.exceptions.*;
import nl.arba.ada.client.api.search.PropertyFilter;
import nl.arba.ada.client.api.search.Search;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that represents a store
 */
public class Store {
    private String id;
    private String name;
    private Domain domain;
    private ArrayList<String> addons;
    private ArrayList<AdaClass> classes = new ArrayList<>();

    /**
     * Set the id of the store
     * @param id The id of the store
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the id of the store
     * @return The id of the store
     */
    public String getId() {
        return id;
    }

    /**
     * Set the name of the store
     * @param name The name of the store
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the store
     * @return The name of the store
     */
    public String getName() {
        return name;
    }

    /**
     * Set the domain of the store
     * @param domain The domain
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Get the domain of the store
     * @return The domain of the store
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Delete a store
     * @throws ClassNotDeletedException An exception can be throwed
     */
    public void delete() throws ClassNotDeletedException {
        try {
            getDomain().deleteStore(getId());
        }
        catch (Exception err) {
            throw new ClassNotDeletedException();
        }
    }

    /**
     * Add a class to the store
     * @param newclass The class to add
     * @return The added class
     * @throws ClassNotCreatedException When the class could not be created
     */
    public AdaClass addClass(AdaClass newclass) throws ClassNotCreatedException{
        try {
            AdaClass result = getDomain().addClass(getId(), newclass);
            result.setStore(this);
            return result;
        }
        catch (Exception err) {
            throw new ClassNotCreatedException();
        }
    }

    /**
     * Get a class with the given name in this store
     * @param name The name of the class
     * @return The class
     * @throws AdaClassNotFoundException Throwed when the class is not found
     */
    public AdaClass getAdaClass(String name) throws AdaClassNotFoundException {
        return getDomain().getAdaClass(this, name);
    }

    /**
     * Add an addon that has to be applied when the store is created
     * @param id The id of the addon to add
     */
    public void addAddOn(String id) {
        if (addons == null)
            addons = new ArrayList<>();
        addons.add(id);
    }

    /**
     * Get the addons to apply when the store is created
     * @return The addons to apply when the store is created
     */
    public String[] getAddons() {
        return addons.toArray(new String[0]);
    }

    /**
     * Add an object to the given store
     * @param source The object to add to the store
     * @return The added object
     * @throws ObjectNotCreatedException When the creation fails
     * @see AdaObject
     */
    public AdaObject createObject(AdaObject source) throws ObjectNotCreatedException {
        AdaObject result = domain.addObject(this, source);
        result.setStore(this);
        return result;
    }

    /**
     * Get the object with the given id
     * @param id The id of the object to retrieve
     * @return The object with the given id
     * @throws ObjectNotFoundException Throwed when the object is not found
     */
    public AdaObject getObject(String id) throws ObjectNotFoundException {
        return domain.getObject(this, id);
    }

    public AdaClass[] getClasses() throws AdaClassNotFoundException{
        if (classes.isEmpty()) {
            classes.addAll(Arrays.asList(getDomain().getAdaClasses(this)));
        }
        return classes.toArray(new AdaClass[0]);
    }

    public Folder[] getRootFolders() throws NoSearchResultsException {
        try {
            AdaClass folderClass = getAdaClass("Folder");
            Property parentFolder = folderClass.getProperty("ParentFolder");
            Search search = Search.create(getAdaClass("Folder"));
            search.addFilter(PropertyFilter.createNullFilter("ParentFolder", parentFolder.getType()));
            AdaObject[] results = domain.search(this, search);
            Folder[] folderResults = new Folder[results.length];
            for (int index = 0; index < folderResults.length; index++)
                folderResults[index] = Folder.create(results[index]);
            return folderResults;
        }
        catch (Exception err) {
            throw new NoSearchResultsException();
        }
    }
}
