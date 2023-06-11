package nl.arba.ada.client.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.arba.ada.client.api.exceptions.AdaClassNotFoundException;
import nl.arba.ada.client.api.security.GrantedRight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Class representing an objectclass in a store
 */
public class AdaClass {
    private String id;
    private String name;
    private boolean folderClass;
    private boolean documentClass;
    private ArrayList <GrantedRight> rights = new ArrayList<>();
    private ArrayList <Property> properties = new ArrayList<>();
    private String description;

    private AdaClass parentClass;
    private String parentClassId;
    private Store store;

    /**
     * Set the unique id of the class
     * @param id The unique id of the class
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the unique id of the class
     * @return The unique id of the class
     */
    public String getId() {
        return id;
    }

    /**
     * Set the name of the class
     * @param value The name of the class
     */
    public void setName(String value) {
        name = value;
    }

    /**
     * Get the name of the class
     * @return The name of the class
     */
    public String getName() {
        return name;
    }

    /**
     * Set if the class is a folderclass
     * @param value <code>true</code> - class is a folderclass, <code>false</code> - the class is not a folderclass
     */
    @JsonProperty("folderclass")
    public void setFolderClass(boolean value) {
        folderClass = value;
    }

    /**
     * Get if the class is a folderclass
     * @return <code>true</code> - class is a folderclass, <code>false</code> - the class is not a folderclass
     */
    public boolean isFolderClass() {
        return folderClass;
    }

    /**
     * Set if the class is a documentclass
     * @param value <code>true</code> - class is a documentclass, <code>false</code> - the class is not a documentclass
     */
    @JsonProperty("documentclass")
    public void setDocumentClass(boolean value) {
        documentClass = value;
    }

    /**
     * Get if the class is a documentclass
     * @return <code>true</code> - class is a documentclass, <code>false</code> - the class is not a documentclass
     */
    public boolean isDocumentClass() {
        return documentClass;
    }

    /**
     * Add a granted right to the class
     * @param right The granted right
     * @see GrantedRight
     */
    public void addRight(GrantedRight right) {
        rights.add(right);
    }

    /**
     * Set the properties of the class
     * @param properties The properties
     * @see Property
     */
    public void setProperties(Property[] properties) {
        this.properties.clear();
        this.properties.addAll(Arrays.asList(properties));
    }

    /**
     * Add a property to the class
     * @param property The property to add
     * @see Property
     */
    public void addProperty(Property property) { properties.add(property);}

    /**
     * Get the properties of the class
     * @return The properties of the class
     */
    public Property[] getProperties() {
        return properties.toArray(new Property[0]);
    }

    /**
     * Convert to property to a json representation
     * @return A json representation of the property
     */
    public String toJson() {
        return "{\"name\" : \"" + getName() + "\", " +
            "\"documentclass\": " + documentClass + "," +
            "\"folderclass\": " + folderClass + "," +
            (getParentClass() == null ? "": ("\"parentclass\": \"" + getParentClass().getId() + "\",")) +
            "\"rights\":[" +
                rights.stream().map(r -> rightToJson(r)).collect(Collectors.joining(",")) +
            "]," +
            "\"properties\":[" +
                properties.stream().map(p ->p.toJson()).collect(Collectors.joining(",")) +
            "]" +
            "}";
    }

    private String rightToJson(GrantedRight right) {
        return "{" +
                "\"grantee\":\"" + right.getGrantee().getId() + "\"," +
                (right.getGrantee().getIdentityProvider() == null ? "" : "\"identityprovider\":\"" + right.getGrantee().getIdentityProvider().getId() + "\",") +
                "\"level\":" + right.getLevel() +
                "}";
    }

    /**
     * Set the description of the class
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the description of the class
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the rights that applies to the class
     * @param rights The rights that applies to the class
     */
    public void setGrantedRights(GrantedRight[] rights) {
        this.rights.clear();
        this.rights.addAll(Arrays.asList(rights));
    }

    public void setParentClass(AdaClass parentclass) {
        this.parentClass = parentclass;
    }

    public void setParentclass(String classid) {
        parentClassId = classid;
    }

    public AdaClass getParentClass() {
        if (parentClass == null && parentClassId != null && getStore() != null) {
            try {
                parentClass = getStore().getAdaClass(parentClassId);
            }
            catch (AdaClassNotFoundException cnfe) {}
        }
        return parentClass;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
