package trollogyadherent.offlineauth.gui.skin;

import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.skin.cape.AvailableCapesListGUI;
import trollogyadherent.offlineauth.gui.skin.cape.CapeListEntry;
import trollogyadherent.offlineauth.request.Request;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.util.RsaKeyUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SkinManagmentGUI extends GuiScreen {
    private GuiScreen previous;

    private List<SkinListEntry> availableSkins;
    private List<CapeListEntry> availableCapes;

    private String status;

    private AvailableSkinsListGUI availableSkinsListGUI;
    private AvailableCapesListGUI availableCapesListGUI;
    private boolean isShowingSkins;
    public GuiCheckBox capeCheckbox = null;
    public GuiCheckBox elytraCheckbox = null;
    private GuiButton capeSkinToggle;
    ItemStack elytraItemStack;
    private boolean switchingToCapesFirst = true;

    public SkinManagmentGUI(GuiScreen previous) {
        this.previous = previous;
    }

    @Override
    public void initGui()
    {
        SkinGuiRenderTicker.skinResourceLocation = null;
        SkinGuiRenderTicker.capeResourceLocation = null;
        SkinGuiRenderTicker.capeObject = null;
        try {
            if (SkinGuiRenderTicker.clientPlayerMP != null) {
                OfflineAuth.varInstanceClient.capeLocationField.set(SkinGuiRenderTicker.clientPlayerMP, null);
            }
        } catch (IllegalAccessException e) {
            OfflineAuth.error("Failed to reflect cape field");
        }
        SkinGuiRenderTicker.yaw = 0;
        isShowingSkins = true;
        this.buttonList.add(new GuiButton(3, this.width - ((this.width - 25) / 4 + 5), this.height - 48, (this.width - 25) / 4, 20, I18n.format("offlineauth.skingui.done")));
        if (ClientUtil.isSinglePlayer()) {
            this.buttonList.add(new GuiButton(5, this.width - 2 * ((this.width - 25) / 4 + 5), this.height - 48, (this.width - 25) / 4, 20, I18n.format("offlineauth.skingui.unset")));
        } else {
            this.buttonList.add(new GuiButton(5, this.width - 2 * ((this.width - 25) / 4 + 5), this.height - 48, (this.width - 25) / 4, 20, I18n.format("offlineauth.skingui.remove")));
        }
        if (ClientUtil.isSinglePlayer()) {
            this.buttonList.add(new GuiButton(1, this.width - 3 * ((this.width - 25) / 4 + 5), this.height - 48, (this.width - 25) / 4, 20, I18n.format("offlineauth.skingui.set")));
        } else {
            this.buttonList.add(new GuiButton(1, this.width - 3 * ((this.width - 25) / 4 + 5), this.height - 48, (this.width - 25) / 4, 20, I18n.format("offlineauth.skingui.upload")));
        }
        this.buttonList.add(new GuiButton(2, this.width - 4 * ((this.width - 25) / 4 + 5), this.height - 48, (this.width - 25) / 4, 20, I18n.format("offlineauth.skingui.open_skin_folder")));
        capeSkinToggle = new GuiButton(4, this.width - 4 * ((this.width - 25) / 4 + 5), 5, 80, 20, I18n.format("offlineauth.skingui.btn.capes"));
     
        if(Config.enableCapes) {
            this.buttonList.add(capeSkinToggle);
        }

        String capeString = I18n.format("Cape");
        int capeStringLen = Minecraft.getMinecraft().fontRenderer.getStringWidth(capeString);
        capeCheckbox = new GuiCheckBox(8, this.width - 20 - capeStringLen, this.height - 60, capeString, false);
        capeCheckbox.setIsChecked(true);
        if(Config.enableCapes) {
            this.buttonList.add(capeCheckbox);
        }
        if (OfflineAuth.isEFRLoaded) {
            String elytraString = I18n.format("Elytra");
            int elytraStringLen = Minecraft.getMinecraft().fontRenderer.getStringWidth(elytraString);
            elytraCheckbox = new GuiCheckBox(9, this.width - 45 - capeStringLen - elytraStringLen, this.height - 60, "Elytra", false);
            this.buttonList.add(elytraCheckbox);

            elytraItemStack = new ItemStack(GameRegistry.findItem("etfuturum", "elytra"), 1);
        }

        this.availableSkins = new ArrayList<>();
        this.availableCapes = new ArrayList<>();
        this.status = I18n.format("offlineauth.skingui.select_skin");
        SkinGuiRenderTicker.yaw = 1;

        String[] skinNames = ClientSkinUtil.getAvailableSkinNames();
        if (skinNames != null) {
            for (String s : skinNames) {
                SkinListEntry entry = new SkinListEntry(this, s);
                this.availableSkins.add(entry);
                //Runnable r = new SkinListEntryRunnable(this, s, availableSkins);
                //new Thread(r).start();
            }
        }
    
        if(Config.enableCapes) {
            String[] capeNames = ClientSkinUtil.getAvailableCapeNames();
            if (capeNames != null) {
                for (String s : capeNames) {
                    //System.out.println("Adding cape " + s);
                    CapeListEntry entry = new CapeListEntry(this, s);
                    this.availableCapes.add(entry);
                }
            }
        }

        this.availableSkinsListGUI = new AvailableSkinsListGUI(this.mc, 200, this.height, 36, this.availableSkins);
        this.availableSkinsListGUI.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.availableSkinsListGUI.registerScrollButtons(7, 8);
    
        if(Config.enableCapes) {
            this.availableCapesListGUI = new AvailableCapesListGUI(this.mc, 200, this.height, 36, this.availableCapes);
            this.availableCapesListGUI.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
            this.availableCapesListGUI.registerScrollButtons(7, 8);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (SkinGuiRenderTicker.clientPlayerMP == null) {
            return;
        }
        if(Config.enableCapes) {
            if (capeCheckbox.isChecked()) {
                if (elytraCheckbox != null) {
                    elytraCheckbox.enabled = true;
                    if (!elytraCheckbox.isChecked()) {
                        SkinGuiRenderTicker.clientPlayerMP.setCurrentItemOrArmor(3, null);
                    } else {
                        try {
                            if (SkinGuiRenderTicker.capeObject != null) {
                                OfflineAuth.varInstanceClient.capeLocationField.set(SkinGuiRenderTicker.clientPlayerMP, SkinGuiRenderTicker.capeObject.getCurrentFrame());
                            }
                        } catch (IllegalAccessException e) {
                            OfflineAuth.error("Reflection error on cape resource field");
                            e.printStackTrace();
                        }
                        SkinGuiRenderTicker.clientPlayerMP.setCurrentItemOrArmor(3, elytraItemStack);
                    }
                }
            } else {
                if (elytraCheckbox != null) {
                    elytraCheckbox.enabled = false;
                    SkinGuiRenderTicker.clientPlayerMP.setCurrentItemOrArmor(3, null);
                }
            }
        }
    }

    public boolean hasSkinEntry(SkinListEntry skinListEntry)
    {
        return this.availableSkins.contains(skinListEntry);
    }
    public boolean hasCapeEntry(CapeListEntry capeListEntry) {
        return this.availableCapes.contains(capeListEntry);
    }

    public List<SkinListEntry> probablyToRemove(SkinListEntry skinListEntry)
    {
        return this.hasSkinEntry(skinListEntry) ? this.availableSkins : null;
    }

    public List<SkinListEntry> getAvailableSkins()
    {
        return this.availableSkins;
    }
    public List<CapeListEntry> getAvailableCapes()
    {
        return this.availableCapes;
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {

            /* Open skin folder in OS file explorer button */
            if (button.id == 2)
            {
                File file1 = new File(isShowingSkins ? OfflineAuth.varInstanceClient.clientSkinsPath : OfflineAuth.varInstanceClient.clientCapesPath);
                String s = file1.getAbsolutePath();

                if (Util.getOSType() == Util.EnumOS.OSX)
                {
                    try
                    {
                        Runtime.getRuntime().exec(new String[] {"/usr/bin/open", s});
                        return;
                    }
                    catch (IOException ioexception1)
                    {
                        OfflineAuth.error("Couldn't open file, " + ioexception1);
                    }
                }
                else if (Util.getOSType() == Util.EnumOS.WINDOWS)
                {
                    String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", s);

                    try
                    {
                        Runtime.getRuntime().exec(s1);
                        return;
                    }
                    catch (IOException ioexception)
                    {
                        OfflineAuth.error("Couldn't open file, " + ioexception);
                    }
                }

                boolean flag = false;

                try
                {
                    Class oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
                    oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, file1.toURI());
                }
                catch (Throwable throwable)
                {
                    OfflineAuth.error("Couldn't open link, " + throwable);
                    flag = true;
                }

                if (flag)
                {
                    OfflineAuth.info("Opening via system class!");
                    Sys.openURL("file://" + s);
                }
            }
            /* Upload button */
            else if (button.id == 1)
            {
                if (ClientUtil.isSinglePlayer()) {
                    if (isShowingSkins) {
                        if (OfflineAuth.varInstanceClient.skinGuiRenderTicker.getSkinResourceLocation() != null) {
                            try {
                                OfflineAuth.varInstanceClient.skinLocationField.set(Minecraft.getMinecraft().thePlayer, OfflineAuth.varInstanceClient.skinGuiRenderTicker.getSkinResourceLocation());
                                OfflineAuth.varInstanceClient.singlePlayerSkinResourceLocation = OfflineAuth.varInstanceClient.skinGuiRenderTicker.getSkinResourceLocation();
                            } catch (IllegalAccessException e) {
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + I18n.format("offlineauth.rest.apply_skin_error");
                                OfflineAuth.error("Fatal error while applying skin");
                                e.printStackTrace();
                                return;
                            }
                        }
                        status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.GREEN) + I18n.format("offlineauth.skingui.sp_skin_set");
                        if (this.availableSkinsListGUI.selectedIndex == -1) {
                            OfflineAuth.error("Could not get skin name to save as last used!");
                            return;
                        }
                        ClientSkinUtil.setLastUsedOfflineSkinName(((SkinListEntry) this.availableSkinsListGUI.skinEntries.get(this.availableSkinsListGUI.selectedIndex)).skinName);
                        //this.mc.displayGuiScreen(this.previous);
                    } else if (Config.enableCapes) {
                        if (OfflineAuth.varInstanceClient.skinGuiRenderTicker.getCapeResourceLocation() != null) {
                            try {
                                OfflineAuth.varInstanceClient.capeLocationField.set(Minecraft.getMinecraft().thePlayer, OfflineAuth.varInstanceClient.skinGuiRenderTicker.getCapeResourceLocation());
                            } catch (IllegalAccessException e) {
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + I18n.format("offlineauth.rest.apply_cape_error");
                                OfflineAuth.error("Fatal error while applying cape");
                                e.printStackTrace();
                                return;
                            }
                        }
                        status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.GREEN) + I18n.format("offlineauth.skingui.sp_cape_set");
                        if (this.availableCapesListGUI.selectedIndex == -1) {
                            OfflineAuth.error("Could not get cape name to save as last used!");
                            return;
                        }
                        ClientSkinUtil.setLastUsedOfflineCapeName(((CapeListEntry) this.availableCapesListGUI.capeEntries.get(this.availableCapesListGUI.selectedIndex)).capeName);
                    }
                } else {
                    if (isShowingSkins) {
                        String skinName = null;
                        if (getAvailableSkins().size() > 0 && this.availableSkinsListGUI.selectedIndex >= 0) {
                            skinName = ((SkinListEntry) this.availableSkinsListGUI.skinEntries.get(this.availableSkinsListGUI.selectedIndex)).skinName;
                        }
                        if (skinName == null) {
                            return;
                        }
                        byte[] skinBytes;
                        skinBytes = trollogyadherent.offlineauth.util.Util.fileToBytes(ClientSkinUtil.getSkinFile(skinName));
                        if(skinBytes == null) {
                            OfflineAuth.error("Failed to load skin to bytes: " + skinName);
                            return;
                        }
                        OAServerData oasd = trollogyadherent.offlineauth.util.Util.getCurrentOAServerData();
                        if (oasd == null) {
                            //validText = "\u2718";
                            //validColor = Color.RED.getRGB();
                            return;
                        }
                        PublicKey clientPubKey = null;
                        PrivateKey clientPriv = null;
                        if (oasd.isUsingKey()) {
                            try {
                                clientPubKey = RsaKeyUtil.loadPublicKey(oasd.getPublicKeyPath());
                                clientPriv = RsaKeyUtil.loadPrivateKey(oasd.getPrivateKeyPath());
                            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                                OfflineAuth.error("Couldn't load account keys!");
                                e.printStackTrace();
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + "Error";
                                return;
                                //throw new RuntimeException(e);
                            }
                        }
                        try {
                            status = I18n.format("offlineauth.skingui.uploading");
                    /*byte [] fakeBytes = new byte[skinBytes.length];
                    new Random().nextBytes(fakeBytes);*/
                            StatusResponseObject stat = Request.uploadSkin(trollogyadherent.offlineauth.util.Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), oasd.getRestPort(), oasd.getIdentifier(), oasd.getPassword(), skinBytes /*fakeBytes*/, clientPubKey, clientPriv);
                            if (stat.getStatusCode() == 200) {
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.GREEN) + I18n.format(stat.getStatus());
                            } else {
                                if (stat.getStatus() == null) {
                                    status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + "Error";
                                            OfflineAuth.error("Status from skin upload is null!");
                                    return;
                                }
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + I18n.format(stat.getStatus());
                            }
                        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                                 InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                                 BadPaddingException | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    } else if (Config.enableCapes) {
                        if (ClientUtil.isSinglePlayer()) {
                            return;
                        }

                        String capeName = null;
                        if (getAvailableCapes().size() > 0 && this.availableCapesListGUI.selectedIndex >= 0) {
                            capeName = ((CapeListEntry) this.availableCapesListGUI.capeEntries.get(this.availableCapesListGUI.selectedIndex)).capeName;
                        }
                        if (capeName == null) {
                            return;
                        }
                        byte[] capeBytes;

                        capeBytes = trollogyadherent.offlineauth.util.Util.fileToBytes(ClientSkinUtil.getCapeFile(capeName));
                        if (capeBytes == null) {
                            OfflineAuth.error("Failed to load cape to bytes: " + capeName);
                            return;
                        }

                        OAServerData oasd = trollogyadherent.offlineauth.util.Util.getCurrentOAServerData();
                        if (oasd == null) {
                            return;
                        }
                        PublicKey clientPubKey = null;
                        PrivateKey clientPriv = null;
                        if (oasd.isUsingKey()) {
                            try {
                                clientPubKey = RsaKeyUtil.loadPublicKey(oasd.getPublicKeyPath());
                                clientPriv = RsaKeyUtil.loadPrivateKey(oasd.getPrivateKeyPath());
                            } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        try {
                            status = I18n.format("offlineauth.skingui.uploading");

                            StatusResponseObject stat = Request.uploadCape(trollogyadherent.offlineauth.util.Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), oasd.getRestPort(), oasd.getIdentifier(), oasd.getPassword(), capeBytes, clientPubKey, clientPriv);
                            if (stat.getStatusCode() == 200) {
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.GREEN) + I18n.format(stat.getStatus());
                            } else {
                                if (stat.getStatus() == null) {
                                    OfflineAuth.error("Status from cape upload is null!");
                                    status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + "Error";
                                    return;
                                }
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + I18n.format(stat.getStatus());
                            }
                        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                                 InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                                 BadPaddingException | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //this.mc.displayGuiScreen(this.previous);
                /* Done button */
            } else if (button.id == 3) {
                ////this.mc.thePlayer = null;
                this.mc.displayGuiScreen(this.previous);
                /* Button switching between cape and skin view */
            } else if (button.id == 4) {

                if (isShowingSkins) {
                    if (switchingToCapesFirst) {
                        //switchingToCapesFirst = false;
                        //return;
                    }
                    isShowingSkins = false;
                    SkinGuiRenderTicker.yaw = 180;
                    this.status = I18n.format("offlineauth.skingui.select_cape");
                    Iterator<GuiButton> i = this.buttonList.iterator();
                    while (i.hasNext()) {
                        GuiButton gb = i.next();
                        if (gb.id == 2) {
                            i.remove();
                        }
                        if (gb.id == 4) {
                            //i.remove();
                        }
                    }
                    this.buttonList.add(new GuiButton(2, this.width - 4 * ((this.width - 25) / 4 + 5), this.height - 48, (this.width - 25) / 4, 20, I18n.format("offlineauth.skingui.open_cape_folder")));
                    //this.buttonList.add(new GuiButton(6, this.width - 4 * ((this.width - 25) / 4 + 5), 5, 80, 20, I18n.format("offlineauth.skingui.btn.skins")));
                    capeSkinToggle.displayString = I18n.format("offlineauth.skingui.btn.skins");
                } else {
                    isShowingSkins = true;
                    SkinGuiRenderTicker.yaw = 0;
                    this.status = I18n.format("offlineauth.skingui.select_skin");

                    Iterator<GuiButton> i = this.buttonList.iterator();
                    while (i.hasNext()) {
                        GuiButton gb = i.next();
                        if (gb.id == 2) {
                            i.remove();
                        }
                        if (gb.id == 6) {
                            //i.remove();
                        }
                    }
                    this.buttonList.add(new GuiButton(2, this.width - 4 * ((this.width - 25) / 4 + 5), this.height - 48, (this.width - 25) / 4, 20, I18n.format("offlineauth.skingui.open_skin_folder")));
                    //this.buttonList.add(new GuiButton(4, this.width - 4 * ((this.width - 25) / 4 + 5), 5, 80, 20, I18n.format("offlineauth.skingui.btn.capes")));
                    capeSkinToggle.displayString = I18n.format("offlineauth.skingui.btn.capes");
                }
            } else if(button.id == 6) {

            } else if (button.id == 5) {
                if (ClientUtil.isSinglePlayer()) {
                    if (isShowingSkins) {
                        ClientSkinUtil.removeLastUsedOfflineSkinName();
                        SkinGuiRenderTicker.skinResourceLocation = null;
                        OfflineAuth.varInstanceClient.singlePlayerSkinResourceLocation = null;
                        try {
                            status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.GREEN) + I18n.format("offlineauth.skingui.sp_skin_remove");
                            OfflineAuth.varInstanceClient.skinLocationField.set(Minecraft.getMinecraft().thePlayer, null);
                        } catch (IllegalAccessException e) {
                            status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + I18n.format("offlineauth.rest.remove_skin_error");
                            OfflineAuth.error("Fatal error while removing skin");
                            e.printStackTrace();
                        }
                    } else {
                        OfflineAuth.varInstanceClient.singlePlayerCapeObject = null;
                        ClientSkinUtil.removeLastUsedOfflineCapeName();
                        SkinGuiRenderTicker.capeResourceLocation = null;
                        if (Config.enableCapes) {
                            try {
                                OfflineAuth.varInstanceClient.capeLocationField.set(Minecraft.getMinecraft().thePlayer, null);
                                ClientSkinUtil.removeLastUsedOfflineSkinName();
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.GREEN) + I18n.format("offlineauth.skingui.sp_cape_remove");
                            } catch (IllegalAccessException e) {
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + I18n.format("offlineauth.rest.remove_cape_error");
                                OfflineAuth.error("Fatal error while removing cape");
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    OAServerData oasd = trollogyadherent.offlineauth.util.Util.getCurrentOAServerData();
                    if (oasd == null) {
                        return;
                    }
                    PublicKey clientPubKey = null;
                    PrivateKey clientPriv = null;
                    if (oasd.isUsingKey()) {
                        try {
                            clientPubKey = RsaKeyUtil.loadPublicKey(oasd.getPublicKeyPath());
                            clientPriv = RsaKeyUtil.loadPrivateKey(oasd.getPrivateKeyPath());
                        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    status = I18n.format("offlineauth.skingui.removing");
                    if (isShowingSkins) {
                        try {
                            StatusResponseObject stat = Request.requestSkinRemoval(trollogyadherent.offlineauth.util.Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), oasd.getRestPort(), oasd.getIdentifier(), oasd.getPassword(), clientPubKey, clientPriv);
                            if (stat.getStatusCode() == 200) {
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.GREEN) + I18n.format(stat.getStatus());
                                SkinGuiRenderTicker.skinResourceLocation = null;
                                if (Minecraft.getMinecraft().thePlayer != null && SkinGuiRenderTicker.clientPlayerMP != null) {
                                    try {
                                        SkinGuiRenderTicker.skinResourceLocation = (ResourceLocation) OfflineAuth.varInstanceClient.skinLocationField.get(Minecraft.getMinecraft().thePlayer);
                                    } catch (IllegalAccessException e) {
                                        OfflineAuth.error("Failed to get ingame skin");
                                    }
                                }
                            } else {
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + I18n.format(stat.getStatus());
                            }
                        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                                 InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                                 BadPaddingException | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            StatusResponseObject stat = Request.requestCapeRemoval(trollogyadherent.offlineauth.util.Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), oasd.getRestPort(), oasd.getIdentifier(), oasd.getPassword(), clientPubKey, clientPriv);
                            if (stat.getStatusCode() == 200) {
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.GREEN) + I18n.format(stat.getStatus());
                                SkinGuiRenderTicker.capeResourceLocation = null;
                            } else {
                                status = trollogyadherent.offlineauth.util.Util.colorCode(trollogyadherent.offlineauth.util.Util.Color.RED) + I18n.format(stat.getStatus());
                            }
                        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                                 InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                                 BadPaddingException | InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        if (isShowingSkins) {
            this.availableSkinsListGUI.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_);
        } else if (Config.enableCapes) {
            this.availableCapesListGUI.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_);
        }
        //this.field_146967_r.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Called when the mouse is moved or a mouse button is released.  Signature: (mouseX, mouseY, which) which==-1 is
     * mouseMove, which==0 or which==1 is mouseUp
     */
    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        super.mouseMovedOrUp(p_146286_1_, p_146286_2_, p_146286_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawBackground(0);
        if (isShowingSkins) {
            this.availableSkinsListGUI.drawScreen(mouseX, mouseY, partialTicks);
        } else if (Config.enableCapes) {
            this.availableCapesListGUI.drawScreen(mouseX, mouseY, partialTicks);
        }
        this.drawCenteredString(this.fontRendererObj, I18n.format(status), this.width / 2, 16, 16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char c, int k) {
        super.keyTyped(c, k);

        if (k == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.previous);
        }

        /*float movementSpeed = 2;

        if (k == Keyboard.KEY_RIGHT) {
            SkinGuiRenderTicker.yaw += movementSpeed;
            System.out.println("Yaw: " + SkinGuiRenderTicker.yaw);
        }

        if (k == Keyboard.KEY_LEFT) {
            SkinGuiRenderTicker.yaw -= movementSpeed;
        }

        if (k == Keyboard.KEY_UP) {
            SkinGuiRenderTicker.pitch += movementSpeed;
        }

        if (k == Keyboard.KEY_DOWN) {
            SkinGuiRenderTicker.pitch -= movementSpeed;
        }

        if (k == Keyboard.KEY_1 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            SkinGuiRenderTicker.angle -= movementSpeed;
            System.out.println("angle: " + SkinGuiRenderTicker.angle);
        } else if (k == Keyboard.KEY_1) {
            SkinGuiRenderTicker.angle += movementSpeed;
            System.out.println("angle: " + SkinGuiRenderTicker.angle);
        }

        if (k == Keyboard.KEY_X && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            SkinGuiRenderTicker.x -= movementSpeed;
            System.out.println("x: " + SkinGuiRenderTicker.x);
        } else if (k == Keyboard.KEY_X) {
            SkinGuiRenderTicker.x += movementSpeed;
            System.out.println("x: " + SkinGuiRenderTicker.x);
        }

        if (k == Keyboard.KEY_Y && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            SkinGuiRenderTicker.y -= movementSpeed;
            System.out.println("y: " + SkinGuiRenderTicker.y);
        } else if (k == Keyboard.KEY_Y) {
            SkinGuiRenderTicker.y += movementSpeed;
            System.out.println("y: " + SkinGuiRenderTicker.y);
        }

        if (k == Keyboard.KEY_Z && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            SkinGuiRenderTicker.z -= 0.01;
            System.out.println("z: " + SkinGuiRenderTicker.z);
        } else if (k == Keyboard.KEY_Z) {
            SkinGuiRenderTicker.z += 0.01;
            System.out.println("z: " + SkinGuiRenderTicker.z);
        }



        if (k == Keyboard.KEY_2 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            SkinGuiRenderTicker.textureWidth -= 1;
            System.out.println("textureWidth: " + SkinGuiRenderTicker.textureWidth);
        } else if (k == Keyboard.KEY_2) {
            SkinGuiRenderTicker.textureWidth += 1;
            System.out.println("textureWidth: " + SkinGuiRenderTicker.textureWidth);
        }

        if (k == Keyboard.KEY_3 && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            SkinGuiRenderTicker.textureHeight -= 1;
            System.out.println("textureHeight: " + SkinGuiRenderTicker.textureHeight);
        } else if (k == Keyboard.KEY_3) {
            SkinGuiRenderTicker.textureHeight += 1;
            System.out.println("textureHeight: " + SkinGuiRenderTicker.textureHeight);
        }
         */
    }

    class SkinListEntryRunnable implements Runnable {
        private SkinManagmentGUI skinManagmentGUI;
        private String s;
        private List availableSkins;

        public SkinListEntryRunnable(SkinManagmentGUI skinManagmentGUI, String s, List<SkinListEntry> availableskins) {
            this.skinManagmentGUI = skinManagmentGUI;
            this.s = s;
            this.availableSkins = availableskins;
        }

        @Override
        public void run() {

        }
    }
 }
