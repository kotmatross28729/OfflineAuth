package trollogyadherent.offlineauth.mixin.late.tabfaces;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;

@Mixin(value = ClientUtil.class, priority = 999)
public class MixinClientUtil {
	
	@Inject(
			method = "skinResourceLocation",
			at = @At(value = "HEAD"),
			cancellable = true,
			remap = false)
	private static void skinResourceLocation(GameProfile profile, CallbackInfoReturnable<ResourceLocation> cir) {
		if (VarInstanceClient.minecraftRef.thePlayer != null
				&& VarInstanceClient.minecraftRef.thePlayer.getDisplayName()
				.equals(profile.getName())) {
			cir.setReturnValue(VarInstanceClient.minecraftRef.thePlayer.getLocationSkin());
		}
		ResourceLocation oaSkin;
		
		if(VarInstanceClient.minecraftRef.theWorld != null) {
			oaSkin = SkinUtil.getSkinResourceLocationByDisplayName(VarInstanceClient.minecraftRef, profile.getName(), true);
		} else {
			oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(profile.getName());
		}
		
		if (oaSkin != null) {
			cir.setReturnValue(oaSkin);
		}
	}
	
	/**
	 * @author kotmatross
	 * @reason useLegacyConversion compat
	 */
	@Overwrite(remap = false)
	public static void drawPlayerFace(ResourceLocation rl, float xPos, float yPos, float alpha) {
		if (rl != null) {
			VarInstanceClient.minecraftRef.getTextureManager().bindTexture(rl);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
			
			//Use 2:1 (64x32)
			if(Config.useLegacyConversion) {
				offlineAuth$drawTexturedRect(xPos, yPos, 8, 8, 0.125, 0.25, 0.25, 0.5);
				offlineAuth$drawTexturedRect(xPos, yPos, 8, 8, 0.625, 0.25, 0.75, 0.5);
			}
			//Use 1:1 (64x64)
			else {
				offlineAuth$drawTexturedRect(xPos, yPos, 8, 8, 0.125, 0.125, 0.25, 0.25);
				offlineAuth$drawTexturedRect(xPos, yPos, 8, 8, 0.625, 0.125, 0.75, 0.25);
			}
		}
	}
	
	@Unique
	private static void offlineAuth$drawTexturedRect(float x, float y, float w, float h, double u0, double v0, double u1, double v1) {
		Tessellator tessellator;
		if (u0 != u1 && v0 != v1) {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			offlineAuth$addRectToBufferWithUV(tessellator, x, y, w, h, u0, v0, u1, v1);
			tessellator.draw();
		} else {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			offlineAuth$addRectToBuffer(tessellator, x, y, w, h);
			tessellator.draw();
		}
	}
	
	@Unique
	private static void offlineAuth$addRectToBuffer(Tessellator tessellator, double x, double y, double w, double h) {
		tessellator.addVertex(x, y + h, 0.0);
		tessellator.addVertex(x + w, y + h, 0.0);
		tessellator.addVertex(x + w, y, 0.0);
		tessellator.addVertex(x, y, 0.0);
	}
	@Unique
	private static void offlineAuth$addRectToBufferWithUV(Tessellator tessellator, float x, float y, float w, float h, double u0, double v0, double u1, double v1) {
		tessellator.addVertexWithUV(x, y + h, 0.0, u0, v1);
		tessellator.addVertexWithUV(x + w, y + h, 0.0, u1, v1);
		tessellator.addVertexWithUV(x + w, y, 0.0, u1, v0);
		tessellator.addVertexWithUV(x, y, 0.0, u0, v0);
	}
}
