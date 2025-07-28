package trollogyadherent.offlineauth.mixin.early.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.ServerConfigurationManager;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import trollogyadherent.offlineauth.util.Util;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
@SuppressWarnings("LocalMayBeArgsOnly") //SHUT THE FUCK UP '@Local may be argsOnly = true' - what a trap, bruh
@Mixin(value = ServerConfigurationManager.class, priority = 999)
public class MixinServerConfigurationManager {
	
	@Inject(method = "initializeConnectionToPlayer", at = @At(value = "FIELD", target = "net/minecraft/server/management/ServerConfigurationManager.logger : Lorg/apache/logging/log4j/Logger;", opcode = Opcodes.GETSTATIC, shift = At.Shift.BEFORE))
	private void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP player, NetHandlerPlayServer nethandlerplayserver, CallbackInfo ci, @Local(ordinal = 1) LocalRef<String> s1) {
		SocketAddress address = netManager.getSocketAddress();
		if(address instanceof InetSocketAddress inetAddress) {
			s1.set(inetAddress.getAddress().getHostAddress());
		} else { //whatever
			s1.set(Util.getIPUniversal(address.toString()));
		}
	}
	
}
