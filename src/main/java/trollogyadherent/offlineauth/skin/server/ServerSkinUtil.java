package trollogyadherent.offlineauth.skin.server;

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
}
