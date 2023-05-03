package nl.arba.ada.client.api.util;

import nl.arba.ada.client.api.exceptions.InsufficientRightsException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Utility class to handle http issues
 */
public class HttpUtils {
    private static CloseableHttpClient client;

    private static CloseableHttpClient getClient() {
        if (client == null)
            client = HttpClients.createDefault();
        return client;
    }

    /**
     * Executes a http get request
     * @param url The url to get
     * @param user The user to authenticate the request
     * @param password The password to authenticate the request
     * @return The result as an inputstream
     * @throws IOException An IOException can occur
     */
    public static InputStream doGet(String url, String user, String password) throws IOException {
        HttpGet get = new HttpGet(url);
        get.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((user+":" + password).getBytes()));
        CloseableHttpResponse response = getClient().execute(get);
        if (response.getCode() == 200) {
            return response.getEntity().getContent();
        }
        else
            throw new IOException("Error on executing get");
    }

    /**
     * Executes a http post request
     * @param url The url to get
     * @param json The payload to post
     * @param user The user to authenticate the request
     * @param password The password to authenticate the request
     * @return The result as an inputstream
     * @throws IOException An IOException can occur
     */
    public static InputStream doPost(String url, String json, String user, String password) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((user+":"+password).getBytes()));
        post.setHeader("Content-type", "application/json");
        post.setEntity(new StringEntity(json));
        CloseableHttpResponse response = getClient().execute(post);
        if (response.getCode() == 200) {
            return response.getEntity().getContent();
        }
        else
            throw new IOException("Error on executing post (" + response.getCode() + ")" + StreamUtils.streamToString(response.getEntity().getContent()));
    }

    /**
     * Executes a http delete request
     * @param url The url to get
     * @param user The user to authenticate the request
     * @param password The password to authenticate the request
     * @return The result as an inputstream
     * @throws IOException An IOException can occur
     * @throws InsufficientRightsException When the user has no rights to perform the delete action
     */
    public static boolean doDelete(String url, String user, String password) throws IOException, InsufficientRightsException {
        HttpDelete delete = new HttpDelete(url);
        delete.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((user+":"+password).getBytes()));
        CloseableHttpResponse response = getClient().execute(delete);
        if (response.getCode() == 200) {
            return true;
        }
        else if (response.getCode() == 401)
            throw new InsufficientRightsException();
        else
            return false;
    }
}
