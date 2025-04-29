package trollogyadherent.offlineauth.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.BlockEvent;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.packets.SendAuthPortPacket;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ServerEventListener {
    
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) throws IllegalAccessException {
        OfflineAuth.info("Player joined server: " + e.player.getDisplayName());
        EntityPlayer player = e.player;

        //((EntityPlayerMP)e.player).playerNetServerHandler.netManage

        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                if (!OfflineAuth.varInstanceServer.authenticatedDisplaynames.contains(e.player.getDisplayName())) {
                    ((EntityPlayerMP)e.player).playerNetServerHandler.kickPlayerFromServer(Config.kickMessage);
                }
            }
        }, Config.secondsBeforeKick, TimeUnit.SECONDS);


        /* Sending auth port to the player, and the auth packet too */
        IMessage msg = new SendAuthPortPacket.SimpleMessage(Config.port);
        PacketHandler.net.sendTo(msg, (EntityPlayerMP)player);

        /*IMessage msg2 = new PlayerJoinPacket.SimpleMessage();
        PacketHandler.net.sendTo(msg2, (EntityPlayerMP)player);*/

        /*DBPlayerData dbp = Database.getPlayerDataByDisplayName(e.player.getDisplayName());

        if (dbp == null) {
            ServerUtil.kickPlayerByName(e.player.getDisplayName(), Config.kickMessage);
            return;
        }

        if (OfflineAuth.varInstanceServer.DEBUGtamperWithUUID) {
            UUID uuid = UUID.fromString(dbp.getUuid());
            OfflineAuth.info("Setting " + e.player.getDisplayName() + " UUID to " + uuid);

            OfflineAuth.varInstanceServer.uuidIdField.set(e.player.field_146106_i, uuid);
            OfflineAuth.varInstanceServer.uuidIdField2.set(e.player, uuid);
        }*/

        //OfflineAuth.varInstanceServer.playerRegistry.add(new ServerPlayerData(dbp.getIdentifier()));
    }

    //not quite it. achievements are kept, but not inventory
    /*@SubscribeEvent
    public void onPlayerJoin2(net.minecraftforge.event.entity.player.PlayerEvent e) throws IllegalAccessException {
        if (OfflineAuth.varInstanceServer.changedUUID) {
            return;
        }

        OfflineAuth.varInstanceServer.changedUUID = true;

        DBPlayerData dbp = Database.getPlayerDataByDisplayName(e.entityPlayer.getDisplayName());

        if (dbp == null) {
            ServerUtil.kickPlayerByName(e.entityPlayer.getDisplayName(), "Failed to find info in database!");
            return;
        }

        UUID uuid = UUID.fromString(dbp.getUuid());
        OfflineAuth.info("Setting " + e.entityPlayer.getDisplayName() + " UUID to " + uuid);
        OfflineAuth.varInstanceServer.uuidIdField.set(e.entityPlayer.field_146106_i, uuid);
        OfflineAuth.varInstanceServer.uuidIdField2.set(e.entityPlayer, uuid);
    }*/

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent e) {
        OfflineAuth.info("Player quit server out: " + e.player.getDisplayName());
        //System.out.println("Removing skinRegistry entry for displayname " + e.player.getDisplayName());
        OfflineAuth.varInstanceServer.playerRegistry.deleteByIdentifier(OfflineAuth.varInstanceServer.playerRegistry.getIdentifierFromDisplayName(e.player.getDisplayName()));

        OfflineAuth.varInstanceServer.authenticatedDisplaynames.remove(e.player.getDisplayName());
        //System.out.println(OfflineAuth.varInstanceServer.playerRegistry.toString());
        /*for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
            IMessage msg = new ResetCachesPacket.SimpleMessage();
            PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
        }*/
    }

    public void warnNotLoggedIn(EntityPlayer player) {
        player.addChatMessage(new ChatComponentText((char) 167 + "cYou are not logged in!"));
    }

    public void warnNotLoggedIn(ICommandSender iCommandSender) {
        iCommandSender.addChatMessage(new ChatComponentText((char) 167 + "cYou are not logged in!"));
    }

    @SubscribeEvent
    public void onBlockPlaceEvent(BlockEvent.PlaceEvent e) {
        if (!OfflineAuth.varInstanceServer.authenticatedDisplaynames.contains(e.player.getDisplayName())) {
            e.setCanceled(true);
            warnNotLoggedIn(e.player);
        }
    }

    @SubscribeEvent
    public void onMultiPlaceEvent(BlockEvent.MultiPlaceEvent e) {
        if (!OfflineAuth.varInstanceServer.authenticatedDisplaynames.contains(e.player.getDisplayName())) {
            e.setCanceled(true);
            warnNotLoggedIn(e.player);
        }
    }

    @SubscribeEvent
    public void onBlockBreakEvent(BlockEvent.BreakEvent e) {
        if (!OfflineAuth.varInstanceServer.authenticatedDisplaynames.contains(e.getPlayer().getDisplayName())) {
            e.setCanceled(true);
            warnNotLoggedIn(e.getPlayer());
        }
    }

    @SubscribeEvent
    public void onCommandEvent(CommandEvent e) {
        if (e.sender instanceof DedicatedServer) {
            return;
        }
        if (!OfflineAuth.varInstanceServer.authenticatedDisplaynames.contains(e.sender.getCommandSenderName())) {
            if (e.isCancelable()) {
                e.setCanceled(true);
                warnNotLoggedIn(e.sender);
            }
        }
    }

    @SubscribeEvent
    public void onEntityEvent(EntityEvent e) throws IllegalAccessException {
        if (!Config.secureEachEntityEvent) {
            return;
        }
        if (!(e.entity instanceof EntityPlayerMP)) {
            return;
        }
//         "Okay, for some fucking reason, server and client use this field DIFFERENTLY. Some have it obfuscated, SOME DO NOT
//          Fucking forge, fucking mcp, AND FUCKING I, because I didn't find out about it before"
//              - My attempt (torture) to remake everything on commandSenderName (this is fucking impossible), @Kotmatross28729 - 04.13.25
        String displayName = (String) OfflineAuth.varInstanceServer.displaynameField.get(e.entity);
        if (!OfflineAuth.varInstanceServer.authenticatedDisplaynames.contains(displayName)) {
            if (e.isCancelable()) {
                e.setCanceled(true);
                //warnNotLoggedIn((EntityPlayerMP)e.entity);
            }
        }
    }

    @SubscribeEvent()
    public void onMessage (ServerChatEvent e) {
        if (!OfflineAuth.varInstanceServer.authenticatedDisplaynames.contains(e.player.getDisplayName())) {
            e.setCanceled(true);
            warnNotLoggedIn(e.player);
        }
    }
}
