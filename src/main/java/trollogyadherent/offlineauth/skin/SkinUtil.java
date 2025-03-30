package trollogyadherent.offlineauth.skin;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.skin.cape.CapeObject;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.packets.QuerySkinNameFromServerPacket;
import trollogyadherent.offlineauth.util.ClientUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinUtil {
	//From the lost FTBLib version
	public static ResourceLocation getSkinResourceLocationByDisplayName(final String displayName) {
		if (ClientUtil.isSinglePlayer()) {
			return OfflineAuth.varInstanceClient.singlePlayerSkinResourceLocation;
		}
		final ResourceLocation r = OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName);
		if (r != null) {
			return r;
		}
		if (!OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
			if (OfflineAuth.varInstanceClient.clientRegistry.getDataByDisplayName(displayName) == null) {
				OfflineAuth.varInstanceClient.clientRegistry.insert(
						(String) null,
						(ResourceLocation) null,
						(EntityPlayer) null,
						(CapeObject) null,
						displayName);
			}
			final IMessage msg = (IMessage) new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
			PacketHandler.net.sendToServer(msg);
			OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
		}
		return null;
	}
	
	//Not the best solution, but
	public static Map<UUID, String> uuidToName = new HashMap<>();
	
	public static ResourceLocation getSkinResourceLocationByDisplayName(UUID uuid) {
		String displayName = uuidToName.get(uuid);
		
		if (ClientUtil.isSinglePlayer()) {
			return OfflineAuth.varInstanceClient.singlePlayerSkinResourceLocation;
		}
		final ResourceLocation r = OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName);
		if (r != null) {
			return r;
		}
		if (!OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
			if (OfflineAuth.varInstanceClient.clientRegistry.getDataByDisplayName(displayName) == null) {
				OfflineAuth.varInstanceClient.clientRegistry.insert(
						(String) null,
						(ResourceLocation) null,
						(EntityPlayer) null,
						(CapeObject) null,
						displayName);
			}
			final IMessage msg = (IMessage) new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
			PacketHandler.net.sendToServer(msg);
			OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
		}
		return null;
	}
	
}
