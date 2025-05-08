package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.registry.cooldown.CooldownList;
import trollogyadherent.offlineauth.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandRegCooldown implements ICommand {
	private final List<String> aliases;
	
	public CommandRegCooldown() {
		aliases = new ArrayList<>();
		aliases.add("regCD");
	}
	
	@Override
	public int compareTo(@Nonnull Object o) {
		return 0;
	}
	
	@Override
	public String getCommandName() {
		return "registrationCooldown";
	}
	
	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/registrationCooldown <(<ip> <delete/del>)/(<clear>)> (alias: regCD)";
	}
	
	@Override
	public List<String> getCommandAliases() {
		return this.aliases;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] argString) {
		if (sender instanceof EntityPlayerMP && !Util.isOp((EntityPlayerMP) sender)) {
			sender.addChatMessage(new ChatComponentText((char) 167 + "cYou do not have permission to use this command"));
			return;
		}
		if (argString.length < 1) {
			sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
			return;
		} else {
			CooldownList cooldownList = OfflineAuth.varInstanceServer.getCooldownList();
			if(argString.length == 1) {
				if ("clear".equals(argString[0])) {
					cooldownList.clear();
					OfflineAuth.info(sender.getCommandSenderName() + " issued registrationCooldown command [ARG: clear]");
					return;
				}
			} else if (argString.length == 2) {
				if ("delete".equals(argString[1]) || "del".equals(argString[1])) {
					if(cooldownList.hasEntryInCooldownList(argString[0])) {
						cooldownList.removeEntry(argString[0]);
						OfflineAuth.info(sender.getCommandSenderName() + " issued registrationCooldown command [ARG: " + argString[0] + "]");
						return;
					}
				}
			}
		}
		sender.addChatMessage(new ChatComponentText("Error while executing registrationCooldown command, command usage: " + getCommandUsage(null)));
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender var1) {
		return true;
	}
	
	@Override
	public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2) {
		return null;
	}
	
	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}
}
