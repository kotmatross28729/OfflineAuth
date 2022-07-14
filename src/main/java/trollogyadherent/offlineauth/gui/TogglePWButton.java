package trollogyadherent.offlineauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TogglePWButton extends GuiButton {
    public static final ResourceLocation TEXTURE = new ResourceLocation("offlineauth", "textures/gui/gui.png");
    private boolean visible = false;

    public TogglePWButton(int id, int x, int y, int width, int height) {
        super(id, x, y, width, height, "");
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (visible) {
            drawTexturedModalRect(xPosition, yPosition, 16, 0, width, height);
        } else {
            drawTexturedModalRect(xPosition, yPosition, 0, 0, width, height);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
