package nl.arba.ada.client.api.security;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class that represents a right. Multiple right can be configured including custom rights
 */
public class Right {
    private String id;
    private String name;
    private SystemRight systemRight;
    private int level;
    private boolean isDomainRight;
    private boolean isStoreRight;
    private boolean isClassRight;
    private boolean isObjectRight;

    /**
     * Set the unique id of the right
     * @param id The unique id of the right
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the unique id of the right
     * @return The unique id of the right
     */
    public String getId() {
        return id;
    }

    /**
     * Set the name of the right
     * @param name The name of the right
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the right
     * @return The name of the right
     */
    public String getName() {
        return name;
    }

    /**
     * Set the systemright that is implemented by this right
     * @param value The systemright
     * @see SystemRight
     */
    @JsonProperty("systemright")
    public void setSystemRight(String value) {
        this.systemRight = SystemRight.fromString(value);
    }

    /**
     * Get the systemright that is implemented by this right
     * @return The systemright that is implemented by this right
     * @see SystemRight
     */
    public SystemRight getSystemRight() {
        return systemRight;
    }

    /**
     * Set the unique level of this right. Multiple rights are combined to one combined (also unique) level
     * @param level The unique level of this right
     * @see GrantedRight
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Get the unique level of this right.
     * @return The unique level of this right
     */
    public int getLevel() {
        return level;
    }

    /**
     * Set if this right applies to domains
     * @param value <code>true</code> - it is a domain right, <code>false</code> it is not a right that applies to domains
     */
    @JsonProperty("domainright")
    public void setDomainRight(boolean value) {
        this.isDomainRight = value;
    }

    /**
     * Get if this right applies to domains
     * @return <code>true</code> - it is a domain right, <code>false</code> it is not a right that applies to domains
     */
    public boolean isDomainRight() {
        return isDomainRight;
    }

    /**
     * Set if this right applies to stores
     * @param value <code>true</code> - it is a store right, <code>false</code> it is not a right that applies to stores
     */
    @JsonProperty("storeright")
    public void setStoreRight(boolean value) {
        this.isStoreRight = value;
    }

    /**
     * Get if this right applies to stores
     * @return <code>true</code> - it is a store right, <code>false</code> it is not a right that applies to stores
     */
    public boolean isStoreRight() {
        return isStoreRight;
    }

    /**
     * Set if this right applies to classes
     * @param value <code>true</code> - it is a class right, <code>false</code> it is not a right that applies to classes
     */
    @JsonProperty("classright")
    public void setClassRight(boolean value) {
        this.isClassRight = value;
    }

    /**
     * Get if this right applies to classes
     * @return <code>true</code> - it is a class right, <code>false</code> it is not a right that applies to classes
     */
    public boolean isClassRight() {
        return isClassRight;
    }

    /**
     * Set if this right applies to objects
     * @param value <code>true</code> - it is an object right, <code>false</code> it is not a right that applies to objects
     */
    @JsonProperty("objectright")
    public void setObjectRight(boolean value) {
        this.isObjectRight = value;
    }

    /**
     * Get if this right applies to objects
     * @return <code>true</code> - it is an object right, <code>false</code> it is not a right that applies to objects
     */
    public boolean isObjectRight() {
        return isObjectRight;
    }
}
