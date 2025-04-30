package trollogyadherent.offlineauth.mixin.late.tabfaces;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.fentanylsolutions.tabfaces.TabFaces;
import org.fentanylsolutions.tabfaces.access.IMixinGui;
import org.fentanylsolutions.tabfaces.access.IMixinGuiScreen;
import org.fentanylsolutions.tabfaces.util.ClientUtil;
import static org.fentanylsolutions.tabfaces.util.ClientUtil.faceWidth;
import static org.fentanylsolutions.tabfaces.util.ClientUtil.fontRenderer;
import static org.fentanylsolutions.tabfaces.util.ClientUtil.serverGuiTTL;
import org.fentanylsolutions.tabfaces.varinstances.VarInstanceClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import trollogyadherent.offlineauth.skin.SkinUtil;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;

import java.util.List;

@Mixin(value = ClientUtil.class, priority = 999)
public class MixinClientUtil {
	
	@Inject(
			method = "skinResourceLocation",
			at = @At(value = "HEAD"),
			cancellable = true,
			remap = false)
	private static void skinResourceLocation(GameProfile profile, CallbackInfoReturnable<ResourceLocation> cir) {
		if(profile == null || profile.getName() == null) {
			return;
		}
		
		String profileName = profile.getName().contains(" ") ? profile.getName().trim() : profile.getName();
		
		if (VarInstanceClient.minecraftRef.thePlayer != null
				&& VarInstanceClient.minecraftRef.thePlayer.getDisplayName()
				.equals(profileName)) {
			cir.setReturnValue(VarInstanceClient.minecraftRef.thePlayer.getLocationSkin());
		}
		ResourceLocation oaSkin;
		
		if(VarInstanceClient.minecraftRef.theWorld != null) {
			oaSkin = SkinUtil.getSkinResourceLocationByDisplayName(VarInstanceClient.minecraftRef, profileName, true);
			if(oaSkin == null)
				oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(profileName);
		} else {
			oaSkin = ClientSkinUtil.loadSkinFromCacheQuiet(profileName);
		}
		
		if (oaSkin != null) {
			cir.setReturnValue(oaSkin);
		}
	}
	
	/**
	 * @author kotmatross
	 * @reason useLegacyConversion compat
	 */
	@Overwrite(remap = false)
	public static void drawPlayerFace(ResourceLocation rl, float xPos, float yPos, float alpha) {
		if (rl != null) {
			VarInstanceClient.minecraftRef.getTextureManager().bindTexture(rl);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
			SkinUtil.drawPlayerFaceAuto(xPos, yPos, 8, 8);
		}
	}
	
	
	/**
	 * @author
	 * @reason
	 */
	@Overwrite(remap = false)
	public static void drawHoveringTextWithFaces(GuiScreen screen, GameProfile[] profiles, List<String> textLines,
												 int x, int y) {
		if (!textLines.isEmpty()) {
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			int boxWidth = 0;
			
			for (String line : textLines) {
				int tmpWidth = fontRenderer.getStringWidth(line);
				
				if (tmpWidth > boxWidth) {
					boxWidth = tmpWidth;
				}
			}
			
			int boxOffsetX = x + 12;
			int boxOffsetY = y - 12;
			int i1 = 8;
			
			if (textLines.size() > 1) {
				i1 += 2 + (textLines.size() - 1) * 10;
			}
			
			if (boxOffsetX + boxWidth > screen.width) {
				boxOffsetX -= 28 + boxWidth;
			}
			
			if (boxOffsetY + i1 + 6 > screen.height) {
				boxOffsetY = screen.height - i1 - 6;
			}
			
			if (profiles != null) {
				for (String line : textLines) {
						int tmpWidth = fontRenderer.getStringWidth(line) + faceWidth;
						if (tmpWidth > boxWidth) {
							boxWidth = tmpWidth;
						}
				}
			}
			
			((IMixinGui) screen).setZLevel(300.0F);
			((IMixinGuiScreen) screen).getItemRender().zLevel = 300.0F;
			int outerBorderColor = -267386864;
			/* outer border */
			((IMixinGui) screen).drawGradientRectPub(
					boxOffsetX - 3,
					boxOffsetY - 4,
					boxOffsetX + boxWidth + 3,
					boxOffsetY - 3,
					outerBorderColor,
					outerBorderColor); // top
			((IMixinGui) screen).drawGradientRectPub(
					boxOffsetX - 3,
					boxOffsetY + i1 + 3,
					boxOffsetX + boxWidth + 3,
					boxOffsetY + i1 + 4,
					outerBorderColor,
					outerBorderColor); // bottom
			((IMixinGui) screen).drawGradientRectPub(
					boxOffsetX - 4,
					boxOffsetY - 3,
					boxOffsetX - 3,
					boxOffsetY + i1 + 3,
					outerBorderColor,
					outerBorderColor); // left
			((IMixinGui) screen).drawGradientRectPub(
					boxOffsetX + boxWidth + 3,
					boxOffsetY - 3,
					boxOffsetX + boxWidth + 4,
					boxOffsetY + i1 + 3,
					outerBorderColor,
					outerBorderColor); // right
			/* Inner box */
			((IMixinGui) screen).drawGradientRectPub(
					boxOffsetX - 3,
					boxOffsetY - 3,
					boxOffsetX + boxWidth + 3,
					boxOffsetY + i1 + 3,
					outerBorderColor,
					outerBorderColor);
			int innerBorderColor = 1347420415;
			int l1 = (innerBorderColor & 16711422) >> 1 | innerBorderColor & -16777216;
			/* inner border */
			((IMixinGui) screen).drawGradientRectPub(
					boxOffsetX - 3,
					boxOffsetY - 3 + 1,
					boxOffsetX - 3 + 1,
					boxOffsetY + i1 + 3 - 1,
					innerBorderColor,
					l1); // left
			// border
			((IMixinGui) screen).drawGradientRectPub(
					boxOffsetX + boxWidth + 2,
					boxOffsetY - 3 + 1,
					boxOffsetX + boxWidth + 3,
					boxOffsetY + i1 + 3 - 1,
					innerBorderColor,
					l1); // right border
			((IMixinGui) screen).drawGradientRectPub(
					boxOffsetX - 3,
					boxOffsetY - 3,
					boxOffsetX + boxWidth + 3,
					boxOffsetY - 3 + 1,
					innerBorderColor,
					innerBorderColor); // upper
			// border
			((IMixinGui) screen).drawGradientRectPub(
					boxOffsetX - 3,
					boxOffsetY + i1 + 2,
					boxOffsetX + boxWidth + 3,
					boxOffsetY + i1 + 3,
					l1,
					l1); // bottom
			// border
			
			for (int i2 = 0; i2 < textLines.size(); ++i2) {
				String s1 = textLines.get(i2);
				boolean fake = true;
				if (profiles != null) {
					for (GameProfile profile : profiles) {
						if (!profile.getId()
								.equals(ClientUtil.fakePlayerUUID) && profile.getName()
								.equals(s1)) {
							
							if (!TabFaces.varInstanceClient.clientRegistry.displayNameInRegistry(s1)) {
								TabFaces.varInstanceClient.clientRegistry
										.insert(s1, profile.getId(), null, true, serverGuiTTL);
							}
							
							ResourceLocation rl = TabFaces.varInstanceClient.clientRegistry
									.getTabMenuResourceLocation(s1, true, serverGuiTTL);
							
							fontRenderer.drawStringWithShadow(s1, boxOffsetX + faceWidth, boxOffsetY, -1);
							
							if (rl != null) {
								drawPlayerFace(rl, boxOffsetX, boxOffsetY, 1.0f);
							}
							
							fake = false;
							break;
						}
					}
				}
				if (fake) {
					fontRenderer.drawStringWithShadow(s1, boxOffsetX, boxOffsetY, -1);
				}
				
				if (i2 == 0) {
					boxOffsetY += 2;
				}
				
				boxOffsetY += 10;
			}
			
			((IMixinGui) screen).setZLevel(0.0F);
			((IMixinGuiScreen) screen).getItemRender().zLevel = 0.0F;
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableStandardItemLighting();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		}
	}
	
}
