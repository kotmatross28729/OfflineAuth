package trollogyadherent.offlineauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.clientdata.ClientData;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.util.RsaKeyUtil;
import trollogyadherent.offlineauth.util.Util;

import javax.swing.JFileChooser;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class KeyManagementGUI extends GuiScreen {
    private GuiButton generateNewKeyPair;
    private GuiButton browsePrivateKey;
    private GuiTextField privateKeyPath;
    private GuiTextField publicKeyPath;
    private GuiButton browsePublicKey;
    private GuiButton back;

    private GuiScreen prev;

    public String message = "";
    private String keyBaseLocation = null;

    private int basey;
    private int basex;

    KeyManagementGUI(GuiScreen prev) {
        this.mc = Minecraft.getMinecraft();
        this.fontRendererObj = mc.fontRenderer;
        this.prev = prev;
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        switch (b.id) {
            case 0:
                actionBrowseServer();
                break;
            case 1:
                actionBrowsePrivate();
                break;
            case 2:
                try {
                    actionRegenerate();
                } catch (NoSuchAlgorithmException | IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case 3:
                actionBack();
                break;
        }
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();

        this.drawString(this.fontRendererObj, I18n.format("offlineauth.keygui.public_key_path"), this.basex, this.basey, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, I18n.format("offlineauth.keygui.private_key_path"), this.basex, this.basey + 70, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, I18n.format("offlineauth.keygui.do_not_share"), this.basex, this.basey + 85, Color.RED.getRGB());

        this.privateKeyPath.drawTextBox();
        this.publicKeyPath.drawTextBox();

        if (!(this.message == null || this.message.isEmpty())) {
            this.drawCenteredString(this.fontRendererObj, this.message, this.width / 2, this.basey - 15, 0xFFFFFF);
        }

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.privateKeyPath.drawTextBox();
        this.publicKeyPath.drawTextBox();
    }

    @Override
    public void initGui() {
        super.initGui();

        this.basey = this.height / 2 - 180 / 2;
        this.basex = this.width / 2 - 180;//this.width / 2 - 155;

        this.publicKeyPath = new GuiTextField(this.fontRendererObj, this.basex, this.basey + 35, 350, 20);
        this.publicKeyPath.setMaxStringLength(2048);
        this.publicKeyPath.setText("");
        this.privateKeyPath = new GuiTextField(this.fontRendererObj, this.basex, this.basey + 125, 350, 20);
        this.privateKeyPath.setMaxStringLength(2048);
        this.privateKeyPath.setText("");


        this.browsePublicKey = new GuiButton(0, this.basex, this.basey + 10, 170, 20, I18n.format("offlineauth.keygui.browse_public_key"));
        this.buttonList.add(this.browsePublicKey);
        this.browsePrivateKey = new GuiButton(1, this.basex, this.basey + 100, 170, 20, I18n.format("offlineauth.keygui.browse_private_key"));
        this.buttonList.add(this.browsePrivateKey);
        this.generateNewKeyPair = new GuiButton(2, this.basex, this.basey + 160, 170, 20, I18n.format("offlineauth.keygui.generate_new_keypair"));
        this.buttonList.add(this.generateNewKeyPair);
        this.back = new GuiButton(3, this.basex + 300, this.basey + 180, 50, 20, I18n.format("offlineauth.keygui.back"));
        this.buttonList.add(this.back);

        OAServerData oasd = Util.getOAServerDataByIP(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), Util.getPort(OfflineAuth.varInstanceClient.selectedServerData));
        if (oasd != null) {
            this.privateKeyPath.setText(oasd.getPrivateKeyPath());
            this.publicKeyPath.setText(oasd.getPublicKeyPath());
        }
    }

    @Override
    protected void keyTyped(char c, int k) {
        super.keyTyped(c, k);

        if (k == Keyboard.KEY_ESCAPE) {
            actionBack();
            return;
        }

        if (k == Keyboard.KEY_TAB) {
            if (publicKeyPath.isFocused()) {
                publicKeyPath.setFocused(false);
                privateKeyPath.setFocused(true);
            } else  {
                publicKeyPath.setFocused(true);
                privateKeyPath.setFocused(false);
            }
        }

        this.privateKeyPath.textboxKeyTyped(c, k);
        this.publicKeyPath.textboxKeyTyped(c, k);
    }

    @Override
    protected void mouseClicked(int x, int y, int b) {
        super.mouseClicked(x, y, b);
        this.privateKeyPath.mouseClicked(x, y, b);
        this.publicKeyPath.mouseClicked(x, y, b);
    }

    private void saveData() {
        if (OfflineAuth.varInstanceClient.selectedServerData == null) {
            message = (char) 167 + "4Error saving data!";
            return;
        }

        /* Looping through cache list, if found, updating the entry */
        boolean found = false;
        for (OAServerData oasd : OfflineAuth.varInstanceClient.OAServerDataCache) {
            if (oasd.getIp().equals(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData)) && oasd.getPort().equals(Util.getPort(OfflineAuth.varInstanceClient.selectedServerData))) {
                found = true;
                oasd.setPrivateKeyPath(privateKeyPath.getText());
                oasd.setPublicKeyPath(publicKeyPath.getText());
                oasd.setUseKey(true);
            }
        }

        /* Actual part where the OfflineAuth.varInstanceClient.OAserverDataCache variable gets dumped into a json file */
        if (found) {
            ClientData.saveData();
        } else {
            message = (char) 167 + "4Error saving data!";
        }
    }

    private void actionBack() {
        this.mc.displayGuiScreen(prev);
    }

    private void actionBrowseServer() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select public key");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().getAbsolutePath().equals(privateKeyPath.getText())) {
                message = (char) 167 + "4Private and public keys should be different!";
            } else {
                this.publicKeyPath.setText(chooser.getSelectedFile().getAbsolutePath());
                this.publicKeyPath.setCursorPositionZero();
                this.message = (char) 167 + "aSaved data";
                saveData();
            }
        } else {
            /* System.out.println("No Selection "); */
        }
    }

    private void actionBrowsePrivate() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select private key");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().getAbsolutePath().equals(publicKeyPath.getText())) {
                message = (char) 167 + "4Private and public keys should be different!";
            } else {
                this.privateKeyPath.setText(chooser.getSelectedFile().getAbsolutePath());
                this.privateKeyPath.setCursorPositionZero();
                this.message = (char) 167 + "aSaved data";
                saveData();
            }
        } else {
            /* System.out.println("No Selection "); */
        }
    }

    private void actionRegenerate() throws NoSuchAlgorithmException, IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select folder where to generate keys");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            /* System.out.println("getCurrentDirectory(): "
                    +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    +  chooser.getSelectedFile()); */

            keyBaseLocation = chooser.getSelectedFile().getAbsolutePath();

            if (new File(chooser.getSelectedFile().getAbsolutePath() + File.separator + "private.key").exists() || new File(chooser.getSelectedFile().getAbsolutePath() + File.separator + "public.key").exists()) {
                Minecraft.getMinecraft().displayGuiScreen(new ConfirmKeyOverwriteGUI((KeyManagementGUI) Minecraft.getMinecraft().currentScreen));
            } else {
                proceedWithKeyGeneration();
            }
        } else {
            /* System.out.println("No Selection "); */
        }
    }

    void proceedWithKeyGeneration() throws NoSuchAlgorithmException, IOException {
        if (keyBaseLocation == null) {
            OfflineAuth.error("keyBaseLocation is null");
            return;
        }

        RsaKeyUtil.SaveKeyPair(keyBaseLocation, RsaKeyUtil.genKeyPair());
        this.privateKeyPath.setText(keyBaseLocation + File.separator + "private.key");
        this.privateKeyPath.setCursorPositionZero();
        this.publicKeyPath.setText(keyBaseLocation + File.separator + "public.key");
        this.publicKeyPath.setCursorPositionZero();
        this.message = (char) 167 + "aGenerated new keypair and saved data";
        saveData();
    }
}
