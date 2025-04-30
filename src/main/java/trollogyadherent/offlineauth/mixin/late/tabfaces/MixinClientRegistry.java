package trollogyadherent.offlineauth.mixin.late.tabfaces;

import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;

@Mixin(value = ClientRegistry.class, priority = 999)
public class MixinClientRegistry {

	/**
	 * @author kotmatross
	 * @reason compat
	 */
	@Overwrite(remap = false)
	public ResourceLocation getTabMenuResourceLocation(String displayName, boolean removeAfterTTL, int ttl) {
		if (VarInstanceClient.minecraftRef.thePlayer != null
				&& VarInstanceClient.minecraftRef.thePlayer.getDisplayName()
				.equals(displayName)) {
	
			return VarInstanceClient.minecraftRef.thePlayer.getLocationSkin();
		}
		
		ResourceLocation oaSkin;
		
		if(VarInstanceClient.minecraftRef.theWorld != null) {
			oaSkin = SkinUtil.getSkinResourceLocationByDisplayName(VarInstanceClient.minecraftRef, displayName, true);
			if(oaSkin == null) {
				oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(displayName);
			}
		} else {
			oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(displayName);

		}
		
		if (oaSkin != null) {
			return oaSkin;
		} else {
			return Config.showQuestionMarkIfUnknown ? OfflineAuth.varInstanceClient.defaultResourceLocation : SkinManager.field_152793_a;
		}
	}
	
}
