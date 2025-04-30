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

public class CommandDeletePlayer implements ICommand {
    private final List<String> aliases;

    public CommandDeletePlayer()
    {
        aliases = new ArrayList<>();
        aliases.add("deluser");
    }

    @Override
    public int compareTo(@Nonnull Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "deleteuser";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/deleteuser <identifier> (alias: delplayer)";
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
        } else {
            DBPlayerData dbPlayerData = Database.getPlayerDataByIdentifier(argString[0]);
            StatusResponseObject responseObject = Database.deleteUserData(argString[0]);
            if (responseObject.getStatusCode() == 200) {
                sender.addChatMessage(new ChatComponentText(Util.colorCode(Util.Color.GREEN) + "Success"));
            } else {
                sender.addChatMessage(new ChatComponentText(Util.colorCode(Util.Color.RED) + "Command failed"));
            }
            OfflineAuth.info(sender.getCommandSenderName() + " issued deleteuser command for player " + argString[0] + " with status " + responseObject.getStatus());
            if (responseObject.getStatusCode() == 200 && dbPlayerData != null) {

                for (EntityPlayerMP e : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    if (e.getDisplayName().equals(dbPlayerData.getDisplayname())) {
                        e.playerNetServerHandler.kickPlayerFromServer(Config.accountDeletionKickMessage);
                        break;
                    }
                }
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