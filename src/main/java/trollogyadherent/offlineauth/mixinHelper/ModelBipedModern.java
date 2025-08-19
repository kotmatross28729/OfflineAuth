package trollogyadherent.offlineauth.mixinHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelBipedModern extends ModelBiped {
	
	public ModelRenderer bipedLeftArmwear;
	public ModelRenderer bipedRightArmwear;
	public ModelRenderer bipedLeftLegwear;
	public ModelRenderer bipedRightLegwear;
	public ModelRenderer bipedBodyWear;
	
	public ModelBipedModern(float scale) {
		super(scale, 0.0F, 64, 64);
		
		this.bipedLeftArm = new ModelRenderer(this, 32, 48);
		this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
		this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.bipedLeftArmwear = new ModelRenderer(this, 48, 48);
		this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
		this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.bipedRightArmwear = new ModelRenderer(this, 40, 32);
		this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
		this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
		
		this.bipedLeftLeg = new ModelRenderer(this, 16, 48);
		this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
		this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.bipedLeftLegwear = new ModelRenderer(this, 0, 48);
		this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
		this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.bipedRightLegwear = new ModelRenderer(this, 0, 32);
		this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
		this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.bipedBodyWear = new ModelRenderer(this, 16, 32);
		this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F);
		this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
	}
	
	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		this.bipedLeftLegwear.render(scaleFactor);
		this.bipedRightLegwear.render(scaleFactor);
		this.bipedLeftArmwear.render(scaleFactor);
		this.bipedRightArmwear.render(scaleFactor);
		this.bipedBodyWear.render(scaleFactor);
	}
	
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		copyModelAngles(this.bipedLeftLeg, this.bipedLeftLegwear);
		copyModelAngles(this.bipedRightLeg, this.bipedRightLegwear);
		copyModelAngles(this.bipedLeftArm, this.bipedLeftArmwear);
		copyModelAngles(this.bipedRightArm, this.bipedRightArmwear);
		copyModelAngles(this.bipedBody, this.bipedBodyWear);
	}
	
	/**
	 * Copies the angles from one object to another. This is used when objects should stay aligned with each other, like
	 * the hair over a players head.
	 */
	public static void copyModelAngles(ModelRenderer source, ModelRenderer dest) {
		dest.rotateAngleX = source.rotateAngleX;
		dest.rotateAngleY = source.rotateAngleY;
		dest.rotateAngleZ = source.rotateAngleZ;
		dest.rotationPointX = source.rotationPointX;
		dest.rotationPointY = source.rotationPointY;
		dest.rotationPointZ = source.rotationPointZ;
	}
	
}
