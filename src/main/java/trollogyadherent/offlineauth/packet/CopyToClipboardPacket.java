package trollogyadherent.offlineauth.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class CopyToClipboardPacket implements IMessageHandler<CopyToClipboardPacket.SimpleMessage, IMessage>
{
    /* Server to Client: copy this thing to clipboard and send this in chat */
    @Override
    public IMessage onMessage(SimpleMessage message, MessageContext ctx)
    {
        if (ctx.side.isClient() && message.exchangecode == 0)
        {
            //System.out.println("CopyToClipboardPacket onMessage triggered, code 0 (from server)");

            StringSelection stringselection = new StringSelection(message.content);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, null);
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message.description));
            return null;
        }

        return null;
    }

    public static class SimpleMessage implements IMessage
    {
        private final String sep = ",";
        private int exchangecode;  // When server queries for password, it's 0. When client responds, it's 1
        private String content;
        private String description;

        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public SimpleMessage() {}

        public SimpleMessage(String content, String description)
        {
            this.exchangecode = 0;
            this.content = content;
            this.description = description;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            String dataString = ByteBufUtils.readUTF8String(buf);
            String[] dataSplit = dataString.split(sep);
            this.exchangecode = Integer.parseInt(String.valueOf(dataSplit[0]));
            this.content = dataSplit[1];
            this.description = dataSplit[2];
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            String dataString = this.exchangecode + sep + this.content + sep + this.description;
            ByteBufUtils.writeUTF8String(buf, dataString);
        }
    }
}