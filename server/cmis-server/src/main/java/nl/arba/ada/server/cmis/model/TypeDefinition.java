package nl.arba.ada.server.cmis.model;

import java.util.ArrayList;
import java.util.List;

public class TypeDefinition {
    private String id;
    private String localName;
    private String localNamespace;
    private String displayName;
    private String queryName;
    private String baseId;
    private boolean createable = false;
    private boolean fileable = false;
    private boolean queryable = false;
    private boolean fulltextIndexed = false;
    private boolean includedInSupertypeQuery = true;
    private boolean controllablePolicy = false;
    private boolean controllableAcl = false;
    private ArrayList <Property> propertDefinitions = new ArrayList<>();

    private TypeDefinition() {

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setLocalName(String localname) {
        localName = localname;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalNamespace(String namespace) {
        localNamespace = namespace;
    }

    public String getLocalNamespace() {
        return localNamespace;
    }

    public void setDisplayName(String displayname) {
        displayName = displayname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setQueryName(String name) {
        queryName = name;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setBaseId(String id) {
        baseId = id;
    }

    public String getBaseId() {
        return baseId;
    }

    public void setCreateable(boolean value) {
        this.createable = value;
    }

    public boolean isCreatable() {
        return createable;
    }
    public boolean isFileable() {
        return fileable;
    }
    public void setFileable(boolean value) {
        this.fileable = value;
    }

    public boolean isQueryable() {
        return queryable;
    }

    public void setQueryable(boolean value) {
        queryable = value;
    }
    public boolean isFulltextIndexed() {
        return fulltextIndexed;
    }

    public void setFulltextIndexed(boolean value) {
        fulltextIndexed = value;
    }
    public boolean isIncludedInSupertypeQuery() {
        return includedInSupertypeQuery;
    }

    public void setIncludedInSupertypeQuery(boolean value) {
        includedInSupertypeQuery = value;
    }
    public boolean hasControllablePolicy() {
        return controllablePolicy;
    }

    public void setControllablePolicy(boolean value) {
        controllablePolicy = value;
    }
    public boolean hasControllableAcl() {
        return controllableAcl;
    }

    public void setControllableAcl(boolean value) {
        controllableAcl = value;
    }

    public void addPropertyDefinition(Property definition) {
        propertDefinitions.add(definition);
    }

    public List<Property> getPropertyDefinitions() {
        return propertDefinitions;
    }

    public static TypeDefinition getCmisDocument() {
        TypeDefinition result = new TypeDefinition();
        result.setId("cmis:document");
        result.setBaseId("cmis:document");
        result.setDisplayName("Document");
        result.setLocalName("document");
        result.setQueryName("cmis:document");
        result.setLocalNamespace("http://arjanbas.nl/ada/cmis/1.1");
        result.setCreateable(true);
        result.setFileable(true);
        result.setQueryable(true);
        result.setFulltextIndexed(false);
        result.setIncludedInSupertypeQuery(true);
        result.setControllableAcl(true);
        result.setControllablePolicy(false);
        result.addPropertyDefinition(Property.createObjectId());
        result.addPropertyDefinition(Property.createdBy());
        result.addPropertyDefinition(Property.createPath());
        result.addPropertyDefinition(Property.createCreationDate());
        result.addPropertyDefinition(Property.createLastModificationDate());
        result.addPropertyDefinition(Property.createLastModifiedBy());
        result.addPropertyDefinition(Property.createBaseTypeId());
        result.addPropertyDefinition(Property.createName());
        result.addPropertyDefinition(Property.createObjectType());
        return result;
    }

    public static TypeDefinition getCmisFolder() {
        TypeDefinition result = new TypeDefinition();
        result.setId("cmis:folder");
        result.setBaseId("cmis:folder");
        result.setDisplayName("Folder");
        result.setLocalName("folder");
        result.setQueryName("cmis:folder");
        result.setLocalNamespace("http://arjanbas.nl/ada/cmis/1.1");
        result.setCreateable(true);
        result.setFileable(false);
        result.setQueryable(true);
        result.setFulltextIndexed(false);
        result.setIncludedInSupertypeQuery(true);
        result.setControllableAcl(true);
        result.setControllablePolicy(false);
        result.addPropertyDefinition(Property.createObjectId());
        result.addPropertyDefinition(Property.createdBy());
        result.addPropertyDefinition(Property.createPath());
        result.addPropertyDefinition(Property.createCreationDate());
        result.addPropertyDefinition(Property.createLastModificationDate());
        result.addPropertyDefinition(Property.createLastModifiedBy());
        result.addPropertyDefinition(Property.createBaseTypeId());
        result.addPropertyDefinition(Property.createName());
        result.addPropertyDefinition(Property.createObjectType());
        return result;
    }
}
