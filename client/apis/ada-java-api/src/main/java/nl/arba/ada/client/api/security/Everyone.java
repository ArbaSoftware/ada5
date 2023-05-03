package nl.arba.ada.client.api.security;

/**
 * Helper class for grantee Everyone
 * @see Grantee
 */
public class Everyone implements Grantee {
    /**
     * Get the id of the grantee
     * @return The id of the grantee
     */
    @Override
    public String getId() {
        return "everyone";
    }

    /**
     * Get the identityprovider for the grantee
     * @return Always null because Everyone applies to all identityproviders
     */
    @Override
    public IdentityProvider getIdentityProvider() {
        return null;
    }

    /**
     * Get the displayname of the grantee
     * @return To show the grantee EVERYONE is supplied
     */
    @Override
    public String getDisplayName() {
        return "EVERYONE";
    }

    /**
     * Helper method te create simple an instance of the Everyone grantee
     * @return An instance of the everyone grantee
     */
    public static Everyone create() {
        return new Everyone();
    }
}
