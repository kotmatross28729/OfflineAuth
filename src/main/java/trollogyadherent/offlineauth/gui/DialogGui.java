package trollogyadherent.offlineauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public abstract class DialogGui extends GuiScreen {
    private GuiButton yes;
    private GuiButton no;

    GuiScreen prev;

    private boolean resolutionChanged = false;

    DialogGui(GuiScreen prev) {
        this.mc = Minecraft.getMinecraft();
        this.fontRendererObj = mc.fontRenderer;
        this.prev = prev;
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        switch (b.id) {
            case 0:
                try {
                    actionConfirm();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 1:
                actionCancel();
                break;
        }
    }

    public void drawModalRectWithCustomSizedTextureFancyBackground(int x, int y, int drawWidth, int drawHeight)
    {
        ResourceLocation border = new ResourceLocation("textures/gui/demo_background.png");
        this.mc.getTextureManager().bindTexture(border);

        GL11.glEnable(GL11.GL_BLEND);

        float margin = 9;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        tessellator.addVertexWithUV(x, (y + drawHeight), 0.0D,              0,                   1);
        //tessellator.addVertexWithUV((x + drawWidth), (y + drawHeight), 0.0D,    1,  1);
        tessellator.addVertexWithUV((drawWidth + margin), (y + drawHeight), 0.0D,    1,  1);
        //tessellator.addVertexWithUV((x + drawWidth), y, 0.0D,               1,  0);
        tessellator.addVertexWithUV((drawWidth + margin), y, 0.0D,               1,  0);
        tessellator.addVertexWithUV(x, y, 0.0D,                         0,                   0);
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (resolutionChanged) {
            resolutionChanged = false;
            prev.drawScreen(0, 0, partialTicks);
        }

        /* popup sizes */
        //int x = (int) (this.width / 5.0 + 4.5); //100.0;
        int x = (int) (this.width / 9);
        int y = (int) (this.height / 4.5); //100.0;
        int effectiveWidth = (this.width - x);
        int effectiveHeight = (this.height - y - 20);

        drawModalRectWithCustomSizedTextureFancyBackground(x, y, effectiveWidth, effectiveHeight);

        super.drawScreen(mouseX, mouseY, partialTicks);

    }

    @Override
    public void initGui() {
        super.initGui();

        this.yes = new GuiButton(0, this.width / 2 - 105, this.height / 2, 100, 20, "Yes");
        this.no = new GuiButton(1, this.width / 2, this.height / 2, 100, 20, "No");

        this.buttonList.add(this.yes);
        this.buttonList.add(this.no);
    }

    @Override
    protected void keyTyped(char c, int k) {
        super.keyTyped(c, k);
        if (k == Keyboard.KEY_ESCAPE) {
            actionCancel();
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft p_146280_1_, int p_146280_2_, int p_146280_3_)
    {
        prev.setWorldAndResolution(p_146280_1_, p_146280_2_, p_146280_3_);
        resolutionChanged = true;

        this.mc = p_146280_1_;
        this.fontRendererObj = p_146280_1_.fontRenderer;
        this.width = p_146280_2_;
        this.height = p_146280_3_;
        if (!MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Pre(this, this.buttonList)))
        {
            this.buttonList.clear();
            this.initGui();
        }
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Post(this, this.buttonList));
    }

    @Override
    public void drawBackground(int param1)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;

        ResourceLocation border = new ResourceLocation("textures/gui/demo_background.png");

        /* popup sizes */
        //double x = this.width / 5.0; //100.0;
        double x = this.width / 9;
        double y = this.height / 4.5; //100.0;
        int effectiveWidth = (int) (this.width - x);
        int effectiveHeight = (int) (this.height - y - 20);

        /* Drawing the texture repurposed as border */
        this.mc.getTextureManager().bindTexture(border);
        float f = 128.0F;//45.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(4210752);

        int textureWidth = 1;//247;
        double textureHeight = 0.5; //165;


        tessellator.addVertexWithUV(x, effectiveHeight, 0D, 0D, textureHeight);
        tessellator.addVertexWithUV(effectiveWidth, effectiveHeight, 0D, textureWidth, textureHeight);
        tessellator.addVertexWithUV(effectiveWidth, y, 0D, textureWidth, param1);
        tessellator.addVertexWithUV(x, y, 0D, 0D, param1);
        tessellator.draw();
    }

    protected void actionCancel() {
        this.mc.displayGuiScreen(prev);
    }

    private void actionConfirm() throws NoSuchAlgorithmException, IOException {
        this.mc.displayGuiScreen(prev);
        actionOnConfirm();
    }

    protected abstract void actionOnConfirm() throws NoSuchAlgorithmException, IOException;
}