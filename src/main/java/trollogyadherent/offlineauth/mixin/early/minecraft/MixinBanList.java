package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.server.management.BanList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import trollogyadherent.offlineauth.util.Util;

import java.net.SocketAddress;
@Mixin(value = BanList.class, priority = 999)
public class MixinBanList {
	
	/**
	 * @author kotmatross
	 * @reason fix for IPv6
	 */
	@Overwrite
	private String func_152707_c(SocketAddress address) {
		String ipport = address.toString();

		return Util.getIPUniversal(ipport);
	}
	
}
