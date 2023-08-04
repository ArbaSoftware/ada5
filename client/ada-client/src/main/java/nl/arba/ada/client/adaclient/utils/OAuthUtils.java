package nl.arba.ada.client.adaclient.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class OAuthUtils {
    public static boolean getToken(String tokenurl, String user, String password) {
        InputStream is = null;
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost(tokenurl);
            ArrayList<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("client_id", "intranet"));
            params.add(new BasicNameValuePair("client_secret", "j20PVbmYMNHrqwiNKKUOywnPlOrpFhas"));
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
