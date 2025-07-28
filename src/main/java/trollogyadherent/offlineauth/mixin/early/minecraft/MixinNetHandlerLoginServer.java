package trollogyadherent.offlineauth.mixin.early.minecraft;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.network.NetHandlerLoginServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.net.InetSocketAddress;
@Mixin(value = NetHandlerLoginServer.class, priority = 999)
public class MixinNetHandlerLoginServer {
	
	@Shadow
	private GameProfile field_147337_i;
	@Shadow
	@Final
	public NetworkManager field_147333_a;
	
	
	/**
	 * @author kotmatross
	 * @reason fix for IPv6
	 */
	@Overwrite
	public String func_147317_d() {
		String ip = ((InetSocketAddress)this.field_147333_a.getSocketAddress()).getAddress().getHostAddress();
		
		return this.field_147337_i != null ? this.field_147337_i + " (" + ip + ")" : ip;
	}
}
