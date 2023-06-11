package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.PropertyValue;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.PropertyData;

import java.util.ArrayList;
import java.util.List;

public class AdaPropertyData extends Extensable implements PropertyData {
    private PropertyValue source;
    private List values;

    private AdaPropertyData(PropertyValue source) {
        this.source = source;
        values = new ArrayList<Object>();
        values.add(source.getValue());
    }

    public static AdaPropertyData create(PropertyValue source) {
        return new AdaPropertyData(source);
    }

    @Override
    public String getId() {
        return source.getId();
    }

    @Override
    public String getLocalName() {
        return source.getName();
    }

    @Override
    public String getDisplayName() {
        return source.getName();
    }

    @Override
    public String getQueryName() {
        return source.getName();
    }

    @Override
    public List getValues() {
        return values;
    }

    @Override
    public Object getFirstValue() {
        if (values.isEmpty())
            return null;
        else
            return values.get(0);
    }

}
