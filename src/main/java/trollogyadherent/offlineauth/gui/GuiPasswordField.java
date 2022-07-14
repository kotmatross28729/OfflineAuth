package trollogyadherent.offlineauth.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

@SideOnly(Side.CLIENT)
class GuiPasswordField extends GuiTextFieldCopy {

    GuiPasswordField(FontRenderer renderer, int posx, int posy, int x, int y) {
        super(renderer, posx, posy, x, y);
    }
    boolean pwVisible = false;

    @Override
    public void setText(String p_146180_1_)
    {
        super.setText(p_146180_1_);
        super.setCursorPositionEnd();
    }

    /* Ripped from mojang code */
    public void drawTextBox() {
        String s_ = this.getPW();
        setText(this.getText());

        if (super.getVisible())
        {
            if (super.getEnableBackgroundDrawing())
            {
                drawRect(super.xPosition - 1, super.yPosition - 1, super.xPosition + super.width + 1, super.yPosition + super.height + 1, -6250336);
                drawRect(super.xPosition, super.yPosition, super.xPosition + super.width, super.yPosition + super.height, -16777216);
            }

            int i = super.isEnabled ? super.enabledColor : super.disabledColor;
            int j = super.cursorPosition - super.lineScrollOffset;
            int k = super.selectionEnd - super.lineScrollOffset;
            String s = super.field_146211_a.trimStringToWidth(super.text.substring(super.lineScrollOffset), super.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = super.isFocused && super.cursorCounter / 6 % 2 == 0 && flag;
            int l = super.enableBackgroundDrawing ? super.xPosition + 4 : super.xPosition;
            int i1 = super.enableBackgroundDrawing ? super.yPosition + (super.height - 8) / 2 : super.yPosition;
            int j1 = l;

            if (k > s.length())
            {
                k = s.length();
            }

            if (s.length() > 0)
            {
            //    String s1 = flag ? s.substring(0, j) : s;
            //    j1 = super.field_146211_a.drawStringWithShadow(s1, l, i1, i);
                j1 = super.field_146211_a.drawStringWithShadow(this.getText(), l, i1, i);
            }

            boolean flag2 = super.cursorPosition < super.text.length() || super.text.length() >= super.getMaxStringLength();
            int k1 = j1;

            if (!flag)
            {
                k1 = j > 0 ? l + super.width : l;
            }
            else if (flag2)
            {
                k1 = j1 - 1;
                --j1;
            }

            if (s.length() > 0 && flag && j < s.length())
            {
                super.field_146211_a.drawStringWithShadow(s.substring(j), j1, i1, i);
            }

            if (flag1)
            {
                if (flag2)
                {
                    Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + super.field_146211_a.FONT_HEIGHT, -3092272);
                }
                else
                {
                    super.field_146211_a.drawStringWithShadow("_", k1, i1, i);
                }
            }

            if (k != j)
            {
                int l1 = l + super.field_146211_a.getStringWidth(s.substring(0, k));
                super.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + super.field_146211_a.FONT_HEIGHT);
            }
        }

        //super.drawTextBox();

        this.setText(s_);

    }

    @Override
    public String getText() {
        if (pwVisible) {
            String text = super.getText();
            if (text.length() > 10) {
                return text.substring(text.length() - 10);
            }
            return text;
        } else {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < super.getText().length() && i < 16; i++) {
                res.append("\u25CF");
            }
            return res.toString();
        }
    }

    String getPW() {
        return super.getText();
    }

    public boolean isPwVisible() {
        return pwVisible;
    }

    public void setPwVisible(boolean visible) {
        this.setFocused(false);
        super.setFocused(false);
        this.pwVisible = visible;
    }
}