package trollogyadherent.offlineauth.mixin.late.tabfaces;

import net.minecraft.util.ResourceLocation;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import trollogyadherent.offlineauth.skin.SkinUtil;

@Mixin(value = ClientRegistry.class, priority = 999)
public class MixinClientRegistry {

	/**
	 * @author kotmatross
	 * @reason compat
	 */
	@Overwrite(remap = false)
	public ResourceLocation getTabMenuResourceLocation(String displayName, boolean removeAfterTTL, int ttl) {
		displayName = displayName.trim(); //In 1.0.8 displayName has a space at the end, probably a bug?
		
		return SkinUtil.getOASkin(VarInstanceClient.minecraftRef, displayName, true);
	}
	
}
