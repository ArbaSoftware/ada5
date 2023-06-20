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
                    else if ("typeDefinition".equals(cmisSelector)) {
                        sendTypeDefinition(request, response);
                    }
                    else {
                        System.out.println("NOT FOUND: " + request.getRequestURI());
                        notFound(response);
                    }
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
        System.out.println(request.getRequestURI());
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
            sendJson("{" +
                    "    \"id\": \"cmis:document\",\n" +
                    "    \"localName\": \"document\",\n" +
                    "    \"localNamespace\": \"http://www.alfresco.org/model/cmis/1.0/cs01\",\n" +
                    "    \"displayName\": \"Document\",\n" +
                    "    \"queryName\": \"cmis:document\",\n" +
                    "    \"description\": \"Document Type\",\n" +
                    "    \"baseId\": \"cmis:document\"}", response);
        }
        catch (Exception err) {
            notFound(response);
        }
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
