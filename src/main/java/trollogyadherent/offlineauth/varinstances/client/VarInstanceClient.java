package trollogyadherent.offlineauth.varinstances.client;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.texture.TextureManager;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.skin.client.ClientPlayerRegistry;
import trollogyadherent.offlineauth.skin.client.ClientSkinRegistry;

import java.io.File;
import java.lang.reflect.Field;
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

    public VarInstanceClient() {
        skinLocationfield.setAccessible(true);
    }
}
