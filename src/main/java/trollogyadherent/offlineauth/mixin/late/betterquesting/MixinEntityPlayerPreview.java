package trollogyadherent.offlineauth.mixin.late.betterquesting;

import betterquesting.api2.utils.EntityPlayerPreview;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import trollogyadherent.offlineauth.mixinHelper.betterquesting.ISetSkinLocation;
@Mixin(value = EntityPlayerPreview.class, priority = 999)
public class MixinEntityPlayerPreview implements ISetSkinLocation {
	@Shadow(remap = false)
	@Final
	@Mutable
	private ResourceLocation resource;
	
	@Override
	public void offlineAuth$setLocationSkin(ResourceLocation resourceLocation) {
		this.resource = resourceLocation;
	}
}
