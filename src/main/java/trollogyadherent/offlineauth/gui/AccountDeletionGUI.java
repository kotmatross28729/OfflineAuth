package trollogyadherent.offlineauth.gui;

import java.awt.*;

public class AccountDeletionGUI extends DialogGui {
    AccountDeletionGUI(GuiLogin prev) {
        super(prev);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.fontRendererObj, "Do you really want to delete this account?", this.width / 2, this.height/2 - 50,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, "Account name: " + (char) 167 + "4" + ((GuiLogin)prev).identifier.getText(), this.width / 2, this.height/2 - 30,
                Color.WHITE.getRGB());

    }

    @Override
    protected void actionOnConfirm() {
        ((GuiLogin)prev).proceedWithAccountDeletion();
    }

}
