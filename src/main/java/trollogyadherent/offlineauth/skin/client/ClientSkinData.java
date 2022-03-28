package trollogyadherent.offlineauth.skin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ClientSkinData {
    public String skinName;
    public String uuid;
    public ResourceLocation resourceLocation;
    public BufferedImage bufferedImage;
    public ClientSkinUtil.OfflineTextureObject offlineTextureObject;
    //public EntityClientPlayerMP entityPlayer;
    public AbstractClientPlayer entityPlayer;

    public ClientSkinData(String uuid, String skinName) throws IOException {
        this.skinName = skinName;
        this.uuid = uuid;
        if (skinName == null) {
            return;
        }
        File imagefile = new File(OfflineAuth.varInstanceClient.clientSkinCachePath, skinName + ".png");
        if (!imagefile.exists()) {
            this.skinName = null;
            OfflineAuth.error("Error skin image does not exist: " + skinName);
            return;
        }

        /* TO CHANGE!!!!! */
        EntityClientPlayerMP clientPlayer = Minecraft.getMinecraft().thePlayer;
        if (Util.offlineUUID(clientPlayer.getDisplayName()).equals(uuid)) {
            this.entityPlayer = clientPlayer;
        } else {
            ClientPlayerData clientPlayerData = OfflineAuth.varInstanceClient.playerRegistry.getByUUID(uuid);
            if (clientPlayerData == null) {
                System.out.println("clientPlayerData null! returning");
                return;
            }
            this.entityPlayer = clientPlayerData.entityPlayer;
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
        return this.skinName + ":" + this.uuid;
    }
}
