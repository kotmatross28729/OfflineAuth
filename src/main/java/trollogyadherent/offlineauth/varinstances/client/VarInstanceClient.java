package trollogyadherent.offlineauth.varinstances.client;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.texture.TextureManager;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.registry.ClientEntityPlayerRegistry;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.registry.ClientPlayerRegistry;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;

public class VarInstanceClient {
    public ServerData selectedServerData;
    public File datafile;
    public ArrayList<OAServerData> OAserverDataCache;
    public TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    public Field skinLocationfield = ReflectionHelper.findField(net.minecraft.client.entity.AbstractClientPlayer.class, "locationSkin", "field_110312_d");
    //public ClientPlayerRegistry skinRegistry = new ClientPlayerRegistry();
    public ClientPlayerRegistry playerRegistry = new ClientPlayerRegistry();
    public ClientEntityPlayerRegistry entityPlayerRegistry = new ClientEntityPlayerRegistry();
    public boolean queriedForPlayerData = false;
    public boolean queriedForSkinFile = false;
    public Thread serverStatusVibecheckThread = null;

    public String serverDataJSONpath = new File(OfflineAuth.rootPath, "serverdata.json").getPath();
    public String clientSkinsPath = new File(OfflineAuth.rootPath, "ClientSkins").getPath();
    public String clientSkinCachePath = Paths.get(OfflineAuth.rootPath, "ClientCache", "Skins").toString();
    public String keyPairPath = new File(OfflineAuth.rootPath, "ClientKeys").getPath();
    public String keyCachePath = new File(OfflineAuth.rootPath, "ClientKeyCache").getPath();
    public int selectedServerIndex = -1;

    public VarInstanceClient() {
        skinLocationfield.setAccessible(true);

        /* Creating dirs */
        File clientSkinsFile = new File(clientSkinsPath);
        if (!clientSkinsFile.exists()) {
            clientSkinsFile.mkdirs();
        }
        File clientSkinCacheFile = new File(clientSkinCachePath);
        if (!clientSkinCacheFile.exists()) {
            clientSkinCacheFile.mkdirs();
        }

        File keyPairFile = new File(keyPairPath);
        if (!keyPairFile.exists()) {
            keyPairFile.mkdirs();
        }
    }
}
