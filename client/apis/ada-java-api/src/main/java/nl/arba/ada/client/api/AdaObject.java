package nl.arba.ada.client.api;

import nl.arba.ada.client.api.security.GrantedRight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * This class represents an object
 */
public class AdaObject {
    private String id;
    private String classId;
    private ArrayList<GrantedRight> rights = new ArrayList <>();
    private ArrayList<PropertyValue> properties = new ArrayList<>();

    /**
     * Set the id of the object
     * @param id The id of the object
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the id of the object
     * @return The id of the object
     */
    public String getId() {
        return id;
    }

    /**
     * Set the classid of the object
     * @param id The classid of the object
     */
    public void setClassid(String id) {
        classId = id;
    }

    /**
     * Get the classid of the object
     * @return The classid of the object
     */
    public String getClassId() {
        return classId;
    }

    /**
     * Create the request to add the object to a store
     * @return
     */
    public String createAddRequest() {
        String json = "{";
        if (!getProperties().isEmpty()) {
            json += "\"properties\": {";
            String prefix = "";
            for (PropertyValue prop : getProperties()) {
                if (prop.getType().equals(PropertyType.STRING)) {
                    json += prefix + "\"" + prop.getName() + "\":\"" + prop.getValue() + "\"";
                    prefix = ",";
                }
                else if (prop.getType().equals(PropertyType.DATE)) {
                    Date dateValue = (Date) prop.getValue();
                    json += prefix + "{\"day\":" + dateValue.getDate() + ",\"month\":" + (dateValue.getMonth()+1) + ",\"year\":" + dateValue.getYear() + "}";
                    prefix = ",";
                }
                else if (prop.getType().equals(PropertyType.OBJECT)) {
                    json += prefix + "\"" + prop.getName() + "\":\"" + prop.getValue() + "\"";
                    prefix = ",";
                }
            }
            json += "}";
        }
        if (!getRights().isEmpty()) {
            json += (json.equals("{")? "" : ",") + "\"rights\":[";
            String prefix = "";
            for (GrantedRight right: getRights()) {
                json += "{\"grantee\":\"" + right.getGrantee().getId() + "\",";
                if (right.getGrantee().getIdentityProvider() != null)
                    json += "\"identityprovider\":\"" + right.getGrantee().getIdentityProvider().getId() + "\",";
                json += "\"level\":" + right.getLevel();
                json += "}";
                prefix = ",";
            }
            json += "]";
        }
        json += "}";
        return json;
    }

    /**
     * Set the rights that applies to the object
     * @param rights The rights that applies to the object
     * @see GrantedRight
     */
    public void setRights(GrantedRight[] rights) {
        this.rights.clear();
        this.rights.addAll(Arrays.asList(rights));
    }

    /**
     * Get the rights that applies to the object
     * @return rights The rights that applies to the object
     * @see GrantedRight
     */
    public List<GrantedRight> getRights() {
        return rights;
    }

    /**
     * Set the property values for the object
     * @param properties The property values for the object
     * @see PropertyValue
     */
    public void setProperties(PropertyValue[] properties) {
        this.properties.clear();
        this.properties.addAll(Arrays.asList(properties));
    }

    public void setStringProperty(String name, String stringvalue) {
        PropertyValue value = new PropertyValue();
        value.setName(name);
        value.setType(PropertyType.STRING);
        value.setValue(stringvalue);
        properties.add(value);
    }

    public void setObjectProperty(String name, String objectid) {
        PropertyValue value = new PropertyValue();
        value.setName(name);
        value.setType(PropertyType.OBJECT);
        value.setValue(objectid);
        properties.add(value);
    }

    /**
     * Get the propertyvalues of the object
     * @return The propertyvalues of the object
     * @see PropertyValue
     */
    public List <PropertyValue> getProperties() {
        return properties;
    }
}
