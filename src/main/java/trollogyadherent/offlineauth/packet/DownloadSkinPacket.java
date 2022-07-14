package trollogyadherent.offlineauth.packet;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.ClientUtil;
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
                    try {
                        message.skinHash = Util.fileHash(ServerSkinUtil.getSkinFile(message.skinName));
                    } catch (NoSuchAlgorithmException e) {
                        OfflineAuth.error("Failed to get hash for skin " + message.skinName);
                        OfflineAuth.error(e.getMessage());
                        //e.printStackTrace();
                    }
                } else {
                    message.skinHash = "-1";
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
                /*if (!ClientSkinUtil.skinCachedOnClient(message.skinName)) {
                    OfflineAuth.warn("Skin " + message.skinName + " not found on client");
                    return null;
                }*/

                try {
                    String localSkinHash = "0";
                    if (ClientSkinUtil.skinCachedOnClient(message.skinName)) {
                        localSkinHash = Util.fileHash(ClientSkinUtil.getSkinFile(message.skinName));
                    }
                    if (localSkinHash == null) {
                        return null;
                    }
                    /* That means we have cached the skin, and it's the same file */
                    if (localSkinHash.equals(message.skinHash)) {
                        OfflineAuth.info("Skin " + message.skinName + " cached");
                        return null;
                    } else {
                        /* The server will know we want to download the skin */
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return null;
                }

                message.exchangeCode = 2;
                return message;
            }

            if (ctx.side.isServer() && message.exchangeCode == 2)
            {
                System.out.println("DownloadSkinPacket onMessage triggered, code 2 (from client)");

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
                    ClientSkinUtil.bytesToClientSkin(message.skinBytes, message.skinName);
                    OfflineAuth.varInstanceClient.playerRegistry.clear();
                    OfflineAuth.varInstanceClient.queriedForSkinFile = false;
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
            private String skinName;
            private byte[] skinBytes;
            private String skinHash;

            // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
            public SimpleMessage() {}

            public SimpleMessage(String skinname)
            {
                this.exchangeCode = 0;
                this.skinName = skinname;
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

                String[] dataStringSplit = stringData.split(",");
                this.exchangeCode = Integer.parseInt(dataStringSplit[0]);
                this.skinName = dataStringSplit[1];
                if (dataStringSplit.length == 3) {
                    this.skinHash = dataStringSplit[2];
                }
            }

            @Override
            public void toBytes(ByteBuf buf)
            {
                try {
                    String stringData = this.exchangeCode + c + this.skinName + c + this.skinHash;
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