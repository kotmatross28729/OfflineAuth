package trollogyadherent.offlineauth.gui.skin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.skin.client.LegacyConversion;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SkinListEntry {
    protected final String skinName;
    protected final Minecraft mc;

    protected final SkinManagmentGUI previous;
    ClientSkinUtil.OfflineTextureObject offlineTextureObject;
    //private static final ResourceLocation temp = new ResourceLocation("textures/gui/resource_packs.png");
    private /*static*/ ResourceLocation temp  = new ResourceLocation("textures/gui/resource_packs.png");
    public SkinListEntry(SkinManagmentGUI skinManagmentGUI, String skinName) {
        this.previous = skinManagmentGUI;
        this.skinName = skinName;
        this.mc = Minecraft.getMinecraft();

        File imageFile = ClientSkinUtil.getSkinFile(skinName);
        if (imageFile == null || !imageFile.exists()) {
            OfflineAuth.error("Error skin image does not exist: " + skinName);
            return;
        }
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            OfflineAuth.error("Error loading skin image " + skinName);
            return;
        }
        /*if (bufferedImage.getHeight() == 64) {
            bufferedImage = new LegacyConversion().convert(bufferedImage);
        }*/
        if (bufferedImage.getHeight() != bufferedImage.getWidth()) {
            System.out.println("texture is not square, " + skinName);
            BufferedImage bufferedImageNew = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() * 2, bufferedImage.getType());
            Graphics g = bufferedImageNew.getGraphics();
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();
            bufferedImage = bufferedImageNew;
        }
        this.offlineTextureObject = new ClientSkinUtil.OfflineTextureObject(bufferedImage);
        temp = new ResourceLocation("offlineauth", "skinlistentryskins/" + skinName);
        ClientSkinUtil.loadTexture(bufferedImage, temp, this.offlineTextureObject);
    }

    public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_)
    {
        this.bindIcon();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int l2 = 8;
        int i3 = 8;

        Gui.func_152125_a(p_148279_2_, p_148279_3_, 8.0F, (float) l2, 8, i3, 32/*8*/, /*8*/32, 64.0F, 64.0F);
        //Gui.func_146110_a(p_148279_2_, p_148279_3_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        int i2;

        if ((this.mc.gameSettings.touchscreen || p_148279_9_) && this.func_148310_d())
        {
            /*this.mc.getTextureManager().bindTexture(temp);
            Gui.drawRect(p_148279_2_, p_148279_3_, p_148279_2_ + 32, p_148279_3_ + 32, -1601138544);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int l1 = p_148279_7_ - p_148279_2_;
            i2 = p_148279_8_ - p_148279_3_;

            if (this.func_148309_e())
            {
                if (l1 < 32)
                {
                    Gui.func_146110_a(p_148279_2_, p_148279_3_, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                }
                else
                {
                    Gui.func_146110_a(p_148279_2_, p_148279_3_, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
            else
            {
                if (this.func_148308_f())
                {
                    if (l1 < 16)
                    {
                        Gui.func_146110_a(p_148279_2_, p_148279_3_, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    }
                    else
                    {
                        Gui.func_146110_a(p_148279_2_, p_148279_3_, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.func_148314_g())
                {
                    if (l1 < 32 && l1 > 16 && i2 < 16)
                    {
                        Gui.func_146110_a(p_148279_2_, p_148279_3_, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    }
                    else
                    {
                        Gui.func_146110_a(p_148279_2_, p_148279_3_, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.func_148307_h())
                {
                    if (l1 < 32 && l1 > 16 && i2 > 16)
                    {
                        Gui.func_146110_a(p_148279_2_, p_148279_3_, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    }
                    else
                    {
                        Gui.func_146110_a(p_148279_2_, p_148279_3_, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }
            } */
        }

        String s = this.getSkinName();
        i2 = this.mc.fontRenderer.getStringWidth(s);

        if (i2 > 157)
        {
            s = this.mc.fontRenderer.trimStringToWidth(s, 157 - this.mc.fontRenderer.getStringWidth("...")) + "...";
        }

        this.mc.fontRenderer.drawStringWithShadow(s, p_148279_2_ + 32 + 2, p_148279_3_ + 1, 16777215);
        List list = this.mc.fontRenderer.listFormattedStringToWidth(this.getSkinDescription(), 157);

        for (int j2 = 0; j2 < 2 && j2 < list.size(); ++j2)
        {
            this.mc.fontRenderer.drawStringWithShadow((String)list.get(j2), p_148279_2_ + 32 + 2, p_148279_3_ + 12 + 10 * j2, 8421504);
        }
    }

    protected String getSkinDescription() {
        return "[insert filesize here or something]";
    }

    protected String getSkinName() {
        return this.skinName;
    }

    protected void bindIcon() {
        this.mc.getTextureManager().bindTexture(temp); //bindTexturePackIcon(this.field_148317_a.getTextureManager());
    }

    protected boolean func_148310_d()
    {
        return true;
    }

    protected boolean func_148309_e()
    {
        return !this.previous.hasSkinEntry(this);
    }

    protected boolean func_148308_f()
    {
        return this.previous.hasSkinEntry(this);
    }

    protected boolean func_148314_g()
    {
        List list = this.previous.probablyToRemove(this);
        int i = list.indexOf(this);
        return i > 0 && ((SkinListEntry)list.get(i - 1)).func_148310_d();
    }

    protected boolean func_148307_h()
    {
        List list = this.previous.probablyToRemove(this);
        int i = list.indexOf(this);
        return i >= 0 && i < list.size() - 1 && ((SkinListEntry)list.get(i + 1)).func_148310_d();
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        if (this.func_148310_d() /*&& p_148278_5_ <= 32 */)
        {
            System.out.println(this.skinName + " 1");
            return true;
        }

        return false;
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) {}
}