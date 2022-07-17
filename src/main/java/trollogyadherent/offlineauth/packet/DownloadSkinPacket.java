package trollogyadherent.offlineauth.packet;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.Util;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class DownloadSkinPacket implements IMessageHandler<DownloadSkinPacket.SimpleMessage, IMessage> {

        /* This is a longer ping pong than PlayerJoinPacket */
        /* 1) Client to Server: give me hash of skin under this name */
        /* 2) Server to Client: *hash* */
        /* 3) Client to Server: if hash does not match, ask for file */
        /* 4) Server to Client: if asked, send skin file */
        /* ??? */
        /* Profit */
        @Override
        public IMessage onMessage(DownloadSkinPacket.SimpleMessage message, MessageContext ctx)
        {
            if (ctx.side.isServer() && message.exchangeCode == 0)
            {
                System.out.println("DownloadSkinPacket onMessage triggered, code 0 (from client)");
                System.out.println(ctx.getServerHandler().playerEntity.getDisplayName() + " asks for hash of skin " + message.skinName);
                if (message.skinName == null) {
                    OfflineAuth.warn("DownloadSkinPacket: got null skinName");
                    return null;
                }
                if (ServerSkinUtil.skinCachedOnServer(message.skinName)) {
                        message.skinHash = Util.sha1Code(ServerSkinUtil.getSkinFile(message.skinName));
                    if (message.skinHash == null) {
                        message.skinHash = "-1";
                        OfflineAuth.error("Failed to get hash for skin " + message.skinName);
                        //e.printStackTrace();
                    }
                } else {
                    DBPlayerData dbpd = Database.getPlayerDataByIdentifier(message.skinName);
                    if (dbpd == null) {
                        message.skinHash = "-1";
                        message.exchangeCode = 1;
                        return message;
                    }
                    byte[] skinBytes = dbpd.getSkinBytes();
                    if (skinBytes == null || skinBytes.length == 1) {
                        message.skinHash = "-1";
                    }
                    ServerSkinUtil.saveBytesToSkinCache(dbpd.getSkinBytes(), dbpd.getDisplayname());

                        message.skinHash = Util.sha1Code(ServerSkinUtil.getSkinFile(message.skinName));
                     if (message.skinHash == null) {
                        message.skinHash = "-1";
                        OfflineAuth.error("Failed to get hash for skin " + message.skinName);
                    }
                }

                message.exchangeCode = 1;
                return message;
            }

            if (ctx.side.isClient() && message.exchangeCode == 1)
            {
                System.out.println("DownloadSkinPacket onMessage triggered, code 1 (from server)");
                System.out.println("Received hash: " + message.skinHash);

                if (message.skinHash.equals("-1")) {
                    OfflineAuth.warn("Skin " + message.skinName + " not found on server");
                    return null;
                }
                /*
                if (!ClientSkinUtil.skinCachedOnClient(message.skinName)) {
                    OfflineAuth.warn("Skin " + message.skinName + " not found on client");
                    return null;
                }
                */

                String localSkinHash = "0";
                if (ClientSkinUtil.skinPresentOnClient(message.skinName)) {
                    localSkinHash = Util.sha1Code(ClientSkinUtil.getSkinFile(message.skinName));
                }
                if (localSkinHash == null) {
                    return null;
                }
                /* That means we have cached the skin, and it's the same file */
                if (localSkinHash.equals(message.skinHash)) {
                    OfflineAuth.info("Skin " + message.skinName + " already cached, not downloading");
                    ResourceLocation skinResourceLocation = ClientSkinUtil.loadSkinFromCache(message.skinName);
                    OfflineAuth.varInstanceClient.clientRegistry.setResourceLocation(message.displayName, skinResourceLocation);
                    OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(message.displayName, false);
                    return null;
                } else {
                    /* The server will know we want to download the skin */
                    OfflineAuth.info("Skin " + message.skinName + " not cached, downloading");
                }


                message.exchangeCode = 2;
                return message;
            }

            if (ctx.side.isServer() && message.exchangeCode == 2)
            {
                System.out.println("DownloadSkinPacket onMessage triggered, code 2 (from client)");
                OfflineAuth.info("Player " + ctx.getServerHandler().playerEntity.getDisplayName() + " requests the skinbytes of " + message.skinName);

                try {
                    message.skinBytes = Util.fileToBytes(ServerSkinUtil.getSkinFile(message.skinName));
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

                message.exchangeCode = 3;
                return message;
            }

            if (ctx.side.isClient() && message.exchangeCode == 3)
            {
                System.out.println("DownloadSkinPacket onMessage triggered, code 3 (from server)");
                try {
                    OfflineAuth.info("Writing received skin to file: " + message.skinName);
                    ClientSkinUtil.bytesToClientSkin(message.skinBytes, message.skinName);
                    ResourceLocation skinResourceLocation = ClientSkinUtil.loadSkinFromCache(message.skinName);
                    OfflineAuth.varInstanceClient.clientRegistry.setResourceLocation(message.displayName, skinResourceLocation);
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
            private String skinName;
            private byte[] skinBytes;
            private String skinHash;

            // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
            public SimpleMessage() {}

            public SimpleMessage(String skinName, String displayName)
            {
                this.exchangeCode = 0;
                this.skinName = skinName;
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

                this.skinBytes = Arrays.copyOfRange(receivingData, 9 + byteDataLen, receivingData.length);

                String[] dataStringSplit = stringData.split(c);
                this.exchangeCode = Integer.parseInt(dataStringSplit[0]);
                this.skinName = dataStringSplit[1];
                this.displayName = dataStringSplit[2];
                if (dataStringSplit.length == 4) {
                    this.skinHash = dataStringSplit[3];
                }
            }

            @Override
            public void toBytes(ByteBuf buf)
            {
                try {
                    String stringData = this.exchangeCode + c + this.skinName + c + this.displayName + c + this.skinHash;
                    byte[] byteData = stringData.getBytes(Charsets.UTF_8);
                    int byteDataLen = byteData.length;
                    byte[] byteDataLenBytes = Util.fillByteArrayLeading(BigInteger.valueOf(byteDataLen).toByteArray(), 4);

                    int skinByteLen = this.skinBytes != null ? this.skinBytes.length : 0;
                    byte[] byteSkinLenBytes = Util.fillByteArrayLeading(BigInteger.valueOf(skinByteLen).toByteArray(), 4);

                    byte[] resultingData;
                    if (this.skinBytes != null) {
                       resultingData = Util.concatByteArrays(Util.concatByteArrays(byteDataLenBytes, byteSkinLenBytes), Util.concatByteArrays(byteData, this.skinBytes));
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