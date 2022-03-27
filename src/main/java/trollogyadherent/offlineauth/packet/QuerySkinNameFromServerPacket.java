package trollogyadherent.offlineauth.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.server.ServerSkinData;
import trollogyadherent.offlineauth.util.Util;

import java.io.IOException;

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
            System.out.println("Requesting player: " + ctx.getServerHandler().playerEntity.getDisplayName() + " (" + Util.offlineUUID(ctx.getServerHandler().playerEntity.getDisplayName()) + ")");
            System.out.println("Requested uuid: " + message.uuid);
            System.out.println("ServerSkinReg before any changes: " + OfflineAuth.varInstanceServer.skinRegistry);
            ServerSkinData sd = OfflineAuth.varInstanceServer.skinRegistry.getSkinDataByUUID(message.uuid);
            if (sd == null && message.uuid.equals(Util.offlineUUID(ctx.getServerHandler().playerEntity.getDisplayName()))) {
                System.out.println("sd is null, message uuid is equal to the player who sent packet (" + ctx.getServerHandler().playerEntity.getDisplayName() + ")");

                //message.skinname = "0";

                // below is temp and test only
                if (ctx.getServerHandler().playerEntity.getDisplayName().equals("test")) {
                    System.out.println("Player name test, setting skinname sneed, adding SkinData to reg");
                    message.skinname = "sneed";
                    OfflineAuth.varInstanceServer.skinRegistry.add(message.uuid, "sneed");
                    System.out.println("ServerSkinReg: " + OfflineAuth.varInstanceServer.skinRegistry);
                } else {
                    System.out.println("Player name other, setting skinname popbob, adding SkinData to reg");
                    message.skinname = "sans";
                    OfflineAuth.varInstanceServer.skinRegistry.add(message.uuid, "sans");
                    System.out.println("ServerSkinReg: " + OfflineAuth.varInstanceServer.skinRegistry);
                }
            } else if (sd != null){
                System.out.println("sd found for uuid " + sd.uuid + ". it says to set skin to " + sd.skinName);
                message.skinname = sd.skinName;
                System.out.println("ServerSkinReg: " + OfflineAuth.varInstanceServer.skinRegistry);
            } else {
                System.out.println("sd is null and the uuid is not equal to the player who sent the packet. (uuid: " + message.uuid + ")");
                /*for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    IMessage msg = new ResetCachesPacket.SimpleMessage();
                    PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
                }*/

                //IMessage msg = new ResetCachesPacket.SimpleMessage();
                //PacketHandler.net.sendTo(msg, ctx.getServerHandler().playerEntity);


                message.skinname = "1";
                //return message;
                //return null;
            }

            message.exchangecode = 1;

            System.out.println("Returning message to client. Skinname: " + message.skinname);
            return message;
        }

        if (ctx.side.isClient() && message.exchangecode == 1)
        {
            System.out.println("QuerySkinNameFromServerPacket onMessage triggered, code 1 (from server)");
            System.out.println("Got skin name: " + message.skinname);

            try {
                if (message.skinname.equals("0")) {
                    OfflineAuth.varInstanceClient.skinRegistry.add(message.uuid, null);
                } else if (message.skinname.equals("1")) {

                } else {
                    OfflineAuth.varInstanceClient.skinRegistry.add(message.uuid, message.skinname);
                    IMessage msg = new DownloadSkinPacket.SimpleMessage(message.skinname);
                    PacketHandler.net.sendToServer(msg);
                }
            } catch (IOException e) {
                OfflineAuth.error("Failed to add skin to client skin registry. name: " + message.skinname);
                e.printStackTrace();
            }
            OfflineAuth.varInstanceClient.queriedForSkinName = false;
            return null;
        }

        return null;
    }

    public static class SimpleMessage implements IMessage
    {
        private int exchangecode;  // When server queries for password, it's 0. When client responds, it's 1
        private String uuid;
        private String skinname;

        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public SimpleMessage() {}

        public SimpleMessage(int exchangecode, String uuid, String skinname)
        {
            this.exchangecode = exchangecode;
            this.uuid = uuid;
            this.skinname = skinname;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            String dataString = ByteBufUtils.readUTF8String(buf);
            String[] dataStringSplit = dataString.split(",");
            this.exchangecode = Integer.parseInt(dataStringSplit[0]);
            this.uuid = dataStringSplit[1];
            if (dataStringSplit.length == 3) {
                this.skinname = dataStringSplit[2];
            }
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, this.exchangecode + "," + this.uuid + "," + this.skinname);
        }
    }
}