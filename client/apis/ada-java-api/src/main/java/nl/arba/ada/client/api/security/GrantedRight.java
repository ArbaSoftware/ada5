package nl.arba.ada.client.api.security;

import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.exceptions.IdentityProviderNotFoundException;

import java.util.Map;

/**
 * Class representing a granted right
 */
public class GrantedRight {
    private Grantee grantee;
    private int level;
    private String granteeId = null;
    private String identityProviderId = null;
    private String granteeType;
    private User user;

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

    public void setGranteeid(String id) {
        if (id.equals("everyone"))
            grantee = Everyone.create();
        else {
            granteeId = id;
        }
    }

    public String getGranteeId() {
        return granteeId;
    }

    public void setIdentityproviderid(String id) {
        if (id != null) {
            identityProviderId = id;
        }
    }

    public String getIdentityProviderId() {
        return identityProviderId;
    }

    private void createGrantee(Domain domain) {
        grantee = new Grantee() {
            @Override
            public String getId() {
                return granteeId;
            }

            @Override
            public IdentityProvider getIdentityProvider() {
                return null;
            }

            @Override
            public String getDisplayName() {
                return null;
            }
        };
    }

    public void setGranteetype(String type) {
        granteeType = type;
    }

    public String getGranteetype() {
        return granteeType;
    }

    public void setUser(Map user) {
        this.user = new User();
        this.user.setEmail((String) user.get("email"));
        this.user.setId((String) user.get("id"));
        this.user.setFirstName((String) user.get("firstname"));;
        this.user.setLastName((String) user.get("lastname"));
    }

    public User getUser() {
        return user;
    }
}
