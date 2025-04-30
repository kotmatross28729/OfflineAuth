package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.misc.Unused;
import trollogyadherent.offlineauth.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Unused
public class CommandUUID implements ICommand {
    private final List<String> aliases;

    public CommandUUID()
    {
        aliases = new ArrayList<>();
    }

    @Override
    public int compareTo(@Nonnull Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "uuid";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/uuid <name>";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if (argString.length != 1) {
            sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
        } else {
            sender.addChatMessage(new ChatComponentText(Util.offlineUUID(argString[0])));
            OfflineAuth.info(sender.getCommandSenderName() + " issued uuid command for name " + argString[0]);
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender var1)
    {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2)
    {
        return false;
    }
}