package trollogyadherent.offlineauth.skin.server;

import trollogyadherent.offlineauth.util.Util;

import java.io.File;

public class ServerSkinUtil {
    public static boolean skinCachedOnServer(String name) {
        return (Util.fileExists(new File("offlineauth/ServerCache/Skins", name + ".png")) || Util.fileExists(new File("offlineauth/DefaultServerSkins", name + ".png")));
    }

    public static File getSkinFile(String name) {
        File temp1 = new File("offlineauth/DefaultServerSkins", name + ".png");
        File temp2 = new File("offlineauth/ServerCache/Skins", name + ".png");
        if (temp1.exists()) {
            return temp1;
        } else {
            return temp2;
        }
    }
}
