package trollogyadherent.offlineauth.command;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.packet.packets.DeletePlayerFromClientRegPacket;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CommandDeleteCape implements ICommand {
    private final List aliases;

    public CommandDeleteCape()
    {
        aliases = new ArrayList();
        aliases.add("delcape");
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "deletecape";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/deletecape <identifier> (alias: delcape)";
    }

    @Override
    public List getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if (sender instanceof EntityPlayerMP && !Util.isOp((EntityPlayerMP) sender)) {
            sender.addChatMessage(new ChatComponentText(Util.colorCode(Util.Color.RED) +  "You do not have permission to use this command"));
            return;
        }
        if (argString.length != 1) {
            sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
        } else {
            StatusResponseObject responseObject = Database.deletePlayerCape(argString[0], "", true);
            DBPlayerData dbpd = Database.getPlayerDataByIdentifier(argString[0]);
            if (dbpd == null) {
                sender.addChatMessage(new ChatComponentText(Util.colorCode(Util.Color.RED) +  "Player not found in database"));
                return;
            }
            String displayname = dbpd.getDisplayname();
            //OfflineAuth.varInstanceServer.playerRegistry.setSkin(dbpd.getDisplayname(), ServerSkinUtil.getRandomDefaultSkinName());
            ServerSkinUtil.removeCapeFromCache(dbpd.getDisplayname());

            for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                if (displayname != null &&  ((EntityPlayerMP)o).getDisplayName().equals(displayname)) {
                    ((EntityPlayerMP)o).addChatMessage(new ChatComponentText(Util.colorCode(Util.Color.RED) +  "Your cape was deleted by a moderator"));
                }
                IMessage msg = new DeletePlayerFromClientRegPacket.SimpleMessage(displayname);
                PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
            }
            if (responseObject.getStatusCode() == 200) {
                sender.addChatMessage(new ChatComponentText(Util.colorCode(Util.Color.GREEN) + "Success"));
            } else {
                sender.addChatMessage(new ChatComponentText(Util.colorCode(Util.Color.RED) + "Command failed"));
            }
            OfflineAuth.info(sender.getCommandSenderName() + " issued deletecape command for player " + argString[0] + " with status " + responseObject.getStatus());
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