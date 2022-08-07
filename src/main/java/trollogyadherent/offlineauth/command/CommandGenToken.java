package trollogyadherent.offlineauth.command;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.packet.packets.CopyToClipboardPacket;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandGenToken implements ICommand {
    private final List aliases;

    public CommandGenToken()
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
        return "gentoken";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/gentoken";
    }

    @Override
    public List getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        if (sender instanceof EntityPlayerMP && !Util.isOp((EntityPlayerMP) sender) || sender instanceof EntityPlayerMP && Util.isOp((EntityPlayerMP) sender) && !Config.allowOpsTokenGen) {
            sender.addChatMessage(new ChatComponentText((char) 167 + "cYou do not have permission to use this command"));
            return;
        }
        try {
            String token = Database.createtoken();
            sender.addChatMessage(new ChatComponentText(token));
            OfflineAuth.info(sender.getCommandSenderName() + " issued gentoken command");
            if (sender instanceof EntityPlayerMP) {
                IMessage msg = new CopyToClipboardPacket.SimpleMessage(token, "Copied to clipboard");
                PacketHandler.net.sendTo(msg, (EntityPlayerMP)sender);
            }
        } catch (IOException e) {
            OfflineAuth.error("Failed to generate token! " + e.getMessage());
            e.printStackTrace();
            sender.addChatMessage(new ChatComponentText((char) 167 + "cFailed to generate token! " + e.getMessage()));
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