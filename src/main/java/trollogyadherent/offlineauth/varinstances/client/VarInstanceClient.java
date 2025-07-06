package trollogyadherent.offlineauth.varinstances.client;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.Tags;
import trollogyadherent.offlineauth.gui.skin.SkinGuiRenderTicker;
import trollogyadherent.offlineauth.gui.skin.cape.CapeObject;
import trollogyadherent.offlineauth.registry.newreg.ClientRegistry;
import trollogyadherent.offlineauth.rest.OAServerData;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class VarInstanceClient {
    public ServerData selectedServerData;
    public File datafile;
    public ArrayList<OAServerData> OAServerDataCache;
    private TextureManager textureManager;

    //public ClientPlayerRegistry skinRegistry = new ClientPlayerRegistry();
    //public ClientPlayerRegistry playerRegistry = new ClientPlayerRegistry();
    //public ClientEntityPlayerRegistry entityPlayerRegistry = new ClientEntityPlayerRegistry();
    public ClientRegistry clientRegistry = new ClientRegistry();
    public Thread serverStatusVibecheckThread = null;
    public boolean checkingForKey = false;
    public boolean prevWasKeyDialog = false;

    public String serverDataJSONpath = new File(OfflineAuth.rootPath, "serverdata.json").getPath();
    public String clientSkinsPath = new File(OfflineAuth.rootPath, "ClientSkins").getPath();
    public String clientCapesPath = new File(OfflineAuth.rootPath, "ClientCapes").getPath();
    public String clientSkinCachePath = Paths.get(OfflineAuth.rootPath, "ClientCache", "Skins").toString();
    public String clientCapeCachePath = Paths.get(OfflineAuth.rootPath, "ClientCache", "Capes").toString();
    public String keyPairPath = new File(OfflineAuth.rootPath, "ClientKeys").getPath();
    public String keyCachePath = new File(OfflineAuth.rootPath, "ClientKeyCache").getPath();
    public int selectedServerIndex = -1;

    /* Initialized in post init, client proxy, because it needs mods to load their items first (3d player model holding items) */
    public SkinGuiRenderTicker skinGuiRenderTicker;
    public String lastUsedOfflineSkinFile = Paths.get(OfflineAuth.rootPath, "lastskin").toString();
    public String lastUsedOfflineCapeFile = Paths.get(OfflineAuth.rootPath, "lastcape").toString();
    public boolean offlineSkinAndCapeLoaded = false;
    public CapeObject singlePlayerCapeObject = null;
    public ResourceLocation singlePlayerSkinResourceLocation = null;
    public String displayNameBeforeServerJoin = null;
    public ResourceLocation questionMarkResourceLocation = new ResourceLocation(Tags.MODID, "textures/gui/questionMark.png");
    public ResourceLocation questionMarkResourceLocation64 = new ResourceLocation(Tags.MODID, "textures/gui/questionMark64.png");
    public ResourceLocation DEFAULT_SKIN_64 = new ResourceLocation(Tags.MODID, "textures/defaultskins/vanilla_override/steve_64.png");
    
    /* Reflection fields */
    public Field skinLocationField = ReflectionHelper.findField(net.minecraft.client.entity.AbstractClientPlayer.class, "locationSkin", "field_110312_d");
    public Field capeLocationField = ReflectionHelper.findField(net.minecraft.client.entity.AbstractClientPlayer.class, "locationCape", "field_110313_e");

    public VarInstanceClient() {
        skinLocationField.setAccessible(true);
        capeLocationField.setAccessible(true);
        
        /* Creating dirs */
        File clientSkinsFile = new File(clientSkinsPath);
        if (!clientSkinsFile.exists()) {
            clientSkinsFile.mkdirs();
        }
        File clientSkinCacheFile = new File(clientSkinCachePath);
        if (!clientSkinCacheFile.exists()) {
            clientSkinCacheFile.mkdirs();
        }

        File clientCapesFile = new File(clientCapesPath);
        if (!clientCapesFile.exists()) {
            clientCapesFile.mkdirs();
        }
        File clientCapeCacheFile = new File(clientCapeCachePath);
        if (!clientCapeCacheFile.exists()) {
            clientCapeCacheFile.mkdirs();
        }

        File keyPairFile = new File(keyPairPath);
        if (!keyPairFile.exists()) {
            keyPairFile.mkdirs();
        }
    }

    public TextureManager getTextureManager() {
        if (this.textureManager == null) {
            this.textureManager = Minecraft.getMinecraft().getTextureManager();
        }
        return this.textureManager;
    }
}
