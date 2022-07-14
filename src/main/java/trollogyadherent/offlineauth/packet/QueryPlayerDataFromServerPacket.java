package trollogyadherent.offlineauth.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.registry.data.ServerPlayerData;

import java.io.IOException;

public class QueryPlayerDataFromServerPacket implements IMessageHandler<QueryPlayerDataFromServerPacket.SimpleMessage, IMessage>
{
    /* Client to Server: what is the skin filename for this uuid? */
    /* Server to Client: the filename is X. in case there is none, it returns "0". this would be the place probably to have custom default server skin TODO */
    @Override
    public IMessage onMessage(SimpleMessage message, MessageContext ctx)
    {
        if (ctx.side.isServer() && message.exchangecode == 0)
        {
            System.out.println("QueryPlayerDataFromServerPacket onMessage triggered, code 0 (from client)");
            System.out.println("Requesting user: " + ctx.getServerHandler().playerEntity.getDisplayName());
            System.out.println("Requested displayname: " + message.displayname);
            System.out.println("ServerSkinReg before any changes: " + OfflineAuth.varInstanceServer.playerRegistry);
            ServerPlayerData spd = OfflineAuth.varInstanceServer.playerRegistry.getPlayerDataByDisplayName(message.displayname);
            if (spd == null && message.displayname.equals(ctx.getServerHandler().playerEntity.getDisplayName())) {
                System.out.println("spd is null, message displayname is equal to the player displayname who sent packet (" + ctx.getServerHandler().playerEntity.getDisplayName() + ")");

                if (ctx.getServerHandler().playerEntity.getDisplayName().equals("test")) {
                    System.out.println("Player name test, setting skinname sneed, adding SkinData to reg");
                    message.skinname = "sneed";
                    //OfflineAuth.varInstanceServer.playerRegistry.add(message.uuid, "sneed");
                    //System.out.println("ServerSkinReg: " + OfflineAuth.varInstanceServer.playerRegistry);
                } else {
                    System.out.println("Player name other, setting skinname popbob, adding SkinData to reg");
                    message.skinname = "sans";
                    //OfflineAuth.varInstanceServer.playerRegistry.add(message.uuid, "sans");
                    //System.out.println("ServerSkinReg: " + OfflineAuth.varInstanceServer.playerRegistry);
                }

                DBPlayerData dbpd = Database.getPlayerDataByDisplayName(message.displayname);
                if (dbpd == null) {
                    OfflineAuth.error("No player associated with this displayname in the DB!");
                    return null;
                }
                message.uuid = dbpd.getUuid();
                message.identifier = dbpd.getIdentifier();
            } else if (spd != null){
                System.out.println("spd found for displayname " + spd.displayname + ". it says to set skin to " + spd.skinName);
                message.skinname = spd.skinName;
                message.identifier = spd.identifier;
                message.displayname = spd.displayname;
                message.uuid = spd.uuid;
                System.out.println("ServerSkinReg: " + OfflineAuth.varInstanceServer.playerRegistry);
            } else {
                System.out.println("spd is null and the displayname is not equal to the player who sent the packet. (displayname: " + message.displayname + ")");
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
            System.out.println("QueryPlayerDataFromServerPacket onMessage triggered, code 1 (from server)");
            System.out.println("Got skin name: " + message.skinname);

            try {
                if (message.skinname.equals("0")) {
                    OfflineAuth.varInstanceClient.playerRegistry.deleteByDisplayName(message.displayname);
                    OfflineAuth.varInstanceClient.playerRegistry.add(message.identifier, message.displayname, message.uuid, message.skinname);
                } else if (message.skinname.equals("1")) {

                } else {
                    OfflineAuth.varInstanceClient.playerRegistry.deleteByDisplayName(message.displayname);
                    OfflineAuth.varInstanceClient.playerRegistry.add(message.identifier, message.displayname, message.uuid, message.skinname);
                    IMessage msg = new DownloadSkinPacket.SimpleMessage(message.skinname);
                    PacketHandler.net.sendToServer(msg);
                }
            } catch (IOException e) {
                OfflineAuth.error("Failed to add playerdata to client player registry. name: " + message.displayname);
                e.printStackTrace();
            }
            OfflineAuth.varInstanceClient.queriedForPlayerData = false;
            return null;
        }

        return null;
    }

    public static class SimpleMessage implements IMessage
    {
        private int exchangecode;  // When server queries for password, it's 0. When client responds, it's 1
        private String displayname;
        private String identifier;
        private String uuid;
        private String skinname;

        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public SimpleMessage() {}

        public SimpleMessage(String displayname)
        {
            this.exchangecode = 0;
            this.displayname = displayname;
            this.identifier = "-";
            this.uuid = "-";
            this.skinname = "-";
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            String dataString = ByteBufUtils.readUTF8String(buf);
            String[] dataStringSplit = dataString.split(",");
            this.exchangecode = Integer.parseInt(dataStringSplit[0]);
            this.displayname = dataStringSplit[1];
            this.identifier = dataStringSplit[2];
            this.uuid = dataStringSplit[3];
            this.skinname = dataStringSplit[4];
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, this.exchangecode + "," + this.displayname + "," + this.identifier + "," + this.uuid + "," + this.skinname);
        }
    }
}