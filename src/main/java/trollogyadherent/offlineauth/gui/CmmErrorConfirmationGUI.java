package trollogyadherent.offlineauth.gui;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class CmmErrorConfirmationGUI extends ConfirmationGui {
    String translatedText;
    int color;

    public CmmErrorConfirmationGUI(GuiScreen prev, String text, String confirmationText, int color) {
        super(prev, confirmationText);
        this.translatedText = I18n.format(text);
        this.color = color;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        setDialogSize(Minecraft.getMinecraft().fontRenderer.getStringWidth(translatedText) + 40, 130);

        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.fontRendererObj, translatedText, this.width / 2, this.height/2 - 50, color);
    }

    @Override
    protected void actionOnConfirm() {
        actionCancel();
    }
}
