package nl.arba.ada.server.cmis;

import org.apache.chemistry.opencmis.commons.data.AclCapabilities;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.data.PermissionMapping;
import org.apache.chemistry.opencmis.commons.definitions.PermissionDefinition;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.SupportedPermissions;

import java.util.List;
import java.util.Map;

public class AdaAclCapabilities implements AclCapabilities {
    private AdaAclCapabilities() {

    }

    public static AdaAclCapabilities create() {
        return new AdaAclCapabilities();
    }

    @Override
    public SupportedPermissions getSupportedPermissions() {
        return SupportedPermissions.BOTH;
    }

    @Override
    public AclPropagation getAclPropagation() {
        return AclPropagation.PROPAGATE;
    }

    @Override
    public List<PermissionDefinition> getPermissions() {
        return null;
    }

    @Override
    public Map<String, PermissionMapping> getPermissionMapping() {
        return null;
    }

    @Override
    public List<CmisExtensionElement> getExtensions() {
        return null;
    }

    @Override
    public void setExtensions(List<CmisExtensionElement> list) {

    }
}
