package trollogyadherent.offlineauth.rest;

import com.google.common.net.MediaType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import spark.Request;
import spark.Response;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import spark.utils.IOUtils;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.packets.DeletePlayerFromClientRegPacket;
import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;
import trollogyadherent.offlineauth.request.objects.ChallengeRequestBodyObject;
import trollogyadherent.offlineauth.request.objects.ChangeDisplaynameRequestBodyObject;
import trollogyadherent.offlineauth.request.objects.ChangePasswordRequestBodyObject;
import trollogyadherent.offlineauth.request.objects.DeleteAccountRequestBodyObject;
import trollogyadherent.offlineauth.request.objects.RegisterRequestBodyObject;
import trollogyadherent.offlineauth.request.objects.RemoveSkinOrCapeRequestBodyObject;
import trollogyadherent.offlineauth.request.objects.UploadSkinOrCapeRequestBodyObject;
import trollogyadherent.offlineauth.request.objects.VibeCheckRequestBodyObject;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.AesKeyUtil;
import trollogyadherent.offlineauth.util.JsonUtil;
import trollogyadherent.offlineauth.util.RsaKeyUtil;
import trollogyadherent.offlineauth.util.ServerUtil;
import trollogyadherent.offlineauth.util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class Rest {

    public static void restStart() {

        staticFiles.externalLocation("static");
        port(Config.port);
        //staticFiles.location("/public");
        staticFiles.expireTime(600L);
        post("/register", Rest::register);
        post("/delete", Rest::delete);
        post("/change", Rest::changePassword);
        post("/changedisplay", (Rest::changeDisplayName));
        post("/vibecheck", Rest::vibecheck);
        /* Not secure, deprecated */ //get("/accounts", (request, response) -> listAccounts(request, response));
        /* Not secure, deprecated */ //get("/token", (request, response) -> handleToken(request, response));
        get("/pubkey", Rest::handlePubKey);
        get("/temppubkey", Rest::handleTempPubKey);
        post("/tokenchallenge", Rest::handleTokenChallenge);
        post("/uploadskin", Rest::handleSkinUpload);
        post("/uploadcape", Rest::handleCapeUpload);
        post("/removeskin", Rest::handleSkinRemoval);
        post("/removecape", Rest::handleCapeRemoval);
    }

    public static String vibecheck(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to check my vibe, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }

        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.raw().getRemoteAddr(), request.raw().getRemoteHost())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.no_keypair_associated", 500));
        }

        VibeCheckRequestBodyObject rbo = (VibeCheckRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()), VibeCheckRequestBodyObject.class);
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.allowDisplayNameChange, 500);
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
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, returnedDisplayName, Config.allowDisplayNameChange, 200);
            return JsonUtil.objectToJson(responseObject);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.allowDisplayNameChange, 500);
            return JsonUtil.objectToJson(responseObject);
        }
    }

    public static String register(Request request, Response response) throws NoSuchAlgorithmException {
        //OfflineAuth.info("Someone tries to register an account, identifier: " + request.queryParams("identifier") + ", displayname: " + request.queryParams("displayname"));
        OfflineAuth.info("Someone tries to register an account, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }

        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.raw().getRemoteAddr(), request.raw().getRemoteHost())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.no_keypair_associated", 500));
        }

        RegisterRequestBodyObject rbo = (RegisterRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()), RegisterRequestBodyObject.class);

        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.aes_mismatch", 500));
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
            } else */
            
            if (Config.allowRegistration) {
                regResult = Database.registerPlayer(rbo.getIdentifier(), rbo.getDisplayname(), rbo.getPassword(), rbo.getUuid(),"", rbo.getPubKey(), new byte[1], new byte[1],false, false);
            } else if (!Config.allowRegistration && Config.allowTokenRegistration && Database.tokenIsValid(rbo.getToken())) {
                regResult = Database.registerPlayer(rbo.getIdentifier(), rbo.getDisplayname(), rbo.getPassword(), rbo.getUuid(), rbo.getToken(), rbo.getPubKey(), new byte[1], new byte[1], false, false);
            } else if (!Config.allowRegistration && Config.allowTokenRegistration && !Database.tokenIsValid(rbo.getToken())) {
                regResult = new StatusResponseObject("offlineauth.rest.registration_invalid_token", 500);
            } else if (!Config.allowRegistration && Config.allowTokenRegistration) {
                regResult = new StatusResponseObject("offlineauth.rest.registration_only_token", 500);
            } else {
                regResult = new StatusResponseObject("offlineauth.rest.registration_disabled", 500);
            }
            return JsonUtil.objectToJson(regResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("offlineauth.rest.registration_error", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String delete(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to delete an account, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }

        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.raw().getRemoteAddr(), request.raw().getRemoteHost())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.no_keypair_associated", 500));
        }

        DeleteAccountRequestBodyObject rbo = (DeleteAccountRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()), DeleteAccountRequestBodyObject.class);

        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.aes_mismatch", 500));
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
            StatusResponseObject delResult = new StatusResponseObject("offlineauth.rest.deletion_error", 500);
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
            StatusResponseObject statusResponseObject = new StatusResponseObject("offlineauth.rest.deletion_error", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String changePassword(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to change a password, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }

        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.raw().getRemoteAddr(), request.raw().getRemoteHost())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.no_keypair_associated", 500));
        }

        ChangePasswordRequestBodyObject rbo = (ChangePasswordRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()), ChangePasswordRequestBodyObject.class);

        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.aes_mismatch", 500));
        }

        String identifier = rbo.getIdentifier();
        String password = rbo.getPassword();
        String newPassword = rbo.getNewPassword();

        try {
            StatusResponseObject changeResult= Database.changePlayerPassword(identifier, password, newPassword);
            return JsonUtil.objectToJson(changeResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("offlineauth.rest.change_password_error", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String changeDisplayName(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to change a displayname, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }
        
        //TODO: ???
        if (!Config.allowDisplayNameChange) {
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", false, 500);
        }

        ChangeDisplaynameRequestBodyObject rbo = (ChangeDisplaynameRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()), ChangeDisplaynameRequestBodyObject.class);
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.allowDisplayNameChange, 500);
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
            StatusResponseObject statusResponseObject = new StatusResponseObject("offlineauth.rest.change_displayname_error", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    /* Not secure, unused */
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
                if (pd != null) {
                    enhancedUserList[i] = userList[i] + ":" + pd.getDisplayname() + ":" + pd.getUuid();
                }
            }
            return JsonUtil.objectToJson(new StatusResponseObject(enhancedUserList, 200));
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : userList) {
                DBPlayerData pd = Database.getPlayerDataByIdentifier(s);
                if (pd != null) {
                    sb.append(s).append(": ").append(pd.getDisplayname()).append(": ").append(pd.getUuid()).append("<br>");
                }
            }
            return sb.deleteCharAt(sb.length() - 1).toString();
        }
    }

    /* Not secure, unused */
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
        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }

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
    }

    public static Object handleTempPubKey(Request request, Response response) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        OfflineAuth.info("Ip " + Util.hideIP(request.raw().getRemoteAddr()) + " from host " + Util.hideIP(request.raw().getRemoteHost()) + " requests temporary public key");

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }

        AesKeyUtil.AesKeyPlusIv tempAesKeyPlusIv = OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
        /* Concatenating iv and aes key byte arrays, just a fancy way to do it */

        //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //outputStream.write(tempAesKeyPlusIv.key.getEncoded());
        //outputStream.write(tempAesKeyPlusIv.iv.getIV());
        byte[] aesKeyPlusIvBytes = Util.concatByteArrays(tempAesKeyPlusIv.key.getEncoded(), tempAesKeyPlusIv.iv.getIV()); //outputStream.toByteArray();

        PrivateKey serverPrivKey = ServerUtil.loadServerPrivateKey();
        String aesKeyPlusIvStr = Base64.getEncoder().encodeToString(aesKeyPlusIvBytes);
        String encryptedAesKeyPlusIvStr  = RsaKeyUtil.encryptWithPrivateKey(aesKeyPlusIvStr, serverPrivKey);

        byte[] encryptedAesKeyPlusIvBytes64 = Base64.getEncoder().encode(encryptedAesKeyPlusIvStr.getBytes(StandardCharsets.UTF_8));
        //System.out.println(Base64.getEncoder().encodeToString(encryptedAesKeyPlusIvBytes64));
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
        OfflineAuth.info("Someone tries to get a key token, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }

        if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(request.raw().getRemoteAddr(), request.raw().getRemoteHost())) {
            OfflineAuth.info("No keypair associated with this host and ip");
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.no_keypair_associated", 500));
        }

        ChallengeRequestBodyObject rbo = (ChallengeRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()), ChallengeRequestBodyObject.class);
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.allowDisplayNameChange, 500);
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
        OfflineAuth.info("Someone tries to upload a skin, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));
        OfflineAuth.info("Received " + request.bodyAsBytes().length + " bytes");

        UploadSkinOrCapeRequestBodyObject rbo =  RestUtil.getUploadSkinOrCapeRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()));
        if (rbo == null) {
            OfflineAuth.debug("(skin upload) rbo is null!");
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.change_skin_error", 500));
        }
        String identifier = rbo.getIdentifier();
        byte[] skinBytes = rbo.getImageBytes();
        String password = rbo.getPassword();
        String token = rbo.getClientKeyToken();
        ServerKeyTokenRegistry.TokenType type = rbo.getType();

        boolean validToken = false;
        if (token.length() > 0) {
            validToken = OfflineAuth.varInstanceServer.keyTokenRegistry.grantIdentifierAccess(identifier, token, type);
        }

        /* The verification token MUST be consumed, that's why these two simple checks are not at the top of the function */
        if (!Config.allowSkinUpload) {
            OfflineAuth.debug("skin upload is disabled, returning");
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.skin_upload_disabled", 500));
        }

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            OfflineAuth.debug("skin too large, returning (maxSkinBytes: " + Config.maxSkinBytes + ")");
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.skin_too_large", 500));
        }

        /* Checking if the bytes are a legit png */
        try {
            if (!Util.pngIsSane(skinBytes)) {
                OfflineAuth.debug("skin image not sane, returning");
                return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.invalid_skin_file", 500));
            }
            BufferedImage test = ImageIO.read(new ByteArrayInputStream(skinBytes));
            if (test == null) {
                OfflineAuth.debug("skin test BufferedImage is null, returning");
                return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.invalid_skin_file", 500));
            }
        } catch (IOException e) {
            OfflineAuth.debug("skin IOException, returning");
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.invalid_skin_file", 500));
        }

        try {
            StatusResponseObject changeResult;
            if (validToken) {
                changeResult = Database.changePlayerSkin(identifier, "", skinBytes, true);
            } else {
                changeResult = Database.changePlayerSkin(identifier, password, skinBytes, false);
            }

            if (changeResult.getStatusCode() == 200) {
                OfflineAuth.debug("Successfully added skin to database for identifier " + identifier);
                /* Checking if player is ingame and if yes, deleting some caches */
                DBPlayerData dbpd = Database.getPlayerDataByIdentifier(identifier);
                if (dbpd == null) {
                    OfflineAuth.error("Identifier " + identifier + " not found in Database!!!");
                    return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.change_skin_error", 500));
                }
                String displayname = dbpd.getDisplayname();
                for (EntityPlayerMP e : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    if (e.getDisplayName().equals(displayname)) {
                        OfflineAuth.debug("Ident " + identifier + ", displayname " + displayname + " is ingame, setting skin in player registry");
                        OfflineAuth.debug("Playerreg before operation: " + OfflineAuth.varInstanceServer.playerRegistry);
                        OfflineAuth.varInstanceServer.playerRegistry.setSkin(displayname, displayname);
                        OfflineAuth.debug("Playerreg after operation: " + OfflineAuth.varInstanceServer.playerRegistry);
                        ServerSkinUtil.removeSkinFromCache(dbpd.getDisplayname());

                        for (EntityPlayerMP o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                            OfflineAuth.debug("Sending skin invalidation packet to player " + displayname);
                            IMessage msg = new DeletePlayerFromClientRegPacket.SimpleMessage(displayname);
                            PacketHandler.net.sendTo(msg, o);
                        }
                        break;
                    }
                }
            }
            OfflineAuth.debug("Sending changeResult " + changeResult.getStatus() + ", code: " + changeResult.getStatusCode());
            return JsonUtil.objectToJson(changeResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("offlineauth.rest.change_skin_error", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String handleCapeUpload(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to upload a cape, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));
        OfflineAuth.info("Received " + request.bodyAsBytes().length + " bytes");

        UploadSkinOrCapeRequestBodyObject rbo =  RestUtil.getUploadSkinOrCapeRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()));
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.allowDisplayNameChange, 500);
            return JsonUtil.objectToJson(responseObject);
        }
        String identifier = rbo.getIdentifier();
        byte[] capeBytes = rbo.getImageBytes();
        String password = rbo.getPassword();
        String token = rbo.getClientKeyToken();
        ServerKeyTokenRegistry.TokenType type = rbo.getType();

        boolean validToken = false;
        if (token.length() > 0) {
            validToken = OfflineAuth.varInstanceServer.keyTokenRegistry.grantIdentifierAccess(identifier, token, type);
        }

        /* The verification token MUST be consumed, that's why these two simple checks are not at the top of the function */
        if (!Config.allowCapeUpload) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.cape_upload_disabled", 500));
        }

        if (request.bodyAsBytes().length > Config.maxCapeBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.cape_too_large", 500));
        }

        /* Checking if the bytes are a legit cape */
        if (!Util.imageIsSane(new ByteArrayInputStream(capeBytes))) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.invalid_cape_file", 500));
        }

        try {
            StatusResponseObject changeResult;
            if (validToken) {
                changeResult = Database.changePlayerCape(identifier, "", capeBytes, true);
            } else {
                changeResult = Database.changePlayerCape(identifier, password, capeBytes, false);
            }

            if (changeResult.getStatusCode() == 200) {
                /* Checking if player is ingame and if yes, deleting some caches */
                DBPlayerData dbpd = Database.getPlayerDataByIdentifier(identifier);
                if (dbpd != null) {
                    String displayname = dbpd.getDisplayname();
                    for (EntityPlayerMP e : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                        if (e.getDisplayName().equals(displayname)) {
                            OfflineAuth.varInstanceServer.playerRegistry.setSkin(displayname, displayname);
                            ServerSkinUtil.removeCapeFromCache(dbpd.getDisplayname());

                            for (EntityPlayerMP o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                                IMessage msg = new DeletePlayerFromClientRegPacket.SimpleMessage(displayname);
                                PacketHandler.net.sendTo(msg, o);
                            }
                            break;
                        }
                    }
                }
            }
            return JsonUtil.objectToJson(changeResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("offlineauth.rest.change_cape_error", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String handleSkinRemoval(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to remove the skin, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }

        RemoveSkinOrCapeRequestBodyObject rbo = (RemoveSkinOrCapeRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()), RemoveSkinOrCapeRequestBodyObject.class);
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.allowDisplayNameChange, 500);
            return JsonUtil.objectToJson(responseObject);
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
            StatusResponseObject changeResult;
            if (validToken) {
                changeResult = Database.deletePlayerSkin(identifier, "", true);
            } else {
                changeResult = Database.deletePlayerSkin(identifier, password, false);
            }
            if (changeResult.getStatusCode() == 200) {
                /* Checking if player is ingame and if yes, deleting some caches */
                DBPlayerData dbpd = Database.getPlayerDataByIdentifier(identifier);
                if (dbpd != null) {
                    String displayname = dbpd.getDisplayname();
                    for (EntityPlayerMP e : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                        if (e.getDisplayName().equals(displayname)) {
                            OfflineAuth.varInstanceServer.playerRegistry.setSkin(dbpd.getDisplayname(), ServerSkinUtil.getRandomDefaultSkinName());
                            ServerSkinUtil.removeSkinFromCache(dbpd.getDisplayname());

                            for (EntityPlayerMP o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                                IMessage msg = new DeletePlayerFromClientRegPacket.SimpleMessage(displayname);
                                PacketHandler.net.sendTo(msg, o);
                            }
                            break;
                        }
                    }
                }
            }
            return JsonUtil.objectToJson(changeResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("offlineauth.rest.remove_skin_error", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String handleCapeRemoval(Request request, Response response) throws NoSuchAlgorithmException {
        OfflineAuth.info("Someone tries to remove the skin, ip: " + Util.hideIP(request.raw().getRemoteAddr()) + ", host: " + Util.hideIP(request.raw().getRemoteHost()));

        if (request.bodyAsBytes().length > Config.maxSkinBytes) {
            return JsonUtil.objectToJson(new StatusResponseObject("offlineauth.rest.data_too_large", 500));
        }

        RemoveSkinOrCapeRequestBodyObject rbo = (RemoveSkinOrCapeRequestBodyObject) RestUtil.getRequestBodyObject(request.bodyAsBytes(), OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(request.raw().getRemoteAddr(), request.raw().getRemoteHost()), RemoveSkinOrCapeRequestBodyObject.class);
        if (rbo == null) {
            OfflineAuth.varInstanceServer.keyRegistry.remove(request.raw().getRemoteAddr(), request.raw().getRemoteHost());
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, "-", Config.allowDisplayNameChange, 500);
            return JsonUtil.objectToJson(responseObject);
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
            StatusResponseObject changeResult;
            if (validToken) {
                changeResult = Database.deletePlayerCape(identifier, "", true);
            } else {
                changeResult = Database.deletePlayerCape(identifier, password, false);
            }
            if (changeResult.getStatusCode() == 200) {
                /* Checking if player is ingame and if yes, deleting some caches */
                DBPlayerData dbpd = Database.getPlayerDataByIdentifier(identifier);
                if (dbpd != null) {
                    String displayname = dbpd.getDisplayname();
                    for (EntityPlayerMP e : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                        if (e.getDisplayName().equals(displayname)) {
                            //OfflineAuth.varInstanceServer.playerRegistry.setSkin(displayname, displayname);
                            ServerSkinUtil.removeCapeFromCache(dbpd.getDisplayname());

                            for (EntityPlayerMP o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                                IMessage msg = new DeletePlayerFromClientRegPacket.SimpleMessage(displayname);
                                PacketHandler.net.sendTo(msg, o);
                            }
                            break;
                        }
                    }
                }
            }
            return JsonUtil.objectToJson(changeResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("offlineauth.rest.remove_cape_error", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }
}
