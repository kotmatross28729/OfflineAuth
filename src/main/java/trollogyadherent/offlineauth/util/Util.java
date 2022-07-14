package trollogyadherent.offlineauth.util;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Session;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.rest.OAServerData;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;


public class Util {
    private final static Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");


    public static boolean isServer() {
        return FMLCommonHandler.instance().getSide() == Side.SERVER;
    }

    public static String offlineUUID(String username) {
        /*String allowed = "abcdefghijklmnopqrstuvwxyz-0123456789";
        for (int i = 0; i < username.length(); i ++) {
            if (!allowed.contains(String.valueOf(username.charAt(i)))) {

            }
        }*/
        return String.valueOf(UUID.nameUUIDFromBytes(("OfflinePlayer:" + username.toLowerCase()).getBytes(Charsets.UTF_8)));
    }

    public static String genSalt() {
        Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }

    public static String getPasswordHash(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (salt.length() == 0) {
            return null;
        }
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        String res = "";
        for (int i = 0; i < hash.length; i ++) {
            res += hash[i];
        }
        return res;
    }

    public static boolean validUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 16) {
            return false;
        }
        String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        for (int i = 0; i < username.length(); i ++) {
            boolean found = false;
            for (int j = 0; j < allowedChars.length(); j ++) {
                if (username.charAt(i) == allowedChars.charAt(j)) {
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public static OAServerData getOAServerDatabyIP(String ip, String port) {
        for (OAServerData oasd : OfflineAuth.varInstanceClient.OAserverDataCache) {
            if (oasd.getIp().equals(ip) && oasd.getPort().equals(port)) {
                return  oasd;
            }
        }
        return null;
    }

    static class Sessionutil {
        /**
         * as the Session field in Minecraft.class is static final we have to
         * access it via reflection
         */
        private static Field sessionField = ReflectionHelper.findField(Minecraft.class, "session", "S", "field_71449_j");

        static Session get() throws IllegalArgumentException, IllegalAccessException {
            return Minecraft.getMinecraft().getSession();
        }

        static void set(Session s) throws IllegalArgumentException, IllegalAccessException {
            Sessionutil.sessionField.set(Minecraft.getMinecraft(), s);
        }
    }

    public static void offlineMode(String username) throws IllegalArgumentException, IllegalAccessException {
        if (username == null) {
            OfflineAuth.error("Cannot set offline Username! Username null!");
            return;
        }
        /* Create offline uuid */
        String uuid = offlineUUID(username);
        Sessionutil.set(new Session(username, uuid, null, "legacy"));
        OfflineAuth.info("Offline Username set to " + username);
    }

    /* The serverIP field actually contains both ip and port, this function gets only the ip */
    public static String getIP(ServerData serverData) {
        return serverData.serverIP.split(":")[0];
    }

    /* Basically same as above but for a string of type ip:port */
    public static String getIP(String ipport) {
        return ipport.split(":")[0];
    }

    public static String getPort(ServerData serverData) {
        String[] spl = serverData.serverIP.split(":");
        if (spl.length > 1) {
            return spl[1];
        } else {
            return "";
        }
    }

    /* Loads image from File to String, encoded inh base64 */
    public static String fileToBs64(File file) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(file);
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }

    public static void bs64SaveToFile(String bs64, File file) throws IOException {
        byte[] bytes = java.util.Base64.getDecoder().decode(bs64);
        FileUtils.writeByteArrayToFile(file, bytes);
    }

    public static byte[] fileToBytes(File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }

    public static void bytesSaveToFile(byte[] bytes, File file) throws IOException {
        FileUtils.writeByteArrayToFile(file, bytes);
    }

    public static boolean fileExists(File file) {
        return file.exists();
    }

    public static String fileToByteString(File file) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(file);
        String res = "";
        for (byte b : bytes) {
            res += String.valueOf(b);
        }
        return res;
    }

    public static void byteStringToFile(String byteString) {

    }

    public static byte[] concatByteArrays(byte[] a, byte[] b) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write( a );
        outputStream.write( b );

        return outputStream.toByteArray( );
    }

    public static byte[] fillByteArrayLeading(byte[] a, int totalLen) throws IOException {
        if (a.length >= totalLen) {
            return a;
        }
        byte[] b = new byte[totalLen - a.length];
        return concatByteArrays(b, a);
    }

    public static String fileHash(File file) throws NoSuchAlgorithmException {
        if (file == null || !file.exists()) {
            return null;
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream is = Files.newInputStream(Paths.get(file.getPath()));
             DigestInputStream dis = new DigestInputStream(is, md))
        {
            /* Read decorated stream (dis) to EOF as normal... */
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest();

        String res = "";
        for (byte b : digest) {
            res += String.valueOf(b);
        }

        return res;
    }

    public static boolean isOp(EntityPlayerMP entityPlayerMP) {
        // func_152596_g: canSendCommands
        return FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152596_g(entityPlayerMP.getGameProfile());
    }

    public static String randomAlphanum() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String genUUID() {
        return UUID.randomUUID().toString();
    }

    public static UUID genRealUUID() {
        return UUID.randomUUID();
    }

    /* Source: https://www.code4copy.com/java/validate-uuid-string-java/ */
    public static boolean uuidValid(String uuid) {
        if (uuid == null) {
            return false;
        }
        return UUID_REGEX_PATTERN.matcher(uuid).matches();
    }

    public static OAServerData getCurrentOAServerData() {
        for (OAServerData oasd : OfflineAuth.varInstanceClient.OAserverDataCache) {
            if (getIP(OfflineAuth.varInstanceClient.selectedServerData).equals(oasd.getIp()) && getPort(OfflineAuth.varInstanceClient.selectedServerData).equals(oasd.getPort())) {
                return oasd;
            }
        }
        return null;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
