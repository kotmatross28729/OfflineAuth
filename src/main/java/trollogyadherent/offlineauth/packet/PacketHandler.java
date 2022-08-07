package trollogyadherent.offlineauth.packet;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import trollogyadherent.offlineauth.packet.packets.*;

public class PacketHandler {
    public static SimpleNetworkWrapper net;

    public static void initPackets()
    {
        net = NetworkRegistry.INSTANCE.newSimpleChannel("OfflineAuth".toUpperCase());
        registerMessage(PlayerJoinPacket.class, PlayerJoinPacket.SimpleMessage.class);
        registerMessage(DownloadSkinPacket.class, DownloadSkinPacket.SimpleMessage.class);
        registerMessage(DownloadCapePacket.class, DownloadCapePacket.SimpleMessage.class);
        registerMessage(QuerySkinNameFromServerPacket.class, QuerySkinNameFromServerPacket.SimpleMessage.class);
        registerMessage(ResetCachesPacket.class, ResetCachesPacket.SimpleMessage.class);
        registerMessage(CopyToClipboardPacket.class, CopyToClipboardPacket.SimpleMessage.class);
        registerMessage(DeletePlayerFromClientRegPacket.class, DeletePlayerFromClientRegPacket.SimpleMessage.class);
        registerMessage(SendAuthPortPacket.class, SendAuthPortPacket.SimpleMessage.class);
    }

    private static int nextPacketId = 0;

    private static void registerMessage(Class packet, Class message)
    {
        net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
        net.registerMessage(packet, message, nextPacketId, Side.SERVER);
        nextPacketId++;
    }
}
