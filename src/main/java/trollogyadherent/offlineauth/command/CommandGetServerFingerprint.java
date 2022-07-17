package trollogyadherent.offlineauth.command;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.packet.CopyToClipboardPacket;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.util.RsaKeyUtil;
import trollogyadherent.offlineauth.util.ServerUtil;
import trollogyadherent.offlineauth.util.Util;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class CommandGetServerFingerprint implements ICommand {
    private final List aliases;

    public CommandGetServerFingerprint()
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
        return "fingerprint";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/fingerprint";
    }

    @Override
    public List getCommandAliases()
    {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] argString) {
        try {
            String fingerprint = RsaKeyUtil.getKeyFingerprint(ServerUtil.loadServerPublicKey());
            sender.addChatMessage(new ChatComponentText(fingerprint));
            OfflineAuth.info(sender.getCommandSenderName() + " issued fingerprint command");
            if (sender instanceof EntityPlayerMP) {
                IMessage msg = new CopyToClipboardPacket.SimpleMessage(fingerprint, "Copied to clipboard");
                PacketHandler.net.sendTo(msg, (EntityPlayerMP)sender);
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            OfflineAuth.error("Failed to get server fingerprint! " + e.getMessage());
            e.printStackTrace();
            sender.addChatMessage(new ChatComponentText((char) 167 + "cFailed to get server fingerprint! " + e.getMessage()));
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