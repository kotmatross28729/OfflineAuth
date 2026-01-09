package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = MinecraftServer.class, priority = 999)
public class MixinMinecraftServer {
	
	@Shadow
	private boolean onlineMode;
	
	/**
	 * @author kotmatross
	 * @reason muhahahaha
	 */
	@Overwrite
	public boolean isServerInOnlineMode() {
		return false;
	}
	
	/**
	 * @author kotmatross
	 * @reason muhahahaha
	 */
	@Overwrite
	public void setOnlineMode(boolean online) {
		this.onlineMode = false;
	}

}
