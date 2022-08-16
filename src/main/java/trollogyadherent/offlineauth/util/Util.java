package trollogyadherent.offlineauth.util;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import de.matthiasmann.twl.utils.PNGDecoder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Session;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;


public class Util {
    private final static Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");


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
        if (salt.length() == 0) {
            return null;
        }
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        String res = "";
        for (int i = 0; i < hash.length; i++) {
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

        static Session get() throws IllegalArgumentException {
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
        if (serverData == null) {
            return null;
        }
        return serverData.serverIP.split(":")[0];
    }

    /* Basically same as above but for a string of type ip:port */
    public static String getIP(String ipport) {
        return ipport.split(":")[0];
    }

    public static String getPort(ServerData serverData) {
        if (serverData == null) {
            return "";
        }
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

    public static void inputStreamSaveToFile(InputStream is, File file) throws IOException {
        FileUtils.copyInputStreamToFile(is, file);
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

    public static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public static byte[] fillByteArrayLeading(byte[] a, int totalLen) throws IOException {
        if (a.length >= totalLen) {
            return a;
        }
        byte[] b = new byte[totalLen - a.length];
        return concatByteArrays(b, a);
    }

    public static int fourFirstBytesToInt(byte[] array) {
        if (array.length < 4) {
            return -1;
        }
        byte[] temp = new byte[4];
        for (int i = 0; i < 4; i ++) {
            temp[i] = array[i];
        }
        return ByteBuffer.wrap(temp).getInt();
    }

    public static String fileHash(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            OfflineAuth.error("Failed to calculate hash of " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
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

    // https://gist.github.com/zeroleaf/6809843
    /**
     * Generate a file 's sha1 hash code.
     * @param file file
     * @return sha1 hash code of this file
     */
    public static String sha1Code(File file) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            OfflineAuth.error("Failed to calculate sha1 code of " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            OfflineAuth.error("Failed to calculate sha1 code of " + file.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
        DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
        byte[] bytes = new byte[1024];
        // read all file content
        while (true) {
            try {
                if (!(digestInputStream.read(bytes) > 0)) break;
            } catch (IOException e) {
                OfflineAuth.error("Failed to calculate sha1 code of " + file.getAbsolutePath());
                e.printStackTrace();
                return null;
            }
            ;
        }

//        digest = digestInputStream.getMessageDigest();
        byte[] resultByteArry = digest.digest();
        return bytesToHexString(resultByteArry);
    }

    /**
     * Convert a array of byte to hex String. <br/>
     * Each byte is covert a two character of hex String. That is <br/>
     * if byte of int is less than 16, then the hex String will append <br/>
     * a character of '0'.
     *
     * @param bytes array of byte
     * @return hex String represent the array of byte
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int value = b & 0xFF;
            if (value < 16) {
                // if value less than 16, then it's hex String will be only
                // one character, so we need to append a character of '0'
                sb.append("0");
            }
            sb.append(Integer.toHexString(value).toUpperCase());
        }
        return sb.toString();
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
        if (OfflineAuth.varInstanceClient.selectedServerData == null) {
            return null;
        }
        for (OAServerData oasd : OfflineAuth.varInstanceClient.OAserverDataCache) {
            if (getIP(OfflineAuth.varInstanceClient.selectedServerData).equals(oasd.getIp()) && getPort(OfflineAuth.varInstanceClient.selectedServerData).equals(oasd.getPort())) {
                return oasd;
            }
        }
        return null;
    }

    /* https://www.baeldung.com/java-generating-random-numbers-in-range */
    /* Upper bound is exclusive */
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static enum Color {
        GREY,
        GREEN,
        RED
    }

    public static String colorCode(Color color) {
        Map<Color, String> colorMap = new HashMap<Color, String>() {{
            put(Color.GREY, "7");
            put(Color.GREEN, "a");
            put(Color.RED, "4");
        }};
        return (char) 167 + colorMap.get(color);
    }

    /* https://www.w3resource.com/java-exercises/io/java-io-exercise-9.php */
    public static double filesizeInMegaBytes(File file) {
        return (double) file.length()/(1024*1024);
    }

    public static double filesizeInKiloBytes(File file) {
        return (double) file.length()/1024;
    }

    public static double filesizeInBytes(File file) {
        return (double) file.length();
    }

    /* Actually doesn't clone lol */
    public static InputStream cloneInputStream(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while (true) {
            try {
                if (!((len = is.read(buffer)) > -1)) break;
            } catch (IOException e) {
                return null;
            }
            baos.write(buffer, 0, len);
        }
        try {
            baos.flush();
        } catch (IOException e) {
            return null;
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static boolean pngIsSane(File imageFile) {
        try {
            PNGDecoder pngDecoder = new PNGDecoder(Files.newInputStream(imageFile.toPath()));
            if (pngDecoder.getWidth() > OfflineAuth.maxPngDimension || pngDecoder.getHeight() > OfflineAuth.maxPngDimension) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean pngIsSane(byte[] bytes) {
        try {
            PNGDecoder pngDecoder = new PNGDecoder(new ByteArrayInputStream(bytes));
            if (pngDecoder.getWidth() > OfflineAuth.maxPngDimension || pngDecoder.getHeight() > OfflineAuth.maxPngDimension) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean pngIsSane(InputStream is) {
        try {
            PNGDecoder pngDecoder = new PNGDecoder(is);
            if (pngDecoder.getWidth() > OfflineAuth.maxPngDimension || pngDecoder.getHeight() > OfflineAuth.maxPngDimension) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean imageIsSane(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while (true) {
            try {
                if (!((len = is.read(buffer)) > -1)) break;
            } catch (IOException e) {
                return false;
            }
            baos.write(buffer, 0, len);
        }
        try {
            baos.flush();
        } catch (IOException e) {
            return false;
        }

        int sanityFlag = 0;
        BufferedImage bi = getFirstGifFrame(new ByteArrayInputStream(baos.toByteArray()));
        if (bi == null) {
            sanityFlag = 1;
        }
        return pngIsSane(new ByteArrayInputStream(baos.toByteArray())) || sanityFlag == 0;
    }

    public static boolean imageIsPng(InputStream is) {
        try {
            new PNGDecoder(is);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static BufferedImage getFirstGifFrame(InputStream is) {
        GifDecoder gifDecoder = new GifDecoder();
        try {
            gifDecoder.read(is);
        } catch (IOException e) {
            OfflineAuth.error("Failed to read gif (possibly not sane)");
            return null;
        }
        int n = gifDecoder.getFrameCount();
        if (n == 0) {
            return null;
        }
        return gifDecoder.getFrame(0);
    }
}
