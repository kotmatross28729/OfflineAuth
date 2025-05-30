package trollogyadherent.offlineauth.gui.skin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.util.ClientUtil;

import java.util.List;

@SideOnly(Side.CLIENT)
public class AvailableSkinsListGUI extends GuiListExtended {
    protected final Minecraft mc;
    protected final List<SkinListEntry> skinEntries;

    int selectedIndex;

    public AvailableSkinsListGUI(Minecraft mc, int listWidth, int listHeight, int entryHeight, List<SkinListEntry> skinEntries) {
        super(mc, listWidth, listHeight, 32, listHeight - 55 + 4 , entryHeight);
        this.mc = mc;
        this.skinEntries = skinEntries;
        this.field_148163_i = false;
        this.setHasListHeader(true, (int)((float)mc.fontRenderer.FONT_HEIGHT * 1.5F));
        selectedIndex = -1;
        if (skinEntries.size() > 0 && ClientUtil.isSinglePlayer()) {
            String skinName = ClientSkinUtil.getLastUsedOfflineSkinName();
            if (skinName != null) {
                for (int i = 0; i < skinEntries.size(); i++) {
                    if (skinEntries.get(i).skinName.equals(skinName)) {
                        OfflineAuth.varInstanceClient.skinGuiRenderTicker.setSkin(skinEntries.get(i).skinName);
                        selectedIndex = i;
                    }
                }
            }
        }
        if (!ClientUtil.isSinglePlayer() /*&& SkinGuiRenderTicker.skinResourceLocation == null*/) {
            if (Minecraft.getMinecraft().thePlayer != null && SkinGuiRenderTicker.clientPlayerMP != null) {
                try {
                    /*ResourceLocation rl*/ SkinGuiRenderTicker.skinResourceLocation = (ResourceLocation) OfflineAuth.varInstanceClient.skinLocationField.get(Minecraft.getMinecraft().thePlayer);
                    /*OfflineAuth.debug("1, " + OfflineAuth.varInstanceClient.skinLocationfield.get(SkinGuiRenderTicker.clientPlayerMP));
                    OfflineAuth.varInstanceClient.skinLocationfield.set(SkinGuiRenderTicker.clientPlayerMP, rl);
                    OfflineAuth.debug("2, " + OfflineAuth.varInstanceClient.skinLocationfield.get(SkinGuiRenderTicker.clientPlayerMP));*/
                } catch (IllegalAccessException e) {
                    OfflineAuth.error("Failed to get ingame skin");
                }
            }
        }
    }

    protected void drawListHeader(int par1, int par2, Tessellator tessellator) {
        String s = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + this.getListHeader();
        this.mc.fontRenderer.drawString(s, par1 + this.width / 2 - this.mc.fontRenderer.getStringWidth(s) / 2, Math.min(this.top + 3, par2), 16777215);
    }

    protected String getListHeader() {
        return I18n.format("offlineauth.skingui.available_skins");
    }
    public List<SkinListEntry> func_148201_l() {
        return this.skinEntries;
    }

    protected int getSize()
    {
        return this.func_148201_l().size();
    }

    public SkinListEntry getListEntry_(int index) {
        return this.func_148201_l().get(index);
    }

    public int getListWidth()
    {
        return this.width;
    }

    protected int getScrollBarX()
    {
        return this.right - 6;
    }

    @Override
    public IGuiListEntry getListEntry(int p_148180_1_) {
        return null;
    }

    public boolean isSelected(int index) {
        return index == selectedIndex;
    }

    @Override
    protected void drawSlot(int index, int p_148126_2_, int p_148126_3_, int p_148126_4_, Tessellator p_148126_5_, int p_148126_6_, int p_148126_7_)
    {
        this.getListEntry_(index).drawEntry(index, p_148126_2_, p_148126_3_, this.getListWidth(), p_148126_4_, p_148126_5_, p_148126_6_, p_148126_7_, this.func_148124_c(p_148126_6_, p_148126_7_) == index);
    }

    @Override
    public boolean func_148179_a(int p_148179_1_, int p_148179_2_, int p_148179_3_)
    {
        if (this.func_148141_e(p_148179_2_))
        {
            int l = this.func_148124_c(p_148179_1_, p_148179_2_);

            if (l >= 0)
            {
                int i1 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                int j1 = this.top + 4 - this.getAmountScrolled() + l * this.slotHeight + this.headerPadding;
                int k1 = p_148179_1_ - i1;
                int l1 = p_148179_2_ - j1;

                if (this.getListEntry_(l).mousePressed(l, p_148179_1_, p_148179_2_, p_148179_3_, k1, l1))
                {
                    //System.out.println("hmm: " + this.getListEntry_(l).skinName);
                    this.selectedIndex = l;
                    //this.func_148143_b(false);  //this thing blocks the ability to drag the scrollbar with the mouse
                    OfflineAuth.varInstanceClient.skinGuiRenderTicker.setSkin(this.getListEntry_(l).skinName);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean func_148181_b(int x, int y, int mouseEvent)
    {
        for (int l = 0; l < this.getSize(); ++l)
        {
            int i1 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int j1 = this.top + 4 - this.getAmountScrolled() + l * this.slotHeight + this.headerPadding;
            int k1 = x - i1;
            int l1 = y - j1;
            this.getListEntry_(l).mouseReleased(l, x, y, mouseEvent, k1, l1);
        }

        this.func_148143_b(true);
        return false;
    }
}
