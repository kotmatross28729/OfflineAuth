package trollogyadherent.offlineauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.Secure;
import trollogyadherent.offlineauth.clientdata.ClientData;
import trollogyadherent.offlineauth.request.Request;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.rest.ResponseObject;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.util.Util;

import java.awt.Color;
import java.io.IOException;
import java.net.URISyntaxException;

public class GuiLogin extends GuiScreen {

    public GuiTextField username;
    private GuiPasswordField pw;
    private GuiButton login;
    private GuiButton cancel;
    private GuiButton offline;
    private GuiButton config;

    private GuiTextField token;
    private GuiPasswordField newPW;
    private GuiTextField port;
    private GuiButton changePW;
    private GuiButton uploadSkin;
    private GuiButton deleteAccount;
    private GuiButton save;

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
                actionChange();
                break;
            case 4:
                System.out.println(b.id);
                break;
            case 5:
                actionsave();
                break;
            case 6:
                actionCancel();
                break;
            case 7:
                actionConfig();
                break;
        }

    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();

        this.drawCenteredString(this.fontRendererObj, "Username:", this.width / 2, this.basey,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, "Password:", this.width / 4, this.basey + 45,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, "New Password:", this.width / 2 + 60, this.basey + 45,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, "Port:", this.width / 4 - 25, this.basey + 90,
                Color.WHITE.getRGB());
        this.drawCenteredString(this.fontRendererObj, "Registration Token:", this.width / 2 - 25, this.basey + 90,
                Color.WHITE.getRGB());
        if (!(this.message == null || this.message.isEmpty())) {
            this.drawCenteredString(this.fontRendererObj, this.message, this.width / 2, this.basey - 15, 0xFFFFFF);
        }
        this.username.drawTextBox();
        this.pw.drawTextBox();

        this.token.drawTextBox();
        this.newPW.drawTextBox();
        this.port.drawTextBox();

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.username.drawTextBox();
        this.pw.drawTextBox();
        this.token.drawTextBox();
        this.newPW.drawTextBox();
        this.port.drawTextBox();
    }

    @Override
    public void initGui() {
        super.initGui();

        this.basey = this.height / 2 - 200 / 2;//this.height / 2 - 110 / 2;

        this.username = new GuiTextField(this.fontRendererObj, this.width / 2 - 155, this.basey + 15, 2 * 155, 20);
        this.username.setMaxStringLength(512);
        this.username.setText(Secure.getUsername());
        this.username.setFocused(true);

        this.pw = new GuiPasswordField(this.fontRendererObj, this.width / 2 - 155, this.basey + 60, 150, 20);
        this.pw.setMaxStringLength(512);

        this.newPW = new GuiPasswordField(this.fontRendererObj, this.width / 2 + 5, this.basey + 60, 150, 20);
        this.newPW.setMaxStringLength(512);
        this.newPW.setText("");

        this.port = new GuiTextField(this.fontRendererObj,this.width / 2 - 155, this.basey + 105, 50, 20);
        this.port.setText("4567");
        this.port.setMaxStringLength(512);

        this.token = new GuiTextField(this.fontRendererObj,this.width / 2 - 155 + 60, this.basey + 105, 2 * 155 - 60, 20);
        this.token.setText("");
        this.token.setMaxStringLength(512);

        OAServerData oasd = Util.getOAServerDatabyIP(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), Util.getPort(OfflineAuth.varInstanceClient.selectedServerData));
        if (oasd != null) {
            this.username.setText(oasd.getUsername());
            this.pw.setText(oasd.getPassword());
            this.port.setText(oasd.getRestPort());
        }

        //this.save = new GuiCheckBox(2, this.width / 2 - 155, this.basey + 85, "Save Password to Config (WARNING: SECURITY RISK!)", false);
        //this.buttonList.add(this.save);

        this.login = new GuiButton(0, this.width / 2 - 155, this.basey + 135, 100, 20, "Register");
        this.offline = new GuiButton(1, this.width / 2 - 50, this.basey + 135, 100, 20, "Check Registration");
        this.deleteAccount = new GuiButton(2, this.width / 2 + 55, this.basey + 135, 100, 20, "Delete Account");
        this.changePW = new GuiButton(3, this.width / 2 - 155, this.basey + 165, 100, 20, "Change Password");
        this.uploadSkin = new GuiButton(4, this.width / 2 - 50, this.basey + 165, 100, 20, "Upload Skin");

        this.buttonList.add(this.login);
        this.buttonList.add(this.offline);
        this.buttonList.add(this.changePW);
        this.buttonList.add(this.uploadSkin);
        this.buttonList.add(this.deleteAccount);


        this.save = new GuiButton(5, this.width - 240, this.height - 23, 75, 20, "Save");
        this.cancel = new GuiButton(6, this.width - 160, this.height - 23, 75, 20, "Cancel");
        this.config = new GuiButton(7, this.width - 80, this.height - 23, 75, 20, "Config");
        this.buttonList.add(this.config);
        this.buttonList.add(this.cancel);
        this.buttonList.add(this.save);
    }

    @Override
    protected void keyTyped(char c, int k) {
        super.keyTyped(c, k);

        if (k == Keyboard.KEY_ESCAPE) {
            actionCancel();
            return;
        }

        this.username.textboxKeyTyped(c, k);
        this.pw.textboxKeyTyped(c, k);
        this.token.textboxKeyTyped(c, k);
        this.newPW.textboxKeyTyped(c, k);
        this.port.textboxKeyTyped(c, k);

        /* TODO: Handle tab properly (cycling through buttons would be great too) */
        if (k == Keyboard.KEY_TAB) {
            this.username.setFocused(!this.username.isFocused());
            this.pw.setFocused(!this.pw.isFocused());
            this.token.setFocused(!this.token.isFocused());

        /* TODO: Handle enter properly */
        } else if (k == Keyboard.KEY_RETURN) {
            if (this.username.isFocused()) {
                this.username.setFocused(false);
                this.pw.setFocused(true);
            } else if (this.pw.isFocused()) {
                this.actionPerformed(this.login);
            }
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int b) {
        super.mouseClicked(x, y, b);
        this.username.mouseClicked(x, y, b);
        this.pw.mouseClicked(x, y, b);
        this.token.mouseClicked(x, y, b);
        this.newPW.mouseClicked(x, y, b);
        this.port.mouseClicked(x, y, b);
    }

    /**
     * used as an interface between this and the secure class
     * <p>
     * returns whether the login was successful
     */
    /*private boolean login() {
        try {
            Secure.login(this.username.getText(), this.pw.getPW(), this.save.isChecked());
            this.message = (char) 167 + "aLogin successful!";
            return true;
        } catch (AuthenticationException e) {
            this.message = (char) 167 + "4Login failed: " + e.getMessage();
            OfflineAuth.error("Login failed:" + e);
            return false;
        } catch (Exception e) {
            this.message = (char) 167 + "4Error: Something went wrong!";
            OfflineAuth.error("Error:" + e);
            return false;
        }
    }*/

    /**
     * sets the name for playing offline
     */
    private boolean playOffline() {
        String username = this.username.getText();
        if (!(username.length() >= 2 && username.length() <= 16)) {
            this.message = (char) 167 + "4Error: Username needs a length between 2 and 16";
            return false;
        }
        if (!username.matches("[A-Za-z0-9_]{2,16}")) {
            this.message = (char) 167 + "4Error: Username has to be alphanumerical";
            return false;
        }
        try {
            Secure.offlineMode(username);
            return true;
        } catch (Exception e) {
            this.message = (char) 167 + "4Error: Something went wrong!";
            OfflineAuth.error("Error:" + e);
            return false;
        }
    }

    private void actionCancel() {
        this.mc.displayGuiScreen(prev);
    }

    private void actionsave() {
        //this.message = (char) 167 + "4Sneed!";
        //boolean validServer, String ip, String port, String username, String password, boolean registrationOpen, boolean registrationTokenOpen, boolean skinUploadAllowed
        OAServerData oaServerDataTemp = new OAServerData(false, Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), Util.getPort(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), username.getText(), pw.getPW(), false, false, false);
        boolean found = false;
        for (OAServerData oasd : OfflineAuth.varInstanceClient.OAserverDataCache) {
            if (oasd.getIp().equals(oaServerDataTemp.getIp()) && oasd.getPort().equals(oaServerDataTemp.getPort())) {
                found = true;
                oasd.setUsername(oaServerDataTemp.getUsername());
                oasd.setPassword(oaServerDataTemp.getPassword());
                oasd.setRestPort(oaServerDataTemp.getRestPort());
            }
        }
        if (!found) {
            OfflineAuth.varInstanceClient.OAserverDataCache.add(oaServerDataTemp);
        }
        ClientData.saveData();
        System.out.println(OfflineAuth.varInstanceClient.OAserverDataCache);
        System.out.println(OfflineAuth.varInstanceClient.OAserverDataCache.size());
        if (Config.savebuttonExit) {
            this.mc.displayGuiScreen(prev);
        }
    }

    private void actionConfig() {
        this.mc.displayGuiScreen(new ConfigGUI(this));
    }

    private void actionRegister() {
        message = (char) 167 + "7Registering...";
        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                /* if (Secure.SessionValid()) {
                validText = "\u2714";
                validColor = Color.GREEN.getRGB();
                } else {
                    validText = "\u2718";
                   validColor = Color.RED.getRGB();
                } */
                try {
                    StatusResponseObject stat = Request.register(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), username.getText(), pw.getPW(), token.getText());
                    if (stat.getStatusCode() == 200) {
                        message = (char) 167 + "a" + stat.getStatus();
                    } else {
                        message = (char) 167 + "4" + stat.getStatus();
                    }
                } catch (IOException e) {
                    message = (char) 167 + "4Error while registering account (IOException)";
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    public void actionDelete() {
        Minecraft.getMinecraft().displayGuiScreen(new AccountDeletionGUI((GuiLogin) Minecraft.getMinecraft().currentScreen));
    }

    public void proceedWithAccountDeletion() {
        message = (char) 167 + "7Deleting account...";

        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    StatusResponseObject stat = Request.delete(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), username.getText(), pw.getPW());
                    if (stat.getStatusCode() == 200) {
                        message = (char) 167 + "a" + stat.getStatus();
                    } else {
                        message = (char) 167 + "4" + stat.getStatus();
                    }
                } catch (IOException e) {
                    message = (char) 167 + "4Error while deleting account (IOException)";
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    private void actionChange() {
        message = (char) 167 + "7Changing account password...";
        Thread registerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    StatusResponseObject stat = Request.change(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), username.getText(), pw.getPW(), newPW.getPW());
                    if (stat.getStatusCode() == 200) {
                        message = (char) 167 + "a" + stat.getStatus();
                        pw.setText(newPW.getPW());
                        newPW.setText("");
                    } else {
                        message = (char) 167 + "4" + stat.getStatus();
                    }
                } catch (IOException e) {
                    message = (char) 167 + "4Error while changing account password (IOException)";
                    e.printStackTrace();
                }
            }
        });
        registerThread.start();
    }

    private void actionCheckRegistration() {
        message = (char) 167 + "7Checking registration...";
        Thread vibeCheckThread = new Thread(new Runnable() {
            public void run() {
                ResponseObject stat = null;
                try {
                    stat = Request.vibeCheck(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), port.getText(), username.getText(), pw.getPW());
                } catch (URISyntaxException e) {
                    message = (char) 167 + "4Error while checking registration";
                    OfflineAuth.error(e.getMessage());
                    //e.printStackTrace();
                }
                if (stat != null && stat.getStatusCode() == 200) {
                    if (stat.isValidUser()) {
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
}