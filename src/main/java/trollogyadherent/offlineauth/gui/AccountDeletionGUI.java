package trollogyadherent.offlineauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.awt.*;

public class AccountDeletionGUI extends DialogGui {
    AccountDeletionGUI(GuiLogin prev) {
        super(prev);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String firstLine = I18n.format("offlineauth.dialog.account_deletion");
        String secondLine = I18n.format("offlineauth.dialog.account_deletion_2").replaceAll("#name#", ((GuiLogin)prev).identifier.getText());
        setDialogSize(Math.max(Minecraft.getMinecraft().fontRenderer.getStringWidth(firstLine), Minecraft.getMinecraft().fontRenderer.getStringWidth(secondLine)) + 40, 130);
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.fontRendererObj, firstLine, this.width / 2, this.height/2 - 50, Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, secondLine, this.width / 2, this.height/2 - 30, Color.WHITE.getRGB());

    }

    @Override
    protected void actionOnConfirm() {
        ((GuiLogin)prev).proceedWithAccountDeletion();
    }

}
