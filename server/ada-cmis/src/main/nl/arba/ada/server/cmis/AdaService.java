package nl.arba.ada.server.cmis;

import nl.arba.ada.client.api.AdaClass;
import nl.arba.ada.client.api.Domain;
import nl.arba.ada.client.api.Store;
import nl.arba.ada.server.cmis.model.AdaTypeDefinition;
import nl.arba.ada.server.cmis.model.AdaTypeDefinitionList;
import nl.arba.ada.server.cmis.model.Repository;
import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.exceptions.CmisProxyAuthenticationException;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdaService extends AbstractCmisService {
    private String restapiUrl;
    private Domain domain;
    private List<Store> stores;

    public AdaService(String resturl, String user, String password) throws CmisProxyAuthenticationException {
        try {
            restapiUrl = resturl;
            domain = Domain.create(resturl);
            domain.login(user, password);
            stores = domain.getStores();
        }
        catch (Exception err) {
            err.printStackTrace();
            throw new CmisProxyAuthenticationException();
        }
        catch (Error e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extensionsData) {
        try {
            List <RepositoryInfo> results = domain.getStores().stream().map(s -> Repository.create(s)).collect(Collectors.toList());
            return results;
        }
        catch (Exception err) {
            err.printStackTrace();
            return new ArrayList<RepositoryInfo>();
        }
    }

    @Override
    public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions, BigInteger maxItems, BigInteger skipCount,ExtensionsData extension) {
        Optional<Store> optStore = stores.stream().filter(s -> s.getId().equals(repositoryId)).findFirst();
        if (optStore.isPresent()) {
            try {
                Store store = optStore.get();
                AdaClass[] allclasses = store.getClasses();
                if (typeId == null) {
                    //return all base object types
                    List<AdaClass> baseClasses = Arrays.asList(allclasses).stream().filter(c -> c.getParentClass() == null).collect(Collectors.toList());
                    return AdaTypeDefinitionList.create(baseClasses.toArray(new AdaClass[baseClasses.toArray().length]));
                } else {
                    AdaClass specific = null;
                    for (AdaClass clasz : allclasses) {
                        if (clasz.getId().equals(typeId)) {
                            specific = clasz;
                            break;
                        }
                    }
                    return AdaTypeDefinitionList.create(new AdaClass[]{specific});
                }
            }
            catch (Exception err) {
                return null;
            }
        }
        else
            return null;
    }

    @Override
    public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {
        Optional<Store> optStore = stores.stream().filter(s -> s.getId().equals(repositoryId)).findFirst();
        if (optStore.isPresent()) {
            try {
                Store store = optStore.get();
                String classId = typeId;
                if ("cmis:document".equals(typeId)) {
                    classId = "Document";
                }
                AdaClass clazz = store.getAdaClass(classId);
                return AdaTypeDefinition.create(clazz);
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ObjectInFolderList getChildren(String s, String s1, String s2, String s3, Boolean aBoolean, IncludeRelationships includeRelationships, String s4, Boolean aBoolean1, BigInteger bigInteger, BigInteger bigInteger1, ExtensionsData extensionsData) {
        System.out.println("Getting children");
        return null;
    }

    @Override
    public List<ObjectParentData> getObjectParents(String s, String s1, String s2, Boolean aBoolean, IncludeRelationships includeRelationships, String s3, Boolean aBoolean1, ExtensionsData extensionsData) {
        System.out.println("Getting object parents");
        return null;
    }

    @Override
    public ObjectData getObject(String repositoryid, String objectid, String filter, Boolean aBoolean, IncludeRelationships includerelationships, String renditionfilter, Boolean includepolicyids, Boolean includeacl, ExtensionsData extensions) {
        Optional<Store> optStore = stores.stream().filter(s -> s.getId().equals(repositoryid)).findFirst();
        if (optStore.isPresent()) {
            return Repository.create(optStore.get());
        }
        else
            return null;
    }
}
