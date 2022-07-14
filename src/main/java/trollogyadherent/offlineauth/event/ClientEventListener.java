package trollogyadherent.offlineauth.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.QueryPlayerDataFromServerPacket;
import trollogyadherent.offlineauth.packet.QuerySkinNameFromServerPacket;
import trollogyadherent.offlineauth.registry.data.ClientPlayerData;
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.util.Util;

import java.io.IOException;

public class ClientEventListener {
    /* Clears OfflineAuth.playerRegistry List. Otherwise the SkinData would exist, but when changing worlds, the texture is unloaded */
    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent e) throws IOException {
        //System.out.println("something joined: " + e.entity.getClass().getName());

        /* If singleplayer, we don't do that */
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer()) {
            return;
        }

        if (!(e.entity instanceof EntityPlayerMP)) {
            return;
        }

        if (Util.isServer()) {
            return;
        }

        System.out.println("Detected player join event, player displayname: " + ((EntityPlayerMP) e.entity).getDisplayName());

        OfflineAuth.varInstanceClient.entityPlayerRegistry.add((EntityPlayer) e.entity);

        /* Asking server for data about this guy, adding to out playerreg there */
        IMessage msg = new QueryPlayerDataFromServerPacket.SimpleMessage(Minecraft.getMinecraft().thePlayer.getDisplayName());
        PacketHandler.net.sendToServer(msg);
        /*OfflineAuth.varInstanceClient.playerRegistry.add(new ClientPlayerData(oasd.getIdentifier(), oasd.getDisplayName(), ));
        OfflineAuth.info("Clearing memory skin cache 1");
        OfflineAuth.varInstanceClient.playerRegistry.clear();
        OfflineAuth.varInstanceClient.playerRegistry.clear();*/
    }

    /* Triggers when another player joins a server */
    @SubscribeEvent
    public void onOtherPlayerJoin(EntityJoinWorldEvent e) throws IOException {
        /* If singleplayer, we don't do that */
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer()) {
            return;
        }

        if (!(e.entity instanceof EntityOtherPlayerMP)) {
            return;
        }

        if (Util.isServer()) {
            return;
        }

        OfflineAuth.varInstanceClient.entityPlayerRegistry.add((EntityPlayer) e.entity);
        /* Asking server for data about this guy, adding to out playerreg there */
        IMessage msg = new QueryPlayerDataFromServerPacket.SimpleMessage(((AbstractClientPlayer) e.entity).getDisplayName());
        PacketHandler.net.sendToServer(msg);

        /*System.out.println("Detected player join event, player displayname: " + ((AbstractClientPlayer) e.entity).getDisplayName());
        OfflineAuth.varInstanceClient.playerRegistry.add(new ClientPlayerData((AbstractClientPlayer) e.entity));
        OfflineAuth.info("Clearing memory skin cache 2");
        OfflineAuth.varInstanceClient.playerRegistry.clear();
         */
    }

    /* Well, this does not trigger when other palyers join lol */
    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent e) {
        /* If singleplayer, we don't do that */
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer()) {
            return;
        }

        if (Util.isServer()) {
            return;
        }

        System.out.println("Detected player leave event, player displayname: " + e.player.getDisplayName());
        OfflineAuth.varInstanceClient.playerRegistry.deleteByDisplayName(e.player.getDisplayName());

        //OfflineAuth.info("Clearing memory skin cache 3");
        //OfflineAuth.varInstanceClient.playerRegistry.clear();

        OfflineAuth.info("Left server, clearing skin query status 1");
        OfflineAuth.varInstanceClient.queriedForPlayerData = false;

        OfflineAuth.varInstanceClient.entityPlayerRegistry.removeByDisplayName(e.player.getDisplayName());
    }

    /* Clears OfflineAuth.skinCache List. Otherwise the SkinData would exist, but when changing worlds, the texture is unloaded */
    /* This triggers on multiplayer */
    @SubscribeEvent
    public void onPlayerJoinFMLEvent(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        OfflineAuth.info("Clearing memory skin cache 4");
        OfflineAuth.varInstanceClient.playerRegistry.clear();
        OfflineAuth.varInstanceClient.queriedForPlayerData = false;
        System.out.println("Joined MP world");
    }

    @SubscribeEvent
    public void onPlayerLeaveFMLEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        /* If singleplayer, we don't do that */
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer()) {
            return;
        }

        if (Util.isServer()) {
            return;
        }

        OfflineAuth.info("Left server, clearing skin query status 2");
        OfflineAuth.varInstanceClient.queriedForPlayerData = false;

        OfflineAuth.info("Clearing memory skin cache 5");
        OfflineAuth.varInstanceClient.playerRegistry.clear();
        System.out.println("Exited MP world");
        //OfflineAuth.varInstanceClient.onDedicatedServer = false;
    }

    /* This loads cached skins in singleplayer */
    /* Every tick checks if skin is null, if yes it tries to load one */
    @SubscribeEvent
    public void ontick(TickEvent.PlayerTickEvent e) {
        if (ClientUtil.isSinglePlayer()) {
            return;
        }

        /* If singleplayer, we don't do that */
        if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().isIntegratedServerRunning()) {
            //return;
        }

        /* IIRC this code still runs on the internal server??? anyways, without this return it copmplains about missing opengl context */
        if (!e.player.worldObj.isRemote) {
            return;
        }

        /* Tick only for current player */
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (!e.player.getDisplayName().equals(player.getDisplayName())) {
            return;
        }

        if (e.player instanceof FakePlayer) {
            return;
        }

        if (OfflineAuth.varInstanceClient.playerRegistry.getIdentifierFromDisplayName(player.getDisplayName()) == null && !OfflineAuth.varInstanceClient.queriedForPlayerData) {
            System.out.println("Querying for skin of the local player (" + player.getDisplayName() + "), Invoked from ontick");
            OfflineAuth.varInstanceClient.queriedForPlayerData = true;
            IMessage msg = new QueryPlayerDataFromServerPacket.SimpleMessage(Minecraft.getMinecraft().thePlayer.getDisplayName());
            PacketHandler.net.sendToServer(msg);
        }
    }

    @SubscribeEvent()
    public void render(RenderPlayerEvent.Pre e) throws IllegalAccessException {
        if (ClientUtil.isSinglePlayer()) {
            return;
        }

        if (!e.entityPlayer.worldObj.isRemote) {
            return;
        }

        /* If singleplayer, we don't do that */
        if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().isIntegratedServerRunning()) {
            //return;
        }

        if (e.isCanceled() || e.entityPlayer == null) {
            return;
        }

        if (e.entityPlayer instanceof FakePlayer) {
            return;
        }

        if (OfflineAuth.varInstanceClient.textureManager == null) {
            OfflineAuth.varInstanceClient.textureManager = Minecraft.getMinecraft().getTextureManager();
        }

        EntityPlayer entityPlayerMP = e.entityPlayer;
        ClientPlayerData cpd = OfflineAuth.varInstanceClient.playerRegistry.getPlayerDataByDisplayName(entityPlayerMP.getDisplayName());
        if (cpd == null && !OfflineAuth.varInstanceClient.queriedForPlayerData) {
            System.out.println("Querying for skin of the player " + entityPlayerMP.getDisplayName() + ", Invoked from render");
            System.out.println(OfflineAuth.varInstanceClient.playerRegistry);
            OfflineAuth.varInstanceClient.queriedForPlayerData = true;
            IMessage msg = new QueryPlayerDataFromServerPacket.SimpleMessage(entityPlayerMP.getDisplayName());
            PacketHandler.net.sendToServer(msg);
        } else if (cpd != null && cpd.skinName != null && cpd.entityPlayer != null) {
            OfflineAuth.varInstanceClient.skinLocationfield.set(cpd.entityPlayer, cpd.resourceLocation);
        } else if (cpd != null && cpd.skinName != null) {
            OfflineAuth.varInstanceClient.playerRegistry.deleteByIdentifier(cpd.identifier);
        }
    }
}
