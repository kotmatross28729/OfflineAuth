package trollogyadherent.offlineauth.mixin.late.tabfaces;

import net.minecraft.util.ResourceLocation;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import trollogyadherent.offlineauth.skin.SkinUtil;

@Mixin(value = ClientRegistry.class, priority = 999)
public class MixinClientRegistry {
	
	@Inject(
			method = "getTabMenuResourceLocation",
			at = @At(value = "HEAD"),
			cancellable = true,
			remap = false)
	public void getTabMenuResourceLocation(String displayName, boolean removeAfterTTL, int ttl, CallbackInfoReturnable<ResourceLocation> cir) {
		if (VarInstanceClient.minecraftRef.thePlayer != null
				&& VarInstanceClient.minecraftRef.thePlayer.getDisplayName()
				.equals(displayName)) {
			cir.setReturnValue(VarInstanceClient.minecraftRef.thePlayer.getLocationSkin());
		}
		
		ResourceLocation oaSkin = null;
		
		if(VarInstanceClient.minecraftRef.theWorld != null) {
			oaSkin = SkinUtil.getSkinResourceLocationByDisplayName(VarInstanceClient.minecraftRef, displayName, true);
		}
		
		if (oaSkin != null) {
			cir.setReturnValue(oaSkin);
		}
	}
	
}
