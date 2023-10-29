package nl.arba.ada.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.arba.ada.client.api.security.IdentityProvider;
import nl.arba.ada.client.api.security.IdentityProviderType;
import nl.arba.ada.client.api.util.StreamUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class TestOAuth {
    private HttpClient httpClient;

    @Before
    public void before() {
        httpClient = HttpClients.createDefault();
    }
    @Test
    public void test() {
        try {
            getToken("http://192.168.2.74:9443/auth/realms/Arba/protocol/openid-connect/token", "api_user", "#Kc@q}LG+L8R6Jv");
            String token = System.getProperty("oauth.token");
            HttpGet get = new HttpGet("http://192.168.2.74:9443/auth/admin/realms/Arba/clients");
            get.addHeader("Authorization", "Bearer " + token);
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(get);
            System.out.println(StreamUtils.streamToString(response.getEntity().getContent()));
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    public boolean getToken(String tokenurl, String user, String password) {
        InputStream is = null;
        try {
            HttpPost post = new HttpPost(tokenurl);
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("client_id", "ada"));
            params.add(new BasicNameValuePair("client_secret", "pXjlhpkKArzAWYGxR4cSPpck1GzmtUbh"));
            params.add(new BasicNameValuePair("username", user));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("grant_type", "password"));
            post.setEntity(new UrlEncodedFormEntity(params));
            CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(post);

            if (response.getCode() == 200) {
                is = response.getEntity().getContent();
                ObjectMapper mapper = new ObjectMapper();
                Map tokenInfo = mapper.readValue(is, Map.class);
                System.setProperty("oauth.token", tokenInfo.get("access_token").toString());
                return true;
            }
            else
                return false;
        }
        catch (Exception err) {
            return false;
        }
        finally {
            try {
                is.close();
            }
            catch (Exception err2) {}
        }
    }
}
