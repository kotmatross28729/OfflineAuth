package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandPardonIp;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import trollogyadherent.offlineauth.util.Util;
@Mixin(value = CommandPardonIp.class, priority = 999)
public abstract class MixinCommandPardonIp extends CommandBase {
	
	/**
	 * @author kotmatross
	 * @reason remove strict v4 check
	 */
	@Overwrite
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length == 1 && args[0].length() > 1) {
			
			if (Util.looksLikeIp(args[0])) {
				MinecraftServer.getServer().getConfigurationManager().getBannedIPs().func_152684_c(args[0]);
				func_152373_a(sender, this, "commands.unbanip.success", args[0]);
			} else {
				throw new SyntaxErrorException("commands.unbanip.invalid");
			}
		} else {
			throw new WrongUsageException("commands.unbanip.usage");
		}
	}
}
