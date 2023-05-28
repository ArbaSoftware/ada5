package nl.arba.ada.client.api.search;

import nl.arba.ada.client.api.AdaClass;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Class that represents a search that holds various property filters
 */
public class Search {
    private ArrayList<PropertyFilter> filters;
    private AdaClass searchClass;

    private Search(AdaClass searchclass) {
        this.searchClass = searchclass;
        filters = new ArrayList<>();
    }

    /**
     * Create an instance of a search
     * @param searchclass The class to search for
     * @return The instance of the search based on the given searchclass
     */
    public static Search create(AdaClass searchclass) {
        return new Search(searchclass);
    }

    /**
     * Add a filter to the search
     * @param filter The filter to add
     * @return This search
     * @see PropertyFilter
     */
    public Search addFilter(PropertyFilter filter) {
        filters.add(filter);
        return this;
    }

    /**
     * Get the json representation of the search
     * @return The json representation of the search
     */
    public String toJson() {
        return "{\"class\":\"" + searchClass.getName() + "\",\"filters\":[" +
                filters.stream().map(f -> f.toJson()).collect(Collectors.joining(",")) +
                "]}";
    }


}
