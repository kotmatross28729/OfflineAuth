package trollogyadherent.offlineauth.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import trollogyadherent.offlineauth.misc.Unused;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Unused
public class CommandChangePlayerUUID implements ICommand {
    private final List<String> aliases;

    public CommandChangePlayerUUID() {
        aliases = new ArrayList<>();
    }

    @Override
    public int compareTo(@Nonnull Object o) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "changeuuid";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/changeuuid <player> <uuid>";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        /**
        if (sender instanceof EntityPlayerMP && !Util.isOp((EntityPlayerMP) sender) || sender instanceof EntityPlayerMP && Util.isOp((EntityPlayerMP) sender) && !Config.allowOpsUUIDChange) {
            sender.addChatMessage(new ChatComponentText((char) 167 + "cYou do not have permission to use this command"));
            return;
        }

        if (argString.length != 2) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            return;
        }

        String identifier = null;
        String[] registeredIdentifiers = Database.getRegisteredIdentifiers();
        for (String s : registeredIdentifiers) {
            if (s.equals(argString[0])) {
                identifier = s;
                break;
            }
        }
        if (identifier == null) {
            DBPlayerData dbpd = Database.getPlayerDataByDisplayName(argString[0]);
            if (dbpd != null) {
                identifier = dbpd.getIdentifier();
            }
        }

        if (identifier == null) {
            sender.addChatMessage(new ChatComponentText("Couldn't find that player"));
            return;
        }

        StatusResponseObject res = Database.setPlayerUUID(identifier, "", argString[1], true);
        OfflineAuth.info(sender.getCommandSenderName() + " issued changeuuid command with status " + res.getStatus());
        */
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