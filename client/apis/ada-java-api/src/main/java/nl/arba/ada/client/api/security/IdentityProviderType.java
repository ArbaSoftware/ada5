package nl.arba.ada.client.api.security;

/**
 * Enumeration of the identityprovider types
 */
public enum IdentityProviderType {
    /**
     * Internal identityprovider
     */
    INTERNAL,
    OAUTH,
    /**
     * Invalid identity provider type
     */
    UNKNOWN;

    /**
     * Converts a string value to an identity provider type
     * @param value The string value
     * @return The type of UNKNOWN if the string value is invalid
     */
    public static IdentityProviderType fromString(String value) {
        if (value.equalsIgnoreCase("internal"))
            return INTERNAL;
        else if (value.equalsIgnoreCase("oauth"))
            return OAUTH;
        else
            return UNKNOWN;
    }
}
