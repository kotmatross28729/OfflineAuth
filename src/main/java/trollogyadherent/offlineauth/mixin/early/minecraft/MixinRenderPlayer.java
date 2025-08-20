package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import trollogyadherent.offlineauth.mixinHelper.ModelBipedModern;

@Mixin(value = RenderPlayer.class, priority = 999)
public abstract class MixinRenderPlayer extends RendererLivingEntity {
	@Shadow
	public ModelBiped modelBipedMain;
	public MixinRenderPlayer(ModelBase p_i1261_1_, float p_i1261_2_) {
		super(p_i1261_1_, p_i1261_2_);
	}
	
	@Redirect(method = "<init>", at = @At(value = "NEW", target = "net/minecraft/client/model/ModelBiped", ordinal = 0))
	private static ModelBiped init(float p_i1148_1_) {
		return new ModelBipedModern(p_i1148_1_);
	}
	
	@Inject(method = "renderFirstPersonArm", at = @At(value = "TAIL"))
	private void renderFirstPersonArm(EntityPlayer p_82441_1_, CallbackInfo ci) {
		((ModelBipedModern) this.modelBipedMain).bipedRightArmwear.render(0.0625F);
	}
}
