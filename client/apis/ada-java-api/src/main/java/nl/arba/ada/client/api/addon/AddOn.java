package nl.arba.ada.client.api.addon;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.security.Right;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Class that represents an addon. A set of classes and rights that will be implemented in a store or domain.
 */
public class AddOn {
    private String id;
    private String name;
    private Right[] rights;
    private AddOnClass[] classes;

    /**
     * Set the id of the addon
     * @param id The id of the addon
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the id of the addon
     * @return The id of the addon
     */
    public String getId() {
        return id;
    }

    /**
     * Set the name of the addon
     * @param name The name of the addon
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the addon
     * @return The name of the addon
     */
    public String getName() {
        return name;
    }

    /**
     * Set the rights that will be implemented by the addon
     * @param rights The rights that will be implemented by the addon
     */
    public void setRights(Right[] rights) {
        this.rights = rights;
    }

    /**
     * Get the rights that will be implemented by the addon
     * @return The rights that will be implemented by the addon
     */
    public Right[] getRights() {
        return rights;
    }

    /**
     * Set the class that will be implemented by the addon
     * @param classes The classes that will be implemented by the addon
     */
    public void setClasses(AddOnClass[] classes) {
        this.classes = classes;
    }

    /**
     * Get the classes that will be implemented by the addon
     * @return The classes that will be implemented by the addon
     */
    public AddOnClass[] getClasses() {
        return classes;
    }

    /**
     * Read the definition of the addon from a json representation
     * @param input The json source
     * @return The Addon definition
     * @throws IOException Can be throwed
     */
    public static AddOn fromJson(InputStream input) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(input, AddOn.class);
        }
        finally {
            input.close();
        }
    }

    /**
     * Get the json representation of the addon
     * @return The json representation of the addon
     */
    public String toJson() {
        return "{" +
            "\"id\":\"" + getId() + "\"," +
            "\"name\":\"" + getName() + "\"," +
            "\"classes\":[" +
            Arrays.stream(getClasses()).map(c -> c.toJson()).collect(Collectors.joining(",")) +
            "]" +
        "}";
    }

}
