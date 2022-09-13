package trollogyadherent.offlineauth.gui;

import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class ConfirmKeyOverwriteGUI extends DialogGui{
    ConfirmKeyOverwriteGUI(KeyManagementGUI prev) {
        super(prev);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawCenteredString(this.fontRendererObj, I18n.format("offlineauth.dialog.key_override"), this.width / 2, this.height/2 - 50,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, I18n.format("offlineauth.dialog.key_override_2"), this.width / 2, this.height/2 - 30,
                Color.WHITE.getRGB());

    }

    @Override
    protected void actionOnConfirm() throws NoSuchAlgorithmException, IOException {
        ((KeyManagementGUI)prev).proceedWithKeyGeneration();
    }
}
