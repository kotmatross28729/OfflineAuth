package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import trollogyadherent.offlineauth.util.Util;

@Mixin(value = EntityPlayerMP.class, priority = 999)
public class MixinEntityPlayerMP {
	
	@Shadow
	public NetHandlerPlayServer playerNetServerHandler;
	
	/**
	 * @author kotmatross
	 * @reason fix for IPv6
	 */
	@Overwrite
	public String getPlayerIP() {
		String ipport = this.playerNetServerHandler.netManager.getSocketAddress().toString();
		
		return Util.getIPUniversal(ipport);
	}
	
}
