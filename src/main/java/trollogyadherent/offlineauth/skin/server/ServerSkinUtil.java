package trollogyadherent.offlineauth.skin.server;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ServerSkinUtil {
    public static boolean skinCachedOnServer(String name) {
        return (Util.fileExists(new File(OfflineAuth.varInstanceServer.serverSkinCachePath, name + ".png")) || Util.fileExists(new File(OfflineAuth.varInstanceServer.defaultServerSkinsPath, name + ".png")));
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

    public static void transferDefaultSkins() throws IOException {
        for (int i = 0; i < 8; i ++) {
            String imageName = "default" + i + ".png";
            InputStream is = ServerSkinUtil.class.getResourceAsStream("/assets/offlineauth/textures/defaultskins/" + imageName);
            if (is == null) {
                OfflineAuth.error("Default skin resource " + i + " not found!");
                return;
            }
            BufferedImage img = ImageIO.read(is);
            File output = new File(OfflineAuth.varInstanceServer.defaultServerSkinsPath + File.separator + imageName);
            ImageIO.write(img, "png", output);
        }
    }

    public static void saveBytesToSkinCache(byte[] skinBytes, String displayname) {
        try {
            Util.bytesSaveToFile(skinBytes, new File(OfflineAuth.varInstanceServer.serverSkinCachePath + File.separator + displayname + ".png"));
        } catch (IOException e) {
            OfflineAuth.error("Failed to save skin to cache for player " + displayname);
        }
    }

    public static void clearSkinCache() {
        try {
            FileUtils.cleanDirectory(new File(OfflineAuth.varInstanceServer.serverSkinCachePath));
        } catch (IOException e) {
            OfflineAuth.error("Failed to clear client skin cache");
        }
    }

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
                i ++;
            }
            if (i == rand) {
                return Files.getNameWithoutExtension(s);
            }
        }
        return null;
    }
}
