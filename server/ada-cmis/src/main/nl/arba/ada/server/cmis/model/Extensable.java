package nl.arba.ada.server.cmis.model;

import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;

import java.util.List;

public class Extensable {
    private List<CmisExtensionElement> extensions;

    public List<CmisExtensionElement> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<CmisExtensionElement> list) {
        extensions = list;
    }
}
