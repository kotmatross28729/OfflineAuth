package trollogyadherent.offlineauth.gui;

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

        this.drawCenteredString(this.fontRendererObj, "Do you want to overwrite existing key files?", this.width / 2, this.height/2 - 50,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, "You might lose access to your account.", this.width / 2, this.height/2 - 30,
                Color.WHITE.getRGB());

    }

    @Override
    protected void actionOnConfirm() throws NoSuchAlgorithmException, IOException {
        ((KeyManagementGUI)prev).proceedWithKeyGeneration();
    }
}
