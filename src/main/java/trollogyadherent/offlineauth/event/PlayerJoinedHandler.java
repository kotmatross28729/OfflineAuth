package trollogyadherent.offlineauth.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.SimplePacket;


public class PlayerJoinedHandler {
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
        System.out.println("SNEED ALERT Player logged in: " + e.player.getDisplayName());
        EntityPlayer player = e.player;

        IMessage msg = new SimplePacket.SimpleMessage(0, "");
        PacketHandler.net.sendTo(msg, (EntityPlayerMP)player);

    }
}
