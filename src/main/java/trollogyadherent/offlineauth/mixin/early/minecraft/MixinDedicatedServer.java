package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = DedicatedServer.class, priority = 999)
public class MixinDedicatedServer {
	@ModifyArg(method = "startServer()Z", at = @At(value = "INVOKE", target = "net/minecraft/server/dedicated/DedicatedServer.setOnlineMode(Z)V"))
	private boolean disableOnlineMode(boolean online) {
		//No I will not
		return false;
	}
}
