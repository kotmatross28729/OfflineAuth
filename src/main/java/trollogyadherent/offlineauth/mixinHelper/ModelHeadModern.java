package trollogyadherent.offlineauth.mixinHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelHeadModern extends ModelBiped {
	
	public ModelRenderer head;
	public ModelRenderer headwear;
	
	public ModelHeadModern() {
		this(0, 35, 64, 64);
	}
	
	public ModelHeadModern(int textureOffsetX, int textureOffsetY, int textureWidth, int textureHeight) {
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		
		this.head = new ModelRenderer(this, textureOffsetX, textureOffsetY);
		this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
		
		this.headwear = new ModelRenderer(this, 32, 0);
		this.headwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, textureOffsetX + 0.5F);
		this.headwear.setRotationPoint(0.0F, 0.0F + textureOffsetY, 0.0F);
	}
	
	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
		this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
		this.head.render(p_78088_7_);
		this.headwear.render(p_78088_7_);
	}
	
	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	 * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
	 * "far" arms and legs can swing at most.
	 */
	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
		super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
		this.head.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
		this.head.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		this.headwear.rotateAngleY = this.head.rotateAngleY;
		this.headwear.rotateAngleX = this.head.rotateAngleX;
	}

}
