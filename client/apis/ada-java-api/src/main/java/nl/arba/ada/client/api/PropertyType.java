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

    /**
     * Get the json representation of a value ot this property type
     * @param value The value to get the representation for
     * @return The value of the given value based on this property type
     */
    public String toJson(Object value) {
        if (this.equals(STRING)) {
            if (value instanceof String)
                return "\"" + value + "\"";
        }
        else if (this.equals(INTEGER)) {
            if (value instanceof Integer)
                return ((Integer) value).toString();
        }
        else if (this.equals(OBJECT)) {
            if (value instanceof String)
                return "\"" + value + "\"";
        }
        return "null";
    }
}
