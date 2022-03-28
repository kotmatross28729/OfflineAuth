package trollogyadherent.offlineauth.varinstances.server;

import org.iq80.leveldb.DB;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.server.ServerSkinRegistry;

import java.io.File;
import java.nio.file.Paths;

public class VarInstanceServer {
    public final String DB_NAME = new File(OfflineAuth.rootPath, "OfflineAuthDatabase").getPath();
    public DB levelDBStore;
    public ServerSkinRegistry skinRegistry = new ServerSkinRegistry();

    public String tokenListPath = new File(OfflineAuth.rootPath, "tokens.json").getPath();
    public String defaultServerSkinsPath = new File(OfflineAuth.rootPath, "DefaultServerSkins").getPath();
    public String serverSkinCachePath = Paths.get(OfflineAuth.rootPath, "ServerCache", "Skins").toString();

    public VarInstanceServer() {
        /* Creating dirs */
        File defaultServerSkinsFile = new File(defaultServerSkinsPath);
        if (!defaultServerSkinsFile.exists()) {
            defaultServerSkinsFile.mkdirs();
        }
        File serverSkinCacheFile = new File(serverSkinCachePath);
        if (!serverSkinCacheFile.exists()) {
            serverSkinCacheFile.mkdirs();
        }
    }
}
