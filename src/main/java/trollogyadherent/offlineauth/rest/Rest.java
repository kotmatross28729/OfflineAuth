package trollogyadherent.offlineauth.rest;

import com.google.common.net.MediaType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import spark.Request;
import spark.Response;
import spark.utils.IOUtils;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.ResetCachesPacket;
import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;
import trollogyadherent.offlineauth.request.objects.*;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletOutputStream;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;

import static spark.Spark.*;

public class Rest {

    public static void restStart() {

        staticFiles.externalLocation("static");
        port(Config.port);
        //staticFiles.location("/public");
        staticFiles.expireTime(600L);
        post("/register", (request, response) -> register(request, response));
        post("/delete", (request, response) -> delete(request, response));
        post("/change", (request, response) -> changePassword(request, response));
        post("/changedisplay", ((request, response) -> changeDisplayName(request, response)));
        post("/vibecheck", (request, response) -> vibecheck(request, response));
        /* Not secure, deprecated */ //get("/accounts", (request, response) -> listAccounts(request, response));
        /* Not secure, deprecated */ //get("/token", (request, response) -> handleToken(request, response));
        get("/pubkey", (request, response) -> handlePubKey(request, response));
        get("/temppubkey", (request, response) -> handleTempPubKey(request, response));
        post("/tokenchallenge", (request, response) -> handleTokenChallenge(request, response));
        post("/uploadskin", (request, response) -> handleSkinUpload(request, response));
    }

    public static String vibecheck(Request request, Response response) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, InvalidKeySpecException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        OfflineAuth.info("Someone tries to check my vibe, ip: " + request.ip() + ", host: " + request.host());
        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.ip(), request.host())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("No keypair associated with this host and ip", 500));
        }

        VibeCheckRequestBodyObject rbo = (VibeCheckRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.ip(), request.host()), VibeCheckRequestBodyObject.class);
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.ip(), request.host());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.motd, Config.other, Config.allowDisplayNameChange, 500);
            return JsonUtil.objectToJson(responseObject);
        }
        String identifier = rbo.getIdentifier();
        String displayname = rbo.getDisplayname();
        String password = rbo.getPassword();
        String token = rbo.getClientKeyToken();
        ServerKeyTokenRegistry.TokenType type = rbo.getType();

        boolean validToken = false;
        if (token.length() > 0) {
            validToken = OfflineAuth.varInstanceServer.keyTokenRegistry.grantIdentifierAccess(identifier, token, type);
        }

        try {
            String returnedDisplayName = "-";
            if (validToken) {
                DBPlayerData dbpd = Database.getPlayerDataByIdentifier(identifier);
                if (dbpd != null && dbpd.getDisplayname().equals(displayname)) {
                    returnedDisplayName = dbpd.getDisplayname();
                }
            } else {
                returnedDisplayName = Database.playerValid(identifier, displayname, password);
            }
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, returnedDisplayName, Config.motd, Config.other, Config.allowDisplayNameChange, 200);
            return JsonUtil.objectToJson(responseObject);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.motd, Config.other, Config.allowDisplayNameChange, 500);
            return JsonUtil.objectToJson(responseObject);
        }
    }

    public static String register(Request request, Response response) throws NoSuchAlgorithmException {
        //OfflineAuth.info("Someone tries to register an account, identifier: " + request.queryParams("identifier") + ", displayname: " + request.queryParams("displayname"));
        OfflineAuth.info("Someone tries to register an account, ip: " + request.ip() + ", host: " + request.host());

        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.ip(), request.host())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("No keypair associated with this host and ip", 500));
        }

        RegisterRequestBodyObject rbo = (RegisterRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.ip(), request.host()), RegisterRequestBodyObject.class);

        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.ip(), request.host());
            return JsonUtil.objectToJson(new StatusResponseObject("AES key mismatch", 500));
        }

        /*String uuid = "";
        if (request.queryParams("uuid") != null) {
            uuid = request.queryParams("uuid");
        }*/

        try {
            StatusResponseObject regResult;
            /*if (request.queryParams("restpassword") != null && Database.restPasswordValid(request.queryParams("restpassword"))) {
                regResult = Database.registerPlayer(request.queryParams("identifier"), request.queryParams("displayname"), request.queryParams("password"), uuid,"", request.queryParams("publickey"), true, true);
            } else if (request.queryParams("restpassword") != null && !Database.restPasswordValid(request.queryParams("restpassword"))){
                regResult = new StatusResponseObject("Rest password invalid or not set!", 500);
            } else */if (Config.allowRegistration) {
                regResult = Database.registerPlayer(rbo.getIdentifier(), rbo.getDisplayname(), rbo.getPassword(), rbo.getUuid(),"", rbo.getPubKey(), new byte[1],false, false);
            } else if (!Config.allowRegistration && Config.allowTokenRegistration && Database.tokenIsValid(rbo.getToken())) {
                regResult = Database.registerPlayer(rbo.getIdentifier(), rbo.getDisplayname(), rbo.getPassword(), rbo.getUuid(), rbo.getToken(), rbo.getPubKey(), new byte[1], false, false);
            } else if (!Config.allowRegistration && Config.allowTokenRegistration && !Database.tokenIsValid(rbo.getToken())) {
                regResult = new StatusResponseObject("Invalid token!", 500);
            } else if (!Config.allowRegistration && Config.allowTokenRegistration) {
                regResult = new StatusResponseObject("Registration possible only with a token!", 500);
            } else {
                regResult = new StatusResponseObject("Registration disabled on this server!", 500);
            }
            return JsonUtil.objectToJson(regResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("Error while registering user", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String delete(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to delete an account, ip: " + request.ip() + ", host: " + request.host());

        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.ip(), request.host())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("No keypair associated with this host and ip", 500));
        }

        DeleteAccountRequestBodyObject rbo = (DeleteAccountRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.ip(), request.host()), DeleteAccountRequestBodyObject.class);

        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.ip(), request.host());
            return JsonUtil.objectToJson(new StatusResponseObject("AES key mismatch", 500));
        }

        String identifier = rbo.getIdentifier();
        String password = rbo.getPassword();
        String token = rbo.getClientKeyToken();
        ServerKeyTokenRegistry.TokenType type = rbo.getType();

        boolean validToken = false;
        if (token.length() > 0) {
            validToken = OfflineAuth.varInstanceServer.keyTokenRegistry.grantIdentifierAccess(identifier, token, type);
        }

        try {
            StatusResponseObject delResult = new StatusResponseObject("Failed to delete user", 500);
            /*if (rbo.getIdentifier() != null && request.queryParams("restpassword") != null && Database.restPasswordValid(request.queryParams("restpassword"))) {
                delResult = Database.deleteUserData(request.queryParams("identifier"));
                return JsonUtil.objectToJson(delResult);
            } else */if (identifier != null && password != null && !validToken) {
                delResult = Database.deletePlayer(identifier, password);
            } else if (validToken) {
                delResult = Database.deleteUserData(identifier);
            }
            return JsonUtil.objectToJson(delResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("Error while deleting user", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String changePassword(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to change a password, ip: " + request.ip() + ", host: " + request.host());

        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.ip(), request.host())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("No keypair associated with this host and ip", 500));
        }

        ChangePasswordRequestBodyObject rbo = (ChangePasswordRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.ip(), request.host()), ChangePasswordRequestBodyObject.class);

        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.ip(), request.host());
            return JsonUtil.objectToJson(new StatusResponseObject("AES key mismatch", 500));
        }

        String identifier = rbo.getIdentifier();
        String password = rbo.getPassword();
        String newPassword = rbo.getNewPassword();

        try {
            StatusResponseObject changeResult= Database.changePlayerPassword(identifier, password, newPassword);
            return JsonUtil.objectToJson(changeResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("Error while changing password", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String changeDisplayName(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to change a displayname, identifier: " + request.queryParams("identifier") + ", new display name: " + request.queryParams("new"));

        ChangeDisplaynameRequestBodyObject rbo = (ChangeDisplaynameRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.ip(), request.host()), ChangeDisplaynameRequestBodyObject.class);
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.ip(), request.host());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.motd, Config.other, Config.allowDisplayNameChange, 500);
            return JsonUtil.objectToJson(responseObject);
        }
        String identifier = rbo.getIdentifier();
        String newDisplayname = rbo.getNewDisplayName();
        String password = rbo.getPassword();
        String token = rbo.getClientKeyToken();
        ServerKeyTokenRegistry.TokenType type = rbo.getType();

        boolean validToken = false;
        if (token.length() > 0) {
            validToken = OfflineAuth.varInstanceServer.keyTokenRegistry.grantIdentifierAccess(identifier, token, type);
        }

        try {
            StatusResponseObject changeResult;
            if (validToken) {
                changeResult = Database.changePlayerDisplayName(identifier, "", newDisplayname, true);
            } else {
                changeResult = Database.changePlayerDisplayName(identifier, password, newDisplayname, false);
            }
            return JsonUtil.objectToJson(changeResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("Error while changing displayname", 500);
            String res = JsonUtil.objectToJson(statusResponseObject);
            return res;
        }
    }

    /* Not secure, deprecated */
    public static String listAccounts(Request request, Response response) throws NoSuchAlgorithmException, InvalidKeySpecException {
        OfflineAuth.info("Someone tries to list all accounts");
        String restpassword = request.queryParams("restpassword");
        if (restpassword == null && !Config.userListPublic) {
            return JsonUtil.objectToJson(new StatusResponseObject("Rest password cannot be null", 500));
        }

        if (!Database.restPasswordValid(restpassword) && !Config.userListPublic) {
            return JsonUtil.objectToJson(new StatusResponseObject("Rest password invalid or not set", 500));
        }

        String[] userList = Database.getRegisteredIdentifiers();
        if (userList.length == 0) {
            if (request.queryParams("humanreadable") == null || (request.queryParams("humanreadable") != null && request.queryParams("humanreadable").equals("false"))) {
                return JsonUtil.objectToJson(new StatusResponseObject(userList, 200));
            } else {
                return "No registered users!";
            }
        }

        if (request.queryParams("humanreadable") == null || (request.queryParams("humanreadable") != null && request.queryParams("humanreadable").equals("false"))) {
            String[] enhancedUserList = new String[userList.length];
            for (int i = 0; i < userList.length; i ++) {
                DBPlayerData pd = Database.getPlayerDataByIdentifier(userList[i]);
                enhancedUserList[i] = userList[i] + ":" + pd.getDisplayname() + ":" + pd.getUuid();
            }
            return JsonUtil.objectToJson(new StatusResponseObject(enhancedUserList, 200));
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : userList) {
                DBPlayerData pd = Database.getPlayerDataByIdentifier(s);
                sb.append(s).append(": ").append(pd.getDisplayname()).append(": ").append(pd.getUuid()).append("<br>");
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
    }

    /* Not secure, deprecated */
    public static String handleToken(Request request, Response response) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        if (request.queryParams("action") == null) {
            return JsonUtil.objectToJson(new StatusResponseObject("Please specify an action: list, generate, delete, clear", 500));
        }
        OfflineAuth.info("Someone tries to use token rest api endpoint with action " + request.queryParams("action"));
        String restpassword = request.queryParams("restpassword");
        if (restpassword == null) {
            return JsonUtil.objectToJson(new StatusResponseObject("Rest password cannot be null", 500));
        }

        if (!Database.restPasswordValid(restpassword)) {
            return JsonUtil.objectToJson(new StatusResponseObject("Rest password invalid or not set", 500));
        }

        if (request.queryParams("action").equals("list")) {
            ArrayList<String> tokens = Database.getTokenList();
            if (tokens.size() == 0) {
                if (request.queryParams("humanreadable") == null || (request.queryParams("humanreadable") != null && request.queryParams("humanreadable").equals("false"))) {
                    return JsonUtil.objectToJson(new StatusResponseObject(tokens, 200));
                } else {
                    return "No registered users!";
                }
            }

            if (request.queryParams("humanreadable") == null || (request.queryParams("humanreadable") != null && request.queryParams("humanreadable").equals("false"))) {
                return JsonUtil.objectToJson(new StatusResponseObject(tokens, 200));
            } else {
                StringBuilder sb = new StringBuilder();
                for (String s : tokens) {
                    sb.append(s).append("<br>");
                }
                return sb.toString();
            }
        } else if (request.queryParams("action").equals("generate")) {
            String token = Database.createtoken();
            return JsonUtil.objectToJson(new StatusResponseObject(token, 200));
        } else if (request.queryParams("action").equals("delete")) {
            if (request.queryParams("token") == null) {
                return JsonUtil.objectToJson(new StatusResponseObject("Action delete needs a token parameter", 500));
            }
            if (Database.tokenIsValid(request.queryParams("token"))) {
                Database.consoomToken(request.queryParams("token"));
                return JsonUtil.objectToJson(new StatusResponseObject("Successfully deleted token", 200));
            } else {
                return JsonUtil.objectToJson(new StatusResponseObject("Invalid token", 500));
            }
        } else if (request.queryParams("action").equals("clear")) {
            ArrayList<String> tokens = Database.getTokenList();
            for (String token : tokens) {
                Database.consoomToken(token);
            }
            return JsonUtil.objectToJson(new StatusResponseObject("Successfully deleted all tokens", 200));
        }

        return JsonUtil.objectToJson(new StatusResponseObject("Error", 500));
    }

    public static Object handlePubKey(Request request, Response response) throws IOException {
        //try {
            //byte[] pubKeyBytes = ServerUtil.loadServerPublicKey().getEncoded();

            File pubKeyFile = new File(OfflineAuth.varInstanceServer.keyPairPath + File.separator + "public.key");
            response.header("Content-Disposition", "attachment; filename=\"public.key\"");
            response.type(MediaType.OCTET_STREAM.toString());
            response.raw().setContentLength((int) pubKeyFile.length());
            //response.status();

            final ServletOutputStream os = response.raw().getOutputStream();
            final FileInputStream in = new FileInputStream(pubKeyFile);
            IOUtils.copy(in, os);
            in.close();
            os.close();

            return null;
        /*} catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            OfflineAuth.error(e.getMessage());
            return JsonUtil.objectToJson(new StatusResponseObject("Error while getting key", 500));
            //throw new RuntimeException(e);
        }*/
    }

    public static Object handleTempPubKey(Request request, Response response) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        OfflineAuth.info("Ip " + request.ip() + " from host " + request.host() + " requests temporary public key");

        AesKeyUtil.AesKeyPlusIv tempAesKeyPlusIv = OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.ip(), request.host());
        /* Concatenating iv and aes key byte arrays, just a fancy way to do it */

        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //outputStream.write(tempAesKeyPlusIv.key.getEncoded());
        //outputStream.write(tempAesKeyPlusIv.iv.getIV());
        byte[] aesKeyPlusIvBytes = Util.concatByteArrays(tempAesKeyPlusIv.key.getEncoded(), tempAesKeyPlusIv.iv.getIV()); //outputStream.toByteArray();

        PrivateKey serverPrivKey = ServerUtil.loadServerPrivateKey();
        String aesKeyPlusIvStr = Base64.getEncoder().encodeToString(aesKeyPlusIvBytes);
        String encryptedAesKeyPlusIvStr  = RsaKeyUtil.encryptWithPrivateKey(aesKeyPlusIvStr, serverPrivKey);
        System.out.println("hmmm");

        byte[] encryptedAesKeyPlusIvBytes64 = Base64.getEncoder().encode(encryptedAesKeyPlusIvStr.getBytes(StandardCharsets.UTF_8));
        System.out.println(Base64.getEncoder().encodeToString(encryptedAesKeyPlusIvBytes64));
        response.header("Content-Disposition", "attachment; filename=\"public.key\"");
        response.type(MediaType.OCTET_STREAM.toString());
        response.raw().setContentLength(encryptedAesKeyPlusIvBytes64.length);
        //response.status();

        final ServletOutputStream os = response.raw().getOutputStream();
        //final FileInputStream in = new FileInputStream(pubKeyFile);
        //IOUtils.copy(in, os);

        final ByteArrayInputStream in = new ByteArrayInputStream(encryptedAesKeyPlusIvBytes64);
        IOUtils.copy(in, os);
        in.close();
        os.close();

        return null;
        /*} catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
            OfflineAuth.error(e.getMessage());
            return JsonUtil.objectToJson(new StatusResponseObject("Error while getting key", 500));
            //throw new RuntimeException(e);
        }*/
    }

    public static String handleTokenChallenge(Request request, Response response) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        OfflineAuth.info("Someone tries to get a key token, ip: " + request.ip() + ", host: " + request.host());
        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.ip(), request.host())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("No keypair associated with this host and ip", 500));
        }

        ChallengeRequestBodyObject rbo = (ChallengeRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.ip(), request.host()), ChallengeRequestBodyObject.class);
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.ip(), request.host());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.motd, Config.other, Config.allowDisplayNameChange, 500);
            return JsonUtil.objectToJson(responseObject);
        }
        String identifier = rbo.getIdentifier();
        String playerPubKeyStr = rbo.getPubKey();
        if (Database.playerValidKey(identifier, playerPubKeyStr).equals("-")) {
            byte[] res = new byte[1];
            response.header("Content-Disposition", "attachment; filename=\"null\"");
            response.type(MediaType.OCTET_STREAM.toString());
            response.raw().setContentLength(res.length);
            //response.status();

            final ServletOutputStream os = response.raw().getOutputStream();
            //final FileInputStream in = new FileInputStream(pubKeyFile);
            //IOUtils.copy(in, os);

            final ByteArrayInputStream in = new ByteArrayInputStream(res);
            IOUtils.copy(in, os);
            in.close();
            os.close();
            return null;
        }

        PublicKey clientPubKey = RsaKeyUtil.pubKeyFromString(playerPubKeyStr);
        String token = OfflineAuth.varInstanceServer.keyTokenRegistry.getTokenAndType(identifier, rbo.getType()).token;
        String encryptedToken = RsaKeyUtil.encryptWithPublicKey(token, clientPubKey);
        byte[] res = encryptedToken.getBytes(StandardCharsets.UTF_8);
        response.header("Content-Disposition", "attachment; filename=\"null\"");
        response.type(MediaType.OCTET_STREAM.toString());
        response.raw().setContentLength(res.length);
        final ServletOutputStream os = response.raw().getOutputStream();
        final ByteArrayInputStream in = new ByteArrayInputStream(res);
        IOUtils.copy(in, os);
        in.close();
        os.close();
        return null;
    }

    public static String handleSkinUpload(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to upload a skin, ip: " + request.ip() + ", host: " + request.host());
        OfflineAuth.info("Received " + request.bodyAsBytes().length + " bytes");

        UploadSkinRequestBodyObject rbo =  RestUtil.getUploadSkinRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.ip(), request.host()));
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.ip(), request.host());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.motd, Config.other, Config.allowDisplayNameChange, 500);
            return JsonUtil.objectToJson(responseObject);
        }
        String identifier = rbo.getIdentifier();
        byte[] skinBytes = rbo.getSkinBytes();
        String password = rbo.getPassword();
        String token = rbo.getClientKeyToken();
        ServerKeyTokenRegistry.TokenType type = rbo.getType();

        boolean validToken = false;
        if (token.length() > 0) {
            validToken = OfflineAuth.varInstanceServer.keyTokenRegistry.grantIdentifierAccess(identifier, token, type);
        }

        /* The verification token MUST be consumed, that's why these two simple checks are not at the top of the function */
        if (!Config.allowSkinUpload) {
            return JsonUtil.objectToJson(new StatusResponseObject("Skin upload is disabled", 500));
        }

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("Skin too large, limit: " + Config.maxSkinBytes + " bytes", 500));
        }

        try {
            StatusResponseObject changeResult;
            if (validToken) {
                changeResult = Database.changePlayerSkin(identifier, "", skinBytes, true);
            } else {
                changeResult = Database.changePlayerSkin(identifier, password, skinBytes, false);
            }

            if (changeResult.getStatusCode() == 200) {
                for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    IMessage msg = new ResetCachesPacket.SimpleMessage();
                    PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
                }
                ServerSkinUtil.clearSkinCache();
            }

            return JsonUtil.objectToJson(changeResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("Error while changing displayname", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }
}
