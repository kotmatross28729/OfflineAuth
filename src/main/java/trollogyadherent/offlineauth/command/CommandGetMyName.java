package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandGetMyName implements ICommand {
	private final List<String> aliases;
	
	public CommandGetMyName() {
		aliases = new ArrayList<>();
	}
	
	@Override
	public int compareTo(@Nonnull Object o) {
		return 0;
	}
	
	@Override
	public String getCommandName() {
		return "myname";
	}
	
	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/myname";
	}
	
	@Override
	public List<String> getCommandAliases()
	{
		return this.aliases;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] argString) {
		if(sender instanceof EntityPlayerMP playerMP) {
			playerMP.addChatMessage(new ChatComponentText("Display Name : " + "{" + playerMP.getDisplayName() + "}"));
			playerMP.addChatMessage(new ChatComponentText("Command Sender Name : " + "[" + playerMP.getCommandSenderName() + "]"));
			playerMP.addChatMessage(new ChatComponentText("Game Profile Name : " + "[" + playerMP.getGameProfile().getName() + "]"));
		}
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
