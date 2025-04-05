package trollogyadherent.offlineauth.mixin.early.serverutilities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
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
import trollogyadherent.offlineauth.registry.newreg.ClientRegistry;
import trollogyadherent.offlineauth.skin.SkinUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
	private static final Map<UUID, SkinUtil.CachedSkin> offlineAuth$skinCache = new ConcurrentHashMap<>();
	@Unique
	private static final long CACHE_EXPIRY_TIME = 10 * 20; // 10 seconds
	
	@Unique
	@SideOnly(Side.CLIENT)
	private ResourceLocation offlineAuth$getOASkin() {
		if(uuid == null) return SkinManager.field_152793_a;
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityClientPlayerMP thePlayer = mc.thePlayer;
		WorldClient theWorld = mc.theWorld;
		UUID dynamicUUID = uuid.equals(ClientUtils.localPlayerHead.uuid)
				? StringUtils.fromString(mc.getSession().getPlayerID())
				: uuid;
		
		//Self
		if (thePlayer.getGameProfile().getId().equals(dynamicUUID)) {
			return thePlayer.getLocationSkin();
		}
		
		SkinUtil.CachedSkin cachedSkin = offlineAuth$skinCache.get(uuid);
		long currentTime = theWorld.getTotalWorldTime();
		
		if (cachedSkin != null && cachedSkin.skin != null && (currentTime - cachedSkin.timestamp) < CACHE_EXPIRY_TIME) {
			return cachedSkin.skin;
		}
		
		ResourceLocation skin = offlineAuth$loadOASkin(dynamicUUID);

		offlineAuth$skinCache.put(uuid, new SkinUtil.CachedSkin(skin, currentTime));
		
		return skin;
	}
	
	@Unique
	@SideOnly(Side.CLIENT)
	private ResourceLocation offlineAuth$loadOASkin(UUID dynamicUUID) {
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
