package trollogyadherent.offlineauth.mixin.late.betterquesting;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.content.PanelPlayerPortrait;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.mixinHelper.betterquesting.ISetSkinLocation;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;

@Mixin(value = PanelPlayerPortrait.class, priority = 999)
public abstract class MixinPanelPlayerPortrait {
	
	@Shadow(remap = false)
	@Final
	@Mutable
	private AbstractClientPlayer player;
	
	@SuppressWarnings({"DiscouragedShift"})
	@Inject(method = "<init>(Lbetterquesting/api2/client/gui/misc/IGuiRect;Lnet/minecraft/client/entity/AbstractClientPlayer;)V", 
			at = @At(value = "INVOKE", target = "net/minecraft/client/Minecraft.getMinecraft ()Lnet/minecraft/client/Minecraft;", shift = At.Shift.BEFORE)
	)
	private void PanelPlayerPortrait(IGuiRect rect, AbstractClientPlayer playerAbstract, CallbackInfo ci) {
		if(this.player instanceof ISetSkinLocation playerHelper) {
			GameProfile profile = this.player.getGameProfile();
			if(profile != null && profile.getName() != null) {
				playerHelper.offlineAuth$setLocationSkin(SkinUtil.getSkinResourceLocationByDisplayName(Minecraft.getMinecraft(), profile.getName(), true));
				if (this.player.getLocationSkin() == null)
					playerHelper.offlineAuth$setLocationSkin(ClientSkinUtil.loadSkinFromCacheQuiet(profile.getName()));
				if (this.player.getLocationSkin() == null)
					playerHelper.offlineAuth$setLocationSkin(Config.useLegacyConversion ? SkinManager.field_152793_a : OfflineAuth.varInstanceClient.DEFAULT_SKIN_64);
			}
		}
	}
	
	@WrapWithCondition(
			method = "<init>(Lbetterquesting/api2/client/gui/misc/IGuiRect;Lnet/minecraft/client/entity/AbstractClientPlayer;)V",
			at = @At(value = "INVOKE", target = "net/minecraft/client/entity/AbstractClientPlayer.getDownloadImageSkin (Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Lnet/minecraft/client/renderer/ThreadDownloadImageData;"))
	private boolean disableDownload(ResourceLocation resourceLocationIn, String username) {
		return false;
	}
	
}
