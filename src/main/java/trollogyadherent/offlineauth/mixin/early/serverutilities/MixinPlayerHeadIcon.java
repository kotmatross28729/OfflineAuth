package trollogyadherent.offlineauth.mixin.early.serverutilities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import serverutils.lib.client.ClientUtils;
import serverutils.lib.icon.PlayerHeadIcon;
import serverutils.lib.util.StringUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.SkinUtil;

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
		String displayName = OfflineAuth.varInstanceClient.clientRegistry.getDisplayNameByUUID(dynamicUUID);
		
		if(SkinUtil.uuidToName.containsKey(dynamicUUID)) {
			return SkinUtil.getSkinResourceLocationByDisplayName(mc, SkinUtil.uuidToName.get(dynamicUUID));
		} else if(displayName != null) {
			SkinUtil.uuidToName.put(dynamicUUID, displayName);
			return SkinUtil.getSkinResourceLocationByDisplayName(mc, displayName);
		}
		
		return SkinManager.field_152793_a;
	}
	
}
