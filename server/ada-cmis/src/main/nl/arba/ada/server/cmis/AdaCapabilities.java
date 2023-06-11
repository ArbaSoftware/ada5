package nl.arba.ada.server.cmis;

import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.CreatablePropertyTypes;
import org.apache.chemistry.opencmis.commons.data.NewTypeSettableAttributes;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.enums.*;

import java.util.ArrayList;
import java.util.List;

public class AdaCapabilities implements RepositoryCapabilities {
    private AdaCapabilities() {

    }

    public static AdaCapabilities create() {
        return new AdaCapabilities();
    }

    @Override
    public CapabilityContentStreamUpdates getContentStreamUpdatesCapability() {
        return CapabilityContentStreamUpdates.PWCONLY;
    }

    @Override
    public CapabilityChanges getChangesCapability() {
        return CapabilityChanges.ALL;
    }

    @Override
    public CapabilityRenditions getRenditionsCapability() {
        return CapabilityRenditions.NONE;
    }

    @Override
    public Boolean isGetDescendantsSupported() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isGetFolderTreeSupported() {
        return Boolean.FALSE;
    }

    @Override
    public CapabilityOrderBy getOrderByCapability() {
        return CapabilityOrderBy.NONE;
    }

    @Override
    public Boolean isMultifilingSupported() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isUnfilingSupported() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isVersionSpecificFilingSupported() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean isPwcSearchableSupported() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean isPwcUpdatableSupported() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean isAllVersionsSearchableSupported() {
        return Boolean.FALSE;
    }

    @Override
    public CapabilityQuery getQueryCapability() {
        return CapabilityQuery.NONE;
    }

    @Override
    public CapabilityJoin getJoinCapability() {
        return CapabilityJoin.NONE;
    }

    @Override
    public CapabilityAcl getAclCapability() {
        return CapabilityAcl.NONE;
    }

    @Override
    public CreatablePropertyTypes getCreatablePropertyTypes() {
        return null;
    }

    @Override
    public NewTypeSettableAttributes getNewTypeSettableAttributes() {
        return null;
    }

    @Override
    public List<CmisExtensionElement> getExtensions() {
        return new ArrayList<CmisExtensionElement>();
    }

    @Override
    public void setExtensions(List<CmisExtensionElement> list) {

    }
}
