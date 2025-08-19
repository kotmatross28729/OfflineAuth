package trollogyadherent.offlineauth.mixin.early.CPM;

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
import trollogyadherent.offlineauth.mixinHelper.CPM.CPMHelper;
import trollogyadherent.offlineauth.mixinHelper.CPM.ModelBipedModern_CPM;

@Mixin(value = RenderPlayer.class, priority = 999)
public abstract class MixinRenderPlayer_CPM extends RendererLivingEntity {
	@Shadow
	public ModelBiped modelBipedMain;
	public MixinRenderPlayer_CPM(ModelBase p_i1261_1_, float p_i1261_2_) {
		super(p_i1261_1_, p_i1261_2_);
	}
	
	// todo: fix armor
	@Redirect(method = "<init>", at = @At(value = "NEW", args = "class=net/minecraft/client/model/ModelBiped"))
	private static ModelBiped init(float p_i1148_1_) {
		return new ModelBipedModern_CPM(p_i1148_1_);
	}
	
	@Inject(method = "renderFirstPersonArm", at = @At(value = "TAIL"))
	private void renderFirstPersonArm(EntityPlayer p_82441_1_, CallbackInfo ci) {
		if(!CPMHelper.isUsingCPMModel(p_82441_1_)) {
			((ModelBipedModern_CPM) this.modelBipedMain).bipedRightArmwear.render(0.0625F);
		}
	}
}
