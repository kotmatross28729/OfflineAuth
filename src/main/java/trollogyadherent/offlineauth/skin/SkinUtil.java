package trollogyadherent.offlineauth.skin;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.packets.QuerySkinNameFromServerPacket;
import trollogyadherent.offlineauth.util.ClientUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinUtil {
	public static Map<UUID, String> uuidToName = new HashMap<>();
	
	//TODO: Can't send QuerySkinNameFromServerPacket without login? (no faces in server sel. menu)
	//	- clientRegistry clears at server menu
	//	- getSkinNameByDisplayName... / skinNameIsBeingQueried check always false
	//	- returns default
	public static ResourceLocation getSkinResourceLocationByDisplayName(Minecraft mc, final String displayName, boolean nullable) {
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
			OfflineAuth.varInstanceClient.clientRegistry.insert(null, null, mc.theWorld.getPlayerEntityByName(displayName), null, displayName);
		}
		return nullable ? null : Config.showQuestionMarkIfUnknown ? OfflineAuth.varInstanceClient.defaultResourceLocation : SkinManager.field_152793_a;
	}
	
	//From lost ftblib fork
//	public static ResourceLocation getSkinResourceLocation(final String displayName) {
//		if (ClientUtil.isSinglePlayer()) {
//			return OfflineAuth.varInstanceClient.singlePlayerSkinResourceLocation;
//		}
//		final ResourceLocation r = OfflineAuth.varInstanceClient.clientRegistry.getResourceLocation(displayName);
//		if (r != null) {
//			return r;
//		}
//		if (!OfflineAuth.varInstanceClient.clientRegistry.skinNameIsBeingQueried(displayName)) {
//			if (OfflineAuth.varInstanceClient.clientRegistry.getDataByDisplayName(displayName) == null) {
//				OfflineAuth.varInstanceClient.clientRegistry.insert((String)null, (ResourceLocation)null, null, null, displayName);
//			}
//			final IMessage msg = (IMessage)new QuerySkinNameFromServerPacket.SimpleMessage(displayName);
//			PacketHandler.net.sendToServer(msg);
//			OfflineAuth.varInstanceClient.clientRegistry.setSkinNameIsBeingQueried(displayName, true);
//		}
//		return null;
//	}
	
}
