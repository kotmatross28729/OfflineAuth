package trollogyadherent.offlineauth.mixin.late.serverutilities;

import net.minecraftforge.event.entity.player.PlayerEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import serverutils.handlers.ServerUtilitiesPlayerEventHandler;

@Mixin(value = ServerUtilitiesPlayerEventHandler.class, priority = 999)
public class MixinServerUtilitiesPlayerEventHandler {
	
	/**
	 * @author kotmatross
	 * @reason disable ability to change name
	 */
	@Overwrite(remap = false)
	public static void onNameFormat(PlayerEvent.NameFormat event) {
		/// Do nothing
	}
	
}
