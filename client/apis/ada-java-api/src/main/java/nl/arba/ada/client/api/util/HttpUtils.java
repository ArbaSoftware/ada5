package nl.arba.ada.client.api.util;

import nl.arba.ada.client.api.exceptions.InsufficientRightsException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Utility class to handle http issues
 */
public class HttpUtils {
    private static CloseableHttpClient client;

    private static CloseableHttpClient getClient() {
        return HttpClients.createDefault();
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
        return doGetWithAuthorizationHeader(url, "Basic " + Base64.getEncoder().encodeToString((user+":" + password).getBytes()));
    }

    public static InputStream doGet(String url, String token) throws IOException {
        return doGetWithAuthorizationHeader(url, "Bearer " + token);
    }

    private static InputStream doGetWithAuthorizationHeader(String url, String authorizationheader) throws IOException {
        HttpGet get = new HttpGet(url);
        get.setHeader("Authorization", authorizationheader);
        ResponseState response = execute(get);
        if (response.getCode() == 200) {
            String result = StreamUtils.streamToString(response.getStream());
            System.out.println(result);
            return new ByteArrayInputStream(result.getBytes());
            //return response.getStream();
        }
        else {
            System.out.println(StreamUtils.streamToString(response.getStream()));
            throw new IOException("Execution of get request failed");
        }
    }

    private static ResponseState execute(ClassicHttpRequest request) throws IOException {
        HttpClientResponseHandler <ResponseState> handler = new HttpClientResponseHandler<ResponseState>() {
            @Override
            public ResponseState handleResponse(ClassicHttpResponse classicHttpResponse) throws HttpException, IOException {
                ResponseState state = new ResponseState();
                state.setCode(classicHttpResponse.getCode());
                state.setStream(StreamUtils.readStream(classicHttpResponse.getEntity().getContent()));
                return state;
            }
        };
        return getClient().execute(request, handler);
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
        return doPostWithAuthenticationHeader(url, json, "Basic " + Base64.getEncoder().encodeToString((user+":"+password).getBytes()));
    }

    public static InputStream doPost(String url, String json, String token) throws IOException {
        return doPostWithAuthenticationHeader(url, json, "Bearer " + token);
    }

    private static InputStream doPostWithAuthenticationHeader(String url, String json, String authorizationheader) throws IOException {
        System.out.println("POST: " + authorizationheader);
        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", authorizationheader);
        post.setHeader("Content-type", "application/json");
        post.setEntity(new StringEntity(json));
        ResponseState response = execute(post);
        if (response.getCode() == 200) {
            return response.getStream();
        }
        else {
            System.out.println(StreamUtils.streamToString(response.getStream()));
            throw new IOException("Error on executing post (" + response.getCode() + ")");
        }
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
        return doDeleteWithAuthorizationHeader(url, "Basic " + Base64.getEncoder().encodeToString((user+":"+password).getBytes()));
    }

    public static boolean doDelete(String url, String token) throws IOException, InsufficientRightsException {
        return doDeleteWithAuthorizationHeader(url, "Bearer " + token);
    }

    private static boolean doDeleteWithAuthorizationHeader(String url, String header) throws IOException, InsufficientRightsException {
        HttpDelete delete = new HttpDelete(url);
        delete.setHeader("Authorization", header);
        ResponseState state = execute(delete);
        if (state.getCode() == 200) {
            return true;
        }
        else if (state.getCode() == 401)
            throw new InsufficientRightsException();
        else
            return false;
    }

    /**
     * Executes a http put request
     * @param url The url to get
     * @param json The payload to post
     * @param user The user to authenticate the request
     * @param password The password to authenticate the request
     * @return The result as an inputstream
     * @throws IOException An IOException can occur
     */
    public static InputStream doPut(String url, String json, String user, String password) throws IOException, InsufficientRightsException {
        return doPutWithAuthorizationHeader(url, json, "Basic " + Base64.getEncoder().encodeToString((user+":"+password).getBytes()));
    }

    public static InputStream doPut(String url, String json, String token) throws IOException, InsufficientRightsException {
        return doPutWithAuthorizationHeader(url, json, "Bearer " + token);
    }

    private static InputStream doPutWithAuthorizationHeader(String url, String json, String authheader) throws IOException, InsufficientRightsException {
        HttpPut put = new HttpPut(url);
        put.setHeader("Authorization", authheader);
        put.setHeader("Content-type", "application/json");
        put.setEntity(new StringEntity(json));
        ResponseState state = execute(put);
        System.out.println(StreamUtils.streamToString(state.getStream()));
        if (state.getCode() == 200) {
            return state.getStream();
        }
        else if (state.getCode() == 401)
            throw new InsufficientRightsException();
        else {
            throw new IOException("Error on executing put (" + state.getCode() + ")" + StreamUtils.streamToString(state.getStream()));
        }
    }

    private static class ResponseState {
        private int code;
        private InputStream stream;
        private boolean finished = false;

        public void setCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setStream(InputStream stream) {
            this.stream = stream;
        }

        public InputStream getStream() {
            return stream;
        }

        public void setFinished() {
            finished = true;
        }

        public boolean isFinished() {
            return finished;
        }
    }
}
