package nl.arba.ada.server.cmis.model;

public class TypeDefinition {
    private String id;
    private String localName;
    private String localNamespace;
    private String displayName;
    private String queryName;
    private String baseId;
    /*
{
    "id": "cmis:document",
    "localName": "document",
    "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
    "displayName": "Document",
    "queryName": "cmis:document",
    "description": "Document Type",
    "baseId": "cmis:document",
    "creatable": true,
    "fileable": true,
    "queryable": true,
    "fulltextIndexed": true,
    "includedInSupertypeQuery": true,
    "controllablePolicy": false,
    "controllableACL": true,
    "versionable": true,
    "contentStreamAllowed": "allowed",
    "propertyDefinitions": {
        "cmis:createdBy": {
            "id": "cmis:createdBy",
            "localName": "createdBy",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Created by",
            "queryName": "cmis:createdBy",
            "description": "The authority who created this object",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": true
        },
        "cmis:isMajorVersion": {
            "id": "cmis:isMajorVersion",
            "localName": "isMajorVersion",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Is Major Version",
            "queryName": "cmis:isMajorVersion",
            "description": "Is this a major version of the document?",
            "propertyType": "boolean",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:lastModifiedBy": {
            "id": "cmis:lastModifiedBy",
            "localName": "lastModifiedBy",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Last Modified By",
            "queryName": "cmis:lastModifiedBy",
            "description": "The authority who last modified this object",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": true
        },
        "alfcmis:nodeRef": {
            "id": "alfcmis:nodeRef",
            "localName": "nodeRef",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/alfcmis",
            "displayName": "Alfresco Node Ref",
            "queryName": "alfcmis:nodeRef",
            "description": "Alfresco Node Ref",
            "propertyType": "id",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:versionSeriesCheckedOutId": {
            "id": "cmis:versionSeriesCheckedOutId",
            "localName": "versionSeriesCheckedOutId",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Version Series Checked Out Id",
            "queryName": "cmis:versionSeriesCheckedOutId",
            "description": "The checked out version series id",
            "propertyType": "id",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:creationDate": {
            "id": "cmis:creationDate",
            "localName": "creationDate",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Creation Date",
            "queryName": "cmis:creationDate",
            "description": "The object creation date",
            "propertyType": "datetime",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": true
        },
        "cmis:versionLabel": {
            "id": "cmis:versionLabel",
            "localName": "versionLabel",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Version Label",
            "queryName": "cmis:versionLabel",
            "description": "The version label",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:isPrivateWorkingCopy": {
            "id": "cmis:isPrivateWorkingCopy",
            "localName": "isPrivateWorkingCopy",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Is private working copy",
            "queryName": "cmis:isPrivateWorkingCopy",
            "description": "Indicates if this instance is a private working copy",
            "propertyType": "boolean",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:objectId": {
            "id": "cmis:objectId",
            "localName": "objectId",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Object Id",
            "queryName": "cmis:objectId",
            "description": "The unique object id (a node ref)",
            "propertyType": "id",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": true
        },
        "cmis:name": {
            "id": "cmis:name",
            "localName": "name",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Name",
            "queryName": "cmis:name",
            "description": "Name",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readwrite",
            "inherited": false,
            "required": true,
            "queryable": true,
            "orderable": true
        },
        "cmis:isLatestMajorVersion": {
            "id": "cmis:isLatestMajorVersion",
            "localName": "isLatestMajorVersion",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Is Latest Major Version",
            "queryName": "cmis:isLatestMajorVersion",
            "description": "Is this the latest major version of the document?",
            "propertyType": "boolean",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:secondaryObjectTypeIds": {
            "defaultValue": [],
            "id": "cmis:secondaryObjectTypeIds",
            "localName": "secondaryObjectTypeIds",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Secondary Object Type Ids",
            "queryName": "cmis:secondaryObjectTypeIds",
            "description": "Ids of the secondary object types for the object",
            "propertyType": "id",
            "cardinality": "multi",
            "updatability": "readwrite",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": false
        },
        "cmis:lastModificationDate": {
            "id": "cmis:lastModificationDate",
            "localName": "lastModificationDate",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Last Modified Date",
            "queryName": "cmis:lastModificationDate",
            "description": "The date this object was last modified",
            "propertyType": "datetime",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": true
        },
        "cmis:changeToken": {
            "id": "cmis:changeToken",
            "localName": "changeToken",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Change token",
            "queryName": "cmis:changeToken",
            "description": "Change Token",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:versionSeriesCheckedOutBy": {
            "id": "cmis:versionSeriesCheckedOutBy",
            "localName": "versionSeriesCheckedOutBy",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Version Series Checked Out By",
            "queryName": "cmis:versionSeriesCheckedOutBy",
            "description": "The authority who checked out this document version series",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:isVersionSeriesCheckedOut": {
            "id": "cmis:isVersionSeriesCheckedOut",
            "localName": "isVersionSeriesCheckedOut",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Is Version Series Checked Out",
            "queryName": "cmis:isVersionSeriesCheckedOut",
            "description": "Is the version series checked out?",
            "propertyType": "boolean",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:baseTypeId": {
            "id": "cmis:baseTypeId",
            "localName": "baseTypeId",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Base Type Id",
            "queryName": "cmis:baseTypeId",
            "description": "Id of the base object type for the object",
            "propertyType": "id",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": false
        },
        "cmis:isLatestVersion": {
            "id": "cmis:isLatestVersion",
            "localName": "isLatestVersion",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Is Latest Version",
            "queryName": "cmis:isLatestVersion",
            "description": "Is this the latest version of the document?",
            "propertyType": "boolean",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:objectTypeId": {
            "id": "cmis:objectTypeId",
            "localName": "objectTypeId",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Object Type Id",
            "queryName": "cmis:objectTypeId",
            "description": "Id of the object’s type",
            "propertyType": "id",
            "cardinality": "single",
            "updatability": "oncreate",
            "inherited": false,
            "required": true,
            "queryable": true,
            "orderable": true
        },
        "cmis:isImmutable": {
            "id": "cmis:isImmutable",
            "localName": "isImmutable",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Is Immutable",
            "queryName": "cmis:isImmutable",
            "description": "Is the document immutable?",
            "propertyType": "boolean",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:contentStreamFileName": {
            "id": "cmis:contentStreamFileName",
            "localName": "contentStreamFileName",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Content Stream Filename",
            "queryName": "cmis:contentStreamFileName",
            "description": "The content stream filename",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": false
        },
        "cmis:description": {
            "id": "cmis:description",
            "localName": "description",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Description",
            "queryName": "cmis:description",
            "description": "Description",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readwrite",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": false
        },
        "cmis:checkinComment": {
            "id": "cmis:checkinComment",
            "localName": "checkinComment",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Checkin Comment",
            "queryName": "cmis:checkinComment",
            "description": "The checkin comment",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:versionSeriesId": {
            "id": "cmis:versionSeriesId",
            "localName": "versionSeriesId",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Version series id",
            "queryName": "cmis:versionSeriesId",
            "description": "The version series id",
            "propertyType": "id",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        },
        "cmis:contentStreamLength": {
            "minValue": -9223372036854775808,
            "maxValue": 9223372036854775807,
            "id": "cmis:contentStreamLength",
            "localName": "contentStreamLength",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Content Stream Length",
            "queryName": "cmis:contentStreamLength",
            "description": "The length of the content stream",
            "propertyType": "integer",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": true
        },
        "cmis:contentStreamMimeType": {
            "id": "cmis:contentStreamMimeType",
            "localName": "contentStreamMimeType",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Content Stream MIME Type",
            "queryName": "cmis:contentStreamMimeType",
            "description": "The content stream MIME type",
            "propertyType": "string",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": true,
            "orderable": true
        },
        "cmis:contentStreamId": {
            "id": "cmis:contentStreamId",
            "localName": "contentStreamId",
            "localNamespace": "http://www.alfresco.org/model/cmis/1.0/cs01",
            "displayName": "Content Stream Id",
            "queryName": "cmis:contentStreamId",
            "description": "Id of the stream",
            "propertyType": "id",
            "cardinality": "single",
            "updatability": "readonly",
            "inherited": false,
            "required": false,
            "queryable": false,
            "orderable": false
        }
    },
    "mandatoryAspects": {
        "mandatoryAspect": "P:sys:localized"
    }
}
     */

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

    public static TypeDefinition getCmisDocument() {
        TypeDefinition result = new TypeDefinition();
        result.setId("cmis:document");
        result.setBaseId("cmis:document");
        result.setDisplayName("Document");
        result.setLocalName("document");
        result.setQueryName("cmis:document");
        result.setLocalNamespace("http://arjanbas.nl/ada/cmis/1.1");
        return result;
    }
}
