package nl.arba.ada.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.arba.ada.client.api.security.IdentityProvider;
import nl.arba.ada.client.api.security.IdentityProviderType;
import nl.arba.ada.client.api.util.StreamUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

public class TestIdentityProvider {
    private String token;

    @Before
    public void before() throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://192.168.2.74:9443/auth/realms/arba/protocol/openid-connect/token");
        ArrayList<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("username", "arjan"));
        params.add(new BasicNameValuePair("password", "hemertje"));
        params.add(new BasicNameValuePair("client_id", "intranet"));
        params.add(new BasicNameValuePair("client_secret", "j20PVbmYMNHrqwiNKKUOywnPlOrpFhas"));
        post.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = client.execute(post);
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> info = mapper.readValue(response.getEntity().getContent(), Map.class);
        token = (String) info.get("access_token");
    }

    @Test
    public void test_search_users() throws Exception {
        Domain domain = Domain.create("http://192.168.2.74:9601/ada");
        domain.login(token);
        IdentityProvider internal = domain.getIdentityProviders().stream().filter(idp -> idp.getType().equals(IdentityProviderType.INTERNAL)).findFirst().get();
        domain.searchUsers(internal, "bas");
        /*
        HttpGet get = new HttpGet("http://192.168.2.74:9601/ada/identityprovider/" + internal.getId() + "/user/search/bas");
        get.setHeader("Authorization", "Bearer " + token);
        CloseableHttpResponse response = HttpClients.createDefault().execute(get);
        System.out.println(response.getCode());
        System.out.println(StreamUtils.streamToString(response.getEntity().getContent()));
         */
    }
}