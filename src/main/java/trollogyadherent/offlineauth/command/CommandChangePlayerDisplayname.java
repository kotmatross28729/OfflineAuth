package trollogyadherent.offlineauth.command;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CommandChangePlayerDisplayname implements ICommand {
    private final List<String> aliases;

    public CommandChangePlayerDisplayname() {
        aliases = new ArrayList<>();
    }

    @Override
    public int compareTo(@Nonnull Object o) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "changename";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "/changename <player> <displayname>";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if (sender instanceof EntityPlayerMP && !Util.isOp((EntityPlayerMP) sender) || sender instanceof EntityPlayerMP && Util.isOp((EntityPlayerMP) sender) && !Config.allowOpsDisplayNameChange) {
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

        DBPlayerData dbpd = Database.getPlayerDataByIdentifier(identifier);
        if (dbpd == null) {
            sender.addChatMessage(new ChatComponentText("Couldn't find that player"));
            return;
        }
        String oldDisplayName = dbpd.getDisplayname();

        StatusResponseObject res = Database.changePlayerDisplayName(identifier, "", argString[1], true);
        OfflineAuth.info(sender.getCommandSenderName() + " issued changeuuid command with status " + res.getStatus());
        if (res.getStatusCode() == 200) {
            for (EntityPlayerMP e : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                if (e.getDisplayName().equals(oldDisplayName)) {
                    e.playerNetServerHandler.kickPlayerFromServer("Your displayname has been changed to \""+ argString[1] +"\" by a moderator.");
                    break;
                }
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