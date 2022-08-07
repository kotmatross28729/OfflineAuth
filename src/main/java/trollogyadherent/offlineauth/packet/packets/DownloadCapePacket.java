package trollogyadherent.offlineauth.packet.packets;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.gui.skin.cape.CapeObject;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.Util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DownloadCapePacket implements IMessageHandler<DownloadCapePacket.SimpleMessage, IMessage> {

        /* This is a longer ping pong than PlayerJoinPacket */
        /* 1) Client to Server: give me hash of cape under this name */
        /* 2) Server to Client: *hash* */
        /* 3) Client to Server: if hash does not match, ask for file */
        /* 4) Server to Client: if asked, send cape file */
        /* ??? */
        /* Profit */
        @Override
        public IMessage onMessage(DownloadCapePacket.SimpleMessage message, MessageContext ctx)
        {
            if (ctx.side.isServer() && message.exchangeCode == 0)
            {
                //System.out.println("DownloadSkinPacket onMessage triggered, code 0 (from client)");
                //System.out.println(ctx.getServerHandler().playerEntity.getDisplayName() + " asks for hash of skin " + message.skinName);
                if (message.capeName == null) {
                    OfflineAuth.warn("DownloadCapePacket: got null skinName");
                    return null;
                }
                if (ServerSkinUtil.capeCachedOnServer(message.capeName)) {
                        message.capeHash = Util.sha1Code(ServerSkinUtil.getCapeFile(message.capeName));
                    if (message.capeHash == null) {
                        message.capeHash = "-1";
                        OfflineAuth.error("Failed to get hash for cape " + message.capeName);
                        //e.printStackTrace();
                    }
                } else {
                    //DBPlayerData dbpd = Database.getPlayerDataByIdentifier(message.skinName);
                    DBPlayerData dbpd = Database.getPlayerDataByDisplayName(message.displayName);
                    if (dbpd == null) {
                        message.capeHash = "-1";
                        message.exchangeCode = 1;
                        return message;
                    }
                    byte[] capeBytes = dbpd.getCapeBytes();
                    if (capeBytes == null || capeBytes.length == 1) {
                        message.capeHash = "-1";
                        message.exchangeCode = 1;
                        return message;
                    }

                    ServerSkinUtil.saveBytesToCapeCache(dbpd.getCapeBytes(), dbpd.getDisplayname());
                    message.capeHash = Util.sha1Code(ServerSkinUtil.getCapeFile(message.capeName));

                    if (message.capeHash == null) {
                        message.capeHash = "-1";
                        OfflineAuth.error("Failed to get hash for skin " + message.capeName);
                        message.exchangeCode = 1;
                        return message;
                    }
                }

                message.exchangeCode = 1;
                return message;
            }

            if (ctx.side.isClient() && message.exchangeCode == 1)
            {
                //System.out.println("DownloadSkinPacket onMessage triggered, code 1 (from server)");
                //System.out.println("Received hash: " + message.skinHash);

                if (message.capeHash.equals("-1")) {
                    OfflineAuth.warn("Cape " + message.capeName + " not found on server");
                    return null;
                }
                /*
                if (!ClientSkinUtil.skinCachedOnClient(message.skinName)) {
                    OfflineAuth.warn("Skin " + message.skinName + " not found on client");
                    return null;
                }
                */

                String localCapeHash = "0";
                if (ClientSkinUtil.capePresentOnClient(message.capeName)) {
                    localCapeHash = Util.sha1Code(ClientSkinUtil.getCapeFile(message.capeName));
                }
                if (localCapeHash == null) {
                    return null;
                }
                /* That means we have cached the cape, and it's the same file */
                if (localCapeHash.equals(message.capeHash)) {
                    OfflineAuth.info("Cape " + message.capeName + " already cached, not downloading");
                    //ResourceLocation skinResourceLocation = ClientSkinUtil.loadSkinFromCache(message.capeName);
                    CapeObject capeObject = ClientSkinUtil.getCapeObject(message.capeName);
                    OfflineAuth.varInstanceClient.clientRegistry.setCapeObject(message.displayName, capeObject);
                    OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(message.displayName, false);
                    return null;
                } else {
                    /* The server will know we want to download the cape */
                    OfflineAuth.info("Cape " + message.capeName + " not cached, downloading");
                }


                message.exchangeCode = 2;
                return message;
            }

            if (ctx.side.isServer() && message.exchangeCode == 2)
            {
                //System.out.println("DownloadSkinPacket onMessage triggered, code 2 (from client)");
                OfflineAuth.info("Player " + ctx.getServerHandler().playerEntity.getDisplayName() + " requests the capebytes of " + message.capeName);

                try {
                    message.capeBytes = Util.fileToBytes(ServerSkinUtil.getCapeFile(message.capeName));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                message.exchangeCode = 3;
                return message;
            }

            if (ctx.side.isClient() && message.exchangeCode == 3)
            {
                //System.out.println("DownloadSkinPacket onMessage triggered, code 3 (from server)");
                try {
                    OfflineAuth.info("Writing received cape to file: " + message.capeName);
                    if (message.capeBytes.length > Math.max(Config.maxCapeBytes, 50000)) {
                        OfflineAuth.error("Error, server sent sussily much bytes, aborting");
                        OfflineAuth.varInstanceClient.clientRegistry.setCapeObject(message.displayName, null);
                        OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(message.displayName, false);
                        return null;
                    }
                    ClientSkinUtil.bytesToClientCape(message.capeBytes, message.capeName);
                    //ResourceLocation skinResourceLocation = ClientSkinUtil.loadSkinFromCache(message.capeName);
                    CapeObject capeObject = ClientSkinUtil.getCapeObject(message.capeName);
                    OfflineAuth.varInstanceClient.clientRegistry.setCapeObject(message.displayName, capeObject);
                    OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(message.displayName, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            return null;
        }

        public static class SimpleMessage implements IMessage
        {
            private static final String c = ",";

            private int exchangeCode;  // When server queries for password, it's 0. When client responds, it's 1
            private String displayName;
            private String capeName;
            private byte[] capeBytes;
            private String capeHash;

            // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
            public SimpleMessage() {}

            public SimpleMessage(String capeName, String displayName)
            {
                this.exchangeCode = 0;
                this.capeName = capeName;
                this.displayName = displayName;
            }

            @Override
            public void fromBytes(ByteBuf buf)
            {
                byte[] receivingData = buf.array();

                byte[] byteDataLenBytes = Arrays.copyOfRange(receivingData, 1, 5);
                int byteDataLen = new BigInteger(byteDataLenBytes).intValue();
                byte[] byteData = Arrays.copyOfRange(receivingData, 9, 9 + byteDataLen);
                String stringData = new String(byteData, StandardCharsets.UTF_8);

                this.capeBytes = Arrays.copyOfRange(receivingData, 9 + byteDataLen, receivingData.length);

                String[] dataStringSplit = stringData.split(c, -1);
                this.exchangeCode = Integer.parseInt(dataStringSplit[0]);
                this.capeName = dataStringSplit[1];
                this.displayName = dataStringSplit[2];
                this.capeHash = dataStringSplit[3];
            }

            @Override
            public void toBytes(ByteBuf buf)
            {
                try {
                    String stringData = this.exchangeCode + c + this.capeName + c + this.displayName + c + this.capeHash + c;
                    byte[] byteData = stringData.getBytes(Charsets.UTF_8);
                    int byteDataLen = byteData.length;
                    byte[] byteDataLenBytes = Util.fillByteArrayLeading(BigInteger.valueOf(byteDataLen).toByteArray(), 4);

                    int skinByteLen = this.capeBytes != null ? this.capeBytes.length : 0;
                    byte[] byteSkinLenBytes = Util.fillByteArrayLeading(BigInteger.valueOf(skinByteLen).toByteArray(), 4);

                    byte[] resultingData;
                    if (this.capeBytes != null) {
                       resultingData = Util.concatByteArrays(Util.concatByteArrays(byteDataLenBytes, byteSkinLenBytes), Util.concatByteArrays(byteData, this.capeBytes));
                    } else {
                        resultingData = Util.concatByteArrays(Util.concatByteArrays(byteDataLenBytes, byteSkinLenBytes), byteData);
                    }
                    buf.writeBytes(resultingData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }