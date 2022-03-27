package trollogyadherent.offlineauth.database;


import org.iq80.leveldb.DBException;
import org.iq80.leveldb.Options;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.util.Util;
import trollogyadherent.offlineauth.varinstances.server.VarInstanceServer;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.iq80.leveldb.impl.Iq80DBFactory.bytes;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;


/* A lot was taken from https://github.com/samolego/SimpleAuth/blob/architectury/common/src/main/java/org/samo_lego/simpleauth/storage/database/LevelDB.java */
public class Database {

    public static void initialize() {
        Options options = new Options();
        try {
            OfflineAuth.varInstanceServer.levelDBStore = factory.open(new File( OfflineAuth.varInstanceServer.DB_NAME), options);
        } catch (IOException e) {
            OfflineAuth.error(e.getMessage());
        }
    }

    public static boolean close() {
        if (OfflineAuth.varInstanceServer.levelDBStore != null) {
            try {
                OfflineAuth.varInstanceServer.levelDBStore.close();
                return true;
            } catch (Error | IOException e) {
                OfflineAuth.error(e.getMessage());
            }
        }
        return false;
    }

    public static boolean isClosed() {
        return OfflineAuth.varInstanceServer.levelDBStore == null;
    }

    /* Registers a player. isCommand serves as an override for most checks (except things like null/invalid values) */
    /* overrideUser re-registers the user (used during password change) */
    public static StatusResponseObject registerPlayer(String username, String password, String token, boolean isCommand, boolean overrideUser) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String uuid = Util.offlineUUID(username);

        if (!Util.validUsername(username)) {
            return new StatusResponseObject("Invalid username!", 500);
        }

        if  (password == null || password.length() < 1) {
            return new StatusResponseObject("Invalid password!", 500);
        }

        if (password.contains(",")) {
            return new StatusResponseObject("Password cannot contain commas!", 500);
        }

        if (isUserRegistered(uuid) && !overrideUser) {
            return new StatusResponseObject("Username already registered!", 500);
        }

        if (!Config.allowRegistration && !Config.allowTokenRegistration && !isCommand) {
            return new StatusResponseObject("Registration and token registration is disabled!", 500);
        }

        if (!Config.allowRegistration && Config.allowTokenRegistration && !tokenIsValid(token)) {
            return new StatusResponseObject("Token registration enabled but token invalid!", 500);
        }

        if (token == null) {
            token = "";
        }

        String salt = Util.genSalt();
        String passwordHash = Util.getPasswordHash(password, salt);
        String skin = "none";

        String data = username + "," + passwordHash + "," + salt + "," + skin;

        try {
            OfflineAuth.varInstanceServer.levelDBStore.put(bytes("UUID:" + uuid), bytes("data:" + data));
            if (!Config.allowRegistration && Config.allowTokenRegistration) {
                consoomToken(token);
            }
            return new StatusResponseObject("Successfully registered user!", 200);
        } catch (Error e) {
            OfflineAuth.error("Register error: " + e.getMessage());
            return new StatusResponseObject("Error while registering user!", 500);
        }
    }

    public static StatusResponseObject deletePlayer(String username, String password) {
        if (username == null || password == null) {
            return new StatusResponseObject("Failed, username or password null", 500);
        }

        try {
            if (playerValid(username, password)) {
                return deleteUserData(Util.offlineUUID(username));
            } else {
                return new StatusResponseObject("Username or password invalid", 500);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Error while deleting user", 500);
            //e.printStackTrace();
        }
    }

    public static StatusResponseObject changePlayerPassword(String username, String password, String newPassword) {
        if (username == null || password == null || newPassword == null) {
            return new StatusResponseObject("Failed, username or password, or new password null", 500);
        }

        if (newPassword.contains(",")) {
            return new StatusResponseObject("Password cannot contain commas!", 500);
        }

        try {
            if (playerValid(username, password)) {
                //StatusResponseObject delData = deleteUserData(Util.offlineUUID(username));
                //if (delData.getStatusCode() == 200) {
                    StatusResponseObject registerData = registerPlayer(username, newPassword, "", true, true);
                    if (registerData.getStatusCode() == 200) {
                        return new StatusResponseObject("Successfully updated password", 200);
                    } else {
                        return new StatusResponseObject("Failed to change password: " + registerData.getStatus(), 500);
                    }
                //} else {
                //    return delData;
                //}
            } else {
                return new StatusResponseObject("Username or password invalid", 500);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Error while changing password", 500);
            //e.printStackTrace();
        }
    }

    public static StatusResponseObject deleteUserData(String uuid) {
        if (!isUserRegistered(uuid)) {
            return new StatusResponseObject("User not registered!", 500);
        }
        try {
            OfflineAuth.varInstanceServer.levelDBStore.delete(bytes("UUID:" + uuid));
            return new StatusResponseObject("Successfully deleted user!", 200);
        } catch (Error e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Failed to delete user!", 500);
        }
    }

    public static PlayerData getPlayerData(String uuid){
        try {
            if(isUserRegistered(uuid)){  // Gets data from db and removes "data:" prefix from it
                String data = new String(OfflineAuth.varInstanceServer.levelDBStore.get(bytes("UUID:" + uuid))).substring(5);
                return new PlayerData(data);
            }
        } catch (Error e) {
            OfflineAuth.error("Error getting data: " + e.getMessage());
        }
        return null;
    }

    public static boolean playerValid(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (username == null || password == null) {
            return false;
        }

        String uuid = Util.offlineUUID(username);

        if (!isUserRegistered(uuid)) {
            return false;
        }

        PlayerData player = getPlayerData(uuid);
        String passwordHash = Util.getPasswordHash(password, player.salt);
        return passwordHash.equals(player.passwordHash);
    }

    public static boolean isUserRegistered(String uuid) {
        try {
            return OfflineAuth.varInstanceServer.levelDBStore.get(bytes("UUID:" + uuid)) != null;
        } catch (DBException e) {
            OfflineAuth.error(e.getMessage());
        }
        return false;
    }

    public static boolean tokenIsValid(String token) {
        return false;
    }

    public static void consoomToken(String token) {

    }
}
