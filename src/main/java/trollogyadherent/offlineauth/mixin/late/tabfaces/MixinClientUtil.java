package trollogyadherent.offlineauth.mixin.late.tabfaces;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.ResourceLocation;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.SkinUtil;

@Mixin(value = ClientUtil.class, priority = 999)
public class MixinClientUtil {
	
	@Inject(
			method = "skinResourceLocation",
			at = @At(value = "HEAD"),
			cancellable = true,
			remap = false)
	private static void skinResourceLocation(GameProfile profile, CallbackInfoReturnable<ResourceLocation> cir) {
		if(profile != null && profile.getName() != null) {
			cir.setReturnValue(SkinUtil.getOASkin(VarInstanceClient.minecraftRef, profile.getName().trim(), true));
		}
	}
	
	/**
	 * @author kotmatross
	 * @reason basicSkinBackport compat
	 */
	@Overwrite(remap = false)
	public static void drawPlayerFace(ResourceLocation rl, float xPos, float yPos, float alpha) {
		if (rl != null) {
            OfflineAuth.varInstanceClient.getTextureManager().bindTexture(rl);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
			SkinUtil.drawPlayerFaceAuto(xPos, yPos, 8, 8);
		}
	}
	
	/// Bypass data check
	@ModifyExpressionValue(
			method = "drawHoveringTextWithFaces",
			at = @At(value = "INVOKE", target = "Lorg/fentanylsolutions/tabfaces/registries/ClientRegistry$Data;hasRealSkin()Z")
			, remap = false
	)
	private static boolean bypassCheck(boolean original) {
		return true;
	}

}
