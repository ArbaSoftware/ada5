package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.PropertyValue;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.PropertyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdaProperties extends Extensable implements Properties {
    private HashMap <String, PropertyData<?>> properties = new HashMap<>();

    private AdaProperties() {

    }

    public static AdaProperties create() {
        return new AdaProperties();
    }

    @Override
    public Map<String, PropertyData<?>> getProperties() {
        return properties;
    }

    @Override
    public List<PropertyData<?>> getPropertyList() {
        return properties.values().stream().collect(Collectors.toList());
    }
}
