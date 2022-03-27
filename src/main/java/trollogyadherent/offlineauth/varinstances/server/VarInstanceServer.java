package trollogyadherent.offlineauth.varinstances.server;

import org.iq80.leveldb.DB;
import trollogyadherent.offlineauth.skin.server.ServerSkinRegistry;

public class VarInstanceServer {
    public final String DB_NAME = "offlineauth/OfflineAuthDatabase";
    public DB levelDBStore;
    public ServerSkinRegistry skinRegistry = new ServerSkinRegistry();
}
