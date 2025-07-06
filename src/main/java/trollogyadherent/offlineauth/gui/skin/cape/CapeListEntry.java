package trollogyadherent.offlineauth.gui.skin.cape;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.skin.SkinManagmentGUI;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@SideOnly(Side.CLIENT)
public class CapeListEntry {
    public final String capeName;
    protected final Minecraft mc;

    protected final SkinManagmentGUI previous;
    //private static final ResourceLocation temp = new ResourceLocation("textures/gui/resource_packs.png");
    private /*static*/ ResourceLocation capeResourceLocation = new ResourceLocation("textures/gui/resource_packs.png");
    private String capeSize;
    public CapeListEntry(SkinManagmentGUI skinManagmentGUI, String capeName) {
        this.previous = skinManagmentGUI;
        this.capeName = capeName;
        this.mc = Minecraft.getMinecraft();
        capeSize = "null";

        File imageFile = ClientSkinUtil.getCapeFile(capeName);
        if (imageFile == null || !imageFile.exists()) {
            OfflineAuth.error("Error cape image does not exist: " + capeName);
            return;
        }
        if (Util.filesizeInMegaBytes(imageFile) >= 1) {
            capeSize = Math.floor(Util.filesizeInMegaBytes(imageFile) * 100) / 100 + " mb";
        } else if (Util.filesizeInKiloBytes(imageFile) >= 1) {
            capeSize = Math.floor(Util.filesizeInKiloBytes(imageFile) * 100) / 100 + " kb";
        } else {
            capeSize = Math.floor(Util.filesizeInBytes(imageFile) * 100) / 100 + " b";
        }

        BufferedImage bufferedImage;
        if (imageFile.getName().endsWith(".png")) {
            if (!Util.pngIsSane(imageFile)) {
                OfflineAuth.error("Error loading cape image, not sane: " + capeName);
                return;
            }
            try {
                bufferedImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                OfflineAuth.error("Error loading cape image " + capeName);
                return;
            }
        } else {
            BufferedImage bi = ClientSkinUtil.getFirstGifFrame(imageFile);
            if (bi == null) {
                OfflineAuth.error("Error loading cape image " + capeName);
                return;
            }
            bufferedImage = bi;
        }

        capeResourceLocation = new ResourceLocation("offlineauth", "skinlistentrycapes/" + capeName);
        ClientSkinUtil.loadTexture(bufferedImage, capeResourceLocation);
    }

    public void drawEntry(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_, Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_)
    {
        this.bindIcon();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int l2 = 8;
        int i3 = 8;

        Gui.func_152125_a(p_148279_2_, p_148279_3_, 8.0F, (float) l2, 8, i3, 32/*8*/, /*8*/32, 64.0F, 64.0F);
        int i2;

        String s = this.getCapeName();
        i2 = this.mc.fontRenderer.getStringWidth(s);

        if (i2 > 157)
        {
            s = this.mc.fontRenderer.trimStringToWidth(s, 157 - this.mc.fontRenderer.getStringWidth("...")) + "...";
        }

        this.mc.fontRenderer.drawStringWithShadow(s, p_148279_2_ + 32 + 2, p_148279_3_ + 1, 16777215);
        List list = this.mc.fontRenderer.listFormattedStringToWidth(this.getCapeDescription(), 157);

        for (int j2 = 0; j2 < 2 && j2 < list.size(); ++j2)
        {
            this.mc.fontRenderer.drawStringWithShadow((String)list.get(j2), p_148279_2_ + 32 + 2, p_148279_3_ + 12 + 10 * j2, 8421504);
        }
    }

    protected String getCapeDescription() {
        return capeSize;
    }

    protected String getCapeName() {
        return this.capeName;
    }

    protected void bindIcon() {
        OfflineAuth.varInstanceClient.getTextureManager().bindTexture(capeResourceLocation); //bindTexturePackIcon(this.field_148317_a.getTextureManager());
    }

    protected boolean func_148310_d()
    {
        return true;
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_)
    {
        if (this.func_148310_d() /*&& p_148278_5_ <= 32 */)
        {
            return true;
        }

        return false;
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_, int p_148277_6_) {}
}