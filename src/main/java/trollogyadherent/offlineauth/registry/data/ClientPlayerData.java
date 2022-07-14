package trollogyadherent.offlineauth.registry.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.skin.client.LegacyConversion;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ClientPlayerData {
    public String identifier;
    public String displayname;
    public String uuid;
    public String skinName;
    public ResourceLocation resourceLocation;
    public BufferedImage bufferedImage;
    public ClientSkinUtil.OfflineTextureObject offlineTextureObject;
    public AbstractClientPlayer entityPlayer;

    public ClientPlayerData(String identifier, String displayname, String uuid, String skinName) throws IOException {
        this.skinName = skinName;
        this.identifier = identifier;
        this.displayname = displayname;
        this.uuid = uuid;

        if (skinName == null || identifier == null || displayname == null || uuid == null) {
            return;
        }
        File imagefile = new File(OfflineAuth.varInstanceClient.clientSkinCachePath, skinName + ".png");
        if (!imagefile.exists()) {
            this.skinName = null;
            OfflineAuth.error("Error skin image does not exist: " + skinName);
            return;
        }

        EntityClientPlayerMP clientPlayer = Minecraft.getMinecraft().thePlayer;
        if (clientPlayer.getDisplayName().equals(displayname)) {
            this.entityPlayer = clientPlayer;
        } else {
            if (OfflineAuth.varInstanceClient.entityPlayerRegistry.getPlayerEntityByDisplayName(displayname) == null) {
                System.out.println("Player entity not found!");
                return;
            }
            this.entityPlayer = (AbstractClientPlayer)OfflineAuth.varInstanceClient.entityPlayerRegistry.getPlayerEntityByDisplayName(displayname);
        }


        this.bufferedImage = ImageIO.read(imagefile);
        if (this.bufferedImage.getHeight() == 64) {
            this.bufferedImage = new LegacyConversion().convert(this.bufferedImage);
        }
        this.offlineTextureObject = new ClientSkinUtil.OfflineTextureObject(this.bufferedImage);
        this.resourceLocation = new ResourceLocation("offlineauth", "skins/" + uuid);


        try {
            loadSkin();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void loadSkin() throws IllegalAccessException {
        ClientSkinUtil.loadTexture(this.bufferedImage, this.resourceLocation, this.offlineTextureObject);
        OfflineAuth.varInstanceClient.skinLocationfield.set(this.entityPlayer, this.resourceLocation);
    }

    @Override
    public String toString() {
        return this.entityPlayer.getDisplayName() + ":" + Util.offlineUUID(this.entityPlayer.getDisplayName());
    }
}
