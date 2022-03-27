package trollogyadherent.offlineauth.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.PlayerJoinPacket;
import trollogyadherent.offlineauth.util.Util;


public class ServerEventListener {
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        System.out.println("SNEED ALERT Player logged in: " + e.player.getDisplayName());
        EntityPlayer player = e.player;

        IMessage msg = new PlayerJoinPacket.SimpleMessage(0, "");
        PacketHandler.net.sendTo(msg, (EntityPlayerMP)player);
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent e) {
        System.out.println("SNEED ALERT Player logged out: " + e.player.getDisplayName());
        EntityPlayer player = e.player;
        String uuid = Util.offlineUUID(player.getDisplayName());
        System.out.println("Removing skinRegistry entry for uuid " + uuid);
        OfflineAuth.varInstanceServer.skinRegistry.deleteByUUID(uuid);
    }
}
