package trollogyadherent.offlineauth.mixin.early.serverutilities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import serverutils.lib.client.ClientUtils;
import serverutils.lib.gui.GuiHelper;
import serverutils.lib.icon.ImageIcon;
import serverutils.lib.icon.PlayerHeadIcon;
import serverutils.lib.util.StringUtils;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.util.Util;

import java.util.List;
import java.util.UUID;

@Mixin(value = PlayerHeadIcon.class, priority = 999)
public class MixinPlayerHeadIcon extends ImageIcon {
	
	public MixinPlayerHeadIcon(ResourceLocation tex) {
		super(tex);
	}
	
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
	
	/**
	 * @author kotmatross
	 * @reason compat
	 */
	@Overwrite(remap = false)
	@SideOnly(Side.CLIENT)
	public void draw(int x, int y, int w, int h) {
		this.bindTexture();
		
		//Use 2:1 (64x32)
		if(Config.useLegacyConversion) {
			GuiHelper.drawTexturedRect(x, y, w, h, this.color, 0.125, 0.25, 0.25, 0.5);
			GuiHelper.drawTexturedRect(x, y, w, h, this.color, 0.625, 0.25, 0.75, 0.5);
		}
		//Use 1:1 (64x64)
		else {
			GuiHelper.drawTexturedRect(x, y, w, h, this.color, 0.125, 0.125, 0.25, 0.25);
			GuiHelper.drawTexturedRect(x, y, w, h, this.color, 0.625, 0.125, 0.75, 0.25);
		}
	}
	@Unique
	@SideOnly(Side.CLIENT)
	private ResourceLocation offlineAuth$getOASkin() {
		if(uuid == null)
			return SkinUtil.getDefaultIcon();
	
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
		NetHandlerPlayClient handler = mc.thePlayer.sendQueue;
		List<GuiPlayerInfo> players = handler.playerInfoList;
		
		ResourceLocation oaSkin = null;
		
		if(!SkinUtil.uuidToName.containsKey(dynamicUUID)) {
			for(GuiPlayerInfo player : players) {
				if(Util.offlineUUID2(player.name).equals(dynamicUUID)) {
					SkinUtil.uuidToName.put(dynamicUUID, player.name);
				}
			}
		} else {
			oaSkin = SkinUtil.getSkinResourceLocationByDisplayName(mc, SkinUtil.uuidToName.get(dynamicUUID), true);
			if(oaSkin == null)
				oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(SkinUtil.uuidToName.get(dynamicUUID));
		}
		
		if (oaSkin != null) {
			return oaSkin;
		} else {
			return SkinUtil.getDefaultIcon();
		}
	}
	
}
