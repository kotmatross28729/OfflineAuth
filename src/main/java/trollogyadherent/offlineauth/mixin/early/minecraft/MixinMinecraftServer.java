package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftServer.class, priority = 999)
public class MixinMinecraftServer {
	@Shadow
	private boolean onlineMode;
	
	@Inject(method = "isServerInOnlineMode()Z", at = @At(value = "HEAD"), cancellable = true)
	public void isServerInOnlineMode(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}
	@Inject(method = "setOnlineMode(Z)V", at = @At(value = "HEAD"), cancellable = true)
	public void setOnlineMode(boolean online, CallbackInfo ci) {
		this.onlineMode = false;
		ci.cancel();
	}
}
