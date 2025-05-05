package trollogyadherent.offlineauth.mixin.early.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Locale;
import java.util.UUID;

@Mixin(value = PlayerProfileCache.class, priority = 999)
public class MixinPlayerProfileCache {
	@Shadow
	private final MinecraftServer field_152664_f;
	
	public MixinPlayerProfileCache(MinecraftServer field_152664_f) {
		this.field_152664_f = field_152664_f;
	}
	
	//Fixed: server always added offline players in lower case
	@WrapOperation(
			method = "func_152651_a",
			at = @At(value = "INVOKE", target = "java/lang/String.toLowerCase (Ljava/util/Locale;)Ljava/lang/String;")
	)
	private String disableLowerCase_func_152651_a(String instance, Locale locale, Operation<String> original) {
		if(!field_152664_f.isServerInOnlineMode()) {
			return instance;
		}
		return original.call(instance, locale);
	}
	
	//Fixed: server always added offline players in lower case
	@WrapOperation(
			method = "func_152655_a",
			at = @At(value = "INVOKE", target = "java/lang/String.toLowerCase (Ljava/util/Locale;)Ljava/lang/String;")
	)
	private String disableLowerCase_func_152655_a(String instance, Locale locale, Operation<String> original) {
		if(!field_152664_f.isServerInOnlineMode()) {
			return instance;
		}
		return original.call(instance, locale);
	}
	
	//Fixed: whitelist, ban, etc. commands still refer to Mojang servers to find accounts even when server was in offline mode
	@Inject(method = "func_152650_a(Lnet/minecraft/server/MinecraftServer;Ljava/lang/String;)Lcom/mojang/authlib/GameProfile;", at = @At(value = "HEAD"), cancellable = true)
	private static void func_152650_a(MinecraftServer p_152650_0_, String p_152650_1_, CallbackInfoReturnable<GameProfile> cir) {
		if(!p_152650_0_.isServerInOnlineMode()) {
			final GameProfile[] agameprofile = new GameProfile[1];
			ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
				public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
					agameprofile[0] = p_onProfileLookupSucceeded_1_;
				}
				
				public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
					agameprofile[0] = null;
				}
			};
			
			UUID uuid = EntityPlayer.func_146094_a(new GameProfile((UUID) null, p_152650_1_));
			GameProfile gameprofile = new GameProfile(uuid, p_152650_1_);
			profilelookupcallback.onProfileLookupSucceeded(gameprofile);
			cir.setReturnValue(agameprofile[0]);
		}
	}
	
}
