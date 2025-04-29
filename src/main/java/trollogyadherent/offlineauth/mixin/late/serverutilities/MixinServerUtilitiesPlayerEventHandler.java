package trollogyadherent.offlineauth.mixin.late.serverutilities;

import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import serverutils.handlers.ServerUtilitiesPlayerEventHandler;
import trollogyadherent.offlineauth.Config;

@Mixin(value = ServerUtilitiesPlayerEventHandler.class, priority = 999)
public class MixinServerUtilitiesPlayerEventHandler {
	
	@Inject(
			method = "onNameFormat",
			at = @At(value = "HEAD"),
			cancellable = true
			, remap = false
	)
	private static void onNameFormat(PlayerEvent.NameFormat event, CallbackInfo ci) {
		if(Config.blockServerUtilitiesDisplayNameChange)
			ci.cancel();
	}
}
