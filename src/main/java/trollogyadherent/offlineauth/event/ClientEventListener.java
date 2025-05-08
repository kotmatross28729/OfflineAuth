package trollogyadherent.offlineauth.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.clientdata.UsernameCacheClient;
import trollogyadherent.offlineauth.gui.skin.SkinManagmentGUI;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.packets.QuerySkinNameFromServerPacket;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.skin.client.LegacyConversion;
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientEventListener {
    
    /* Clears OfflineAuth.playerRegistry List. Otherwise, the SkinData would persist, but so, when changing worlds, the old textures are unloaded */
    /* Triggers when we join */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerJoin(EntityJoinWorldEvent e) {
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
        
        //System.out.println("Detected player join event, we joined");

        OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, (EntityPlayer) e.entity, null, displayName);

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

        //System.out.println("Detected player join event: " + displayName);
        OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, (EntityPlayer) e.entity, null, displayName);
        /* Asking server for data about this guy, adding to playerreg there */
        IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
        PacketHandler.net.sendToServer(msg);
        OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
    }

    /* Well, this does not trigger when other players leave lol */
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

        //System.out.println("We left a world");
        OfflineAuth.varInstanceClient.clientRegistry.clear();
        SkinUtil.uuidFastCache.clear();
    }

    /* Clears OfflineAuth.skinCache List. Otherwise the SkinData would exist, but when changing worlds, the texture is unloaded */
    /* This triggers on multiplayer */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerJoinFMLEvent(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        OfflineAuth.info("Clearing memory skin cache 4");
        OfflineAuth.varInstanceClient.clientRegistry.clear();
    
        OfflineAuth.debug("Clearing uuidFastCache 1");
        SkinUtil.uuidFastCache.clear();
        //System.out.println("Joined MP world");
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerLeaveFMLEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        OfflineAuth.varInstanceClient.offlineSkinAndCapeLoaded = false;
        /* If singleplayer, we don't do that */
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isSinglePlayer()) {
            return;
        }

        if (Util.isServer()) {
            return;
        }

        OfflineAuth.debug("Left server, clearing skin query status 2");

        OfflineAuth.debug("Clearing memory skin cache 5");
        //ClientSkinUtil.clearSkinCache();
        OfflineAuth.varInstanceClient.clientRegistry.clear();
    
        OfflineAuth.debug("Clearing uuidFastCache 2");
        SkinUtil.uuidFastCache.clear();
        //System.out.println("Exited MP world");
        //OfflineAuth.varInstanceClient.onDedicatedServer = false;
    }

    /* This loads player's arms skin, if he doesn't switch to a view that forces the whole player to be rendered */
    /* This can also load cached skins in singleplayer */
    /* Every tick checks if skin is null, if yes it tries to load one */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void ontick(TickEvent.PlayerTickEvent e) throws IllegalAccessException {
        /* Tick only for current player */
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) {
            return;
        }

        /* IIRC this code still runs on the internal server??? anyway, without this return it copmplains about missing opengl context */
        if (!e.player.worldObj.isRemote) {
            return;
        }
        
        if(Config.saveUserData) {
            if (e.player.worldObj.getTotalWorldTime() % Config.clientUserDataCheckInterval == 0) { //Every 5 seconds by default
                UUID uuid = e.player.getUniqueID();
                String displayName = e.player.getDisplayName();
                if (!UsernameCacheClient.containsUUID(uuid) && displayName != null) {
                    UsernameCacheClient.setUsername(uuid, displayName);
                }
            }
        }
    
        if (ClientUtil.isSinglePlayer()) {
            if (!OfflineAuth.varInstanceClient.offlineSkinAndCapeLoaded) {
                OfflineAuth.varInstanceClient.offlineSkinAndCapeLoaded = true;
                loadSingleplayerSkin();
                loadSingleplayerCape();
            } else {
                OfflineAuth.varInstanceClient.skinLocationField.set(Minecraft.getMinecraft().thePlayer, OfflineAuth.varInstanceClient.singlePlayerSkinResourceLocation);
            }
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
            OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, mc.thePlayer, null, displayName);
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
            PacketHandler.net.sendToServer(msg);
            OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
            return;
        }


        if (OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName) == null && !OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
            OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, mc.thePlayer, null, displayName);
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
            PacketHandler.net.sendToServer(msg);
            OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
        }

        if (OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName) == null) {
            return;
        }

        OfflineAuth.varInstanceClient.skinLocationField.set(mc.thePlayer, OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName));
    }

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Specials.Pre e) throws IllegalAccessException {
        if (Minecraft.getMinecraft().currentScreen instanceof SkinManagmentGUI) {
            return;
        }
        
        if (e.entityPlayer == null) {
            return;
        }
        
        if (!e.entityPlayer.worldObj.isRemote) {
            return;
        }

        if (e.isCanceled()) {
            return;
        }

        if (e.entityPlayer instanceof FakePlayer) {
            return;
        }
        if (OfflineAuth.varInstanceClient.skinGuiRenderTicker.getCapeObject() == null) {
            return;
        }
        if(Config.enableCapes) {
            if (ClientUtil.isSinglePlayer()) {
                if (OfflineAuth.varInstanceClient.singlePlayerCapeObject != null) {
                    OfflineAuth.varInstanceClient.capeLocationField.set(Minecraft.getMinecraft().thePlayer, OfflineAuth.varInstanceClient.singlePlayerCapeObject.getCurrentFrame(e.partialRenderTick));
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent()
    public void render(RenderPlayerEvent.Pre e) throws IllegalAccessException, IOException {
        if (e.entityPlayer == null) {
            return;
        }
        
        if (!e.entityPlayer.worldObj.isRemote) {
            return;
        }

        if (e.isCanceled()) {
            return;
        }

        if (e.entityPlayer instanceof FakePlayer) {
            return;
        }
        
        if (ClientUtil.isSinglePlayer()) {
            if(Config.enableCapes) {
                if (OfflineAuth.varInstanceClient.singlePlayerCapeObject != null) {
                    OfflineAuth.varInstanceClient.capeLocationField.set(Minecraft.getMinecraft().thePlayer, OfflineAuth.varInstanceClient.singlePlayerCapeObject.getCurrentFrame(e.partialRenderTick));
                }
            }
            return;
        }

        if (OfflineAuth.varInstanceClient.textureManager == null) {
            OfflineAuth.varInstanceClient.textureManager = Minecraft.getMinecraft().getTextureManager();
        }

        EntityPlayer entityPlayerMP = e.entityPlayer;
        String displayName = entityPlayerMP.getDisplayName();
        
        if (OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName) != null && OfflineAuth.varInstanceClient.clientRegistry.getPlayerEntityByDisplayName(displayName) == null) {
            OfflineAuth.varInstanceClient.clientRegistry.setEntityPlayer(displayName, entityPlayerMP);
        }

        if (OfflineAuth.varInstanceClient.clientRegistry.getPlayerEntityByDisplayName(displayName) == null) {
            OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, entityPlayerMP, null, displayName);
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
            PacketHandler.net.sendToServer(msg);
            OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
            return;
        }

        if (OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName) == null && !OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
            OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, entityPlayerMP, null, displayName);
            IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
            PacketHandler.net.sendToServer(msg);
            OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
            return;
        }

        if (OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName) != null) {
            OfflineAuth.varInstanceClient.skinLocationField.set(/*OfflineAuth.varInstanceClient.clientRegistry.getPlayerEntityByDisplayName(displayName)*/entityPlayerMP, OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName));
        }
        
        if (Config.enableCapes && OfflineAuth.varInstanceClient.clientRegistry.getCapeObject(displayName) != null) {
            OfflineAuth.varInstanceClient.capeLocationField.set(entityPlayerMP, OfflineAuth.varInstanceClient.clientRegistry.getCapeObject(displayName).getCurrentFrame(e.partialRenderTick));
        } else {
            OfflineAuth.varInstanceClient.capeLocationField.set(entityPlayerMP, null);
        }
    }

    public static void loadSingleplayerSkin() {
        String skinName = ClientSkinUtil.getLastUsedOfflineSkinName();
        if (skinName != null) {
            ResourceLocation rl = new ResourceLocation("offlineauth", "offlineskin/" + skinName);
            File imageFile = ClientSkinUtil.getSkinFile(skinName);
            if (imageFile == null || !imageFile.exists()) {
                OfflineAuth.error("Error skin image does not exist: " + skinName);
                return;
            }
            if (!Util.pngIsSane(imageFile)) {
                OfflineAuth.error("Error loading skin image, not sane" + skinName);
                return;
            }
            BufferedImage bufferedImage;
            try {
                bufferedImage = ImageIO.read(imageFile);
            } catch (IOException e_) {
                OfflineAuth.error("Error loading skin image " + skinName);
                e_.printStackTrace();
                return;
            }
            if(Config.useLegacyConversion) {
                //1:1 -> 2:1
                if (bufferedImage.getWidth() == bufferedImage.getHeight()) {
                    bufferedImage = new LegacyConversion().convertToOld(bufferedImage);
                }
            } else {
                //2:1 -> 1:1
                if (bufferedImage.getWidth() / bufferedImage.getHeight() == 2) {
                    bufferedImage = new LegacyConversion().convertToNew(bufferedImage);
                }
            }

            ClientSkinUtil.loadTexture(bufferedImage, rl);
            OfflineAuth.varInstanceClient.singlePlayerSkinResourceLocation = rl;
            try {
                OfflineAuth.varInstanceClient.skinLocationField.set(Minecraft.getMinecraft().thePlayer, rl);
                /*if (OfflineAuth.isEFRLoaded) {
                    ResourceLocation rlFuturum = new ResourceLocation("etfuturum", "offlineskin/" + skinName);
                    ClientSkinUtil.loadTexture(bufferedImage, rlFuturum);
                }*/
            } catch (IllegalAccessException e_) {
                OfflineAuth.error("Fatal error while applying skin");
                e_.printStackTrace();
            }
        }
    }

    public static void loadSingleplayerCape() {
        String capeName = ClientSkinUtil.getLastUsedOfflineCapeName();
        if (capeName != null) {
            OfflineAuth.varInstanceClient.singlePlayerCapeObject = ClientSkinUtil.getCapeObject(capeName);
        }
    }
}
