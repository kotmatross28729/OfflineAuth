package trollogyadherent.offlineauth.command;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.ResetCachesPacket;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.Util;

import java.util.ArrayList;
import java.util.List;

public class CommandTestSkinChange implements ICommand {
    private final List aliases;

    public CommandTestSkinChange()
    {
        aliases = new ArrayList();
    }

    @Override
    public int compareTo(Object o)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "skin";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/skin <name>";
    }

    @Override
    public List getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        System.out.println("Issued test skin change command");
        if (argString.length == 1) {
            /*if (SkinUtilServer.skinCachedOnServer(argString[0])) {
                for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    IMessage msg = new ChangeSkinPacket.SimpleMessage(0, argString[0], new byte[0], 0);
                    PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
                }
            } else {
                sender.addChatMessage(new ChatComponentText("Skin not found"));
            }*/

            if (!(sender instanceof EntityPlayerMP)) {
                sender.addChatMessage(new ChatComponentText("You can only use this command ingame"));
                return;
            }

            if (ServerSkinUtil.skinCachedOnServer(argString[0])) {
                EntityPlayerMP senderPlayer = (EntityPlayerMP) sender;
                String uuid = Util.offlineUUID(senderPlayer.getDisplayName());
                OfflineAuth.varInstanceServer.skinRegistry.deleteByUUID(uuid);
                OfflineAuth.varInstanceServer.skinRegistry.add(uuid, argString[0]);
                for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    IMessage msg = new ResetCachesPacket.SimpleMessage();
                    PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
                }
            } else {
                sender.addChatMessage(new ChatComponentText("Skin not found"));
            }
        } else {
            sender.addChatMessage(new ChatComponentText("Command usage: " + getCommandUsage(null)));
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
