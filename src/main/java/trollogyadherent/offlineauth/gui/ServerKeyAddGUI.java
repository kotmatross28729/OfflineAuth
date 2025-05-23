package trollogyadherent.offlineauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.util.RsaKeyUtil;

import java.awt.Color;
import java.io.IOException;
import java.security.PublicKey;

public class ServerKeyAddGUI extends DialogGui {
    String ip;
    String port;
    PublicKey pubKey;

    public ServerKeyAddGUI(GuiScreen prev, String ip, String port, PublicKey pubKey) {
        super(prev);
        this.ip = ip;
        this.port = port;
        this.pubKey = pubKey;

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        String firstLine = I18n.format("offlineauth.dialog.fingerprint").replaceAll("#ip#", ip).replaceAll("#port#", port);
        String secondLine = (char) 167 + "d" + RsaKeyUtil.getKeyFingerprint(pubKey);
        setDialogSize(Math.max(Minecraft.getMinecraft().fontRenderer.getStringWidth(firstLine), Minecraft.getMinecraft().fontRenderer.getStringWidth(secondLine)) + 40, 130);
        super.drawScreen(mouseX, mouseY, partialTicks);

        //this.drawCenteredString(this.fontRendererObj, "Is this fingerprint correct? (" + ip + ":" + port + ")", this.width / 2, this.height/2 - 50, Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, firstLine, this.width / 2, this.height/2 - 50, Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, secondLine, this.width / 2, this.height/2 - 30, Color.WHITE.getRGB());
    }

    @Override
    protected void actionOnConfirm() throws IOException {
        ClientUtil.SaveServerPublicKeyToCache(pubKey, ip, port);
        OfflineAuth.varInstanceClient.selectedServerIndex = -1;
        OfflineAuth.varInstanceClient.checkingForKey = false;
        if (prev instanceof GuiLogin) {
            OfflineAuth.varInstanceClient.prevWasKeyDialog = true;
        }
    }

    @Override
    protected void actionCancel() {
        OfflineAuth.varInstanceClient.checkingForKey = false;
        if (prev instanceof GuiLogin) {
            OfflineAuth.varInstanceClient.prevWasKeyDialog = true;
        }
        super.actionCancel();
    }
}
