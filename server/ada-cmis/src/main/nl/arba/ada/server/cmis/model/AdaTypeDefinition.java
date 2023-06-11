package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.AdaClass;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeMutability;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

import java.util.Map;

public class AdaTypeDefinition extends Extensable implements TypeDefinition {
    private AdaClass source;

    private AdaTypeDefinition(AdaClass source) {
        this.source = source;
    }

    public static AdaTypeDefinition create(AdaClass source) {
        return new AdaTypeDefinition(source);
    }

    @Override
    public String getId() {
        return source.getId();
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalNamespace() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getQueryName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public BaseTypeId getBaseTypeId() {
        return null;
    }

    @Override
    public String getParentTypeId() {
        return null;
    }

    @Override
    public Boolean isCreatable() {
        return null;
    }

    @Override
    public Boolean isFileable() {
        return null;
    }

    @Override
    public Boolean isQueryable() {
        return null;
    }

    @Override
    public Boolean isFulltextIndexed() {
        return null;
    }

    @Override
    public Boolean isIncludedInSupertypeQuery() {
        return null;
    }

    @Override
    public Boolean isControllablePolicy() {
        return null;
    }

    @Override
    public Boolean isControllableAcl() {
        return null;
    }

    @Override
    public Map<String, PropertyDefinition<?>> getPropertyDefinitions() {
        return null;
    }

    @Override
    public TypeMutability getTypeMutability() {
        return null;
    }
}
