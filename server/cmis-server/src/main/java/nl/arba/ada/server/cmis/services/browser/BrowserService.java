package nl.arba.ada.server.cmis.services.browser;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.arba.ada.client.api.exceptions.NoSearchResultsException;
import nl.arba.ada.client.api.exceptions.StoreNotFoundException;
import nl.arba.ada.server.cmis.model.*;
import nl.arba.ada.server.cmis.services.CMISService;
import nl.arba.ada.server.cmis.services.Cache;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class BrowserService extends CMISService {
    private ObjectMapper mapper;

    public BrowserService(String apiurl, Cache cache) {
        super(apiurl, cache);
    }

    private ObjectMapper getMapper() {
        if (mapper == null)
            mapper = new ObjectMapper();
        return mapper;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getRequestURL() + "?" + request.getQueryString());
        if (verifyAuthorization(request.getHeader("Authorization"))) {
            String uri = request.getRequestURI();
            if (uri.equals("/browser"))
                sendRepositories(request, response);
            else {
                Map<String,String[]> params = request.getParameterMap();
                if (params.containsKey("objectId")) {
                    String objectId = params.get("objectId")[0];
                    String cmisSelector = params.get("cmisselector")[0];
                    if ("root".equals(objectId) && "object".equals(cmisSelector)) {
                        try {
                            sendRepository(request, response);
                        }
                        catch (StoreNotFoundException snfe) {
                            notFound(response);
                        }
                    }
                    else {
                        System.out.println("NOT FOUND: " + request.getRequestURI());
                        notFound(response);
                    }
                }
                else if (params.containsKey("cmisselector")) {
                    String cmisselector = params.get("cmisselector")[0];
                    if ("typeChildren".equals(cmisselector))
                        sendTypeChildren(request, response);
                    else if ("typeDefinition".equals(cmisselector))
                        sendTypeDefinition(request, response);
                }
                else {
                    String[] uriItems = request.getRequestURI().split(Pattern.quote("/"));
                    if (uriItems.length == 3) {
                        try {
                            String storeId = uriItems[2];
                            sendRepository(request, response);
                        }
                        catch (StoreNotFoundException snfe) {
                            notFound(response);
                        }
                    }
                }
            }
        }
        else {
            notAuthorized(response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println(request.getRequestURL() + "?" + request.getQueryString());
    }

    private void sendRepositories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Repository[] repos = getRepositories(request);
            HashMap<String, Map> input = new HashMap<>();
            for (int index = 0; index < repos.length; index++) {
                Repository current = repos[index];
                input.put(current.getId(), getJson(current, request));
            }
            sendJson(getMapper().writeValueAsString(input), response);
        }
        catch (Error err) {
            err.printStackTrace();
            throw new IOException();
        }
    }

    private void sendRepository(HttpServletRequest request, HttpServletResponse response) throws IOException, StoreNotFoundException {
        String[] uriItems = request.getRequestURI().split(Pattern.quote("/"));
        Repository repo = getRepository(uriItems[2], request);
        sendObject(repo, response);
    }

    private void sendObject(CMISObject object, HttpServletResponse response) throws IOException {
        HashMap <String, Object> jsonSource = new HashMap<>();
        HashMap<String, Object> properties = new HashMap<>();
        for (PropertyValue prop: object.getProperties()) {
            HashMap <String, Object> propMap = new HashMap<>();
            propMap.put("id", prop.getProperty().getId());
            propMap.put("localName", prop.getProperty().getLocalName());
            propMap.put("displayName", prop.getProperty().getDisplayName());
            propMap.put("queryName", prop.getProperty().getQueryName());
            propMap.put("type",prop.getProperty().getType().getValue());
            propMap.put("cardinality", prop.getProperty().getCardinality().getValue());
            propMap.put("value", prop.getValue());
            properties.put(prop.getProperty().getId(), propMap);
        }
        jsonSource.put("properties", properties);
        sendJson(getMapper().writeValueAsString(jsonSource), response);
    }

    private void sendRootfolders(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String[] uriItems = request.getRequestURI().split(Pattern.quote("/"));
            CMISObject[] rootFolders = getRootFolders(uriItems[2], request);
            sendJson("{\"objects\":[]}", response);
        }
        catch (Exception nsre) {
            notFound(response);
        }
    }

    private void sendTypeDefinition(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String typeId = request.getParameter("typeId");
            if ("cmis:document".equals(typeId)) {
                sendJson(getMapper().writeValueAsString(createTypeDefinitionJson(TypeDefinition.getCmisDocument())), response);
            }
            else if ("cmis:folder".equals(typeId)) {
                sendJson(getMapper().writeValueAsString(createTypeDefinitionJson(TypeDefinition.getCmisFolder())), response);
            }
            else
                throw new Exception("Unknonw");
        }
        catch (Exception err) {
            notFound(response);
        }
    }

    private Map<String,Object> createTypeDefinitionJson(TypeDefinition definition) {
        HashMap<String, Object> defJson = new HashMap<>();
        defJson.put("id", definition.getId());
        defJson.put("localName", definition.getLocalName());
        defJson.put("localNamespace",definition.getLocalNamespace());
        defJson.put("displayName", definition.getDisplayName());
        defJson.put("queryName", definition.getQueryName());
        defJson.put("description", "");
        defJson.put("baseId", definition.getBaseId());
        defJson.put("creatable", definition.isCreatable());
        defJson.put("fileable", definition.isFileable());
        defJson.put("queryable", definition.isQueryable());
        defJson.put("fulltextIndexed", definition.isFulltextIndexed());
        defJson.put("includedInSupertypeQuery", definition.isIncludedInSupertypeQuery());
        defJson.put("controllablePolicy", definition.hasControllablePolicy());
        defJson.put("controllableACL", definition.hasControllableAcl());
        HashMap<String, Map<String,Object>> propDefs = new HashMap<>();
        for (Property def: definition.getPropertyDefinitions())
            propDefs.put(def.getId(), createPropertyDefinitionJson(def));
        defJson.put("propertyDefinitions", propDefs);
        return defJson;
    }

    private Map <String,Object> createPropertyDefinitionJson(Property property) {
        HashMap <String,Object> json = new HashMap<>();
        json.put("id", property.getId());
        json.put("localName", property.getLocalName());
        json.put("localNamespace", "http://www.arjanbas.nl/ada/cmis/1.1");
        json.put("displayName", property.getDisplayName());
        json.put("queryName", property.getQueryName());
        json.put("description", "");
        json.put("propertyType", property.getType().getValue());
        json.put("cardinality", property.getCardinality().getValue());
        return json;
    }

    private void sendTypeChildren(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap <String, Object> json = new HashMap<>();
        ArrayList <Map> types = new ArrayList<>();
        TypeDefinition[] definitions = new TypeDefinition[] {
                TypeDefinition.getCmisDocument(),
                TypeDefinition.getCmisFolder()
        };
        for (TypeDefinition definition: definitions) {
            Map<String,Object> defJson = createTypeDefinitionJson(definition);
            types.add(defJson);
        }
        json.put("types", types);
        String resultjson = getMapper().writeValueAsString(types);
        System.out.println(resultjson);
        sendJson(resultjson, response);
    }

    private Map getJson(Repository source, HttpServletRequest request) throws IOException {
        HashMap <String, Object> jsoninput = new HashMap<>();
        jsoninput.put("repositoryId", source.getId());
        jsoninput.put("repositoryName", source.getName());
        jsoninput.put("repositoryDescription", "");
        jsoninput.put("vendorName", Vendor.getName());
        jsoninput.put("productName", Product.getName());
        jsoninput.put("productVersion", Product.getVersion());
        jsoninput.put("rootFolderId", "root");
        jsoninput.put("capabilities", new Capablities());
        jsoninput.put("repositoryUrl", request.getRequestURL()+ "/" + source.getId());
        jsoninput.put("rootFolderUrl", request.getRequestURL() + "/" + source.getId() + "/root");
        return jsoninput;
    }

    private void sendJson(String json, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentLength(json.length());
        response.setContentType("text/json");
        OutputStream os = response.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();
    }
}
