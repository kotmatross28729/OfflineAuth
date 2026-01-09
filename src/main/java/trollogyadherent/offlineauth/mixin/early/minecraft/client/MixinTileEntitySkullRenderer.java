package trollogyadherent.offlineauth.mixin.early.minecraft.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import trollogyadherent.offlineauth.mixinHelper.ModelHeadModern;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.varinstances.client.VarInstanceClient;

@Mixin(value = TileEntitySkullRenderer.class, priority = 999)
public abstract class MixinTileEntitySkullRenderer extends TileEntitySpecialRenderer {

	@Unique
	private static final ResourceLocation SKELETON_TEXTURE = new ResourceLocation("textures/entity/skeleton/skeleton.png");
	@Unique
	private static final ResourceLocation WITHER_SKELETON_TEXTURE = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
	@Unique
	private static final ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation("textures/entity/zombie/zombie.png");
	@Unique
	private static final ResourceLocation CREEPER_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper.png");
	@Unique
	private final ModelSkeletonHead HEAD_MODEL_LEGACY = new ModelSkeletonHead(0, 0, 64, 32);
	@Unique
	private final ModelHeadModern HEAD_MODEL_MODERN = new ModelHeadModern(0, 0, 64, 64);
	
	/**
	 * @author kotmatross
	 * @reason support for offlineauth / basicSkinBackport
	 */
	@Overwrite
	public void func_152674_a(float x, float y, float z, int rotationType, float yaw, int skullType, GameProfile profile) {
		ModelBase headModel = this.HEAD_MODEL_LEGACY;
		
		switch (skullType) {
			case 0:
			default:
				this.bindTexture(SKELETON_TEXTURE);
				break;
			case 1:
				this.bindTexture(WITHER_SKELETON_TEXTURE);
				break;
			case 2:
				this.bindTexture(ZOMBIE_TEXTURE);
				headModel = this.HEAD_MODEL_MODERN;
				break;
			case 3:
				ResourceLocation resourcelocation = VarInstanceClient.DEFAULT_SKIN_64;
				
				if (profile != null && profile.getName() != null) {
					String profileName = profile.getName();
					Minecraft minecraft = Minecraft.getMinecraft();
					
					if (minecraft.thePlayer != null && minecraft.thePlayer.getDisplayName().equals(profileName)) {
						resourcelocation = minecraft.thePlayer.getLocationSkin();
					} else {
						ResourceLocation oaSkin;
						if(minecraft.theWorld != null) {
							oaSkin = SkinUtil.getSkinResourceLocationByDisplayName(minecraft, profileName, true);
							if(oaSkin == null)
								oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(profileName);
						} else {
							oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(profileName);
						}
						if (oaSkin != null) {
							resourcelocation = oaSkin;
						}
					}
				}
				
				this.bindTexture(resourcelocation);
				headModel = this.HEAD_MODEL_MODERN;
				break;
			case 4:
				this.bindTexture(CREEPER_TEXTURE);
		}
		
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		if (rotationType != 1) {
			switch (rotationType) {
				case 2:
					GL11.glTranslatef(x + 0.5F, y + 0.25F, z + 0.74F);
					break;
				case 3:
					GL11.glTranslatef(x + 0.5F, y + 0.25F, z + 0.26F);
					yaw = 180.0F;
					break;
				case 4:
					GL11.glTranslatef(x + 0.74F, y + 0.25F, z + 0.5F);
					yaw = 270.0F;
					break;
				case 5:
				default:
					GL11.glTranslatef(x + 0.26F, y + 0.25F, z + 0.5F);
					yaw = 90.0F;
			}
		} else {
			GL11.glTranslatef(x + 0.5F, y, z + 0.5F);
		}
		
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		headModel.render(null, 0.0F, 0.0F, 0.0F, yaw, 0.0F, 1.0F / 16.0F);
		GL11.glPopMatrix();
	}
	
}
