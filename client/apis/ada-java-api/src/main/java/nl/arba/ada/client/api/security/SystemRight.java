package nl.arba.ada.client.api.security;

/**
 * Enumeration of all system rights that are used
 */
public enum SystemRight {
    /**
     * Right to read something
     */
    READ,
    /**
     * Right to create something
     */
    CREATE,
    /**
     * Right to update something
     */
    UPDATE,
    /**
     * Right to delete something
     */
    DELETE,
    /**
     * Right to create a store
     */
    CREATESTORE,
    /**
     * Right to create a class
     */
    CREATECLASS,
    /**
     * No system right
     */
    NONE;

    /**
     * Helper method to convert a string value to a system right
     * @param value The string value
     * @return A system right or NONE if the value is invalid
     * @see NONE
     */
    public static SystemRight fromString(String value) {
        if (value.equalsIgnoreCase("read"))
            return READ;
        else if (value.equalsIgnoreCase("create"))
            return CREATE;
        else if (value.equalsIgnoreCase("UPDATE"))
            return UPDATE;
        else if (value.equalsIgnoreCase("delete"))
            return DELETE;
        else if (value.equalsIgnoreCase("createstore"))
            return CREATESTORE;
        else if (value.equalsIgnoreCase("createclass"))
            return CREATECLASS;
        else
            return NONE;
    }
}
