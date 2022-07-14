package trollogyadherent.offlineauth.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.clientdata.ClientData;
import trollogyadherent.offlineauth.request.Request;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.rest.ResponseObject;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
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
    private GuiTextField displayname;

    private GuiScreen prev;
    public GuiScreen prev_ = prev;

    private int basey;

    public String message = "";

    GuiLogin(GuiScreen prev) {
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
                System.out.println(b.id);
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
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();

        this.drawString(this.fontRendererObj, "Identifier:", this.width / 2 - 155, this.basey, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, "Display Name:", this.width / 2 - 155, this.basey + 45, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, "Port:", this.width / 2 - 55, this.basey + 45, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, "Password:", this.width / 2 - 55, this.basey, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, "New Password:", this.width / 2 + 55, this.basey, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, "Use Key:", this.width / 2 - 155,  this.basey + 90, Color.WHITE.getRGB());
        this.drawString(this.fontRendererObj, "Registration Token:", this.width / 2 + 5, this.basey + 45, Color.WHITE.getRGB());
        if (!(this.message == null || this.message.isEmpty())) {
            this.drawCenteredString(this.fontRendererObj, this.message, this.width / 2, this.basey - 15, 0xFFFFFF);
        }
        this.identifier.drawTextBox();
        this.displayname.drawTextBox();
        this.pw.drawTextBox();

        this.token.drawTextBox();
        this.newPW.drawTextBox();
        this.port.drawTextBox();

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
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

            this.manageKey.enabled = true;
            //this.privateKeyPath.setEnabled(true);
        } else {
            this.identifier.setEnabled(true);
            this.displayname.setEnabled(true);
            this.pw.setEnabled(true);
            this.newPW.setEnabled(true);

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

        this.displayname = new GuiTextField(this.fontRendererObj, this.width / 2 - 155, this.basey + 60, 90, 20);
        this.displayname.setMaxStringLength(512);

        this.pw = new GuiPasswordField(this.fontRendererObj, this.width / 2 - 55, this.basey + 15, 100, 20);
        this.pw.setMaxStringLength(512);

        this.newPW = new GuiPasswordField(this.fontRendererObj, this.width / 2 + 55, this.basey + 15, 100, 20);
        this.newPW.setMaxStringLength(512);
        this.newPW.setText("");

        this.port = new GuiTextField(this.fontRendererObj,this.width / 2 - 55, this.basey + 60, 50, 20);
        this.port.setText("4567");
        this.port.setMaxStringLength(512);

        this.token = new GuiTextField(this.fontRendererObj,this.width / 2 + 5, this.basey + 60, 150, 20);
        this.token.setText("");
        this.token.setMaxStringLength(512);


        this.login = new GuiButton(0, this.width / 2 - 155, this.basey + 135, 100, 20, "Register");
        this.offline = new GuiButton(1, this.width / 2 - 50, this.basey + 135, 100, 20, "Check Registration");
        this.deleteAccount = new GuiButton(2, this.width / 2 + 55, this.basey + 135, 100, 20, "Delete Account");
        this.changePW = new GuiButton(3, this.width / 2 - 155, this.basey + 165, 100, 20, "Change Password");
        this.changeDisplayName = new GuiButton(31, this.width / 2 - 50, this.basey + 165, 100, 20, "Change Displayn.");
        this.uploadSkin = new GuiButton(4, this.width / 2 + 55, this.basey + 165, 100, 20, "Upload Skin");
        this.togglePWButton = new TogglePWButton(10, this.pw.xPosition + this.pw.width - 18, this.pw.yPosition + 2, 16, 16);
        this.buttonList.add(this.login);
        this.buttonList.add(this.offline);
        this.buttonList.add(this.changePW);
        this.buttonList.add(this.changeDisplayName);
        this.buttonList.add(this.uploadSkin);
        this.buttonList.add(this.deleteAccount);
        this.buttonList.add(this.togglePWButton);

        this.save = new GuiButton(5, this.width - 240, this.height - 23, 75, 20, "Save");
        this.cancel = new GuiButton(6, this.width - 160, this.height - 23, 75, 20, "Cancel");
        this.config = new GuiButton(7, this.width - 80, this.height - 23, 75, 20, "Config");
        this.buttonList.add(this.config);
        this.buttonList.add(this.cancel);
        this.buttonList.add(this.save);

        this.useKey = new GuiCheckBox(8, this.width / 2 - 145, this.basey + 105, "", false);
        this.manageKey = new GuiButton(9, this.width / 2 - 110, this.basey + 105, 50, 20, "Manage");
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

        /* TODO: Handle tab properly (cycling through buttons would be great too) */
        if (k == Keyboard.KEY_TAB) {
            this.identifier.setFocused(!this.identifier.isFocused());
            this.pw.setFocused(!this.pw.isFocused());
            this.token.setFocused(!this.token.isFocused());

        /* TODO: Handle enter properly */
        } else if (k == Keyboard.KEY_RETURN) {
            if (this.identifier.isFocused()) {
                this.identifier.setFocused(false);
                this.pw.setFocused(true);
            } else if (this.pw.isFocused()) {
                this.actionPerformed(this.login);
            }
        }
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
        System.out.println(OfflineAuth.varInstanceClient.OAserverDataCache);
        System.out.println(OfflineAuth.varInstanceClient.OAserverDataCache.size());

        /* According to config, either we go to parent GUI, or we stay in this GUI */
        if (quitGui && Config.savebuttonExit) {
            this.mc.displayGuiScreen(prev);
        }
    }

    private void actionConfig() {
        this.mc.displayGuiScreen(new ConfigGUI(this));
    }

    private void actionRegister() {
        if (!Util.validUsername(displayname.getText())) {
            message = (char) 167 + "4Invalid Display Name. Must be alphanumeric and from 3 to 16 chars.";
            return;
        }
        message = (char) 167 + "7Registering...";
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
                        message = (char) 167 + "a" + stat.getStatus();
                    } else {
                        message = (char) 167 + "4" + stat.getStatus();
                    }
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                    message = (char) 167 + "4Error while registering account (IOException)";
                    e.printStackTrace();
                } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                         InvalidKeyException | InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
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
        message = (char) 167 + "7Deleting account...";

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
                        message = (char) 167 + "a" + stat.getStatus();
                    } else {
                        message = (char) 167 + "4" + stat.getStatus();
                    }
                } catch (IOException e) {
                    message = (char) 167 + "4Error while deleting account (IOException)";
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                         NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException |
                         InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        registerThread.start();
    }

    private void actionChangePW() {
        message = (char) 167 + "7Changing account password...";
        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    StatusResponseObject stat = Request.changePW(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), identifier.getText(), pw.getPW(), newPW.getPW());
                    if (stat.getStatusCode() == 200) {
                        message = (char) 167 + "a" + stat.getStatus();
                        pw.setText(newPW.getPW());
                        newPW.setText("");
                        actionSave(false, false);
                    } else {
                        message = (char) 167 + "4" + stat.getStatus();
                    }
                } catch (IOException e) {
                    message = (char) 167 + "4Error while changing account password (IOException)";
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                         NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException |
                         InvalidKeyException e) {
                    //throw new RuntimeException(e);
                    message = (char) 167 + "4Error while changing account password";
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    private void actionCheckRegistration() {
        message = (char) 167 + "7Checking registration...";
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
                } catch (URISyntaxException e) {
                    message = (char) 167 + "4Error while checking registration";
                    OfflineAuth.error(e.getMessage());
                    //e.printStackTrace();
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                         InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchPaddingException |
                         BadPaddingException | InvalidKeyException | NoSuchProviderException e) {
                    OfflineAuth.error(e.getMessage());
                    //throw new RuntimeException(e);
                }
                if (stat != null && stat.getStatusCode() == 200) {
                    if (!stat.getDisplayName().equals("-")) {
                        message = (char) 167 + "aRegistered and password valid!";
                    } else {
                        message = (char) 167 + "4User not registered or password invalid!";
                    }
                } else {
                    message = (char) 167 + "4Error while checking registration";
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
        message = (char) 167 + "7Checking if server allows name change...";
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
                        message = (char) 167 + "4Server does not allow displayname change";
                    } else {
                        message = (char) 167 + "4Error while talking with the server";
                    }
                } catch (URISyntaxException | IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                         InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchPaddingException |
                         BadPaddingException | InvalidKeyException | NoSuchProviderException e) {
                    OfflineAuth.error(e.getMessage());
                    //throw new RuntimeException(e);
                }
            }
        });
        registerThread.start();
    }

    public void proceedWithDisplayNameChange() {
        message = (char) 167 + "7Changing displayname...";
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
                        message = (char) 167 + "a" + stat.getStatus();
                        actionSave(true, false);
                    } else {
                        message = (char) 167 + "4" + stat.getStatus();
                    }
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
                    message = (char) 167 + "4Error while changing displayname";
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    public void actionTogglePWvisibility() {
        System.out.println("testerino");
        this.togglePWButton.setVisible(!this.togglePWButton.isVisible());
        this.pw.setPwVisible(!this.pw.isPwVisible());
        this.pw.setFocused(false);
    }
}