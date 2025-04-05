package trollogyadherent.offlineauth.skin;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.packets.QuerySkinNameFromServerPacket;
import trollogyadherent.offlineauth.util.ClientUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinUtil {
	public static Map<UUID, String> uuidToName = new HashMap<>();
	
	public static ResourceLocation getSkinResourceLocationByDisplayName(Minecraft mc, final String displayName) {
		if (ClientUtil.isSinglePlayer()) {
			return OfflineAuth.varInstanceClient.singlePlayerSkinResourceLocation;
		}
		final ResourceLocation r = OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName);
		if (r != null) {
			return r;
		}
		
		if (OfflineAuth.varInstanceClient.clientRegistry.getSkinNameByDisplayName(displayName) == null && !OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
			IMessage msg = new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
			PacketHandler.net.sendToServer(msg);
			OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
			OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, mc.theWorld.getPlayerEntityByName(displayName), null, displayName, mc.theWorld.getPlayerEntityByName(displayName) == null ? null : mc.theWorld.getPlayerEntityByName(displayName).getUniqueID());
		}
		return SkinManager.field_152793_a;
	}
}
