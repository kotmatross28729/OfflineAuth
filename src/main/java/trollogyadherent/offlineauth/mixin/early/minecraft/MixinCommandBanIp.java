package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandBanIp;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import trollogyadherent.offlineauth.util.Util;
@Mixin(value = CommandBanIp.class, priority = 999)
public abstract class MixinCommandBanIp extends CommandBase {
	
	/**
	 * @author kotmatross
	 * @reason remove strict v4 check
	 */
	@Overwrite
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length >= 1 && args[0].length() > 1) {
			IChatComponent ichatcomponent = null;
			
			if (args.length >= 2) {
				ichatcomponent = func_147178_a(sender, args, 1);
			}
			
			if (Util.looksLikeIp(args[0])) {
				this.func_147210_a(sender, args[0], ichatcomponent == null ? null : ichatcomponent.getUnformattedText());
			} else {
				EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[0]);
				
				if (entityplayermp == null) {
					throw new PlayerNotFoundException("commands.banip.invalid");
				}
				
				this.func_147210_a(sender, entityplayermp.getPlayerIP(), ichatcomponent == null ? null : ichatcomponent.getUnformattedText());
			}
		} else {
			throw new WrongUsageException("commands.banip.usage");
		}
	}
	
	@Shadow
	protected void func_147210_a(ICommandSender p_147210_1_, String p_147210_2_, String p_147210_3_) {}
	
}
