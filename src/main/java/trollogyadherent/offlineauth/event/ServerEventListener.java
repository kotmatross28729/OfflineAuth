package trollogyadherent.offlineauth.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.DBPlayerData;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.PlayerJoinPacket;
import trollogyadherent.offlineauth.packet.ResetCachesPacket;
import trollogyadherent.offlineauth.packet.SendAuthPortPacket;
import trollogyadherent.offlineauth.util.ServerUtil;

import java.util.UUID;


public class ServerEventListener {
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) throws IllegalAccessException {
        OfflineAuth.info("Player joined server: " + e.player.getDisplayName());
        EntityPlayer player = e.player;

        /* Sending auth port to the player, just in case */
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
        //System.out.println(OfflineAuth.varInstanceServer.playerRegistry.toString());
        /*for (Object o : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList) {
            IMessage msg = new ResetCachesPacket.SimpleMessage();
            PacketHandler.net.sendTo(msg, (EntityPlayerMP)o);
        }*/
    }
}
