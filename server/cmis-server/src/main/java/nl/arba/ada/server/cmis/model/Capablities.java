package nl.arba.ada.server.cmis.model;

public class Capablities {
    /*
"capabilities": {
            "capabilityRenditions": "read",
            "capabilityGetDescendants": true,
            "capabilityGetFolderTree": true,
            "capabilityMultifiling": true,
            "capabilityUnfiling": false,
            "capabilityVersionSpecificFiling": false,
            "capabilityPWCSearchable": false,
            "capabilityPWCUpdatable": true,
            "capabilityAllVersionsSearchable": false,
            "capabilityOrderBy": null,
            "capabilityQuery": "bothcombined",
            "capabilityJoin": "none",
            "capabilityACL": "manage"
        },
     */

    public String getCapabilityContentStreamUpdatability() {
        return "none";
    }

    public String getCapabilityChanges() {
        return "none";
    }

    public String getCapabilityRenditions() {
        return "read";
    }

    public boolean getCapabilityGetDescendants() {
        return true;
    }
    public boolean getCapabilityGetFolderTree() {
        return true;
    }

    public boolean getCapabilityMultifiling() {
        return true;
    }

    public boolean getCapabilityUnfiling() {
        return true;
    }
    public boolean getcapabilityVersionSpecificFiling() {
        return false;
    }
    public boolean getcapabilityPWCSearchable() {
        return false;
    }
    public boolean getcapabilityPWCUpdatable() {
        return false;
    }
    public boolean getcapabilityAllVersionsSearchable() {
        return false;
    }
}
