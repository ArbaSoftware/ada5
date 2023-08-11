package nl.arba.ada.client.api.security;

public class Role implements Grantee {
    private String id;
    private String name;
    private IdentityProvider idp;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setIdentityProvider(IdentityProvider provider) {
        idp = provider;
    }

    @Override
    public IdentityProvider getIdentityProvider() {
        return idp;
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
