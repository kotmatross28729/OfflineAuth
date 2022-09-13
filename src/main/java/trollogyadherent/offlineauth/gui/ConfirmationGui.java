package trollogyadherent.offlineauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

abstract public class ConfirmationGui extends DialogGui{
    String confirmationText;
    ConfirmationGui(GuiScreen prev, String confirmationText) {
        super(prev);
        this.confirmationText = confirmationText;
    }

    @Override
    public void initGui() {
        String translatedText = I18n.format(confirmationText);
        int buttonWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(translatedText) + 12;

        int buttonsHeight = this.height / 2 + 15;

        this.yes = new GuiButton(0, this.width / 2 - buttonWidth / 2, buttonsHeight, buttonWidth, 20, translatedText);

        this.buttonList.add(this.yes);
    }
}
