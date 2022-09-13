package trollogyadherent.offlineauth.gui;

import net.minecraft.client.resources.I18n;

import java.awt.*;

public class NameChangeGUI extends DialogGui{

    NameChangeGUI(GuiLogin prev) {
        super(prev);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.fontRendererObj, I18n.format("offlineauth.dialog.displayname_change"), this.width / 2, this.height/2 - 50,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, I18n.format("offlineauth.dialog.displayname_change_2"), this.width / 2, this.height/2 - 30,
                Color.WHITE.getRGB());

    }

    @Override
    protected void actionOnConfirm() {
        ((GuiLogin)prev).proceedWithDisplayNameChange();
    }
}
