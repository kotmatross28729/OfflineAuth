package trollogyadherent.offlineauth.gui.skin;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Util;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.request.Request;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.rest.ResponseObject;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.util.ClientUtil;
import trollogyadherent.offlineauth.util.RsaKeyUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SkinManagmentGUI extends GuiScreen {
    private GuiScreen previous;

    private List availableSkins;

    private GuiButton done;
    private GuiButton cancel;
    private String status;

    private AvailableSkinsListGUI availableSkinsListGUI;

    public SkinManagmentGUI(GuiScreen previous) {
        this.previous = previous;
    }

    public void initGui()
    {
        this.buttonList.add(new GuiOptionButton(2, this.width / 2 - /*154*/234, this.height - 48, I18n.format("Open skin folder")));
        if (ClientUtil.isSinglePlayer()) {
            this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 76/* + 4*/, this.height - 48, I18n.format("Set")));
        } else {
            this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 76/* + 4*/, this.height - 48, I18n.format("Upload")));
        }
        this.buttonList.add(new GuiOptionButton(3, this.width / 2 + 84, this.height - 48, I18n.format("Done")));
        this.availableSkins = new ArrayList();
        this.status = "Select Skin";
        //this.field_146969_h = new ArrayList();
        //ResourcePackRepository resourcepackrepository = this.mc.getResourcePackRepository();
        //resourcepackrepository.updateRepositoryEntriesAll();



        /*ArrayList arraylist = Lists.newArrayList(resourcepackrepository.getRepositoryEntriesAll());
        arraylist.removeAll(resourcepackrepository.getRepositoryEntries());
        Iterator iterator = arraylist.iterator();
        ResourcePackRepository.Entry entry;

        while (iterator.hasNext())
        {
            entry = (ResourcePackRepository.Entry)iterator.next();
            this.field_146966_g.add(new ResourcePackListEntryFound(this, entry));
        }

        iterator = Lists.reverse(resourcepackrepository.getRepositoryEntries()).iterator();

        while (iterator.hasNext())
        {
            entry = (ResourcePackRepository.Entry)iterator.next();
            this.field_146969_h.add(new ResourcePackListEntryFound(this, entry));
        }*/

        String[] skinNames = ClientSkinUtil.getAvailableSkinNames();
        if (skinNames != null) {
            for (String s : skinNames) {
                //System.out.println("Adding skin " + s);
                SkinListEntry entry = new SkinListEntry(this, s);
                this.availableSkins.add(entry);
            }
        }

        //this.field_146969_h.add(new ResourcePackListEntryDefault(this));
        this.availableSkinsListGUI = new AvailableSkinsListGUI(this.mc, 200, this.height, 36, this.availableSkins);
        this.availableSkinsListGUI.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
        this.availableSkinsListGUI.registerScrollButtons(7, 8);
        //this.field_146967_r = new GuiResourcePackSelected(this.mc, 200, this.height, this.field_146969_h);
        //this.field_146967_r.setSlotXBoundsFromLeft(this.width / 2 + 4);
        //this.field_146967_r.registerScrollButtons(7, 8);
    }

    public boolean hasSkinEntry(SkinListEntry skinListEntry)
    {
        return this.availableSkins.contains(skinListEntry);
    }

    public List probablyToRemove(SkinListEntry skinListEntry)
    {
        return this.hasSkinEntry(skinListEntry) ? this.availableSkins : null;
    }

    public List getAvailableSkins()
    {
        return this.availableSkins;
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            /* Open skin folder in OS file explorer button */
            if (button.id == 2)
            {
                File file1 = new File(OfflineAuth.varInstanceClient.clientSkinsPath);
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
                    if (OfflineAuth.varInstanceClient.skinGuiRenderTicker.getSkinResourceLocation() != null) {
                        try {
                            OfflineAuth.varInstanceClient.skinLocationfield.set(Minecraft.getMinecraft().thePlayer, OfflineAuth.varInstanceClient.skinGuiRenderTicker.getSkinResourceLocation());
                        } catch (IllegalAccessException e) {
                            OfflineAuth.error("Fatal error while applying skin");
                            e.printStackTrace();
                        }
                    }
                    ClientSkinUtil.setLastUsedOfflineSkinName(((SkinListEntry)this.availableSkinsListGUI.skinEntries.get(this.availableSkinsListGUI.selectedIndex)).skinName);
                    this.mc.displayGuiScreen(this.previous);
                    return;
                }

                String skinName = null;
                if (getAvailableSkins().size() > 0 && this.availableSkinsListGUI.selectedIndex >= 0) {
                    skinName = ((SkinListEntry)this.availableSkinsListGUI.skinEntries.get(this.availableSkinsListGUI.selectedIndex)).skinName;
                }
                if (skinName == null) {
                    return;
                }
                byte[] skinBytes = null;
                try {
                    skinBytes = trollogyadherent.offlineauth.util.Util.fileToBytes(ClientSkinUtil.getSkinFile(skinName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (skinBytes == null) {
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
                        throw new RuntimeException(e);
                    }
                }
                try {
                    status = "Uploading...";
                    StatusResponseObject stat = Request.uploadSkin(trollogyadherent.offlineauth.util.Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), oasd.getRestPort(), oasd.getIdentifier(), oasd.getPassword(), skinBytes, clientPubKey, clientPriv);
                    if (stat.getStatusCode() == 200) {
                        status = (char) 167 + "a" + stat.getStatus();
                    } else {
                        status = (char) 167 + "4" + stat.getStatus();
                    }
                } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                         InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                         BadPaddingException | InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
                //this.mc.displayGuiScreen(this.previous);
                /* Done button */
            } else if (button.id == 3) {
                ////this.mc.thePlayer = null;
                this.mc.displayGuiScreen(this.previous);
            }
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.availableSkinsListGUI.func_148179_a(p_73864_1_, p_73864_2_, p_73864_3_);
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
        this.availableSkinsListGUI.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, I18n.format(status), this.width / 2, 16, 16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char c, int k) {
        super.keyTyped(c, k);

        if (k == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.previous);
        }
    }
}
