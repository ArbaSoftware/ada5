package nl.arba.ada.client.api.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class HttpUtils {
    private static CloseableHttpClient client;

    private static CloseableHttpClient getClient() {
        if (client == null)
            client = HttpClients.createDefault();
        return client;
    }

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
}
