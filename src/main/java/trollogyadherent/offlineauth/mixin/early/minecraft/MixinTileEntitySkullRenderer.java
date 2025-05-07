package trollogyadherent.offlineauth.mixin.early.minecraft;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;

@Mixin(value = TileEntitySkullRenderer.class, priority = 999)
public class MixinTileEntitySkullRenderer {
	///TODO: skull
	@Inject(
			method = "func_152674_a",
			at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/tileentity/TileEntitySkullRenderer.bindTexture(Lnet/minecraft/util/ResourceLocation;)V")
			//, remap = false
	)
	public void func_152674_a(float p_152674_1_, float p_152674_2_, float p_152674_3_, int p_152674_4_, float p_152674_5_, int p_152674_6_, GameProfile profile, CallbackInfo ci
	, @Local LocalRef<ResourceLocation> resourcelocation
	) {
		if(profile == null || profile.getName() == null) {
			return;
		}
		if(resourcelocation.get() == AbstractClientPlayer.locationStevePng) {
			String profileName = profile.getName().contains(" ") ? profile.getName().trim() : profile.getName(); //IDK, why not
			
			Minecraft minecraftRef = Minecraft.getMinecraft();
			
			if (minecraftRef.thePlayer != null
					&& minecraftRef.thePlayer.getDisplayName()
					.equals(profileName)) {
				resourcelocation.set(minecraftRef.thePlayer.getLocationSkin());
			}
			ResourceLocation oaSkin;
			
			if(minecraftRef.theWorld != null) {
				oaSkin = SkinUtil.getSkinResourceLocationByDisplayName(minecraftRef, profileName, true);
				if(oaSkin == null)
					oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(profileName);
			} else {
				oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(profileName);
			}
			
			if (oaSkin != null) {
				resourcelocation.set(oaSkin);
			}
		}
	}
	
}
