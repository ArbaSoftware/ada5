package nl.arba.ada.server.cmis.model;

import nl.arba.ada.client.api.Store;
import nl.arba.ada.server.cmis.AdaAclCapabilities;
import nl.arba.ada.server.cmis.AdaCapabilities;
import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;

import java.util.List;

public class Repository extends Extensable implements RepositoryInfo, ObjectData {
    private Store store;
    private AdaProperties properties;

    private Repository(Store store) {
        this.store = store;
    }

    public static Repository create(Store store) {
        return new Repository(store);
    }

    @Override
    public String getId() {
        return store.getId();
    }

    @Override
    public BaseTypeId getBaseTypeId() {
        return BaseTypeId.CMIS_FOLDER;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public AllowableActions getAllowableActions() {
        return null;
    }

    @Override
    public List<ObjectData> getRelationships() {
        return null;
    }

    @Override
    public ChangeEventInfo getChangeEventInfo() {
        return null;
    }

    @Override
    public Acl getAcl() {
        return null;
    }

    @Override
    public Boolean isExactAcl() {
        return null;
    }

    @Override
    public PolicyIdList getPolicyIds() {
        return null;
    }

    @Override
    public List<RenditionData> getRenditions() {
        return null;
    }

    @Override
    public String getName() {
        return store.getName();
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getVendorName() {
        return "Arba software";
    }

    @Override
    public String getProductName() {
        return "Arba Digital Archive (ADA)";
    }

    @Override
    public String getProductVersion() {
        return "5.0.0.";
    }

    @Override
    public String getRootFolderId() {
        return "root";
    }

    @Override
    public RepositoryCapabilities getCapabilities() {
        return AdaCapabilities.create();
    }

    @Override
    public AclCapabilities getAclCapabilities() {
        return AdaAclCapabilities.create();
    }

    @Override
    public String getLatestChangeLogToken() {
        return null;
    }

    @Override
    public String getCmisVersionSupported() {
        return "1.1";
    }

    @Override
    public CmisVersion getCmisVersion() {
        return CmisVersion.CMIS_1_1;
    }

    @Override
    public String getThinClientUri() {
        return null;
    }

    @Override
    public Boolean getChangesIncomplete() {
        return null;
    }

    @Override
    public List<BaseTypeId> getChangesOnType() {
        return null;
    }

    @Override
    public String getPrincipalIdAnonymous() {
        return null;
    }

    @Override
    public String getPrincipalIdAnyone() {
        return null;
    }

    @Override
    public List<ExtensionFeature> getExtensionFeatures() {
        return null;
    }
}
