package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.database.Database;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class CommandRegisterPlayerServer implements ICommand {
    private final List aliases;

    public CommandRegisterPlayerServer()
    {
        aliases = new ArrayList();
        aliases.add("register");
        aliases.add("reg");
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "registerplayer";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/registerplayer <username> <password>";
    }

    @Override
    public List getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        System.out.println("Issued registerplayer command");
        if (argString.length != 2) {
            sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
            return;
        }
        try {
            Database.registerPlayer(argString[0], argString[1], "", true, false);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
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
