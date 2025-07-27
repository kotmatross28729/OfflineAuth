package trollogyadherent.offlineauth.request;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.ServerKeyAddGUI;
import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;
import trollogyadherent.offlineauth.rest.ResponseObject;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.util.AesKeyUtil;
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.util.JsonUtil;
import trollogyadherent.offlineauth.util.RsaKeyUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class Request {
    
    //TODO: test for v6 | looks like it's not working
    // 'Host name may not be blank'
    
    public static ResponseObject vibeCheck(String ip, String port, String identifier, String displayname, String password, PublicKey clientPubKey, PrivateKey clientPrivKey) throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException, NoSuchProviderException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            PublicKey pubKey = getServerPubKey(ip, port);
            if (pubKey == null) {
                return new ResponseObject(false, false, false, "-", false, 500);
            }
            if (!OfflineAuth.varInstanceClient.checkingForKey) {
                OfflineAuth.varInstanceClient.checkingForKey = true;
                Minecraft.getMinecraft().displayGuiScreen(new ServerKeyAddGUI(Minecraft.getMinecraft().currentScreen, ip, port, pubKey));
            }
            return new ResponseObject(false, false, false, "-",  false, 500);
        }

        String clientKeyToken = "";
        if (clientPubKey != null && clientPrivKey != null) {
            String tempToken = getChallengeToken(ip, port, identifier, clientPubKey, clientPrivKey, ServerKeyTokenRegistry.TokenType.VIBECHECK);
            if (tempToken == null) {
                OfflineAuth.error("clientToken is null!");
                return new ResponseObject(false, false, false, "-", false, 500);
            }
            clientKeyToken = tempToken;
        }
        
        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "vibecheck";
        
        //todo: delete
        
        OfflineAuth.info("IP::RAW : " + ip);
        OfflineAuth.info("IP::ADR : " + addr.getIP());
        OfflineAuth.info("URL : " + baseUrl);
        
        HttpPost post = new HttpPost(baseUrl + requestPath);

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return new ResponseObject(false, false, false, "-",false, 500);
        }

        post.setEntity(RequestUtil.getVibeCheckRequestBody(aesKeyPlusIv, identifier, displayname, password, clientKeyToken));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            return (ResponseObject) JsonUtil.jsonToObject(responseString, ResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            //e.printStackTrace();
            return new ResponseObject(false, false, false, "-", false, 500);
        }
    }

    public static StatusResponseObject register(String ip, String port, String identifier, String displayname, String password, String uuid, String token, String clientPubKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return new StatusResponseObject("Could not find server public key in cache", 500);
        }
        /*
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            PublicKey pubKey = getServerPubKey(ip, port);
            if (pubKey == null) {
                return new StatusResponseObject("Couldn't obtain server public key", 500);
            }
            Minecraft.getMinecraft().displayGuiScreen(new ServerKeyAddGUI(Minecraft.getMinecraft().currentScreen, ip, port, pubKey));
            return new StatusResponseObject("Please try again, if key fingerprint is correct", 500);
        }
        */

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "register";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return new StatusResponseObject("offlineauth.key_or_connection_invalid", 500);
        }
        post.setEntity(RequestUtil.getRegisterRequestBody(aesKeyPlusIv, identifier, displayname, password, uuid, token, clientPubKey));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            String responseString = EntityUtils.toString(response.getEntity());
            return (StatusResponseObject) JsonUtil.jsonToObject(responseString, StatusResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            e.printStackTrace();
            return new StatusResponseObject("Connection failed! Check if the port is correct", 500);
        }
    }

    /* POST request to delete an account */
    public static StatusResponseObject delete(String ip, String port, String identifier, String password, PublicKey clientPubKey, PrivateKey clientPrivKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return new StatusResponseObject("Could not find server public key in cache", 500);
        }

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "delete";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        String clientKeyToken = "";
        if (clientPubKey != null && clientPrivKey != null) {
            String tempToken = getChallengeToken(ip, port, identifier, clientPubKey, clientPrivKey, ServerKeyTokenRegistry.TokenType.DELETEACCOUNT);
            if (tempToken == null) {
                OfflineAuth.error("clientToken is null!");
                return new StatusResponseObject("clientToken is null!", 500);
            }
            clientKeyToken = tempToken;
        }

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return new StatusResponseObject("offlineauth.key_or_connection_invalid", 500);
        }
        post.setEntity(RequestUtil.getDeleteAccountRequestBody(aesKeyPlusIv, identifier, password, clientKeyToken));

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
    public static StatusResponseObject changePW(String ip, String port, String identifier, String password, String newPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return new StatusResponseObject("Could not find server public key in cache", 500);
        }

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "change";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return new StatusResponseObject("offlineauth.key_or_connection_invalid", 500);
        }
        post.setEntity(RequestUtil.getChangePasswordRequestBody(aesKeyPlusIv, identifier, password, newPassword));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            return (StatusResponseObject) JsonUtil.jsonToObject(responseString, StatusResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Connection failed! Check if the port is correct", 500);
        }
    }

    /* POST request to change an account displayname */
    public static StatusResponseObject changeDisplayName(String ip, String port, String identifier, String password, String newDisplayName, PublicKey clientPubKey, PrivateKey clientPrivKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return new StatusResponseObject("Could not find server public key in cache", 500);
        }

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "changedisplay";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        String clientKeyToken = "";
        if (clientPubKey != null && clientPrivKey != null) {
            String tempToken = getChallengeToken(ip, port, identifier, clientPubKey, clientPrivKey, ServerKeyTokenRegistry.TokenType.CHANGEDISPLAYNAME);
            if (tempToken == null) {
                OfflineAuth.error("clientToken is null!");
                return new StatusResponseObject("clientToken is null!", 500);
            }
            clientKeyToken = tempToken;
        }

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return new StatusResponseObject("offlineauth.key_or_connection_invalid", 500);
        }

        post.setEntity(RequestUtil.getChangeDisplaynameRequestBody(aesKeyPlusIv, identifier, password, newDisplayName, clientKeyToken));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            return (StatusResponseObject) JsonUtil.jsonToObject(responseString, StatusResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Connection failed! Check if the port is correct", 500);
        }
    }

    public static PublicKey getServerPubKey(String ip, String port) {
        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "pubkey";

        HttpGet get = new HttpGet(baseUrl + requestPath);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {

            //String responseString = EntityUtils.toString(response.getEntity());
            //return (ResponseObject) JsonUtil.jsonToObject(responseString, ResponseObject.class);

            byte[] keyBytes = EntityUtils.toByteArray(response.getEntity());
            return RsaKeyUtil.pubKeyFromString(Base64.getEncoder().encodeToString(keyBytes));
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static AesKeyUtil.AesKeyPlusIv getServerTempKeyPlusIv(String ip, String port) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return null;
        }

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "temppubkey";

        HttpGet get = new HttpGet(baseUrl + requestPath);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {

            //String responseString = EntityUtils.toString(response.getEntity());
            //return (ResponseObject) JsonUtil.jsonToObject(responseString, ResponseObject.class);

            byte[] encryptedKeyPlusIvBytes64 = EntityUtils.toByteArray(response.getEntity());

            String encryptedAesKeyPlusIvStr = new String(Base64.getDecoder().decode(encryptedKeyPlusIvBytes64));
            String aesKeyPlusIvStr = RsaKeyUtil.decryptWithPublicKey(encryptedAesKeyPlusIvStr, ClientUtil.getServerPublicKeyFromCache(ip, port));
            byte[] aesKeyPlusIvBytes = Base64.getDecoder().decode(aesKeyPlusIvStr);
            byte[] keyBytes = new byte[16];
            byte[] ivBytes = new byte[16];
            if (aesKeyPlusIvBytes.length != 32) {
                OfflineAuth.error("Wrong aesKeyPlusIvBytes size!");
                return null;
            }
            for (int i = 0; i < 16; i ++) {
                keyBytes[i] = aesKeyPlusIvBytes[i];
                ivBytes[i] = aesKeyPlusIvBytes[i + 16];
            }
            return new AesKeyUtil.AesKeyPlusIv(AesKeyUtil.keyFromBytes(keyBytes), AesKeyUtil.ivFromBytes(ivBytes));
        } catch (Exception e) {
            OfflineAuth.error(Arrays.toString(e.getStackTrace()));
            //e.printStackTrace();
            return null;
        }
    }

    public static String getChallengeToken(String ip, String port, String identifier, PublicKey clientPubKey, PrivateKey clientPrivKey, ServerKeyTokenRegistry.TokenType type) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return null;
        }

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "tokenchallenge";
        HttpPost post = new HttpPost(baseUrl + requestPath);

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return null;
        }

        post.setEntity(RequestUtil.getChallengeRequestBody(aesKeyPlusIv, identifier, Base64.getEncoder().encodeToString(clientPubKey.getEncoded()), type));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            byte[] encryptedTokenBytes = EntityUtils.toByteArray(response.getEntity());

            if (encryptedTokenBytes.length == 1) {
                return null;
            }

            String encryptedToken = new String(encryptedTokenBytes);
            return RsaKeyUtil.decryptWithPrivateKey(encryptedToken, clientPrivKey);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            //e.printStackTrace();
            return null;
        }
    }

    public static StatusResponseObject uploadSkin(String ip, String port, String identifier, String password, byte[] skinBytes, PublicKey clientPubKey, PrivateKey clientPrivKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return new StatusResponseObject("Could not find server public key in cache", 500);
        }

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "uploadskin";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        String clientKeyToken = "";
        if (clientPubKey != null && clientPrivKey != null) {
            String tempToken = getChallengeToken(ip, port, identifier, clientPubKey, clientPrivKey, ServerKeyTokenRegistry.TokenType.UPLOADSKINORCAPE);
            if (tempToken == null) {
                OfflineAuth.error("clientToken is null!");
                return new StatusResponseObject("clientToken is null!", 500);
            }
            clientKeyToken = tempToken;
        }

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return new StatusResponseObject("offlineauth.key_or_connection_invalid", 500);
        }

        post.setEntity(RequestUtil.getUploadSkinOrCapeRequestBody(aesKeyPlusIv, identifier, password, skinBytes, clientKeyToken));


        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            return(StatusResponseObject) JsonUtil.jsonToObject(responseString, StatusResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Connection failed! Check if the port is correct", 500);
        }
    }

    public static StatusResponseObject uploadCape(String ip, String port, String identifier, String password, byte[] capeBytes, PublicKey clientPubKey, PrivateKey clientPrivKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return new StatusResponseObject("Could not find server public key in cache", 500);
        }

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "uploadcape";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        String clientKeyToken = "";
        if (clientPubKey != null && clientPrivKey != null) {
            String tempToken = getChallengeToken(ip, port, identifier, clientPubKey, clientPrivKey, ServerKeyTokenRegistry.TokenType.UPLOADSKINORCAPE);
            if (tempToken == null) {
                OfflineAuth.error("clientToken is null!");
                return new StatusResponseObject("clientToken is null!", 500);
            }
            clientKeyToken = tempToken;
        }

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return new StatusResponseObject("offlineauth.key_or_connection_invalid", 500);
        }

        post.setEntity(RequestUtil.getUploadSkinOrCapeRequestBody(aesKeyPlusIv, identifier, password, capeBytes, clientKeyToken));


        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            return(StatusResponseObject) JsonUtil.jsonToObject(responseString, StatusResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Connection failed! Check if the port is correct", 500);
        }
    }

    public static StatusResponseObject requestSkinRemoval(String ip, String port, String identifier, String password, PublicKey clientPubKey, PrivateKey clientPrivKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return new StatusResponseObject("Could not find server public key in cache", 500);
        }

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "removeskin";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        String clientKeyToken = "";
        if (clientPubKey != null && clientPrivKey != null) {
            String tempToken = getChallengeToken(ip, port, identifier, clientPubKey, clientPrivKey, ServerKeyTokenRegistry.TokenType.REMOVESKINORCAPE);
            if (tempToken == null) {
                OfflineAuth.error("clientToken is null!");
                return new StatusResponseObject("clientToken is null!", 500);
            }
            clientKeyToken = tempToken;
        }

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return new StatusResponseObject("offlineauth.key_or_connection_invalid", 500);
        }

        post.setEntity(RequestUtil.getRemoveSkinOrCapeRequestBody(aesKeyPlusIv, identifier, password, clientKeyToken));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            String responseString = EntityUtils.toString(response.getEntity());
            return (StatusResponseObject) JsonUtil.jsonToObject(responseString, StatusResponseObject.class);
        } catch (Exception e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Connection failed! Check if the port is correct", 500);
        }
    }

    public static StatusResponseObject requestCapeRemoval(String ip, String port, String identifier, String password, PublicKey clientPubKey, PrivateKey clientPrivKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if (ClientUtil.getServerPublicKeyFromCache(ip, port) == null) {
            return new StatusResponseObject("Could not find server public key in cache", 500);
        }

        ServerAddress addr = ServerAddress.func_78860_a(ip + ":" + 25565);
        String baseUrl = "http://" + addr.getIP() + ":" + port + "/";
        String requestPath = "removecape";

        HttpPost post = new HttpPost(baseUrl + requestPath);

        String clientKeyToken = "";
        if (clientPubKey != null && clientPrivKey != null) {
            String tempToken = getChallengeToken(ip, port, identifier, clientPubKey, clientPrivKey, ServerKeyTokenRegistry.TokenType.REMOVESKINORCAPE);
            if (tempToken == null) {
                OfflineAuth.error("clientToken is null!");
                return new StatusResponseObject("clientToken is null!", 500);
            }
            clientKeyToken = tempToken;
        }

        AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = getServerTempKeyPlusIv(ip, port);
        if (aesKeyPlusIv == null) {
            OfflineAuth.error("aesKeyPlusIv is null!");
            return new StatusResponseObject("offlineauth.key_or_connection_invalid", 500);
        }

        post.setEntity(RequestUtil.getRemoveSkinOrCapeRequestBody(aesKeyPlusIv, identifier, password, clientKeyToken));

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
