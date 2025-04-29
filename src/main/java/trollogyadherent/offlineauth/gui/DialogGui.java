package trollogyadherent.offlineauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public abstract class DialogGui extends GuiScreen {
    GuiButton yes;
    GuiButton no;

    GuiScreen prev;

    private boolean resolutionChanged = false;

    int dialogWidth;
    int dialogHeight;

    DialogGui(GuiScreen prev) {
        this.mc = Minecraft.getMinecraft();
        this.fontRendererObj = mc.fontRenderer;
        this.prev = prev;
        setDialogSize(0, 0);
    }

    /* Anything extending this class knows better what size it should be.
    * This could be automated based on the buttonlist but extra elements
    * like strings are not present in any standard array, just plain drawn. */
    public void setDialogSize(int width, int height) {
        dialogWidth = width;
        dialogHeight = height;
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

        Tessellator tessellator = Tessellator.instance;
        tessellator.setTranslation(0, 0, 0);
        tessellator.startDrawingQuads();

        /* Ok so basically, the texture is a bit smaller than the size of the image itself.
        * These values are passed to the tesselator and tell it how much of the image it should actually draw,
        * in a 0 to 1 double (like a percentage). */
        double textureWidth = 0.96875;
        double textureHeight = 0.6484375;

        tessellator.addVertexWithUV(x, (y + drawHeight), 0.0D, 0, textureHeight);
        tessellator.addVertexWithUV((x + drawWidth), (y + drawHeight), 0.0D, textureWidth, textureHeight);
        tessellator.addVertexWithUV((x + drawWidth), y, 0.0D, textureWidth, 0);
        tessellator.addVertexWithUV(x, y, 0.0D, 0, 0);
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (resolutionChanged) {
            resolutionChanged = false;
            prev.drawScreen(0, 0, partialTicks);
        }

        //dialogWidth = this.width - this.width / 9;
        //dialogHeight = this.height - MathHelper.floor_double(this.height / 4.5) - 20;

        if (dialogWidth == 0) {
            /* A sensible default */
            setDialogSize(this.width / 5 * 3, this.height / 6 * 3);
        }

        drawModalRectWithCustomSizedTextureFancyBackground(this.width / 2 - dialogWidth / 2, this.height / 2 - dialogHeight / 2, dialogWidth, dialogHeight);

        super.drawScreen(mouseX, mouseY, partialTicks);

    }

    @Override
    public void initGui() {
        super.initGui();

        int buttonsHeight = this.height / 2 + 15;

        this.yes = new GuiButton(0, this.width / 2 - 105,  buttonsHeight, 100, 20, I18n.format("offlineauth.dialog.yes"));
        this.no = new GuiButton(1, this.width / 2, buttonsHeight, 100, 20, I18n.format("offlineauth.dialog.no"));

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
        /*
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;

        ResourceLocation border = new ResourceLocation("textures/gui/demo_background.png");

        /* popup sizes /
        double x = this.width / 9;
        double y = this.height / 4.5;
        int effectiveWidth = (int) (this.width - x);
        int effectiveHeight = (int) (this.height - y - 20);

        /* Drawing the texture repurposed as border /
        this.mc.getTextureManager().bindTexture(border);
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(4210752);

        int textureWidth = 1;
        double textureHeight = 0.5;


        tessellator.addVertexWithUV(x, effectiveHeight, 0D, 0D, textureHeight);
        tessellator.addVertexWithUV(effectiveWidth, effectiveHeight, 0D, textureWidth, textureHeight);
        tessellator.addVertexWithUV(effectiveWidth, y, 0D, textureWidth, param1);
        tessellator.addVertexWithUV(x, y, 0D, 0D, param1);
        tessellator.draw();
        */
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