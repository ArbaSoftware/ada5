package nl.arba.ada.server.cmis.services.browser;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nl.arba.ada.server.cmis.model.Capablities;
import nl.arba.ada.server.cmis.model.Product;
import nl.arba.ada.server.cmis.model.Repository;
import nl.arba.ada.server.cmis.model.Vendor;
import nl.arba.ada.server.cmis.services.CMISService;
import nl.arba.ada.server.cmis.services.Cache;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        }
        else {
            notAuthorized(response);
        }
    }

    private void sendRepositories(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Repository[] repos = getRepositories(request);
        HashMap <String, Map> input = new HashMap<>();
        for (int index = 0; index < repos.length; index++) {
            Repository current = repos[index];
            input.put(current.getId(), getJson(current, request));
        }
        sendJson(getMapper().writeValueAsString(input), response);
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
