package trollogyadherent.offlineauth.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;


public class ResetCachesPacket implements IMessageHandler<ResetCachesPacket.SimpleMessage, IMessage>  {

    /* Server to Client: reset your caches */
    /* Client: resets caches */
    @Override
    public IMessage onMessage(ResetCachesPacket.SimpleMessage message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {
            System.out.println("ResetCachesPacket onMessage triggered (from server)");

            OfflineAuth.varInstanceClient.playerRegistry.clear();
            ClientSkinUtil.clearSkinCache();
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