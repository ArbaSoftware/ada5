package nl.arba.ada.client.api.exceptions;

/**
 * Exception that is thrown if a string value could not be converted to a property type
 */
public class InvalidPropertyTypeException extends Exception{

    /**
     * Constructor to create the exception
     * @param value The invalid string value that causes the exception
     */
    public InvalidPropertyTypeException(String value) {
        super("Invalid property type value (" + value + ")");
    }
}
