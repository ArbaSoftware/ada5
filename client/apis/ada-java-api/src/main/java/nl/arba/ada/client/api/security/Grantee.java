package nl.arba.ada.client.api.security;

/**
 * Interface that represents a grantee to which right are applied.
 */
public interface Grantee {
    /**
     * Get the id of the grantee
     * @return The id of the grantee
     */
    public String getId();

    /**
     * Get the identityprovider of the grantee
     * @return The identityprovider of the grantee
     * @see IdentityProvider
     */
    public IdentityProvider getIdentityProvider();

    /**
     * Get the display name of the grantee
     * @return The display name of the grantee
     */
    public String getDisplayName();
}
