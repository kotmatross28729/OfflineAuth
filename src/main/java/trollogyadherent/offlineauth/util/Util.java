package trollogyadherent.offlineauth.util;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Session;
import org.apache.commons.codec.binary.Base64;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.Secure;
import trollogyadherent.offlineauth.rest.OAServerData;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.UUID;

public class Util {
    public static boolean isServer() {
        return FMLCommonHandler.instance().getSide() == Side.SERVER;
    }

    public static String offlineUUID(String username) {
        return String.valueOf(UUID.nameUUIDFromBytes(("OfflinePlayer:" + username.toLowerCase()).getBytes(Charsets.UTF_8)));
    }

    public static String genSalt() {
        Random r = new SecureRandom();
        byte[] salt = new byte[32];
        r.nextBytes(salt);
        return Base64.encodeBase64String(salt);
    }

    public static String getPasswordHash(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
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
        if (username.length() < 3 || username.length() > 16) {
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
        for (OAServerData oasd : OfflineAuth.OAserverDataCache) {
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
        /* Create offline uuid */
        String uuid = offlineUUID(username);
        Sessionutil.set(new Session(username, uuid, null, "legacy"));
        OfflineAuth.info("Offline Username set!");
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
}
