package nl.arba.ada.client.api.search;

/**
 * Class that represents a filter operator
 * @see Search
 */
public enum FilterOperator {
    /**
     * Filter operator to search for a value that equals the given value
     */
    EQUALS,
    /**
     * Filter operator to search for a null value
     */
    ISNULL;

    /**
     * Get the json representation of the operator
     * @return The json representation of the operator
     */
    public String toJson() {
        if (this.equals(ISNULL))
            return "isnull";
        else if (this.equals(EQUALS))
            return "equals";
        else
            return null;
    }
}
