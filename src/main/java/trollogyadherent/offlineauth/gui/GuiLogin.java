package trollogyadherent.offlineauth.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.clientdata.ClientData;
import trollogyadherent.offlineauth.gui.skin.SkinManagmentGUI;
import trollogyadherent.offlineauth.request.Request;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.rest.ResponseObject;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.util.RsaKeyUtil;
import trollogyadherent.offlineauth.util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.awt.Color;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@SideOnly(Side.CLIENT)
public class GuiLogin extends GuiScreen {

    public GuiTextField identifier;
    private GuiPasswordField pw;
    private TogglePWButton togglePWButton;
    private GuiButton login;
    private GuiButton cancel;
    private GuiButton offline;
    private GuiButton config;

    private GuiTextField token;
    private GuiPasswordField newPW;
    private GuiTextField port;
    private GuiButton changePW;
    private GuiButton changeDisplayName;
    private GuiButton uploadSkin;
    private GuiButton deleteAccount;
    private GuiButton save;
    private GuiCheckBox useKey;
    private GuiButton manageKey;
    private GuiTextFieldEnabledSectionSign displayname;

    private GuiScreen prev;

    private int basey;

    public String message = "";

    private Object[] textFieldTabArray = new Object[6];

    public GuiLogin(GuiScreen prev) {
        this.mc = Minecraft.getMinecraft();
        this.fontRendererObj = mc.fontRenderer;
        this.prev = prev;
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        switch (b.id) {
            case 0:
                actionRegister();
                break;
            case 1:
                actionCheckRegistration();
                break;
            case 2:
                actionDelete();
                break;
            case 3:
                actionChangePW();
                break;
            case 31:
                actionChangeDisplayName();
                break;
            case 4:
                actionUploadSkin();
                break;
            case 5:
                actionSave(false, true);
                break;
            case 6:
                actionCancel();
                break;
            case 7:
                actionConfig();
                break;
            case 9:
                actionManageKey();
                break;
            case 10:
                actionTogglePWvisibility();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!Config.showUseKey) {
            this.useKey.visible = false;
            this.manageKey.visible = false;
        }

        if (!Config.showConfigInAuth) {
            this.config.visible = false;
        }

        this.drawDefaultBackground();

        this.drawString(this.fontRendererObj, I18n.format("offlineauth.guilogin.identifier"), this.width / 2 - 155, this.basey, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, I18n.format("offlineauth.guilogin.displayname"), this.width / 2 - 155, this.basey + 45, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, I18n.format("offlineauth.guilogin.port"), this.width / 2 - 55, this.basey + 45, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, I18n.format("offlineauth.guilogin.password"), this.width / 2 - 55, this.basey, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, I18n.format("offlineauth.guilogin.new_password"), this.width / 2 + 55, this.basey, Color.WHITE.getRGB());
        if (Config.showUseKey) {
            this.drawString(this.fontRendererObj, I18n.format("offlineauth.guilogin.use_key"), this.width / 2 - 155, this.basey + 90, Color.WHITE.getRGB());
        }
        this.drawString(this.fontRendererObj, I18n.format("offlineauth.guilogin.registration_token"), this.width / 2 + 5, this.basey + 45, Color.WHITE.getRGB());
        if (!(this.message == null || this.message.isEmpty())) {
            this.drawCenteredString(this.fontRendererObj, this.message, this.width / 2, this.basey - 15, 0xFFFFFF);
        }
        this.identifier.drawTextBox();
        this.displayname.drawTextBox();
        this.pw.drawTextBox();

        this.token.drawTextBox();
        this.newPW.drawTextBox();
        this.port.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.identifier.drawTextBox();
        this.displayname.drawTextBox();
        this.pw.drawTextBox();
        this.token.drawTextBox();
        this.newPW.drawTextBox();
        this.port.drawTextBox();

        if (this.useKey.isChecked()) {
            //this.identifier.setEnabled(false);
            //this.identifier.setFocused(false);
            //this.displayname.setEnabled(false);
            //this.displayname.setFocused(false);
            this.pw.setEnabled(false);
            this.pw.setFocused(false);
            this.newPW.setEnabled(false);
            this.newPW.setFocused(false);
            this.togglePWButton.enabled = false;

            this.manageKey.enabled = true;
            //this.privateKeyPath.setEnabled(true);
        } else {
            this.identifier.setEnabled(true);
            this.displayname.setEnabled(true);
            this.pw.setEnabled(true);
            this.newPW.setEnabled(true);
            this.togglePWButton.enabled = true;

            this.manageKey.enabled = false;
            //this.privateKeyPath.setEnabled(false);
            //this.privateKeyPath.setFocused(false);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        this.basey = this.height / 2 - 200 / 2;//this.height / 2 - 110 / 2;

        this.identifier = new GuiTextField(this.fontRendererObj, this.width / 2 - 155, this.basey + 15, 90, 20);
        this.identifier.setMaxStringLength(512);
        //this.username.setFocused(true);

        this.displayname = new GuiTextFieldEnabledSectionSign(this.fontRendererObj, this.width / 2 - 155, this.basey + 60, 90, 20);
        this.displayname.setMaxStringLength(512);

        this.pw = new GuiPasswordField(this.fontRendererObj, this.width / 2 - 55, this.basey + 15, 100, 20);
        this.pw.setMaxStringLength(512);

        this.newPW = new GuiPasswordField(this.fontRendererObj, this.width / 2 + 55, this.basey + 15, 100, 20);
        this.newPW.setMaxStringLength(512);
        this.newPW.setText("");

        this.port = new GuiTextField(this.fontRendererObj,this.width / 2 - 55, this.basey + 60, 50, 20);
        this.port.setText(String.valueOf(Config.port));
        this.port.setMaxStringLength(512);

        this.token = new GuiTextField(this.fontRendererObj,this.width / 2 + 5, this.basey + 60, 150, 20);
        this.token.setText("");
        this.token.setMaxStringLength(512);


        this.login = new GuiButton(0, this.width / 2 - 155, this.basey + 135, 100, 20, I18n.format("offlineauth.guilogin.btn.register"));
        this.offline = new GuiButton(1, this.width / 2 - 50, this.basey + 135, 100, 20, I18n.format("offlineauth.guilogin.btn.check_registration"));
        this.deleteAccount = new GuiButton(2, this.width / 2 + 55, this.basey + 135, 100, 20, I18n.format("offlineauth.guilogin.btn.delete_account"));
        this.changePW = new GuiButton(3, this.width / 2 - 155, this.basey + 165, 100, 20, I18n.format("offlineauth.guilogin.btn.change_password"));
        this.changeDisplayName = new GuiButton(31, this.width / 2 - 50, this.basey + 165, 100, 20, I18n.format("offlineauth.guilogin.btn.change_name"));
        this.uploadSkin = new GuiButton(4, this.width / 2 + 55, this.basey + 165, 100, 20, I18n.format("offlineauth.guilogin.btn.upload_skin"));
        this.togglePWButton = new TogglePWButton(10, this.pw.xPosition + this.pw.width - 18, this.pw.yPosition + 2, 16, 16);
        this.buttonList.add(this.login);
        this.buttonList.add(this.offline);
        this.buttonList.add(this.changePW);
        this.buttonList.add(this.changeDisplayName);
        this.buttonList.add(this.uploadSkin);
        this.buttonList.add(this.deleteAccount);
        this.buttonList.add(this.togglePWButton);

        this.save = new GuiButton(5, this.width - 240, this.height - 23, 75, 20, I18n.format("offlineauth.guilogin.btn.save"));
        this.cancel = new GuiButton(6, this.width - 160, this.height - 23, 75, 20, I18n.format("offlineauth.guilogin.btn.cancel"));
        this.config = new GuiButton(7, this.width - 80, this.height - 23, 75, 20, I18n.format("offlineauth.guilogin.btn.config"));
        this.buttonList.add(this.config);
        this.buttonList.add(this.cancel);
        this.buttonList.add(this.save);

        this.useKey = new GuiCheckBox(8, this.width / 2 - 145, this.basey + 105, "", false);
        this.manageKey = new GuiButton(9, this.width / 2 - 110, this.basey + 105, 50, 20, I18n.format("offlineauth.guilogin.btn.manage"));
        //this.browsePrivateKey = new GuiButton(10, this.width / 2 - 55, this.basey + 60, 50, 20, "Browse");
        this.buttonList.add(this.useKey);
        this.buttonList.add(this.manageKey);
        //this.buttonList.add(this.browsePrivateKey);

        OAServerData oasd = Util.getOAServerDatabyIP(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), Util.getPort(OfflineAuth.varInstanceClient.selectedServerData));
        if (oasd != null) {
            if (oasd.getIdentifier() != null) {
                this.identifier.setText(oasd.getIdentifier());
            }
            if (oasd.getDisplayName() != null) {
                this.displayname.setText(oasd.getDisplayName());
            }
            if (oasd.getPassword() != null) {
                this.pw.setText(oasd.getPassword());
            }
            if (oasd.getRestPort() != null) {
                this.port.setText(oasd.getRestPort());
            }
            //if (oasd.getUseKey() != null) {
                this.useKey.setIsChecked(oasd.isUsingKey());
            //}
        }

        textFieldTabArray = new Object[]{identifier, pw, newPW, displayname, port, token};

        if (!OfflineAuth.varInstanceClient.prevWasKeyDialog) {
            checkForKey();
        }
        OfflineAuth.varInstanceClient.prevWasKeyDialog = false;
    }

    boolean isAnyTextFieldFocused() {
        for (int i = 0; i < textFieldTabArray.length; i ++) {
            if (textFieldTabArray[i] instanceof GuiTextField) {
                GuiTextField textField = (GuiTextField) textFieldTabArray[i];
                if (textField.isFocused()) {
                    return true;
                }
            } else {
                GuiPasswordField pwField = (GuiPasswordField) textFieldTabArray[i];
                if (pwField.isFocused()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void keyTyped(char c, int k) {
        super.keyTyped(c, k);

        if (k == Keyboard.KEY_ESCAPE) {
            actionCancel();
            return;
        }

        this.identifier.textboxKeyTyped(c, k);
        this.displayname.textboxKeyTyped(c, k);
        this.pw.textboxKeyTyped(c, k);
        this.token.textboxKeyTyped(c, k);
        this.newPW.textboxKeyTyped(c, k);
        this.port.textboxKeyTyped(c, k);

        /* Cycling focus through all text fields with the tab key. */
        if (k == Keyboard.KEY_TAB) {
            if (!isAnyTextFieldFocused()) {
                identifier.setFocused(true);
            } else {

                boolean previousWasFocused = false;

                for (int i = 0; i < textFieldTabArray.length; i++) {
                    if (textFieldTabArray[i] instanceof GuiTextField) {
                        GuiTextField textField = (GuiTextField) textFieldTabArray[i];
                        if (textField.isFocused()) {
                            textField.setFocused(false);
                            if (i == textFieldTabArray.length - 1) {
                                identifier.setFocused(true);
                                continue;
                            }
                            previousWasFocused = true;
                        } else if (previousWasFocused && textField.isEnabled) {
                            textField.setFocused(true);
                            previousWasFocused = false;
                        }
                    } else {
                        GuiPasswordField pwField = (GuiPasswordField) textFieldTabArray[i];
                        if (pwField.isFocused()) {
                            pwField.setFocused(false);
                            if (i == textFieldTabArray.length - 1) {
                                identifier.setFocused(true);
                                continue;
                            }
                            previousWasFocused = true;
                        } else if (previousWasFocused && pwField.isEnabled) {
                            pwField.setFocused(true);
                            previousWasFocused = false;
                        }
                    }
                }
            }

        } /*else if (k == Keyboard.KEY_RETURN) {
            if (this.identifier.isFocused()) {
                this.identifier.setFocused(false);
                this.pw.setFocused(true);
            } else if (this.pw.isFocused()) {
                this.actionPerformed(this.login);
            }
        } */
        if (port.isFocused()) {
            checkForKey();
        }
    }

    void checkForKey() {
        if (OfflineAuth.varInstanceClient.checkingForKey) {
            return;
        }
        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                OfflineAuth.varInstanceClient.checkingForKey = true;
                String ip = Util.getIP(OfflineAuth.varInstanceClient.selectedServerData);
                try {
                    if (ClientUtil.getServerPublicKeyFromCache(ip, port.getText()) == null) {
                        PublicKey pubKey = Request.getServerPubKey(ip, port.getText());
                        if (pubKey == null) {
                            OfflineAuth.varInstanceClient.checkingForKey = false;
                            return;
                        }
                        Minecraft.getMinecraft().displayGuiScreen(new ServerKeyAddGUI(Minecraft.getMinecraft().currentScreen, ip, port.getText(), pubKey));
                    }
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                    OfflineAuth.error("Failed to read public key for " + OfflineAuth.varInstanceClient.selectedServerData.serverIP);
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    @Override
    protected void mouseClicked(int x, int y, int b) {
        super.mouseClicked(x, y, b);
        this.identifier.mouseClicked(x, y, b);
        this.displayname.mouseClicked(x, y, b);
        this.pw.mouseClicked(x, y, b);
        this.token.mouseClicked(x, y, b);
        this.newPW.mouseClicked(x, y, b);
        this.port.mouseClicked(x, y, b);
    }


    /* Goes to previous GUI (prev variable) */
    private void actionCancel() {
        this.mc.displayGuiScreen(prev);
    }


    /* Saves data about the server, identified by ip and port, to a json file */
    private void actionSave(boolean saveDisplayName, boolean quitGui) {

        /* Trying to get privateKeyPath and publicServerKeyPath from previously saved data, since they can't be taken from this gui screen */
        OAServerData oaServerDataSaved = null;
        String privateKeyPathSaved = ""; //setting default blank value
        String publicServerKeyPathSaved = ""; //setting default blank value
        for (OAServerData oasd : OfflineAuth.varInstanceClient.OAserverDataCache) {
            if (oasd.getIp().equals(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData)) && oasd.getPort().equals(Util.getPort(OfflineAuth.varInstanceClient.selectedServerData))) {
                oaServerDataSaved = oasd;
                break;
            }
        }
        if (oaServerDataSaved != null) {
            if (oaServerDataSaved.getPrivateKeyPath() != null) {
                privateKeyPathSaved = oaServerDataSaved.getPrivateKeyPath();
            }
            if (oaServerDataSaved.getPublicKeyPath() != null) {
                publicServerKeyPathSaved = oaServerDataSaved.getPublicKeyPath();
            }
        }

        /* Constructing a temporary OAServerData object, used to update the List of server datas held by the client */
        OAServerData oaServerDataTemp = new OAServerData(false, Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), Util.getPort(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), identifier.getText(), displayname.getText(), pw.getPW(), useKey.isChecked(), privateKeyPathSaved, publicServerKeyPathSaved, false, false, false);
        boolean found = false;

        /* Looping through cache list, if found, updating the entry */
        for (OAServerData oasd : OfflineAuth.varInstanceClient.OAserverDataCache) {
            if (oasd == null) {
                OfflineAuth.warn("(actionSave): oasd null");
                continue;
            }
            if (oasd.getIp().equals(oaServerDataTemp.getIp()) && oasd.getPort().equals(oaServerDataTemp.getPort())) {
                found = true;
                oasd.setIdentifier(oaServerDataTemp.getIdentifier());
                oasd.setPassword(oaServerDataTemp.getPassword());
                oasd.setRestPort(oaServerDataTemp.getRestPort());
                oasd.setUseKey(oaServerDataTemp.isUsingKey());
                oasd.setPrivateKeyPath(oaServerDataTemp.getPrivateKeyPath());
                oasd.setPublicKeyPath(oaServerDataTemp.getPublicKeyPath());
                //if (saveDisplayName) {
                    oasd.setDisplayName(oaServerDataTemp.getDisplayName());
                //}
            }
        }

        /* In case this server does not exist in the cache list, we add a new entry to it */
        if (!found) {
            OfflineAuth.varInstanceClient.OAserverDataCache.add(oaServerDataTemp);
        }

        /* Actual part where the OfflineAuth.varInstanceClient.OAserverDataCache variable gets dumped into a json file */
        ClientData.saveData();

        /* debug prints */
        //System.out.println(OfflineAuth.varInstanceClient.OAserverDataCache);
        //System.out.println(OfflineAuth.varInstanceClient.OAserverDataCache.size());

        /* According to config, either we go to parent GUI, or we stay in this GUI */
        if (quitGui && Config.savebuttonExit) {
            actionCancel();
        }
    }

    private void actionConfig() {
        this.mc.displayGuiScreen(new ConfigGUI(this));
    }

    private void actionRegister() {
        actionSave(true, false);
        if (!Util.validUsername(displayname.getText())) {
            message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.invalid_displayname");
            return;
        }
        message = Util.colorCode(Util.Color.GREY) + I18n.format("offlineauth.guilogin.status.registering");
        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    String clientPubKey = "";
                    OAServerData oasd = Util.getCurrentOAServerData();
                    if (oasd != null && Util.getCurrentOAServerData().isUsingKey()) {
                        clientPubKey = Base64.getEncoder().encodeToString(RsaKeyUtil.loadPublicKey(Util.getCurrentOAServerData().getPublicKeyPath()).getEncoded());
                    }
                    String uuid = "";
                    StatusResponseObject stat = Request.register(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), identifier.getText(), displayname.getText(), pw.getPW(), uuid, token.getText(), clientPubKey);
                    if (stat.getStatusCode() == 200) {
                        message = Util.colorCode(Util.Color.GREEN) + I18n.format(stat.getStatus());
                    } else {
                        message = Util.colorCode(Util.Color.RED) + I18n.format(stat.getStatus());
                    }
                } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                         InvalidKeyException | InvalidAlgorithmParameterException | IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                    message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.error_registering");
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    public void actionDelete() {
        actionSave(true, false);
        Minecraft.getMinecraft().displayGuiScreen(new AccountDeletionGUI((GuiLogin) Minecraft.getMinecraft().currentScreen));
    }

    public void proceedWithAccountDeletion() {
        message = Util.colorCode(Util.Color.GREY) + I18n.format("offlineauth.guilogin.status.deleting");

        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    OAServerData oasd = Util.getCurrentOAServerData();
                    PublicKey clientPubKey = null;
                    PrivateKey clientPrivKey = null;
                    if (oasd != null && oasd.isUsingKey()) {
                        clientPubKey = RsaKeyUtil.loadPublicKey(oasd.getPublicKeyPath());
                        clientPrivKey = RsaKeyUtil.loadPrivateKey(oasd.getPrivateKeyPath());
                    }

                    StatusResponseObject stat = Request.delete(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), identifier.getText(), pw.getPW(), clientPubKey, clientPrivKey);
                    if (stat.getStatusCode() == 200) {
                        message = Util.colorCode(Util.Color.GREEN) + I18n.format(stat.getStatus());
                    } else {
                        message = Util.colorCode(Util.Color.RED) + I18n.format(stat.getStatus());
                    }
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                         NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException |
                         InvalidKeyException | IOException e) {
                    message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.error_deleting");
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    private void actionChangePW() {
        message = Util.colorCode(Util.Color.GREY) + I18n.format("offlineauth.guilogin.status.changing_password");
        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    StatusResponseObject stat = Request.changePW(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), identifier.getText(), pw.getPW(), newPW.getPW());
                    if (stat.getStatusCode() == 200) {
                        message = Util.colorCode(Util.Color.GREEN) + I18n.format(stat.getStatus());
                        pw.setText(newPW.getPW());
                        newPW.setText("");
                        actionSave(false, false);
                    } else {
                        message = Util.colorCode(Util.Color.RED) + I18n.format(stat.getStatus());
                    }
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                         NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException |
                         InvalidKeyException | IOException e) {
                    message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.error_changing_password");
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    private void actionCheckRegistration() {
        message = Util.colorCode(Util.Color.GREY) + I18n.format("offlineauth.guilogin.status.checking_registration");
        actionSave(true, false);
        Thread vibeCheckThread = new Thread(new Runnable() {
            public void run() {
                ResponseObject stat = null;
                try {
                    OAServerData oasd = Util.getCurrentOAServerData();
                    PublicKey clientPubKey = null;
                    PrivateKey clientPrivKey = null;
                    if (oasd != null && oasd.isUsingKey()) {
                        clientPubKey = RsaKeyUtil.loadPublicKey(oasd.getPublicKeyPath());
                        clientPrivKey = RsaKeyUtil.loadPrivateKey(oasd.getPrivateKeyPath());
                    }
                    stat = Request.vibeCheck(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), identifier.getText(), displayname.getText(), pw.getPW(), clientPubKey, clientPrivKey);
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                         InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchPaddingException |
                         BadPaddingException | InvalidKeyException | NoSuchProviderException | URISyntaxException e) {
                    message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.error_changing_registration");
                    e.printStackTrace();
                }
                if (stat != null && stat.getStatusCode() == 200) {
                    if (!stat.getDisplayName().equals("-")) {
                        message = Util.colorCode(Util.Color.GREEN) + I18n.format("offlineauth.guilogin.status.registered");
                    } else {
                        message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.guilogin.status.not_registered");
                    }
                } else {
                    message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.error_changing_registration");
                }
            }
        });
        vibeCheckThread.start();
    }

    private void actionManageKey() {
        actionSave(true, false);
        Minecraft.getMinecraft().displayGuiScreen(new KeyManagementGUI(Minecraft.getMinecraft().currentScreen));
    }

    private void actionChangeDisplayName() {
        actionSave(true, false);
        message = Util.colorCode(Util.Color.GREY) + I18n.format("offlineauth.guilogin.status.checking_name_change_allowed");
        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    OAServerData oasd = Util.getCurrentOAServerData();
                    PublicKey clientPubKey = null;
                    PrivateKey clientPrivKey = null;
                    if (oasd != null && oasd.isUsingKey()) {
                        clientPubKey = RsaKeyUtil.loadPublicKey(oasd.getPublicKeyPath());
                        clientPrivKey = RsaKeyUtil.loadPrivateKey(oasd.getPrivateKeyPath());
                    }
                    ResponseObject ro = Request.vibeCheck(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), identifier.getText(), displayname.getText(), pw.getPW(), clientPubKey, clientPrivKey);
                    if (ro.getStatusCode() == 200 && ro.isDisplayNameChangeAllowed()) {
                        message = "";
                        Minecraft.getMinecraft().displayGuiScreen(new NameChangeGUI((GuiLogin) Minecraft.getMinecraft().currentScreen));
                    } else if (ro.getStatusCode() == 200 && !ro.isDisplayNameChangeAllowed()){
                        message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.guilogin.status.name_change_disallowed");
                    } else {
                        message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.error_talking_server");
                    }
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException  |
                         InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchPaddingException |
                         BadPaddingException | InvalidKeyException | NoSuchProviderException | URISyntaxException  e) {
                    OfflineAuth.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    public void proceedWithDisplayNameChange() {
        message = Util.colorCode(Util.Color.GREY) + I18n.format("offlineauth.guilogin.status.changing_displayname");
        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    OAServerData oasd = Util.getCurrentOAServerData();
                    PublicKey clientPubKey = null;
                    PrivateKey clientPrivKey = null;
                    if (oasd != null && oasd.isUsingKey()) {
                        clientPubKey = RsaKeyUtil.loadPublicKey(oasd.getPublicKeyPath());
                        clientPrivKey = RsaKeyUtil.loadPrivateKey(oasd.getPrivateKeyPath());
                    }
                    StatusResponseObject stat = Request.changeDisplayName(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), identifier.getText(), pw.getPW(), displayname.getText(), clientPubKey, clientPrivKey);
                    if (stat.getStatusCode() == 200) {
                        message = Util.colorCode(Util.Color.GREEN) + I18n.format(stat.getStatus());
                        actionSave(true, false);
                    } else {
                        message = Util.colorCode(Util.Color.RED) + I18n.format(stat.getStatus());
                    }
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                    message = Util.colorCode(Util.Color.RED) + I18n.format("offlineauth.error_changing_displayname");
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    public void actionTogglePWvisibility() {
        this.togglePWButton.setVisible(!this.togglePWButton.isVisible());
        this.pw.setPwVisible(!this.pw.isPwVisible());
        this.pw.setFocused(false);
    }

    public void actionUploadSkin() {
        actionSave(true, false);
        Minecraft.getMinecraft().displayGuiScreen(new SkinManagmentGUI(Minecraft.getMinecraft().currentScreen));
    }
}