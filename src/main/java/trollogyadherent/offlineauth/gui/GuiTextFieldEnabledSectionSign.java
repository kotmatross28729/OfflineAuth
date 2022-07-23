package trollogyadherent.offlineauth.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldEnabledSectionSign extends GuiTextField {
    public GuiTextFieldEnabledSectionSign(FontRenderer p_i1032_1_, int p_i1032_2_, int p_i1032_3_, int p_i1032_4_, int p_i1032_5_) {
        super(p_i1032_1_, p_i1032_2_, p_i1032_3_, p_i1032_4_, p_i1032_5_);
    }

    @Override
    public boolean textboxKeyTyped(char character, int p_146201_2_)
    {
        if (!this.isFocused())
        {
            return false;
        }
        else
        {
            switch (character)
            {
                case 1:
                    this.setCursorPositionEnd();
                    this.setSelectionPos(0);
                    return true;
                case 3:
                    GuiScreen.setClipboardString(this.getSelectedText());
                    return true;
                case 22:
                    if (this.isEnabled)
                    {
                        this.writeText(GuiScreen.getClipboardString());
                    }

                    return true;
                case 24:
                    GuiScreen.setClipboardString(this.getSelectedText());

                    if (this.isEnabled)
                    {
                        this.writeText("");
                    }

                    return true;
                default:
                    switch (p_146201_2_)
                    {
                        case 14:
                            if (GuiScreen.isCtrlKeyDown())
                            {
                                if (this.isEnabled)
                                {
                                    this.deleteWords(-1);
                                }
                            }
                            else if (this.isEnabled)
                            {
                                this.deleteFromCursor(-1);
                            }

                            return true;
                        case 199:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                this.setSelectionPos(0);
                            }
                            else
                            {
                                this.setCursorPositionZero();
                            }

                            return true;
                        case 203:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                if (GuiScreen.isCtrlKeyDown())
                                {
                                    this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                                }
                                else
                                {
                                    this.setSelectionPos(this.getSelectionEnd() - 1);
                                }
                            }
                            else if (GuiScreen.isCtrlKeyDown())
                            {
                                this.setCursorPosition(this.getNthWordFromCursor(-1));
                            }
                            else
                            {
                                this.moveCursorBy(-1);
                            }

                            return true;
                        case 205:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                if (GuiScreen.isCtrlKeyDown())
                                {
                                    this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                                }
                                else
                                {
                                    this.setSelectionPos(this.getSelectionEnd() + 1);
                                }
                            }
                            else if (GuiScreen.isCtrlKeyDown())
                            {
                                this.setCursorPosition(this.getNthWordFromCursor(1));
                            }
                            else
                            {
                                this.moveCursorBy(1);
                            }

                            return true;
                        case 207:
                            if (GuiScreen.isShiftKeyDown())
                            {
                                this.setSelectionPos(this.getText().length());
                            }
                            else
                            {
                                this.setCursorPositionEnd();
                            }

                            return true;
                        case 211:
                            if (GuiScreen.isCtrlKeyDown())
                            {
                                if (this.isEnabled)
                                {
                                    this.deleteWords(1);
                                }
                            }
                            else if (this.isEnabled)
                            {
                                this.deleteFromCursor(1);
                            }

                            return true;
                        default:
                            if (isAllowedCharacter(character))
                            {
                                if (this.isEnabled)
                                {
                                    this.writeText(Character.toString(character));
                                }

                                return true;
                            }
                            else
                            {
                                return false;
                            }
                    }
            }
        }
    }

    @Override
    public void writeText(String str)
    {
        String s1 = "";
        String s2 = filerAllowedCharacters(str);
        int i = Math.min(this.getCursorPosition(), this.getSelectionEnd());
        int j = Math.max(this.getCursorPosition(), this.getSelectionEnd());
        int k = this.getMaxStringLength() - this.getText().length() - (i - this.getSelectionEnd());
        boolean flag = false;

        if (this.getText().length() > 0)
        {
            s1 = s1 + this.getText().substring(0, i);
        }

        int l;

        if (k < s2.length())
        {
            s1 = s1 + s2.substring(0, k);
            l = k;
        }
        else
        {
            s1 = s1 + s2;
            l = s2.length();
        }

        if (this.getText().length() > 0 && j < this.getText().length())
        {
            s1 = s1 + this.getText().substring(j);
        }

        this.setText(s1);
        this.moveCursorBy(i - this.getSelectionEnd() + l);
    }

    public static String filerAllowedCharacters(String str)
    {
        StringBuilder stringbuilder = new StringBuilder();
        char[] achar = str.toCharArray();
        int i = achar.length;

        for (int j = 0; j < i; ++j)
        {
            char c0 = achar[j];

            if (isAllowedCharacter(c0))
            {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    public static boolean isAllowedCharacter(char character)
    {
        return  character >= 32 && character != 127;
    }
}
