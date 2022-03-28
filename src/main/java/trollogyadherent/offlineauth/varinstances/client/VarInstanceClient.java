package trollogyadherent.offlineauth.varinstances.client;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.texture.TextureManager;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.skin.client.ClientPlayerRegistry;
import trollogyadherent.offlineauth.skin.client.ClientSkinRegistry;

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
    public ClientSkinRegistry skinRegistry = new ClientSkinRegistry();
    public ClientPlayerRegistry playerRegistry = new ClientPlayerRegistry();
    public boolean queriedForSkinName = false;
    public boolean queriedForSkinFile = false;

    public String serverDataJSONpath = new File(OfflineAuth.rootPath, "serverdata.json").getPath();
    public String clientSkinsPath = new File(OfflineAuth.rootPath, "ClientSkins").getPath();
    public String clientSkinCachePath = Paths.get(OfflineAuth.rootPath, "ClientCache", "Skins").toString();

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
    }
}
