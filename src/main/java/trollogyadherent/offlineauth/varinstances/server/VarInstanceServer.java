package trollogyadherent.offlineauth.varinstances.server;

import cpw.mods.fml.relauncher.ReflectionHelper;
import org.iq80.leveldb.DB;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.registry.ServerKeyRegistry;
import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;
import trollogyadherent.offlineauth.registry.ServerPlayerRegistry;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;

public class VarInstanceServer {
    public final String DB_NAME = new File(OfflineAuth.rootPath, "OfflineAuthDatabase").getPath();
    public DB levelDBStore;
    public ServerPlayerRegistry playerRegistry = new ServerPlayerRegistry();

    public String tokenListPath = new File(OfflineAuth.rootPath, "tokens.txt").getPath();
    public String defaultServerSkinsPath = new File(OfflineAuth.rootPath, "DefaultServerSkins").getPath();
    public String serverSkinCachePath = Paths.get(OfflineAuth.rootPath, "ServerCache", "Skins").toString();
    public String serverCapeCachePath = Paths.get(OfflineAuth.rootPath, "ServerCache", "Capes").toString();
    public Field uuidIdField = ReflectionHelper.findField(com.mojang.authlib.GameProfile.class, "id", "field_111170_d");
    public Field uuidIdField2 = ReflectionHelper.findField(net.minecraft.entity.Entity.class, "entityUniqueID", "field_96093_i");
    public String keyPairPath = new File(OfflineAuth.rootPath, "ServerKeys").getPath();
    public ServerKeyRegistry keyRegistry = new ServerKeyRegistry();
    public ServerKeyTokenRegistry keyTokenRegistry = new ServerKeyTokenRegistry();

    public ArrayList<String> authenticatedDisplaynames = new ArrayList<>();

    public boolean changedUUID = false;

    public boolean DEBUGtamperWithUUID = false;

    public VarInstanceServer() {
        uuidIdField.setAccessible(true);
        uuidIdField2.setAccessible(true);

        /* Creating dirs */
        File defaultServerSkinsFile = new File(defaultServerSkinsPath);
        if (!defaultServerSkinsFile.exists()) {
            defaultServerSkinsFile.mkdirs();
        }
        File serverSkinCacheFile = new File(serverSkinCachePath);
        if (!serverSkinCacheFile.exists()) {
            serverSkinCacheFile.mkdirs();
        }
        File serverCapeCacheFile = new File(serverCapeCachePath);
        if (!serverCapeCacheFile.exists()) {
            serverCapeCacheFile.mkdirs();
        }
    }
}
