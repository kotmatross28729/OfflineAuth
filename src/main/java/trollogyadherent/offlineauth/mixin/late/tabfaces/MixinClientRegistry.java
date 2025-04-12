package trollogyadherent.offlineauth.mixin.late.tabfaces;

import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.SkinUtil;

import java.util.Map;

@Mixin(value = ClientRegistry.class, priority = 999)
public class MixinClientRegistry {
	
	@Shadow
	private Map<String, ClientRegistry.Data> playerEntities;
	
	
	//TODO: change to inject (Overwrite only for testing)
	/**
	 * @author
	 * @reason
	 */
	@Overwrite(remap = false)
	public ResourceLocation getTabMenuResourceLocation(String displayName, boolean removeAfterTTL, int ttl) {
		/* thePlayer is null when we're in the server selection menu */
		if (VarInstanceClient.minecraftRef.thePlayer != null
				&& VarInstanceClient.minecraftRef.thePlayer.getDisplayName()
				.equals(displayName)) {
			return VarInstanceClient.minecraftRef.thePlayer.getLocationSkin();
		}
		
		ResourceLocation oaSkin = SkinUtil.getSkinResourceLocation(displayName);
		if (oaSkin != null) {
			return oaSkin;
		}
		
		return Config.showQuestionMarkIfUnknown ? OfflineAuth.varInstanceClient.defaultResourceLocation : SkinManager.field_152793_a;
	}
	
}
