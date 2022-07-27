package trollogyadherent.offlineauth.skin.server;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("UnusedReturnValue")
public class ServerSkinUtil {
    public static boolean skinCachedOnServer(String name) {
        return (Util.fileExists(new File(OfflineAuth.varInstanceServer.serverSkinCachePath, name + ".png")) || Util.fileExists(new File(OfflineAuth.varInstanceServer.defaultServerSkinsPath, name + ".png")));
    }

    public static boolean capeCachedOnServer(String name) {
        return (Util.fileExists(new File(OfflineAuth.varInstanceServer.serverCapeCachePath, name + ".png")) || Util.fileExists(new File(OfflineAuth.varInstanceServer.serverCapeCachePath, name + ".gif")));
    }

    public static File getSkinFile(String name) {
        File temp1 = new File(OfflineAuth.varInstanceServer.defaultServerSkinsPath, name + ".png");
        File temp2 = new File(OfflineAuth.varInstanceServer.serverSkinCachePath, name + ".png");
        if (temp1.exists()) {
            return temp1;
        } else {
            return temp2;
        }
    }

    public static File getCapeFile(String name) {
        File temp1 = new File(OfflineAuth.varInstanceServer.serverCapeCachePath, name + ".png");
        File temp2 = new File(OfflineAuth.varInstanceServer.serverCapeCachePath, name + ".gif");
        if (temp1.exists()) {
            return temp1;
        } else {
            return temp2;
        }
    }

    public static void transferDefaultSkins() throws IOException {
        InputStream is = ServerSkinUtil.class.getResourceAsStream("/assets/offlineauth/textures/defaultskins/server/default.png");
        if (is == null) {
            OfflineAuth.error("Default skin resource not found!");
            return;
        }
        if (!Util.pngIsSane(ServerSkinUtil.class.getResourceAsStream("/assets/offlineauth/textures/defaultskins/server/default.png"))) {
            OfflineAuth.error("Default skin resoruce not sane!");
            return;
        }
        BufferedImage img = ImageIO.read(is);
        File output = new File(OfflineAuth.varInstanceServer.defaultServerSkinsPath + File.separator + "default.png");
        ImageIO.write(img, "png", output);

        /*for (int i = 0; i < 8; i ++) {
            String imageName = "default" + i + ".png";
            InputStream is = ServerSkinUtil.class.getResourceAsStream("/assets/offlineauth/textures/defaultskins/" + imageName);
            if (is == null) {
                OfflineAuth.error("Default skin resource " + i + " not found!");
                return;
            }
            BufferedImage img = ImageIO.read(is);
            File output = new File(OfflineAuth.varInstanceServer.defaultServerSkinsPath + File.separator + imageName);
            ImageIO.write(img, "png", output);
        }*/
    }

    public static void saveBytesToSkinCache(byte[] skinBytes, String displayname) {
        try {
            Util.bytesSaveToFile(skinBytes, new File(OfflineAuth.varInstanceServer.serverSkinCachePath + File.separator + displayname + ".png"));
        } catch (IOException e) {
            OfflineAuth.error("Failed to save skin to cache for player " + displayname);
        }
    }

    public static void saveBytesToCapeCache(byte[] capeBytes, String displayname) {
        try {
            if (!Util.imageIsSane(new ByteArrayInputStream(capeBytes))) {
                return;
            }
            if (Util.imageIsPng(new ByteArrayInputStream(capeBytes))) {
                Util.bytesSaveToFile(capeBytes, new File(OfflineAuth.varInstanceServer.serverCapeCachePath + File.separator + displayname + ".png"));
            } else {
                Util.bytesSaveToFile(capeBytes, new File(OfflineAuth.varInstanceServer.serverCapeCachePath + File.separator + displayname + ".gif"));
            }
        } catch (IOException e) {
            OfflineAuth.error("Failed to save cape to cache for player " + displayname);
        }
    }

    public static void clearSkinAndCapeCache() {
        try {
            FileUtils.cleanDirectory(new File(OfflineAuth.varInstanceServer.serverSkinCachePath));
            FileUtils.cleanDirectory(new File(OfflineAuth.varInstanceServer.serverCapeCachePath));
        } catch (IOException e) {
            OfflineAuth.error("Failed to clear client skin cache");
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static void removeSkinFromCache(String name) {
        File skin = new File(OfflineAuth.varInstanceServer.serverSkinCachePath, name + ".png");
        if (skin.exists()) {
            skin.delete();
        }
    }

    public static void removeCapeFromCache(String name) {
        File cape1 = new File(OfflineAuth.varInstanceServer.serverCapeCachePath, name + ".png");
        File cape2 = new File(OfflineAuth.varInstanceServer.serverCapeCachePath, name + ".gif");
        if (cape1.exists()) {
            cape1.delete();
        }
        if (cape2.exists()) {
            cape2.delete();
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String getRandomDefaultSkinName() {
        File defaultSkinDir = new File(OfflineAuth.varInstanceServer.defaultServerSkinsPath);
        String[] fileList = defaultSkinDir.list();
        if (fileList == null) {
            OfflineAuth.error("Could not get default server skin directory!");
            return null;
        }
        int pngCount = 0;
        for (String s : fileList) {
            if (Files.getFileExtension(s).equals("png")) {
                pngCount ++;
            }
        }
        int rand = Util.getRandomNumber(0, pngCount);
        int i = 0;
        for (String s : fileList) {
            if (Files.getFileExtension(s).equals("png")) {
                if (i == rand) {
                    return Files.getNameWithoutExtension(s);
                }
                i ++;
            }
        }
        return null;
    }
}
