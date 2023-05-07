package nl.arba.ada.client.api;

import nl.arba.ada.client.api.exceptions.InvalidPropertyTypeException;

/**
 * Enumeration for property types
 */

public enum PropertyType {
    /**
     * A string type
     */
    STRING,
    /**
     * A numeric type
     */
    INTEGER,
    /**
     *  A date type
     */
    DATE,
    /**
     * An object type
     */
    OBJECT;

    /**
     * Convert a string to a propertytype
     * @param value The string value
     * @return A property type
     * @throws InvalidPropertyTypeException When the input value is invalid
     */
    public static PropertyType fromString(String value) throws InvalidPropertyTypeException {
        if (value.equalsIgnoreCase("string"))
            return STRING;
        else if (value.equalsIgnoreCase("integer"))
            return INTEGER;
        else if (value.equalsIgnoreCase("date"))
            return DATE;
        else if (value.equalsIgnoreCase("object"))
            return OBJECT;
        else
            throw new InvalidPropertyTypeException(value);
    }

    /**
     * Convert property type to a string
     * @return The string value of the property
     */
    public String toString() {
        if (this.equals(STRING))
            return "string";
        else if (this.equals(INTEGER))
            return "integer";
        else if (this.equals(DATE))
            return "date";
        else if (this.equals(OBJECT))
            return "object";
        else
            return null;
    }
}
