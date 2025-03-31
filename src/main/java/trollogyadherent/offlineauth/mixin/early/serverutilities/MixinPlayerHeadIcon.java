package trollogyadherent.offlineauth.mixin.early.serverutilities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import serverutils.lib.client.ClientUtils;
import serverutils.lib.icon.PlayerHeadIcon;
import serverutils.lib.util.StringUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.registry.data.ServerPlayerData;
import trollogyadherent.offlineauth.registry.newreg.ClientRegistry;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.util.Util;

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
		Minecraft.getMinecraft().getTextureManager().bindTexture(offlineAuth$loadOASkin());
	}

	@Unique
	@SideOnly(Side.CLIENT)
	private ResourceLocation offlineAuth$loadOASkin() {
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

		ClientRegistry.Data dataC = OfflineAuth.varInstanceClient.clientRegistry.getDataByUUID(dynamicUUID);
		
		if (dataC != null) {
			if(dataC.displayName != null) {
				final ResourceLocation oar = SkinUtil.getSkinResourceLocationByDisplayName(dataC.displayName);
				if (oar != null) {
					return oar;
				}
			}
		}

		return SkinManager.field_152793_a;
	}
}
