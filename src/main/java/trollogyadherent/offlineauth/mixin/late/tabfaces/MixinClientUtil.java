package trollogyadherent.offlineauth.mixin.late.tabfaces;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;

import java.util.List;

@Mixin(value = ClientUtil.class, priority = 999)
public class MixinClientUtil {
	
	@Inject(
			method = "skinResourceLocation",
			at = @At(value = "HEAD"),
			cancellable = true,
			remap = false)
	private static void skinResourceLocation(GameProfile profile, CallbackInfoReturnable<ResourceLocation> cir) {
		if(profile == null || profile.getName() == null) {
			return;
		}
		
		String profileName = profile.getName().contains(" ") ? profile.getName().trim() : profile.getName();
		
		if (VarInstanceClient.minecraftRef.thePlayer != null
				&& VarInstanceClient.minecraftRef.thePlayer.getDisplayName()
				.equals(profileName)) {
			cir.setReturnValue(VarInstanceClient.minecraftRef.thePlayer.getLocationSkin());
		}
		ResourceLocation oaSkin;
		
		if(VarInstanceClient.minecraftRef.theWorld != null) {
			oaSkin = SkinUtil.getSkinResourceLocationByDisplayName(VarInstanceClient.minecraftRef, profileName, true);
			if(oaSkin == null)
				oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(profileName);
		} else {
			oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(profileName);
		}
		
		if (oaSkin != null) {
			cir.setReturnValue(oaSkin);
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
	
	@Shadow(remap = false)
	public static FontRenderer fontRenderer = null;
	
	//Bypass data check
	@Inject(
			method = "drawHoveringTextWithFaces",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "org/fentanylsolutions/tabfaces/registries/ClientRegistry.getByDisplayName (Ljava/lang/String;)Lorg/fentanylsolutions/tabfaces/registries/ClientRegistry$Data;",
					ordinal = 0,
					shift = At.Shift.AFTER
			)
			, remap = false
	)
	private static void drawHoveringTextWithFaces(
			GuiScreen screen, GameProfile[] profiles, List<String> textLines, int x, int y,
			CallbackInfo ci,
			//Me, when adding non LocalRef locals will lead to a fucking non-working injection:
			@Local LocalRef<ClientRegistry.Data> data,
			@Local LocalRef<String> line,
			@Local(ordinal = 0) LocalIntRef boxWidth
			//Also, how the fuck do ordinals even work here?
			//	ClientRegistry.Data data should be ordinal 1, but if I do that, the injection just fucking stops working.
			//	And boxWidth is assumed to be the first local, so it doesn't actually need ordinal, but fucking guess what?
	) {
		if(data.get() == null || !data.get().foundRealSkin) {
			int tmpWidth = fontRenderer.getStringWidth(line.get()) + ClientUtil.faceWidth;
			if (tmpWidth > boxWidth.get()) {
				boxWidth.set(tmpWidth);
			}
		}
	}
	
	//Bypass data check
	@Inject(
			method = "drawHoveringTextWithFaces",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "org/fentanylsolutions/tabfaces/registries/ClientRegistry.getByDisplayName (Ljava/lang/String;)Lorg/fentanylsolutions/tabfaces/registries/ClientRegistry$Data;",
					ordinal = 1,
					shift = At.Shift.AFTER
			)
			, remap = false
	)
	private static void drawHoveringTextWithFaces2(
			GuiScreen screen, GameProfile[] profiles, List<String> textLines, int x, int y,
			CallbackInfo ci,
			@Local LocalRef<ClientRegistry.Data> data
			//So, it's been about 1.5 hours since I did @Inject above.
			// I tried WrapWithCondition, WrapOperation, Redirect, and nothing worked.
			// So I decided to do 2 injects again, and just force foundRealSkin = true, but that didn't work either.
			// And it's all because of that Local, I STILL DON'T UNDERSTAND WHY IT'S NOT ordinal = 1???
	) {
		if(data.get() != null)
			data.get().foundRealSkin = true;
	}

}
