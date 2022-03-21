package trollogyadherent.offlineauth.request;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.util.JsonUtil;
import trollogyadherent.offlineauth.rest.ResponseObject;
import trollogyadherent.offlineauth.rest.StatusResponseObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Request {
    public static ResponseObject vibeCheck(String ip, String port, String username, String password) throws URISyntaxException {
        String baseUrl = "http://" + ip + ":" + port + "/";
        String requestPath = "vibecheck";

        HttpGet get = new HttpGet(baseUrl + requestPath);

        URI uri = new URIBuilder(get.getURI()).addParameter("username", username).addParameter("password", password).build();

        get.setURI(uri);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {

            String responseString = EntityUtils.toString(response.getEntity());
            return (ResponseObject) JsonUtil.jsonToObject(responseString, ResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            //e.printStackTrace();
            return new ResponseObject(false, false, false, false, "", "", 500);
        }
    }

    public static StatusResponseObject register(String ip, String port, String username, String password, String token) throws IOException {
        String baseUrl = "http://" + ip + ":" + port + "/";
        String requestPath = "register";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username", username));
        urlParameters.add(new BasicNameValuePair("password", password));
        urlParameters.add(new BasicNameValuePair("token", token));

        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(urlParameters);
        System.out.println(urlEncodedFormEntity.toString());
        post.setEntity(urlEncodedFormEntity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            String responseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response: " + responseString);
            return (StatusResponseObject) JsonUtil.jsonToObject(responseString, StatusResponseObject.class);
        } catch (Exception e) {
            //e.printStackTrace();
            //OfflineAuth.error("Failed to create user " + username + "!");
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Connection failed! Check if the port is correct", 500);
        }
    }

    /* POST request to delete an account */
    public static StatusResponseObject delete(String ip, String port, String username, String password) throws IOException {
        String baseUrl = "http://" + ip + ":" + port + "/";
        String requestPath = "delete";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username", username));
        urlParameters.add(new BasicNameValuePair("password", password));

        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(urlParameters);
        post.setEntity(urlEncodedFormEntity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            return (StatusResponseObject) JsonUtil.jsonToObject(responseString, StatusResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Connection failed! Check if the port is correct", 500);
        }
    }

    /* POST request to change an account password */
    public static StatusResponseObject change(String ip, String port, String username, String password, String newPassword) throws IOException {
        String baseUrl = "http://" + ip + ":" + port + "/";
        String requestPath = "change";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("username", username));
        urlParameters.add(new BasicNameValuePair("password", password));
        urlParameters.add(new BasicNameValuePair("new", newPassword));

        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(urlParameters);
        post.setEntity(urlEncodedFormEntity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            return (StatusResponseObject) JsonUtil.jsonToObject(responseString, StatusResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Connection failed! Check if the port is correct", 500);
        }
    }
}
