package nl.arba.ada.client.api.search;

import nl.arba.ada.client.api.PropertyType;

/**
 * Class that represents a property filter that can be added to a search
 * @see Search
 */
public class PropertyFilter {
    private String propertyName;
    private FilterOperator operator;
    private Object filterValue;
    private PropertyType propertyType;

    private PropertyFilter(String property, PropertyType type, FilterOperator operator) {
        this(property, type, operator, null);
    }

    private PropertyFilter(String property, PropertyType type, FilterOperator operator, Object filtervalue) {
        this.propertyName = property;
        this.operator = operator;
        this.filterValue = filtervalue;
        this.propertyType = type;
    }

    /**
     * Create a filter to search for null values
     * @param property The property to search for
     * @param type The type of the property
     * @return A filter that search for null values
     * @see PropertyType
     */
    public static PropertyFilter createNullFilter(String property, PropertyType type) {
        return new PropertyFilter(property, type, FilterOperator.ISNULL);
    }

    /**
     * Create a filter to search for a value that is equals to the property value
     * @param property The property
     * @param type The type of the property
     * @param filtervalue The filter value
     * @return A filter that search property values that equals the given value
     * @see PropertyType
     */
    public static PropertyFilter createEqualFilter(String property, PropertyType type, Object filtervalue) {
        return new PropertyFilter(property, type, FilterOperator.EQUALS, filtervalue);
    }

    /**
     * Get the json representation of the property filter
     * @return The json representation of the property filter
     */
    public String toJson() {
        String json = "{\"property\":\"" + propertyName + "\",\"operator\":\"" + operator.toJson() + "\"";
        if (filterValue == null)
            json += "}";
        else {
            json += ",\"value\": " + propertyType.toJson(filterValue) + "}";
        }
        return json;
    }
}
