package trollogyadherent.offlineauth.skin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.ConfigMixins;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.packet.packets.QuerySkinNameFromServerPacket;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.varinstances.VarInstanceClient;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SkinUtil {
	
	/**
	 * If you wanna get offlineauth skin, then that's probably what you wanna use
	 * @param mc Minecraft instance
	 * @param displayName Name of whoever you want to get a skin from
	 * @param fallbackIcon Determines whether a question mark can be used as a fallback
	 * @return Player's skin, yeah
	 */
	@Nonnull
	public static ResourceLocation getOASkin(Minecraft mc, final String displayName, boolean fallbackIcon) {
		ResourceLocation oaSkin = (mc.theWorld != null) ? SkinUtil.getSkinByDisplayName(mc, displayName, true) : null;
		
		if (oaSkin == null) {
			ResourceLocation cached = ClientSkinUtil.loadSkinFromCacheQuiet(displayName);
			oaSkin = (cached != null) ? cached : (fallbackIcon ? getDefaultIcon() : getDefaultSkin());
		}
		
		return oaSkin;
	}
			
	public static ResourceLocation getSkinByDisplayName(Minecraft mc, final String displayName, boolean nullable) {
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
		return nullable ? null : SkinUtil.getDefaultIcon();
	}
	
	public static ResourceLocation getDefaultSkin() {
		return ConfigMixins.basicSkinBackport ? VarInstanceClient.DEFAULT_SKIN_64 : VarInstanceClient.DEFAULT_SKIN_32;
	}
	
	public static ResourceLocation getDefaultIcon() {
		if(ConfigMixins.basicSkinBackport) {
			return Config.showQuestionMarkIfUnknown ? VarInstanceClient.questionMarkResourceLocation64 : VarInstanceClient.DEFAULT_SKIN_64;
		} else {
			return Config.showQuestionMarkIfUnknown ? VarInstanceClient.questionMarkResourceLocation : VarInstanceClient.DEFAULT_SKIN_32;
		}
	}
	
	public static void drawPlayerFaceAuto(float xPos, float yPos, float width, float height) {
		if(ConfigMixins.basicSkinBackport) {
			/// Use 1:1 (64x64)
			SkinUtil.drawPlayerFaceModern(xPos, yPos, width, height);
		} else {
			/// Use 2:1 (64x32)
			SkinUtil.drawPlayerFaceLegacy(xPos, yPos, width, height);
		}
	}
	
	public static void drawPlayerFaceLegacy(float xPos, float yPos, float width, float height) {
		/// UV that are responsible for head location 
		drawTexturedRect(xPos, yPos, width, height, 0.125, 0.25, 0.25, 0.5);
		/// UV that are responsible for head's 2nd layer location 
		drawTexturedRect(xPos, yPos, width, height, 0.625, 0.25, 0.75, 0.5);
	}
	public static void drawPlayerFaceModern(float xPos, float yPos, float width, float height) {
		/// Since this is a modern format (2x height), we divide v's (v0, v1) by 2, u's (u0, u1) remains the same (because the width remained the same)
		drawTexturedRect(xPos, yPos, width, height, 0.125, 0.125, 0.25, 0.25);
		drawTexturedRect(xPos, yPos, width, height, 0.625, 0.125, 0.75, 0.25);
	}
	
	/**
	 * Used for a quick mapping storage
	 * <p> 
	 * Destroyed when:
	 * 1) TTL expired
	 * 2) Player leaves the server
	 * 3) Player joins the server
	 */
	public static final Cache<UUID, String> uuidFastCache = CacheBuilder.newBuilder()
			.maximumSize(8000) /// 8000 (UUID + String) ~= 1 MB
			.expireAfterAccess(2, TimeUnit.MINUTES)
			.build();
	
	private static void drawTexturedRect(float x, float y, float w, float h, double u0, double v0, double u1, double v1) {
		Tessellator tessellator;
		if (u0 != u1 && v0 != v1) {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			addRectToBufferWithUV(tessellator, x, y, w, h, u0, v0, u1, v1);
			tessellator.draw();
		} else {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			addRectToBuffer(tessellator, x, y, w, h);
			tessellator.draw();
		}
	}
	
	private static void addRectToBuffer(Tessellator tessellator, double x, double y, double w, double h) {
		tessellator.addVertex(x, y + h, 0.0);
		tessellator.addVertex(x + w, y + h, 0.0);
		tessellator.addVertex(x + w, y, 0.0);
		tessellator.addVertex(x, y, 0.0);
	}
	
	private static void addRectToBufferWithUV(Tessellator tessellator, float x, float y, float w, float h, double u0, double v0, double u1, double v1) {
		tessellator.addVertexWithUV(x, y + h, 0.0, u0, v1);
		tessellator.addVertexWithUV(x + w, y + h, 0.0, u1, v1);
		tessellator.addVertexWithUV(x + w, y, 0.0, u1, v0);
		tessellator.addVertexWithUV(x, y, 0.0, u0, v0);
	}
	
}
