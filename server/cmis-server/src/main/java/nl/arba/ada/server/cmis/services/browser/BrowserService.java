package nl.arba.ada.server.cmis.services.browser;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            System.out.println(uri);
            if (uri.equals("/browser"))
                sendRepositories(request, response);
            else {
                String[] uriItems = uri.split(Pattern.quote("/"));
                if (uriItems.length == 3) {
                    notFound(response);
                    /*
                    try {
                        String storeId = uriItems[2];
                        sendRepository(storeId, request, response);
                    }
                    catch (StoreNotFoundException snfe) {
                        notFound(response);
                    }
                     */
                }
                else if (uriItems.length == 4) {
                    try {
                        String storeId = uriItems[2];
                        String objectId = uriItems[3];
                        if (objectId.equals("root")) {
                            sendObject(getRootFolder(storeId, request), response);
                        } else
                            notFound(response);
                    }
                    catch (StoreNotFoundException snfe) {
                        notFound(response);
                    }
                }
                else {
                    notFound(response);
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

    private void sendRepository(String storeid, HttpServletRequest request, HttpServletResponse response) throws IOException, StoreNotFoundException {
        Repository repo = getRepository(storeid, request);
        sendJson(getMapper().writeValueAsString(repo), response);
    }

    private void sendObject(CMISObject object, HttpServletResponse response) throws IOException {
        sendJson(getMapper().writeValueAsString(object), response);
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
        System.out.println(json);
        response.setStatus(200);
        response.setContentLength(json.length());
        response.setContentType("text/json");
        OutputStream os = response.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();
    }
}
