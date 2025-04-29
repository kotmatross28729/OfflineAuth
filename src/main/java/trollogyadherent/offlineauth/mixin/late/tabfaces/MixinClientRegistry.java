package trollogyadherent.offlineauth.mixin.late.tabfaces;

import net.minecraft.util.ResourceLocation;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.registries.ClientRegistry;
import org.fentanylsolutions.tabfaces.util.PingUtil;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(value = ClientRegistry.class, priority = 999)
public class MixinClientRegistry {
	@Shadow(remap = false)
	private Map<String, ClientRegistry.Data> playerEntities = new HashMap();
	@Shadow(remap = false)
	private volatile boolean fetchingServerStatus = false;
	
	@Inject(
			method = "getTabMenuResourceLocation",
			at = @At(value = "HEAD"),
			cancellable = true,
			remap = false)
	public void getTabMenuResourceLocation(String displayName, boolean removeAfterTTL, int ttl, CallbackInfoReturnable<ResourceLocation> cir) {
		if (VarInstanceClient.minecraftRef.thePlayer != null
				&& VarInstanceClient.minecraftRef.thePlayer.getDisplayName()
				.equals(displayName)) {
			cir.setReturnValue(VarInstanceClient.minecraftRef.thePlayer.getLocationSkin());
		}
		
		ClientRegistry.Data data = playerEntities.get(displayName);
		if (data == null) {
			if (!fetchingServerStatus) {
				fetchingServerStatus = true;
				new Thread(() -> {
					TabFaces.debug("Starting new ServerPingThread");
					PingUtil.ServerStatusCallbackClientRegistry callback = new PingUtil.ServerStatusCallbackClientRegistry();
					PingUtil.pingServer(callback);
					fetchingServerStatus = false;
				}, "ServerPingThread-" + displayName).start();
			}
		}
		
		ResourceLocation oaSkin;
		
		if(VarInstanceClient.minecraftRef.theWorld != null) {
			oaSkin = SkinUtil.getSkinResourceLocationByDisplayName(VarInstanceClient.minecraftRef, displayName, true);
		} else {
			oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(displayName);
		}
		
		if (oaSkin != null) {
			cir.setReturnValue(oaSkin);
		}
	}

	@Shadow(remap = false)
	public ClientRegistry.Data getByDisplayName(String displayName) {
		return (ClientRegistry.Data)this.playerEntities.get(displayName);
	}

	@Inject(
			method = "insert",
			at = @At(value = "TAIL"),
			remap = false
			)
	public void insert(String displayName, UUID id, ResourceLocation skinResourceLocation, boolean removeAfterTTL, int ttl, CallbackInfo ci) {
		this.getByDisplayName(displayName).foundRealSkin = true; //Required to bypass check in ClientUtil.drawHoveringTextWithFaces
	}
	
}
