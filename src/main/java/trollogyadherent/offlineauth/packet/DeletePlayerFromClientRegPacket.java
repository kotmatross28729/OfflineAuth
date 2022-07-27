package trollogyadherent.offlineauth.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;


public class DeletePlayerFromClientRegPacket implements IMessageHandler<DeletePlayerFromClientRegPacket.SimpleMessage, IMessage>  {

    /* Server to Client: reset your caches */
    /* Client: resets caches */
    @Override
    public IMessage onMessage(DeletePlayerFromClientRegPacket.SimpleMessage message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {
            //System.out.println("DeletePlayerFromClientRegPacket onMessage triggered (from server), displayName: " + message.displayName);
            OfflineAuth.varInstanceClient.clientRegistry.removeByDisplayName(message.displayName);
            ClientSkinUtil.removeSkinFromCache(message.displayName);
            ClientSkinUtil.removeCapeFromCache(message.displayName);
            //OfflineAuth.varInstanceClient.capeLocationfield.set(Minecraft.getMinecraft);

            //OfflineAuth.varInstanceClient.entityPlayerRegistry = new ClientEntityPlayerRegistry();
            //ClientSkinUtil.clearSkinCache();
        }

        return null;
    }

    public static class SimpleMessage implements IMessage
    {
        private String displayName;

        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public SimpleMessage() {}

        public SimpleMessage(String displayName) {
            this.displayName = displayName;
        }


        @Override
        public void fromBytes(ByteBuf buf)
        {
            this.displayName = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, this.displayName);
        }
    }
}