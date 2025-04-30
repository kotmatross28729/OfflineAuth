package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.misc.Unused;
import trollogyadherent.offlineauth.rest.StatusResponseObject;

import javax.annotation.Nonnull;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@Unused
public class CommandSetRestPassword implements ICommand {

    private final List<String> aliases;

    public CommandSetRestPassword()
    {
        aliases = new ArrayList<>();
        aliases.add("pwd");
    }

    @Override
    public int compareTo(@Nonnull Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "restpassword";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/restpassword <password> (alias: pwd)";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if ((sender instanceof EntityPlayerMP)) {
            sender.addChatMessage(new ChatComponentText("You can only use this command in a server console"));
            return;
        }
        if (argString.length != 1) {
            sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
        } else {
            try {
                StatusResponseObject responseObject = Database.setRestPassword(argString[0]);
                sender.addChatMessage(new ChatComponentText(responseObject.getStatus()));
                OfflineAuth.info(sender.getCommandSenderName() + " issued restpassword command with status " + responseObject.getStatus());
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                sender.addChatMessage(new ChatComponentText("Failed to set rest password!"));
                e.printStackTrace();
            }
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
