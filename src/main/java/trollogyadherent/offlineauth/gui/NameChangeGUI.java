package trollogyadherent.offlineauth.gui;

import java.awt.*;

public class NameChangeGUI extends DialogGui{

    NameChangeGUI(GuiLogin prev) {
        super(prev);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.fontRendererObj, "Do you really want to change your displayname?", this.width / 2, this.height/2 - 50,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, "You will lose your inventory and achievements.", this.width / 2, this.height/2 - 30,
                Color.WHITE.getRGB());

    }

    @Override
    protected void actionOnConfirm() {
        ((GuiLogin)prev).proceedWithDisplayNameChange();
    }
}
