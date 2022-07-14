package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CommandListUsers implements ICommand {
    private final List aliases;

    public CommandListUsers()
    {
        aliases = new ArrayList();
        aliases.add("luser");
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "listusers";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/listusers (alias: luser)";
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
        String[] temp = Database.getRegisteredIdentifiers();
        for (String s : temp) {
            DBPlayerData pd = Database.getPlayerDataByIdentifier(s);
            sender.addChatMessage(new ChatComponentText(s + ": " + pd.getDisplayname()));
        }

        OfflineAuth.info(sender.getCommandSenderName() + " issued listusers command");
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