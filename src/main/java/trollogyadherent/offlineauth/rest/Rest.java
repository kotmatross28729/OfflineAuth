package trollogyadherent.offlineauth.rest;

import spark.Request;
import spark.Response;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.util.JsonUtil;
//import trollogyadherent.offlineauth.rest.index.IndexController;
//import trollogyadherent.offlineauth.rest.user.UserDao;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static spark.Spark.*;

public class Rest {

    //public static UserDao userDao;

    public static void restStart() {
        port(Config.port);
        staticFiles.location("/public");
        staticFiles.expireTime(600L);
        post("/register", (request, response) -> register(request, response));
        post("/delete", (request, response) -> delete(request, response));
        post("/change", (request, response) -> change(request, response));
        get("/vibecheck", (request, response) -> vibecheck(request, response));

        /* TODO */
        get("/registeredaccounts", (request, response) -> vibecheck(request, response));
    }

    public static String register(Request request, Response response) throws NoSuchAlgorithmException, InvalidKeySpecException {
        OfflineAuth.info("Someone tries to register an account, username: " + request.queryParams("username"));
        try {
            StatusResponseObject regResult = new StatusResponseObject("Failed to register user!", 500);
            if (Config.allowRegistration) {
                regResult = Database.registerPlayer(request.queryParams("username"), request.queryParams("password"), "", false, false);
            } else if (!Config.allowRegistration && Config.allowTokenRegistration && Database.tokenIsValid(request.queryParams("token"))) {
                regResult = Database.registerPlayer(request.queryParams("username"), request.queryParams("password"), request.queryParams("token"), false, false);
            }
            return JsonUtil.objectToJson(regResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("Error while registering user", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String delete(Request request, Response response) {
        OfflineAuth.info("Someone tries to delete an account, username: " + request.queryParams("username"));
        try {
            StatusResponseObject delResult = Database.deletePlayer(request.queryParams("username"), request.queryParams("password"));
            return JsonUtil.objectToJson(delResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("Error while deleting user", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String change(Request request, Response response) {
        OfflineAuth.info("Someone tries to change a password, username: " + request.queryParams("username"));
        try {
            StatusResponseObject changeResult = Database.changePlayerPassword(request.queryParams("username"), request.queryParams("password"), request.queryParams("new"));
            return JsonUtil.objectToJson(changeResult);
        } catch (Exception e) {
            e.printStackTrace();
            StatusResponseObject statusResponseObject = new StatusResponseObject("Error while changing password", 500);
            return JsonUtil.objectToJson(statusResponseObject);
        }
    }

    public static String vibecheck(Request request, Response response) {
        OfflineAuth.info("Someone tries to check my vibe, username: " + request.queryParams("username"));
        String username = request.queryParams("username");
        String password = request.queryParams("password");
        boolean validPlayer = false;
        try {
            validPlayer = Database.playerValid(username, password);
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, validPlayer, Config.motd, Config.other, 200);
            return JsonUtil.objectToJson(responseObject);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            ResponseObject responseObject = new ResponseObject(Config.allowRegistration, Config.allowTokenRegistration, Config.allowSkinUpload, false, Config.motd, Config.other, 500);
            return JsonUtil.objectToJson(responseObject);
        }

    }
}
