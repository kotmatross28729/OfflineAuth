package trollogyadherent.offlineauth.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.registry.data.ServerPlayerData;

public class QuerySkinNameFromServerPacket implements IMessageHandler<QuerySkinNameFromServerPacket.SimpleMessage, IMessage>
{
    /* Client to Server: what is the skin filename for this uuid? */
    /* Server to Client: the filename is X. in case there is none, it returns "0". this would be the place probably to have custom default server skin TODO */
    @Override
    public IMessage onMessage(SimpleMessage message, MessageContext ctx)
    {
        if (ctx.side.isServer() && message.exchangecode == 0)
        {
            System.out.println("QuerySkinNameFromServerPacket onMessage triggered, code 0 (from client)");
            System.out.println("Requesting player: " + ctx.getServerHandler().playerEntity.getDisplayName());
            System.out.println("Requested displayName: " + message.displayName);
            System.out.println("ServerSkinReg before any changes: " + OfflineAuth.varInstanceServer.playerRegistry);
            ServerPlayerData sd = OfflineAuth.varInstanceServer.playerRegistry.getPlayerDataByDisplayName(message.displayName);
            if (sd == null) {
                System.out.println("Player not found in server registry!");
            } else {
                System.out.println("Player found in server registry, skin is " + sd.skinName);
                message.skinName = sd.skinName;
                System.out.println("ServerSkinReg: " + OfflineAuth.varInstanceServer.playerRegistry);
            }

            message.exchangecode = 1;

            System.out.println("Returning message to client. Skinname: " + message.skinName);
            return message;
        }

        if (ctx.side.isClient() && message.exchangecode == 1)
        {
            System.out.println("QuerySkinNameFromServerPacket onMessage triggered, code 1 (from server)");
            System.out.println("Got skin name: " + message.skinName);

            if (message.skinName.equals("-")) {
                OfflineAuth.error("Player " + message.displayName + " not found on server, it seems");
            } else {
                //OfflineAuth.varInstanceClient.skinRegistry.add(message.uuid, message.skinname);
                IMessage msg = new DownloadSkinPacket.SimpleMessage(message.skinName, message.displayName);
                PacketHandler.net.sendToServer(msg);
            }
            OfflineAuth.varInstanceClient.clientRegistry.setSkinName(message.displayName, message.skinName);
            OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(message.displayName, false);
            return null;
        }

        return null;
    }

    public static class SimpleMessage implements IMessage
    {
        private final String sep = ",";
        private int exchangecode;  // When server queries for password, it's 0. When client responds, it's 1
        private String displayName;
        private String skinName;

        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public SimpleMessage() {}

        public SimpleMessage(String displayName)
        {
            this.exchangecode = 0;
            this.displayName = displayName;
            this.skinName = "-";
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            String dataString = ByteBufUtils.readUTF8String(buf);
            String[] dataStringSplit = dataString.split(sep);
            this.exchangecode = Integer.parseInt(dataStringSplit[0]);
            this.displayName = dataStringSplit[1];
            if (dataStringSplit.length == 3) {
                this.skinName = dataStringSplit[2];
            }
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, this.exchangecode + sep + this.displayName + sep + this.skinName);
        }
    }
}