package nl.arba.ada.client.api;

/**
 * Class that represents a store
 */
public class Store {
    private String id;
    private String name;

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
}
