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
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.util.Util;

import java.io.IOException;

public class ClientEventListener {
    /* Clears OfflineAuth.playerRegistry List. Otherwise, the SkinData would persist, but so, when changing worlds, the old textures are unloaded */
    /* Triggers when we join */
    @SuppressWarnings("unused")
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

        String displayName = Minecraft.getMinecraft().thePlayer.getDisplayName();

        System.out.println("Detected player join event, we joined");

        OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, (EntityPlayer) e.entity);

        /* Asking server for data about us, adding to playerreg there */
        IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
        PacketHandler.net.sendToServer(msg);
        OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
    }

    /* Triggers when another player joins a server */
    @SuppressWarnings("unused")
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

        String displayName = ((AbstractClientPlayer) e.entity).getDisplayName();

        System.out.println("Detected player join event: " + displayName);
        OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, (EntityPlayer) e.entity);
        /* Asking server for data about this guy, adding to playerreg there */
        IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
        PacketHandler.net.sendToServer(msg);
        OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
    }

    /* Well, this does not trigger when other palyers leave lol */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent e) {
        /* If singleplayer, we don't do that */
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer()) {
            return;
        }

        if (Util.isServer()) {
            return;
        }

        System.out.println("We left a world");
        OfflineAuth.varInstanceClient.clientRegistry.clear();
    }

    /* Clears OfflineAuth.skinCache List. Otherwise the SkinData would exist, but when changing worlds, the texture is unloaded */
    /* This triggers on multiplayer */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerJoinFMLEvent(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        OfflineAuth.info("Clearing memory skin cache 4");
        OfflineAuth.varInstanceClient.clientRegistry.clear();
        System.out.println("Joined MP world");
    }

    @SuppressWarnings("unused")
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
        //ClientSkinUtil.clearSkinCache();
        OfflineAuth.varInstanceClient.clientRegistry.clear();
        System.out.println("Exited MP world");
        //OfflineAuth.varInstanceClient.onDedicatedServer = false;
    }

    /* This loads player's arms skin, if he doesn't switch to a view that forces the whole player to be rendered */
    /* This can also load cached skins in singleplayer */
    /* Every tick checks if skin is null, if yes it tries to load one */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void ontick(TickEvent.PlayerTickEvent e) throws IllegalAccessException {
        if (ClientUtil.isSinglePlayer()) {
            return;
        }

        /* IIRC this code still runs on the internal server??? anyway, without this return it copmplains about missing opengl context */
        if (!e.player.worldObj.isRemote) {
            return;
        }

        /* Tick only for current player */
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) {
            return;
        }
        EntityClientPlayerMP player = mc.thePlayer;
        /* We only want to load our own skin */
        if (!e.player.getDisplayName().equals(player.getDisplayName())) {
            return;
        }

        if (e.player instanceof FakePlayer) {
            return;
        }

        String displayName = player.getDisplayName();
        if (OfflineAuth.varInstanceClient.clientRegistry.getPlayerEntityByDisplayName(displayName) == null) {
            OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, mc.thePlayer);
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
            PacketHandler.net.sendToServer(msg);
            OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
            return;
        }


        if (OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName) == null && !OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
            OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, mc.thePlayer);
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
            PacketHandler.net.sendToServer(msg);
            OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
        }

        if (OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName) == null) {
            return;
        }

        OfflineAuth.varInstanceClient.skinLocationfield.set(OfflineAuth.varInstanceClient.clientRegistry.getPlayerEntityByDisplayName(displayName), OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent()
    public void render(RenderPlayerEvent.Pre e) throws IllegalAccessException, IOException {
        if (ClientUtil.isSinglePlayer()) {
            return;
        }

        if (!e.entityPlayer.worldObj.isRemote) {
            return;
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
        String displayName = entityPlayerMP.getDisplayName();

        if (OfflineAuth.varInstanceClient.clientRegistry.getPlayerEntityByDisplayName(displayName) == null) {
            OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, entityPlayerMP);
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
            PacketHandler.net.sendToServer(msg);
            OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
            return;
        }

        if (OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName) == null && !OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
            OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, entityPlayerMP);
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
            PacketHandler.net.sendToServer(msg);
            OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
            return;
        }

        if (OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName) == null) {
            return;
        }

        OfflineAuth.varInstanceClient.skinLocationfield.set(OfflineAuth.varInstanceClient.clientRegistry.getPlayerEntityByDisplayName(displayName), OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName));
    }
}
