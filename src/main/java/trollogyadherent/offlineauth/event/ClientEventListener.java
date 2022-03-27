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
import trollogyadherent.offlineauth.packet.QuerySkinNameFromServerPacket;
import trollogyadherent.offlineauth.skin.client.ClientPlayerData;
import trollogyadherent.offlineauth.skin.client.ClientSkinData;
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.util.Util;

public class ClientEventListener {
    /* Clears OfflineAuth.skinCache List. Otherwise the SkinData would exist, but when changing worlds, the texture is unloaded */
    /* This triggers on singleplayer */
    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent e) {
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

        System.out.println("Dected player join event, player displayname: " + ((EntityPlayerMP) e.entity).getDisplayName());
        OfflineAuth.varInstanceClient.playerRegistry.add(new ClientPlayerData((EntityClientPlayerMP) e.entity));

        OfflineAuth.info("Clearing memory skin cache 1");
        OfflineAuth.varInstanceClient.skinRegistry.clear();
        OfflineAuth.varInstanceClient.playerRegistry.clear();
    }

    /* Triggers when another player joins a server */
    @SubscribeEvent
    public void onOtherPlayerJoin(EntityJoinWorldEvent e) {
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

        System.out.println("Detected player join event, player displayname: " + ((AbstractClientPlayer) e.entity).getDisplayName());
        OfflineAuth.varInstanceClient.playerRegistry.add(new ClientPlayerData((AbstractClientPlayer) e.entity));

        OfflineAuth.info("Clearing memory skin cache 2");
        OfflineAuth.varInstanceClient.skinRegistry.clear();
        OfflineAuth.varInstanceClient.playerRegistry.clear();
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
        OfflineAuth.varInstanceClient.playerRegistry.deleteByUUID(Util.offlineUUID(e.player.getDisplayName()));

        OfflineAuth.info("Clearing memory skin cache 3");
        OfflineAuth.varInstanceClient.skinRegistry.clear();
        OfflineAuth.varInstanceClient.playerRegistry.clear();

        OfflineAuth.info("Left server, clearing skin query status 1");
        OfflineAuth.varInstanceClient.queriedForSkinName = false;
    }

    /* Clears OfflineAuth.skinCache List. Otherwise the SkinData would exist, but when changing worlds, the texture is unloaded */
    /* This triggers on multiplayer */
    @SubscribeEvent
    public void onPlayerJoinFMLEvent(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        OfflineAuth.info("Clearing memory skin cache 4");
        OfflineAuth.varInstanceClient.skinRegistry.clear();
        OfflineAuth.varInstanceClient.playerRegistry.clear();
        OfflineAuth.varInstanceClient.queriedForSkinName = false;
        System.out.println("Joined MP world");
        //OfflineAuth.varInstanceClient.onDedicatedServer = true;
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
        OfflineAuth.varInstanceClient.queriedForSkinName = false;

        OfflineAuth.info("Clearing memory skin cache 5");
        OfflineAuth.varInstanceClient.skinRegistry.clear();
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

        if (OfflineAuth.varInstanceClient.skinRegistry.getSkinDataByUUID(Util.offlineUUID(player.getDisplayName())) == null && !OfflineAuth.varInstanceClient.queriedForSkinName) {
            System.out.println("Querying for skin of the local player (" + player.getDisplayName() + "), Invoked from ontick");
            OfflineAuth.varInstanceClient.queriedForSkinName = true;
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(0, Util.offlineUUID(Minecraft.getMinecraft().thePlayer.getDisplayName()), "");
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
        String uuid = Util.offlineUUID(entityPlayerMP.getDisplayName());
        ClientSkinData sd = OfflineAuth.varInstanceClient.skinRegistry.getSkinDataByUUID(uuid);
        if (sd == null && !OfflineAuth.varInstanceClient.queriedForSkinName) {
            System.out.println("Querying for skin of the player " + entityPlayerMP.getDisplayName() + ", Invoked from render");
            System.out.println(entityPlayerMP.getDisplayName() + "'s uuid is: " + uuid);
            System.out.println(OfflineAuth.varInstanceClient.skinRegistry);
            OfflineAuth.varInstanceClient.queriedForSkinName = true;
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(0, uuid, "");
            PacketHandler.net.sendToServer(msg);
        } else if (sd != null && sd.skinName != null && sd.entityPlayer != null) {
            OfflineAuth.varInstanceClient.skinLocationfield.set(sd.entityPlayer, sd.resourceLocation);
        } else if (sd != null && sd.skinName != null && sd.entityPlayer == null) {
            OfflineAuth.varInstanceClient.skinRegistry.deleteByUUID(uuid);
            OfflineAuth.varInstanceClient.playerRegistry.add(new ClientPlayerData((AbstractClientPlayer) entityPlayerMP));
        }
    }
}
