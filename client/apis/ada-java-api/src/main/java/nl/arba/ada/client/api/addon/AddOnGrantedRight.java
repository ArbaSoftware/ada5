package nl.arba.ada.client.api.addon;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Granted right that can be used in an addon
 */
public class AddOnGrantedRight {
    private String grantee;
    private String[] rights;

    /**
     * Set the grantee the rights are granted to
     * @param grantee The grantee to grant the rights
     */
    public void setGrantee(String grantee) {
        this.grantee = grantee;
    }

    /**
     * Get the grantee the rights are granted to
     * @return The grantee the rights are granted to
     */
    public String getGrantee() {
        return grantee;
    }

    /**
     * Set the rights to apply
     * @param rights The rights to apply
     */
    public void setRights(String[] rights) {
        this.rights = rights;
    }

    /**
     * Get the rights to apply
     * @return The rights to apply
     */
    public String[] getRights() {
        return rights;
    }

    /**
     * Get the json representation of the granted right
     * @return The json representation of the granted right
     */
    public String toJson() {
        return "{" +
                "\"grantee\":\"" + getGrantee() + "\"," +
                "\"rights\":[" + Arrays.asList(getRights()).stream().map(r -> ("\"" + r + "\"")).collect(Collectors.joining(",")) +
                "]" +
                "}";
    }
}
