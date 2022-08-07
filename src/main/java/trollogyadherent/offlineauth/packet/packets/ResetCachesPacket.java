package trollogyadherent.offlineauth.packet.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import trollogyadherent.offlineauth.OfflineAuth;


public class ResetCachesPacket implements IMessageHandler<ResetCachesPacket.SimpleMessage, IMessage>  {

    /* Server to Client: reset your caches */
    /* Client: resets caches */
    @Override
    public IMessage onMessage(ResetCachesPacket.SimpleMessage message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {
            OfflineAuth.debug("ResetCachesPacket onMessage triggered (from server)");

            //OfflineAuth.varInstanceClient.entityPlayerRegistry = new ClientEntityPlayerRegistry();
            //ClientSkinUtil.clearSkinCache();
        }

        return null;
    }

    public static class SimpleMessage implements IMessage
    {
        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public SimpleMessage() {}


        @Override
        public void fromBytes(ByteBuf buf)
        {

        }

        @Override
        public void toBytes(ByteBuf buf)
        {

        }
    }
}