package trollogyadherent.offlineauth.database;


import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.util.ServerUtil;
import trollogyadherent.offlineauth.util.Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

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
    public static StatusResponseObject registerPlayer(String identifier, String displayname, String password, String uuid, String token, String publicKey, byte[] skinBytes, boolean isCommand, boolean overrideUser) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {

        if (!Util.validUsername(displayname)) {
            return new StatusResponseObject("Invalid Display Name!", 500);
        }

        if (!(uuid.equals("")) && !Util.uuidValid(uuid)) {
            return new StatusResponseObject("Invalid UUID!", 500);
        }

        boolean passwordNull = password == null || password.length() < 1;
        boolean publicKeyNull = publicKey == null || publicKey.length() < 10;

        if  (passwordNull && publicKeyNull) {
            return new StatusResponseObject("Invalid password or public key!", 500);
        }

        if  (!passwordNull && !publicKeyNull) {
            return new StatusResponseObject("You can only either register using password or public key!", 500);
        }

        if (!passwordNull && password.contains(",")) {
            return new StatusResponseObject("Password cannot contain commas!", 500);
        }

        if (identifier.contains(",")) {
            return new StatusResponseObject("Identifier cannot contain commas!", 500);
        }

        if (isUserRegisteredByIdentifier(identifier) && !overrideUser) {
            return new StatusResponseObject("Identifier already registered!", 500);
        }

        if (isUserRegisteredByDisplayname(displayname) && !overrideUser) {
            return new StatusResponseObject("Displayname already registered!", 500);
        }

        if (!Config.allowRegistration && !Config.allowTokenRegistration && !isCommand) {
            return new StatusResponseObject("Registration and token registration is disabled!", 500);
        }

        if (!Config.allowRegistration && Config.allowTokenRegistration && !tokenIsValid(token) && !isCommand) {
            return new StatusResponseObject("Token registration enabled but token invalid!", 500);
        }

        if (token == null) {
            token = "";
        }

        if (publicKey == null) {
            publicKey = "none";
        }

        String salt = "";
        String passwordHash = "";
        if (!passwordNull) {
            salt = Util.genSalt();
            passwordHash = Util.getPasswordHash(password, salt);
        }
        String skin = "none";

        if (uuid.equals("")) {
            uuid = Util.genUUID();
        }


        if (putPlayerDataInDB(identifier, displayname, passwordHash, salt, uuid, publicKey, skinBytes)) {
            if (!Config.allowRegistration && Config.allowTokenRegistration) {
                consoomToken(token);
            }
            return new StatusResponseObject("Successfully registered user!", 200);
        } else {
            OfflineAuth.error("Registration error");
            return new StatusResponseObject("Error while registering user!", 500);
        }
    }

    public static StatusResponseObject deletePlayer(String identifier, String password) {
        if (identifier == null || password == null) {
            return new StatusResponseObject("Failed, identifier or password null", 500);
        }

        try {
            if (playerValidIgnoreDisplayName(identifier, password)) {
                return deleteUserData(identifier);
            } else {
                return new StatusResponseObject("Identfier or password invalid", 500);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Error while deleting user", 500);
            //e.printStackTrace();
        }
    }

    public static StatusResponseObject changePlayerPassword(String identifier, String password, String newPassword) {
        if (identifier == null || password == null || newPassword == null) {
            return new StatusResponseObject("Failed, identifier or password, or new password null", 500);
        }

        if (newPassword.contains(",")) {
            return new StatusResponseObject("Password cannot contain commas!", 500);
        }

        try {
            if (playerValidIgnoreDisplayName(identifier, password)) {
                DBPlayerData pd = getPlayerDataByIdentifier(identifier);
                if (pd == null) {
                    return new StatusResponseObject("User not found", 500);
                }
                StatusResponseObject registerData = registerPlayer(identifier, pd.displayname, newPassword, pd.getUuid(),"", pd.publicKey, pd.skinBytes, true, true);
                if (registerData.getStatusCode() == 200) {
                    return new StatusResponseObject("Successfully updated password", 200);
                } else {
                    return new StatusResponseObject("Failed to change password: " + registerData.getStatus(), 500);
                }
            } else {
                return new StatusResponseObject("Identifier or password invalid", 500);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Error while changing password", 500);
            //e.printStackTrace();
        }
    }

    public static StatusResponseObject changePlayerDisplayName(String identifier, String password, String newDisplayName, boolean isCommand) {
        if (!isCommand && !Config.allowDisplayNameChange) {
            return new StatusResponseObject("Displayname change disallowed", 500);
        }
        if (identifier == null || (password == null && !isCommand) || newDisplayName == null) {
            return new StatusResponseObject("Failed, identifier or password, or new password null", 500);
        }

        if (!Util.validUsername(newDisplayName)) {
            return new StatusResponseObject("Invalid displayname!", 500);
        }

        if (isUserRegisteredByDisplayname(newDisplayName)) {
            return new StatusResponseObject("Displayname already registered!", 500);
        }

        DBPlayerData pd = getPlayerDataByIdentifier(identifier);

        if (pd == null) {
            return new StatusResponseObject("Error, user not found!", 500);
        }

        try {
            if (isCommand || playerValidIgnoreDisplayName(identifier, password)) {

                try {
                    putPlayerDataInDB(identifier, newDisplayName, pd.passwordHash, pd.salt, pd.uuid, pd.publicKey, pd.skinBytes);

                    return new StatusResponseObject("Successfully changed displayname!", 200);
                } catch (Error e) {
                    OfflineAuth.error("Register error: " + e.getMessage());
                    return new StatusResponseObject("Error while changing displayname!", 500);
                }


            } else {
                return new StatusResponseObject("Identifier or password invalid", 500);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Error while changing displayname", 500);
            //e.printStackTrace();
        }
    }

    public static StatusResponseObject setPlayerUUID(String identifier, String password, String uuid, boolean isCommand) {
        if (identifier == null || password == null || uuid == null) {
            return new StatusResponseObject("Failed, identifier or password, or UUID null", 500);
        }

        if (!Util.uuidValid(uuid)) {
            return new StatusResponseObject("Failed, invalid UUID", 500);
        }

        try {
            if (isCommand || playerValidIgnoreDisplayName(identifier, password)) {
                DBPlayerData pd = getPlayerDataByIdentifier(identifier);
                if (pd == null) {
                    return new StatusResponseObject("Error! Player Data not found!", 500);
                }

                if (putPlayerDataInDB(pd.identifier, pd.displayname, pd.passwordHash, pd.salt, uuid, pd.publicKey, pd.skinBytes)) {
                    return new StatusResponseObject("Successfully updated UUID", 200);
                } else {
                    return new StatusResponseObject("Failed to change UUID", 500);
                }
            } else {
                return new StatusResponseObject("Identifier or password invalid", 500);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Error while setting UUID", 500);
        }
    }

    public static StatusResponseObject changePlayerSkin(String identifier, String password, byte[] skinBytes, boolean force) {
        if (force && identifier == null || (password == null && !force) || (password.equals("") && !force) || skinBytes == null || skinBytes.length == 1) {
            return new StatusResponseObject("Failed, identifier or password, or skin null", 500);
        }

        try {
            if (force || playerValidIgnoreDisplayName(identifier, password)) {
                DBPlayerData pd = getPlayerDataByIdentifier(identifier);
                if (pd == null) {
                    return new StatusResponseObject("User not found", 500);
                }
                putPlayerDataInDB(pd.identifier, pd.displayname, pd.passwordHash, pd.salt, pd.uuid, pd.publicKey, skinBytes);
                return new StatusResponseObject("Successfully uploaded skin", 200);
            } else {
                return new StatusResponseObject("Identifier or password invalid", 500);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Error while uploading skin", 500);
            //e.printStackTrace();
        }
    }

    public static StatusResponseObject deletePlayerSkin(String identifier, String password, boolean force) {
        if (identifier == null || (password == null && !force)) {
            return new StatusResponseObject("Failed, identifier or password null", 500);
        }

        try {
            if (force || playerValidIgnoreDisplayName(identifier, password)) {
                DBPlayerData pd = getPlayerDataByIdentifier(identifier);
                if (pd == null) {
                    return new StatusResponseObject("User not found", 500);
                }
                putPlayerDataInDB(pd.identifier, pd.displayname, pd.passwordHash, pd.salt, pd.uuid, pd.publicKey, new byte[1]);
                return new StatusResponseObject("Successfully deleted skin", 200);
            } else {
                return new StatusResponseObject("Identifier or password invalid", 500);
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Error while deleting skin", 500);
            //e.printStackTrace();
        }
    }

    public static StatusResponseObject deleteUserData(String identifier) {
        if (!isUserRegisteredByIdentifier(identifier)) {
            return new StatusResponseObject("User not registered!", 500);
        }
        try {
            DBPlayerData pd = getPlayerDataByIdentifier(identifier);
            OfflineAuth.varInstanceServer.levelDBStore.delete(bytes("ID:" + identifier));
            if (pd != null) {
                ServerUtil.kickPlayerByName(pd.identifier, Config.accountDeletionKickMessage);
            }
            return new StatusResponseObject("Successfully deleted user!", 200);
        } catch (Error e) {
            OfflineAuth.error(e.getMessage());
            return new StatusResponseObject("Failed to delete user!", 500);
        }
    }

    public static StatusResponseObject setRestPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if  (password == null || password.length() < 1) {
            return new StatusResponseObject("Invalid password!", 500);
        }

        if (password.contains(",")) {
            return new StatusResponseObject("Password cannot contain commas!", 500);
        }

        String salt = Util.genSalt();
        String passwordHash = Util.getPasswordHash(password, salt);

        String data = passwordHash + "," + salt;

        try {
            OfflineAuth.varInstanceServer.levelDBStore.put(bytes("restpassword"), bytes("data:" + data));
            return new StatusResponseObject("Successfully changed rest password!", 200);
        } catch (Error e) {
            OfflineAuth.error("Rest password set error: " + e.getMessage());
            return new StatusResponseObject("Error while changing rest password!", 500);
        }
    }

    public static StatusResponseObject delRestPassword() {
        try {
            OfflineAuth.varInstanceServer.levelDBStore.delete(bytes("restpassword"));
            return new StatusResponseObject("Successfully deleted rest password!", 200);
        } catch (Error e) {
            OfflineAuth.error("Rest password delete error: " + e.getMessage());
            return new StatusResponseObject("Error while deleting rest password!", 500);
        }
    }

    public static boolean putPlayerDataInDB(String identifier, String displayname, String passwordHash, String passwordSalt, String uuid, String publicKey, byte[] skinBytes) {
        String sep = ",";
        String dataStr = identifier + sep + displayname + sep + passwordHash + sep + passwordSalt + sep + uuid + sep + publicKey + sep;
        byte[] dataBytes = dataStr.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytesLenAsByteArray = Util.intToByteArray(dataBytes.length);
        byte[] finalData;
        try {
            byte[] lenPlusData = Util.concatByteArrays(dataBytesLenAsByteArray, dataBytes);
            finalData = Util.concatByteArrays(lenPlusData, skinBytes);
        } catch (IOException e) {
            OfflineAuth.error("Error writing paler data");
            return false;
        }

        OfflineAuth.varInstanceServer.levelDBStore.put(bytes("ID:" + identifier), finalData);
        return true;
    }

    public static DBPlayerData getPlayerDataByIdentifier(String identifier){
        try {
            if(isUserRegisteredByIdentifier(identifier)){  // Gets data from db and removes "data:" prefix from it
                // entry structure: 4 bytes telling how much data there is, the general data (entry1,entry2,entry3) in base64, and the skin image in bytes
                byte[] allBytes = OfflineAuth.varInstanceServer.levelDBStore.get(bytes("ID:" + identifier));
                int dataLen = Util.fourFirstBytesToInt(allBytes);
                byte[] data = new byte[dataLen];
                for (int i = 0; i < dataLen; i ++) {
                    data[i] = allBytes[i + 4];
                }
                byte[] skinBytes = new byte[allBytes.length - (dataLen + 4)];
                for (int i = dataLen + 4; i < allBytes.length; i ++) {
                    skinBytes[i - (dataLen + 4)] = allBytes[i];
                }

                String dataStr = new String(data);
                String[] dataSplit = dataStr.split(",", -1); /* https://stackoverflow.com/a/14602089 */
                return new DBPlayerData(dataSplit[0], dataSplit[1], dataSplit[2], dataSplit[3], dataSplit[4], dataSplit[5], skinBytes);
            }
        } catch (Error e) {
            OfflineAuth.error("Error getting data: " + e.getMessage());
        }
        return null;
    }

    public static DBPlayerData getPlayerDataByDisplayName(String displayname) {
        for (String ident : getRegisteredIdentifiers()) {
            DBPlayerData dbpd = getPlayerDataByIdentifier(ident);
            if (dbpd.displayname.equals(displayname)) {
                return dbpd;
            }
        }
        return null;
    }

    public static String playerValid(String identifier, String displayname, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String errorString = "-";

        if (identifier == null || displayname == null || password == null) {
            return errorString;
        }

        if (!isUserRegisteredByIdentifier(identifier)) {
            return errorString;
        }

        DBPlayerData player = getPlayerDataByIdentifier(identifier);
        if (player == null) {
            return errorString;
        }
        if (!player.displayname.equals(displayname)) {
            return errorString;
        }
        String passwordHash = Util.getPasswordHash(password, player.salt);
        if (passwordHash == null) {
            return errorString;
        }
        if (passwordHash.equals(player.passwordHash)) {
            return player.displayname;
        } else {
            return errorString;
        }
    }

    /* WARNING not a complete player check, only checks if a public key is associated to a player */
    public static String playerValidKey(String identifier, String playerPubKey) {
        String errorString = "-";

        if (identifier == null || playerPubKey == null) {
            return errorString;
        }

        if (!isUserRegisteredByIdentifier(identifier)) {
            return errorString;
        }

        DBPlayerData player = getPlayerDataByIdentifier(identifier);
        if (player == null) {
            return errorString;
        }
        if (!player.publicKey.equals(playerPubKey)) {
            return errorString;
        }

        return player.displayname;
    }

    public static boolean playerValidIgnoreDisplayName(String identifier, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (identifier == null || password == null) {
            return false;
        }

        if (!isUserRegisteredByIdentifier(identifier)) {
            return false;
        }

        DBPlayerData player = getPlayerDataByIdentifier(identifier);
        String passwordHash = Util.getPasswordHash(password, player.salt);
        return passwordHash.equals(player.passwordHash);
    }

    public static boolean restPasswordValid(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (password == null) {
            return false;
        }

        if (OfflineAuth.varInstanceServer.levelDBStore.get(bytes("restpassword")) == null) {
            return false;
        }

        String restPasswordData = new String(OfflineAuth.varInstanceServer.levelDBStore.get(bytes("restpassword"))).substring(5);
        String[] splitData = restPasswordData.split(",");
        String storedPasswordHash = splitData[0];
        String salt = splitData[1];
        String passwordHash = Util.getPasswordHash(password, salt);
        return passwordHash.equals(storedPasswordHash);
    }

    public static boolean isUserRegisteredByIdentifier(String identifier) {
        try {
            return OfflineAuth.varInstanceServer.levelDBStore.get(bytes("ID:" + identifier)) != null;
        } catch (DBException e) {
            OfflineAuth.error(e.getMessage());
        }
        return false;
    }

    public static boolean isUserRegisteredByDisplayname(String displayname) {
        return Arrays.asList(getRegisteredDisplaynames()).contains(displayname);
    }

    public static ArrayList<String> getTokenList() throws IOException {
        File f = new File(OfflineAuth.varInstanceServer.tokenListPath);
        if (!f.exists()) {
            return new ArrayList<>();
        }

        /* https://www.baeldung.com/java-file-to-arraylist */
        FileReader fr = new FileReader(f);
        StringBuffer sb = new StringBuffer();
        ArrayList<String> result = new ArrayList<>();
        while (fr.ready()) {
            char c = (char) fr.read();
            if (c == '\n') {
                result.add(sb.toString());
                sb = new StringBuffer();
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            result.add(sb.toString());
        }

        ArrayList<String> trimmed = new ArrayList<>();

        for (String s: result) {
            trimmed.add(s.trim());
        }

        return trimmed;
    }

    public static boolean tokenIsValid(String token) throws IOException {
        File f = new File(OfflineAuth.varInstanceServer.tokenListPath);
        if (!f.exists()) {
            return false;
        }
        ArrayList<String> tokens = getTokenList();
        return tokens.contains(token);
    }

    public static String createtoken() throws IOException {
        String token = Util.randomAlphanum();
        File f = new File(OfflineAuth.varInstanceServer.tokenListPath);
        if (!f.exists()) {
            f.createNewFile();
        }
        Files.write(Paths.get(OfflineAuth.varInstanceServer.tokenListPath), (token + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
        return token;
    }

    public static void consoomToken(String token) throws IOException {
        File f = new File(OfflineAuth.varInstanceServer.tokenListPath);
        if (!f.exists()) {
            OfflineAuth.error("Error consooming token, file does not exist");
            return;
        }
        ArrayList<String> tokens = getTokenList();
        if (!tokens.contains(token)) {
            OfflineAuth.error("Error consooming token, token does not exist");
            return;
        }
        tokens.remove(token);
        FileWriter writer = new FileWriter(f);
        for (String t : tokens) {
            writer.write(t + System.lineSeparator());
        }
        writer.close();
    }

    public static String[] getRegisteredIdentifiers() {
        ArrayList<String> res = new ArrayList<>();
        DBIterator dbIterator = OfflineAuth.varInstanceServer.levelDBStore.iterator();
        for (dbIterator.seekToFirst(); dbIterator.hasNext(); dbIterator.next()) {
            String key = new String(dbIterator.peekNext().getKey());
            if (key.startsWith("ID")) {
                DBPlayerData dbpd = getPlayerDataByIdentifier(key.substring(3));
                res.add(dbpd.identifier);
            }
        }
        return res.toArray(new String[res.size()]);
    }

    public static String[] getRegisteredDisplaynames() {
        ArrayList<String> res = new ArrayList<>();
        DBIterator dbIterator = OfflineAuth.varInstanceServer.levelDBStore.iterator();
        for (dbIterator.seekToFirst(); dbIterator.hasNext(); dbIterator.next()) {
            String key = new String(dbIterator.peekNext().getKey());
            if (key.startsWith("ID")) {
                DBPlayerData dbpd = getPlayerDataByIdentifier(key.substring(3));
                res.add(dbpd.displayname);
            }
        }
        return res.toArray(new String[res.size()]);
    }
}
