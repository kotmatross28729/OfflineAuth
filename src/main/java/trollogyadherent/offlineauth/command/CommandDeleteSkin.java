package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CommandDeleteSkin implements ICommand {
    private final List aliases;

    public CommandDeleteSkin()
    {
        aliases = new ArrayList();
        aliases.add("delskin");
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "deleteskin";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/deleteskin <identifier> (alias: delskin)";
    }

    @Override
    public List getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if (sender instanceof EntityPlayerMP && !Util.isOp((EntityPlayerMP) sender)) {
            sender.addChatMessage(new ChatComponentText((char) 167 + "cYou do not have permission to use this command"));
            return;
        }
        if (argString.length != 1) {
            sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
        } else {
            StatusResponseObject responseObject = Database.deletePlayerSkin(argString[0], "", true);
            sender.addChatMessage(new ChatComponentText(responseObject.getStatus()));
            OfflineAuth.info(sender.getCommandSenderName() + " issued deleteplayer command for player " + argString[0] + " with status " + responseObject.getStatus());
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender var1)
    {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender var1, String[] var2)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2)
    {
        return false;
    }
}