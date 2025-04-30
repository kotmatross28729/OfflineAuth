package trollogyadherent.offlineauth.packet.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.clientdata.ClientData;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.util.Util;


public class SendAuthPortPacket implements IMessageHandler<SendAuthPortPacket.SimpleMessage, IMessage>  {

    /* Server to Client: reset your player registry caches */
    /* Client: resets caches */
    @Override
    public IMessage onMessage(SendAuthPortPacket.SimpleMessage message, MessageContext ctx)
    {
        if (ctx.side.isClient() && message.exchangecode == 0)
        {
            String portString = String.valueOf(message.port);
            OfflineAuth.debug("Got port: " + message.port);

            /* Trying to get privateKeyPath and publicServerKeyPath from previously saved data, since they can't be taken from this gui screen */
            OAServerData oaServerDataSaved = null;
            for (OAServerData oasd : OfflineAuth.varInstanceClient.OAServerDataCache) {
                if (oasd.getIp().equals(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData)) && oasd.getPort().equals(Util.getPort(OfflineAuth.varInstanceClient.selectedServerData))) {
                    oaServerDataSaved = oasd;
                    break;
                }
            }
            if (oaServerDataSaved != null) {
                OfflineAuth.debug("oaServerDataSaved not null, setting rest port");
                oaServerDataSaved.setRestPort(portString);
            }

            /* Actual part where the OfflineAuth.varInstanceClient.OAserverDataCache variable gets dumped into a json file */
            ClientData.saveData();
            message.exchangecode = 1;
            return message;
        }

        if (ctx.side.isServer() && message.exchangecode == 1) {
            IMessage msg = new PlayerJoinPacket.SimpleMessage();
            PacketHandler.net.sendTo(msg, ctx.getServerHandler().playerEntity);
        } else if (ctx.side.isServer()) {
            ctx.getServerHandler().playerEntity.playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
        }
        return null;
    }

    public static class SimpleMessage implements IMessage
    {
        private final String sep = ",";
        private int port;
        private int exchangecode;

        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public SimpleMessage() {}

        public SimpleMessage(int port) {
            this.port = port;
            this.exchangecode = 0;
        }


        @Override
        public void fromBytes(ByteBuf buf)
        {
            String dataString = ByteBufUtils.readUTF8String(buf);
            String[] dataStringSplit = dataString.split(sep);
            this.exchangecode = Integer.parseInt(dataStringSplit[0]);
            this.port = Integer.parseInt(dataStringSplit[1]);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, this.exchangecode + sep + this.port);
        }
    }
}