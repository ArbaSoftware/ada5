package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.AdaClass;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdaTypeDefinitionList extends Extensable implements TypeDefinitionList {
    private AdaClass[] classes;

    private AdaTypeDefinitionList(AdaClass[] classes) {
        this.classes = classes;
    }

    public static AdaTypeDefinitionList create(AdaClass[] classes) {
        return new AdaTypeDefinitionList(classes);
    }

    @Override
    public List<TypeDefinition> getList() {
        return Arrays.asList(classes).stream().map(c -> AdaTypeDefinition.create(c)).collect(Collectors.toList());
    }

    @Override
    public Boolean hasMoreItems() {
        return null;
    }

    @Override
    public BigInteger getNumItems() {
        return new BigInteger(Integer.toString(classes.length));
    }
}
