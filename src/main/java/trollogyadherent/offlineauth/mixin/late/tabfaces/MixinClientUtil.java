package trollogyadherent.offlineauth.mixin.late.tabfaces;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
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
import org.spongepowered.asm.mixin.Unique;
import trollogyadherent.offlineauth.Config;

import java.util.List;

@Mixin(value = ClientUtil.class, priority = 999)
public class MixinClientUtil {
	
	/**
	 * @author kotmatross
	 * @reason useLegacyConversion compat
	 */
	@Overwrite(remap = false)
	public static void drawPlayerFace(ResourceLocation rl, float xPos, float yPos, float alpha) {
		if (rl != null) {
			VarInstanceClient.minecraftRef.getTextureManager().bindTexture(rl);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
			
			//Use 2:1 (64x32)
			if(Config.useLegacyConversion) {
				offlineAuth$drawTexturedRect(xPos, yPos, 8, 8, 0.125, 0.25, 0.25, 0.5);
				offlineAuth$drawTexturedRect(xPos, yPos, 8, 8, 0.625, 0.25, 0.75, 0.5);
			}
			//Use 1:1 (64x64)
			else {
				offlineAuth$drawTexturedRect(xPos, yPos, 8, 8, 0.125, 0.125, 0.25, 0.25);
				offlineAuth$drawTexturedRect(xPos, yPos, 8, 8, 0.625, 0.125, 0.75, 0.25);
			}
		}
	}
	
	@Unique
	private static void offlineAuth$drawTexturedRect(float x, float y, float w, float h, double u0, double v0, double u1, double v1) {
		Tessellator tessellator;
		if (u0 != u1 && v0 != v1) {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			offlineAuth$addRectToBufferWithUV(tessellator, x, y, w, h, u0, v0, u1, v1);
			tessellator.draw();
		} else {
			tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			offlineAuth$addRectToBuffer(tessellator, x, y, w, h);
			tessellator.draw();
		}
	}
	
	@Unique
	private static void offlineAuth$addRectToBuffer(Tessellator tessellator, double x, double y, double w, double h) {
		tessellator.addVertex(x, y + h, 0.0);
		tessellator.addVertex(x + w, y + h, 0.0);
		tessellator.addVertex(x + w, y, 0.0);
		tessellator.addVertex(x, y, 0.0);
	}
	@Unique
	private static void offlineAuth$addRectToBufferWithUV(Tessellator tessellator, float x, float y, float w, float h, double u0, double v0, double u1, double v1) {
		tessellator.addVertexWithUV((double)x, (double)(y + h), 0.0, u0, v1);
		tessellator.addVertexWithUV((double)(x + w), (double)(y + h), 0.0, u1, v1);
		tessellator.addVertexWithUV((double)(x + w), (double)y, 0.0, u1, v0);
		tessellator.addVertexWithUV((double)x, (double)y, 0.0, u0, v0);
	}
	
	
	
	/**
	 * @author kotmatross
	 * @reason Remove ClientRegistry.Data usage
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
