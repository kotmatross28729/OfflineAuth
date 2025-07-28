package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import trollogyadherent.offlineauth.util.Util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

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
		SocketAddress address = this.playerNetServerHandler.netManager.getSocketAddress();
		if(address instanceof InetSocketAddress inetAddress) {
			return inetAddress.getAddress().getHostAddress();
		} else { //whatever
			return Util.getIPUniversal(address.toString());
		}
	}
}
