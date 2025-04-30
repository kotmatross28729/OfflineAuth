package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.misc.Unused;
import trollogyadherent.offlineauth.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Unused
public class CommandConnectDBServer implements ICommand {
    private final List<String> aliases;

    public CommandConnectDBServer()
    {
        aliases = new ArrayList<>();
        aliases.add("con");
    }

    @Override
    public int compareTo(@Nonnull Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "connectdb";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/connectdb";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if (sender instanceof EntityPlayerMP && !Util.isOp((EntityPlayerMP) sender)) {
            sender.addChatMessage(new ChatComponentText((char) 167 + "cYou do not have permission to use this command"));
            return;
        }

        Database.initialize();

        OfflineAuth.info(sender.getCommandSenderName() + " issued connectdb command");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender var1)
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2)
    {
        return false;
    }
}
