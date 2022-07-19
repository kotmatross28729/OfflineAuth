package trollogyadherent.offlineauth.packet;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.compress.utils.IOUtils;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;
import trollogyadherent.offlineauth.registry.data.ServerPlayerData;
import trollogyadherent.offlineauth.request.Request;
import trollogyadherent.offlineauth.request.RequestUtil;
import trollogyadherent.offlineauth.request.objects.VibeCheckRequestBodyObject;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.rest.RestUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class PlayerJoinPacket implements IMessageHandler<PlayerJoinPacket.SimpleMessage, IMessage>
{
    /* Server to Client: give me your password */
    /* Client to Server; *sends password*. If the hash does not match, player kicked */
    @Override
    public IMessage onMessage(SimpleMessage message, MessageContext ctx)
    {
        /* This happens on the client after it got a request to send credentials */
        if (ctx.side.isClient() && message.exchangecode == 0)
        {
            /* Deleting skin cache */
            ClientSkinUtil.clearSkinCache();

            OAServerData oasd = Util.getOAServerDatabyIP(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), Util.getPort(OfflineAuth.varInstanceClient.selectedServerData));
            if (oasd == null) {
                OfflineAuth.error("OASD null!");
                return message;
            }



            try {
                if (ClientUtil.getServerPublicKey(oasd.getIp(), oasd.getRestPort()) == null) {
                    OfflineAuth.error("Public server key not in cache!");
                    return message;
                }
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
            System.out.println("PlayerJoinPacket onMessage triggered, code 0 (from server)");

            /*message.identifier = oasd.getIdentifier();
            message.displayname = oasd.getDisplayName();
            message.password = oasd.getPassword();*/

            String clientKeyToken = "";
            if (oasd.isUsingKey()) {
                PublicKey clientPubKey;
                PrivateKey clientPrivKey;
                try {
                    clientPubKey = RsaKeyUtil.loadPublicKey(oasd.getPublicKeyPath());
                    clientPrivKey = RsaKeyUtil.loadPrivateKey(oasd.getPrivateKeyPath());
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                    //throw new RuntimeException(e);
                    OfflineAuth.error(Arrays.toString(e.getStackTrace()));
                    return message;
                }
                if (clientPrivKey == null || clientPubKey == null) {
                    OfflineAuth.error("Client Private or public key is null");
                    return message;
                }
                String tempToken = null;
                try {
                    tempToken = Request.getChallengeToken(oasd.getIp(), oasd.getRestPort(), oasd.getIdentifier(), clientPubKey, clientPrivKey, ServerKeyTokenRegistry.TokenType.VIBECHECK);
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                         InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                         BadPaddingException | InvalidKeyException e) {
                    //throw new RuntimeException(e);
                    OfflineAuth.error(Arrays.toString(e.getStackTrace()));
                    return message;
                }
                if (tempToken == null) {
                    OfflineAuth.error("clientToken is null!");
                    return message;
                }
                clientKeyToken = tempToken;
            }

            AesKeyUtil.AesKeyPlusIv aesKeyPlusIv = null;
            try {
                aesKeyPlusIv = Request.getServerTempKeyPlusIv(oasd.getIp(), oasd.getRestPort());
            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
            if (aesKeyPlusIv == null) {
                OfflineAuth.error("aesKeyPlusIv is null!");
                return message;
            }

            try {
                //message.encryptedData = EntityUtils.toString(RequestUtil.getVibeCheckRequestBody(aesKeyPlusIv, oasd.getIdentifier(), oasd.getDisplayName(), oasd.getPassword(), clientKeyToken));
                message.encryptedData = Base64.getEncoder().encodeToString(IOUtils.toByteArray(RequestUtil.getVibeCheckRequestBody(aesKeyPlusIv, oasd.getIdentifier(), oasd.getDisplayName(), oasd.getPassword(), clientKeyToken).getContent()));
            } catch (InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchPaddingException |
                     NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IOException e) {
                //throw new RuntimeException(e);
                OfflineAuth.error(Arrays.toString(e.getStackTrace()));
                return message;
            }

            message.exchangecode = 1;
            return message;
        }

        /* This happens on server when credentials are received */
        EntityPlayerMP entityPlayerMP = ctx.getServerHandler().playerEntity;

        try {
            if (ctx.side.isServer() && message.exchangecode == 1)
            {
                for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    if (((EntityPlayerMP)o).getDisplayName().equals(entityPlayerMP.getDisplayName())) {
                        continue;
                    }
                    IMessage msg = new DeletePlayerFromClientRegPacket.SimpleMessage(entityPlayerMP.getDisplayName());
                    PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
                }

                System.out.println("PlayerJoinPacket onMessage triggered, code 1 (from client)");
                if (message.encryptedData.equals("-")) {
                    entityPlayerMP.playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
                }

                String ip  = ((InetSocketAddress)ctx.getServerHandler().playerEntity.playerNetServerHandler.netManager.getSocketAddress()).getAddress().getHostAddress();
                String host = ((InetSocketAddress)ctx.getServerHandler().playerEntity.playerNetServerHandler.netManager.getSocketAddress()).getHostString();
                if (!OfflineAuth.varInstanceServer.keyRegistry.ipHasKeyPair(ip, host)) {
                    OfflineAuth.info("No keypair associated with this host and ip");
                    entityPlayerMP.playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
                    return null;
                }
                byte[] encryptedMessageBytes = Base64.getDecoder().decode(message.encryptedData);
                VibeCheckRequestBodyObject rbo = (VibeCheckRequestBodyObject) RestUtil.getRequestBodyObject(encryptedMessageBytes, OfflineAuth.varInstanceServer.keyRegistry.getAesKeyPlusIv(ip, host), VibeCheckRequestBodyObject.class);

                String identifier = rbo.getIdentifier();
                String displayname = rbo.getDisplayname();
                String password = rbo.getPassword();
                String token = rbo.getClientKeyToken();
                ServerKeyTokenRegistry.TokenType type = rbo.getType();

                boolean validToken = false;
                if (token.length() > 0) {
                    validToken = OfflineAuth.varInstanceServer.keyTokenRegistry.grantIdentifierAccess(identifier, token, type);
                }

                try {
                    String displayName = "-";
                    if (validToken) {
                        DBPlayerData dbpd = Database.getPlayerDataByIdentifier(identifier);
                        if (dbpd != null) {
                            displayName = dbpd.getDisplayname();
                        }
                    } else {
                        displayName = Database.playerValid(identifier, displayname, password);
                    }

                    if (!displayName.equals("-")) {
                        if (!displayname.equals(ctx.getServerHandler().playerEntity.getDisplayName())) {
                            entityPlayerMP.playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
                        }

                        OfflineAuth.info("User " + entityPlayerMP.getDisplayName() + " successfully logged in");

                        /* Setting our own UUID */
                        DBPlayerData dbpd = Database.getPlayerDataByDisplayName(ctx.getServerHandler().playerEntity.getDisplayName());
                        if (dbpd == null) {
                            System.out.println("dbpd null");
                            entityPlayerMP.playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
                            return null;
                        }

                        if (OfflineAuth.varInstanceServer.DEBUGtamperWithUUID) {
                            //ctx.getServerHandler().playerEntity.field_146106_i.id = dbpd.getUuid();
                            try {
                                //OfflineAuth.varInstanceClient.uuidIdField.set(ctx.getServerHandler().playerEntity.field_146106_i, dbpd.getUuid());

                                //UUID uuid = Util.genRealUUID();
                                UUID uuid = UUID.fromString(dbpd.getUuid());
                                OfflineAuth.varInstanceServer.uuidIdField.set(ctx.getServerHandler().playerEntity.field_146106_i, uuid);
                                OfflineAuth.varInstanceServer.uuidIdField2.set(ctx.getServerHandler().playerEntity, uuid);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        /* skin resolution */
                        String skinName = ServerSkinUtil.getRandomDefaultSkinName();
                        if (dbpd.getSkinBytes().length > 1) {
                            ServerSkinUtil.saveBytesToSkinCache(dbpd.getSkinBytes(), dbpd.getDisplayname());
                            skinName = dbpd.getDisplayname();
                        }

                        /* Adding player to registry */
                        System.out.println("Adding player " + dbpd.getDisplayname() + " to server playerRegistry");
                        System.out.println(OfflineAuth.varInstanceServer.playerRegistry);
                        OfflineAuth.varInstanceServer.playerRegistry.add(new ServerPlayerData(dbpd.getIdentifier(), dbpd.getDisplayname(), dbpd.getUuid(), skinName));

                    } else {
                        entityPlayerMP.playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
                    }
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    entityPlayerMP.playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
                    OfflineAuth.error(e.getMessage());
                    e.printStackTrace();
                }

                /*for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
                    IMessage msg = new ResetCachesPacket.SimpleMessage();
                    PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
                }*/
                return null;
            } else {
                entityPlayerMP.playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
            }
        } catch (Exception e) {
            entityPlayerMP.playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
            OfflineAuth.error(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static class SimpleMessage implements IMessage
    {
        final String sep = ",";
        private int exchangecode;  // When server queries for password, it's 0. When client responds, it's 1
        /*private String password;
        private String displayname;
        private String identifier;*/
        private String encryptedData;

        // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        //public SimpleMessage() {}

        public SimpleMessage()
        {
            this.exchangecode = 0;
            /*this.identifier = "-";
            this.displayname = "-";
            this.password = "-";*/
            this.encryptedData = "-";
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            String dataString = ByteBufUtils.readUTF8String(buf);
            String[] dataStringSplit = dataString.split(sep);
            this.exchangecode = Integer.parseInt(dataStringSplit[0]);
            /*this.identifier = dataStringSplit[1];
            this.displayname = dataStringSplit[2];
            this.password = dataStringSplit[3];*/
            this.encryptedData = dataStringSplit[1];
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            //ByteBufUtils.writeUTF8String(buf, this.exchangecode + "," + this.identifier + "," + this.displayname + "," + this.password);
            ByteBufUtils.writeUTF8String(buf, this.exchangecode + sep + this.encryptedData);
        }
    }
}