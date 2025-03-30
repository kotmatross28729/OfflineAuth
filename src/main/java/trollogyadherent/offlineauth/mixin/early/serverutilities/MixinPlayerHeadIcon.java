package trollogyadherent.offlineauth.mixin.early.serverutilities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import serverutils.lib.client.ClientUtils;
import serverutils.lib.icon.PlayerHeadIcon;
import serverutils.lib.util.StringUtils;
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
		
		EntityPlayer player = mc.theWorld.func_152378_a(dynamicUUID);
		
		if(SkinUtil.uuidToName.containsKey(dynamicUUID)) {
			final ResourceLocation oar = SkinUtil.getSkinResourceLocationByDisplayName(dynamicUUID);
			if (oar != null) {
				return oar;
			}
		} else if (player != null) {
			String displayName = player.getDisplayName();
			
			final ResourceLocation oar = SkinUtil.getSkinResourceLocationByDisplayName(displayName);

			if (oar != null) {
				SkinUtil.uuidToName.put(dynamicUUID, player.getDisplayName());
				return oar;
			}
		}
		
		return SkinManager.field_152793_a;
	}
}
