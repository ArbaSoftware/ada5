package nl.arba.ada.client.api.security;

/**
 * Class representing an identityprovider. Multiple identityprovider can be configured for a d domain. Each identityprovider can hold multiple users.
 * @see User
 */
public class IdentityProvider {
    private String id;
    private String name;
    private IdentityProviderType type;

    /**
     * Set the unique id of the identityprovider
     * @param id The unique id of the identityprovider
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the unique id of the identityprovider
     * @return The unique id of the identityprovider
     */
    public String getId() {
        return id;
    }

    /**
     * Set the name of the identityprovider
     * @param name The name of the identityprovider
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the identityprovider
     * @return The name of the identityprovider
     */
    public String getName() {
        return name;
    }

    /**
     * Set the type of the identityprovider
     * @param type The type of the identityprovider
     * @see IdentityProviderType
     */
    public void setType(String type) {
        this.type = IdentityProviderType.fromString(type);
    }

    /**
     * Get the type of the identityprovider
     * @return The type of the identityprovider
     * @see IdentityProviderType
     */
    public IdentityProviderType getType() {
        return type;
    }
}
