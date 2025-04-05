package trollogyadherent.offlineauth.mixin.early.serverutilities;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import serverutils.lib.client.ClientUtils;
import serverutils.lib.icon.PlayerHeadIcon;
import serverutils.lib.util.StringUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.packets.QuerySkinNameFromServerPacket;
import trollogyadherent.offlineauth.registry.newreg.ClientRegistry;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Mixin(value = PlayerHeadIcon.class, priority = 999)
public class MixinPlayerHeadIcon {
	
	@Shadow(remap = false)
	@Final
	public UUID uuid;
	
	/**
	 * @author kotmatross
	 * @reason compat
	 */
	@Overwrite(remap = false)
	@SideOnly(Side.CLIENT)
	public void bindTexture() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(offlineAuth$getOASkin());
	}
	@Unique
	@SideOnly(Side.CLIENT)
	private ResourceLocation offlineAuth$getOASkin() {
		if(uuid == null) return SkinManager.field_152793_a;

		Minecraft mc = Minecraft.getMinecraft();
		EntityClientPlayerMP thePlayer = mc.thePlayer;
		UUID dynamicUUID = uuid.equals(ClientUtils.localPlayerHead.uuid)
				? StringUtils.fromString(mc.getSession().getPlayerID())
				: uuid;
		
		//Self
		if (thePlayer.getGameProfile().getId().equals(dynamicUUID)) {
			return thePlayer.getLocationSkin();
		}

		return offlineAuth$loadOASkin(mc, dynamicUUID);
	}
	
	@Unique
	@SideOnly(Side.CLIENT)
	private ResourceLocation offlineAuth$loadOASkin(Minecraft mc, UUID dynamicUUID) {
		Logger log = LogManager.getLogger();
	
		String displayName = OfflineAuth.varInstanceClient.clientRegistry.getDisplayNameByUUID(StringUtils.fromUUID(dynamicUUID));
		
		log.fatal("NAME NULL ? : " + (displayName == null));
		
		if(displayName != null) {

			log.warn("displayName ? : " + (displayName));
			
			if (OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName) == null && !OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
				IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
				PacketHandler.net.sendToServer(msg);
				OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
				OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, mc.theWorld.getPlayerEntityByName(displayName), null, displayName);
			} else {
				ResourceLocation rl;
				File imageFile = ClientSkinUtil.getSkinFile(OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName));
				if (imageFile == null || !imageFile.exists()) {
					return SkinManager.field_152793_a;
				} else {
					if (OfflineAuth.varInstanceClient.clientRegistry.getTabMenuResourceLocation(displayName) == null) {
						BufferedImage bufferedImage;
						try {
							if (!Util.pngIsSane(imageFile)) {
								OfflineAuth.error("Sussy error loading skin image, not sane: " + displayName);
								return SkinManager.field_152793_a;
							}
							bufferedImage = ImageIO.read(imageFile);
							if (bufferedImage.getHeight() != bufferedImage.getWidth()) {
								BufferedImage bufferedImageNew = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() * 2, bufferedImage.getType());
								Graphics g = bufferedImageNew.getGraphics();
								g.drawImage(bufferedImage, 0, 0, null);
								g.dispose();
								bufferedImage = bufferedImageNew;
							}
							rl = new ResourceLocation("offlineauth", "tabmenuskins/" + displayName);
							ClientSkinUtil.loadTexture(bufferedImage, rl);
							OfflineAuth.varInstanceClient.clientRegistry.setTabMenuResourceLocation(displayName, rl);
						} catch (IOException e_) {
							OfflineAuth.error("Error loading skin image " + displayName);
							return SkinManager.field_152793_a;
						}
					}
					
					return OfflineAuth.varInstanceClient.clientRegistry.getTabMenuResourceLocation(displayName);
				}
			}
		}
		return SkinManager.field_152793_a;
	}
	
	
}
