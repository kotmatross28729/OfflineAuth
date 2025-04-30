package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.rest.StatusResponseObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class CommandDeleteRestPassword implements ICommand {

    private final List<String> aliases;

    public CommandDeleteRestPassword()
    {
        aliases = new ArrayList<>();
        aliases.add("delpwd");
    }

    @Override
    public int compareTo(@Nonnull Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "deleterestpassword";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/deleterestpassword <password> (alias: delpwd)";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if ((sender instanceof EntityPlayerMP)) {
            sender.addChatMessage(new ChatComponentText("You can only use this command in a server console"));
            return;
        }
        StatusResponseObject responseObject = Database.delRestPassword();
        sender.addChatMessage(new ChatComponentText(responseObject.getStatus()));
        OfflineAuth.info(sender.getCommandSenderName() + " issued deleterestpassword command with status " + responseObject.getStatus());
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
