package nl.arba.ada.client.api;

import nl.arba.ada.client.api.exceptions.ClassNotCreatedException;
import nl.arba.ada.client.api.exceptions.ClassNotDeletedException;

import java.util.ArrayList;

/**
 * Class that represents a store
 */
public class Store {
    private String id;
    private String name;
    private Domain domain;
    private ArrayList<String> addons;

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
            return getDomain().addClass(getId(), newclass);
        }
        catch (Exception err) {
            throw new ClassNotCreatedException();
        }
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
}
