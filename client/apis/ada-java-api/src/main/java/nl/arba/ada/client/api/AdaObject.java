package nl.arba.ada.client.api;

import nl.arba.ada.client.api.exceptions.*;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.security.IdentityProvider;
import nl.arba.ada.client.api.security.User;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents an object
 */
public class AdaObject {
    private String id;
    private String classId;
    private ArrayList<GrantedRight> rights = new ArrayList <>();
    private ArrayList<PropertyValue> properties = new ArrayList<>();
    private Store store;
    private int majorVersion;
    private int minorVersion;
    private String mimetype;
    private boolean checkedout;
    private String checkoutUserId;

    private User checkoutUser;

    private String checkoutIdentityProviderId;

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
     * @return The request as json
     * @throws IOException Throwed when a json problem occurs
     */
    public String createAddRequest() throws IOException {
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
                    Calendar calendarValue = Calendar.getInstance();
                    calendarValue.setTime((Date) prop.getValue());
                    json += prefix + "\"" + prop.getName() + "\":{\"day\":" + calendarValue.get(Calendar.DAY_OF_MONTH) + ",\"month\":" + (calendarValue.get(Calendar.MONTH)+1) + ",\"year\":" + calendarValue.get(Calendar.YEAR) + "}";
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
        if (this instanceof ContentHolding) {
            Content content = ((ContentHolding) this).getContent();
            json += (json.equals("{")? "" : ",") + "\"content\":" + content.toJson();
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

    private Optional<PropertyValue> findProperty(String name) {
        return properties.stream().filter(v -> v.getName().equals(name)).findFirst();
    }

    /**
     * Set the value of a string property
     * @param name The name of the property to set the value for
     * @param stringvalue The new value for the property
     */
    public void setStringProperty(String name, String stringvalue) {
        Optional<PropertyValue> existing = findProperty(name);
        if (existing.isPresent())
            existing.get().setValue(stringvalue);
        else {
            PropertyValue value = new PropertyValue();
            value.setName(name);
            value.setType(PropertyType.STRING);
            value.setValue(stringvalue);
            properties.add(value);
        }
    }

    /**
     * Get the value of a string property
     * @param name The name of the property to get the value for
     * @return The string value for the property
     * @throws InvalidPropertyTypeException Throwed when the property has not the string type
     * @throws PropertyNotFoundException Throwed when the property is not found in the collection
     */
    public String getStringProperty(String name) throws InvalidPropertyTypeException, PropertyNotFoundException {
        Optional<PropertyValue> existing = findProperty(name);
        if (existing.isPresent()) {
            PropertyValue value = existing.get();
            if (value.getType().equals(PropertyType.STRING)) {
                return (String) value.getValue();
            }
            else
                throw new InvalidPropertyTypeException("");
        }
        else
            throw new PropertyNotFoundException();
    }

    /**
     * Get the value of a date property
     * @param name The name of the property to get the value for
     * @return The date value for the property
     * @throws InvalidPropertyTypeException Throwed when the property has not the string type
     * @throws PropertyNotFoundException Throwed when the property is not found in the collection
     */
    public Date getDateProperty(String name) throws InvalidPropertyTypeException, PropertyNotFoundException {
        Optional<PropertyValue> existing = findProperty(name);
        if (existing.isPresent()) {
            PropertyValue value = existing.get();
            if (value.getType().equals(PropertyType.DATE)) {
                return (Date) value.getValue();
            }
            else
                throw new InvalidPropertyTypeException("");
        }
        else
            throw new PropertyNotFoundException();
    }

    /**
     * Sets the value for an object property
     * @param name The name of the property
     * @param objectid The value of the property (an objectid)
     */
    public void setObjectProperty(String name, String objectid) {
        Optional <PropertyValue> optValue = findProperty(name);
        if (optValue.isPresent())
            optValue.get().setValue(objectid);
        else {
            PropertyValue value = new PropertyValue();
            value.setName(name);
            value.setType(PropertyType.OBJECT);
            value.setValue(objectid);
            properties.add(value);
        }
    }

    public void setDateProperty(String name, Date datevalue) {
        Optional <PropertyValue> optValue = findProperty(name);
        if (optValue.isPresent())
            optValue.get().setValue(datevalue);
        else {
            PropertyValue value = new PropertyValue();
            value.setName(name);
            value.setType(PropertyType.DATE);
            value.setValue(datevalue);
            properties.add(value);
        }
    }

    public void setNullProperty(String name) {
        Optional optValue = findProperty(name);
        if (optValue.isPresent())
            properties.remove(optValue.get());
    }

    /**
     * Get the propertyvalues of the object
     * @return The propertyvalues of the object
     * @see PropertyValue
     */
    public List <PropertyValue> getProperties() {
        return properties;
    }

    /**
     * Set the store in which the object is stored
     * @param store The store in which the object is stored
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * Get the store in which the object is stored
     * @return The store in which the object is stored
     */
    public Store getStore() {
        return store;
    }

    public void setContent(Map source) {
        majorVersion = (Integer) source.get("majorversion");
        minorVersion = (Integer) source.get("minorversion");
        mimetype = (String) source.get("mimetype");
        checkedout = (Boolean) source.get("checkedout");
        if (checkedout) {
            checkoutIdentityProviderId = (String) source.get("checkedoutidentityproviderid");
            checkoutUserId = (String) source.get("checkedoutuser");
            if (getStore() != null) {
                retrieveUser();
            }
        }
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public String getMimetype() {
        return mimetype;
    }

    public boolean isCheckedOut() {
        return checkedout;
    }

    private User retrieveUser() {
        try {
            IdentityProvider idp = getStore().getDomain().getIdentityProvider(checkoutIdentityProviderId);
            checkoutUser = new User();
            checkoutUser.setIdentityProvider(idp);
            checkoutUser.setEmail(checkoutUserId);
        } catch (IdentityProviderNotFoundException idpnfe) {
        }
        return checkoutUser;
    }

    public User getCheckOutUser() {
        if (checkoutUser == null) {
            retrieveUser();
        }
        return checkoutUser;
    }

    public AdaObject update() throws AdaObjectNotUpdatedException, InsufficientRightsException, LostRightsException {
        return getStore().updateObject(this);
    }

    public String toJson() {
        String json = "{\"id\":\"" + getId() + "\", \"classid\":\"" + this.getClassId() + "\",";
        json += "\"properties\":[";
        json += properties.stream().map(p -> p.toJson()).collect(Collectors.joining(","));
        json += "], \"rights\":[";
        json += getRights().stream().map(r -> r.toJson()).collect(Collectors.joining(","));
        json += "]}";
        return json;
    }

}
