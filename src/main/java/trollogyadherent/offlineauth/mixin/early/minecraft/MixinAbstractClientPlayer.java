package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import trollogyadherent.offlineauth.OfflineAuth;
@Mixin(value = AbstractClientPlayer.class, priority = 999)
public class MixinAbstractClientPlayer {
	
	@Shadow
	@Final
	@Mutable
	public static ResourceLocation locationStevePng = new ResourceLocation("textures/entity/steve.png");
	
	@Inject(method = "<clinit>", at = @At(value = "TAIL"))
	private static void whatever(CallbackInfo ci) {
		locationStevePng = OfflineAuth.varInstanceClient.DEFAULT_SKIN_64;
	}
	
}
