package nl.arba.ada.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.arba.ada.client.api.addon.base.Folder;
import nl.arba.ada.client.api.security.GrantedRight;
import nl.arba.ada.client.api.util.StreamUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Pattern;

public class TestCU {
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

    public void createStore() throws Exception {
        Domain domain = Domain.create("http://192.168.2.74:9601/ada");
        domain.login(token);
        Store store = domain.getStore("ChristenUnie");
        Folder[] rootfolders = store.getRootFolders();
        domain.getObjectPath(store, "cd563748-21a5-11ee-915b-98f2b3f20cf4");
        System.out.println(store);
        /*
        Store store = domain.createStore("ChristenUnie", new GrantedRight[] {}, new String[]{"base"});
        Folder memberFolder = Folder.create(store, "Leden", null);
        Folder bestuurFolder = Folder.create(store, "Bestuur", null);
        Folder twentythreeFolder = Folder.create(store, "2023",bestuurFolder);
         */
        //store.createObject(memberFolder);
    }
}
