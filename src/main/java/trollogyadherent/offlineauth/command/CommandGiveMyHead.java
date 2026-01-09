package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandGiveMyHead implements ICommand {
	private final List<String> aliases;
	
	public CommandGiveMyHead() {
		aliases = new ArrayList<>();
	}
	
	@Override
	public int compareTo(@Nonnull Object o) {
		return 0;
	}
	
	@Override
	public String getCommandName() {
		return "myhead";
	}
	
	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/myhead";
	}
	
	@Override
	public List<String> getCommandAliases() {
		return this.aliases;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] argString) {
		if (sender instanceof EntityPlayerMP && !Util.isOp((EntityPlayerMP) sender)) {
			sender.addChatMessage(new ChatComponentText((char) 167 + "cYou do not have permission to use this command"));
		} else if (sender instanceof EntityPlayerMP playerMP) {
			ItemStack head = new ItemStack(Items.skull, 1, 3);
			head.stackTagCompound = new NBTTagCompound();
			head.stackTagCompound.setString("SkullOwner", playerMP.getDisplayName());
			if (!playerMP.inventory.addItemStackToInventory(head)) {
				playerMP.dropPlayerItemWithRandomChoice(head, false);
			}
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
