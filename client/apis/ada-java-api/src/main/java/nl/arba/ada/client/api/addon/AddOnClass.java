package nl.arba.ada.client.api.addon;

import nl.arba.ada.client.api.AdaClass;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Class definition to be implemented by the addon
 */
public class AddOnClass extends AdaClass {
    private AddOnGrantedRight[] rights;

    /**
     * Set the security that applies to the class
     * @param rights The security to apply to the class
     */
    public void setSecurity(AddOnGrantedRight[] rights) {
        this.rights = rights;
    }

    /**
     * Get the security that applies to the class
     * @return The security that had to be applied to the class
     */
    public AddOnGrantedRight[] getSecurity() {
        return rights;
    }

    public String toJson() {
        return "{" +
                "\"name\":\"" + getName() + "\"," +
                "\"description\":\"" + getDescription() + "\"," +
                "\"folderclass\":" + isFolderClass() + "," +
                "\"documentclass\":" + isDocumentClass() + "," +
                "\"properties\":[" +
                Arrays.asList(getProperties()).stream().map(p -> p.toJson()).collect(Collectors.joining(",")) +
                "]," +
                "\"security\":[" +
                Arrays.asList(getSecurity()).stream().map(r -> r.toJson()).collect(Collectors.joining(",")) +
                "]" +
                "}";
    }
}
