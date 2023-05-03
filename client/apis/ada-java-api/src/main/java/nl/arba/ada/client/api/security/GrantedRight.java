package nl.arba.ada.client.api.security;

/**
 * Class representing a granted right
 */
public class GrantedRight {
    private Grantee grantee;
    private int level;

    /**
     * Set the grantee to which the right is granted
     * @param grantee The grantee to which the right is granted
     * @see Grantee
     */
    public void setGrantee(Grantee grantee) {
        this.grantee = grantee;
    }

    /**
     * Set the grantee as string value (used for special grantees)
     * @param grantee The grantee
     */
    public void setGrantee(String grantee) {
        if (grantee.equalsIgnoreCase("everyone"))
            this.grantee = Everyone.create();
    }

    /**
     * Get the grantee to which the right is granted
     * @return The grantee to which the right is granted
     * @see Grantee
     */
    public Grantee getGrantee() {
        return grantee;
    }

    /**
     * Sets the level of the granted right. This is the combination of all rights that are applied
     * @param level The level of the granted right which is a combination of all rights that are applied
     * @see Right
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Get the level of the rights that are applied
     * @return The level of the rights that are applied
     * @see Right
     */
    public int getLevel() {
        return level;
    }

    /**
     * Helper method to create a granted right
     * @param grantee The grantee to which the rights are applied
     * @param level The combination of rights that are applied
     * @return An instance of a granted right
     * @see Right
     */
    public static GrantedRight create(Grantee grantee, int level) {
        GrantedRight right = new GrantedRight();
        right.setGrantee(grantee);
        right.setLevel(level);
        return right;
    }
}
