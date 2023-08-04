package nl.arba.ada.client.api.security;

/**
 * Class representing a user.
 * @see Grantee
 */
public class User implements Grantee {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private IdentityProvider identityProvider;

    /**
     * Set the unique id of the user
     * @param id The unique id of the user
     */
    public void setId(String id) {
        this.id= id;
    }

    /**
     * Get the unique id of the user
     * @return The unique id of the user
     */
    public String getId() {
        return id;
    }

    /**
     * Set the mail address of the user
     * @param email The mail address of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the mail address of the user
     * @return The mail address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the first name of the user
     * @param name The first name of the user
     */
    public void setFirstName(String name) {
        firstName = name;
    }
    public void setFirstname(String name) {
        setFirstName(name);
    }

    /**
     * Get the first name of the user
     * @return The first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the last name of the user
     * @param name The last name of the user
     */
    public void setLastName(String name) {
        this.lastName = name;
    }
    public void setLastname(String name) {
        setLastName(name);
    }

    /**
     * Get the last name of the user
     * @return The last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the identityprovider which holds the user
     * @param provider The identityprovider that holds the user
     * @see IdentityProvider
     */
    public void setIdentityProvider(IdentityProvider provider) {
        identityProvider = provider;
    }

    /**
     * Get the identityprovider that holds the user
     * @return The identityprovider that holds the user
     * @see IdentityProvider
     */
    public IdentityProvider getIdentityProvider() {
        return identityProvider;
    }

    /**
     * Get the displayname of the user
     * @return The displayname of the user
     */
    @Override
    public String getDisplayName() {
        return getFirstName() + " " + getLastName();
    }
}
