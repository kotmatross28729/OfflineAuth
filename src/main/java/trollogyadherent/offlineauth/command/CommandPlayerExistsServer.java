package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandPlayerExistsServer implements ICommand {
    private final List<String> aliases;

    public CommandPlayerExistsServer()
    {
        aliases = new ArrayList<>();
        aliases.add("playerex");
    }

    @Override
    public int compareTo(@Nonnull Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "playerexists";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/playerexists <identifier>";
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
        if (argString.length != 1) {
            sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
            return;
        }
        DBPlayerData dbpd = null;
        if (Database.isUserRegisteredByIdentifier(argString[0])) {
            dbpd = Database.getPlayerDataByIdentifier(argString[0]);
        } else if (Database.isUserRegisteredByDisplayname(argString[0])) {
            dbpd = Database.getPlayerDataByDisplayName(argString[0]);
        }
        if (dbpd == null) {
            sender.addChatMessage(new ChatComponentText("Neither username nor displayname found"));
        } else {
            sender.addChatMessage(new ChatComponentText("User registered (username: \"" + dbpd.getIdentifier() + "\", displayname: \"" + dbpd.getDisplayname() + "\")"));
            OfflineAuth.debug(dbpd.toString());
        }

        OfflineAuth.info(sender.getCommandSenderName() + " issued playerexists command");
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
