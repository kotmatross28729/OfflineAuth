package trollogyadherent.offlineauth.gui;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;

import trollogyadherent.offlineauth.gui.cmm_compat.IActionJoinServerWrapper;
import trollogyadherent.offlineauth.gui.cmm_compat.IActionObjectGuiLoginWrapper;
import trollogyadherent.offlineauth.request.Request;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.rest.ResponseObject;
import trollogyadherent.offlineauth.util.RsaKeyUtil;
import trollogyadherent.offlineauth.util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

@SideOnly(Side.CLIENT)
public class GuiHandler {

    private String validText;
    private int validColor;
    private Thread validator;

    static boolean enabled = true;
    static boolean bold = true;

    // Access transformers don't work on stuff already touched by forge, so reflection is needed
    Field btnlstField;
    Field labelListField;
    Field textField;
    Object reflectedBtnLst = null;
    Object reflectedCMMbuttonList = null;
    Object reflectedCMMlabelList = null;

    public GuiHandler() {
        btnlstField = ReflectionHelper.findField(net.minecraft.client.gui.GuiScreen.class, "buttonList", "field_146292_n");
        btnlstField.setAccessible(true);

        if (Loader.isModLoaded("CustomMainMenu")) {
            labelListField = ReflectionHelper.findField(lumien.custommainmenu.gui.GuiCustom.class, "textLabels");
            labelListField.setAccessible(true);

            textField = ReflectionHelper.findField(lumien.custommainmenu.gui.GuiCustomLabel.class, "text");
            textField.setAccessible(true);
        }
    }

    @SubscribeEvent
    public void attach(DrawScreenEvent.Pre e) {
        /* If we are in the singleplayer world selection menu, set the displayname to the one ths user chose while launching minecraft */
        if (e.gui instanceof GuiSelectWorld) {
            if (OfflineAuth.varInstanceClient.displayNameBeforeServerJoin != null && !Minecraft.getMinecraft().getSession().getUsername().equals(OfflineAuth.varInstanceClient.displayNameBeforeServerJoin)) {
                try {
                    Util.offlineMode(OfflineAuth.varInstanceClient.displayNameBeforeServerJoin);
                    OfflineAuth.debug("Restored displayname: " + OfflineAuth.varInstanceClient.displayNameBeforeServerJoin);
                } catch (IllegalAccessException ex) {
                    OfflineAuth.error("Failed to set back original offline username");
                    ex.printStackTrace();
                }
            }
        }

        /* Injecting functions to appropriately named custom main menu buttons, if the mod is loaded */
        if (Loader.isModLoaded("CustomMainMenu") && (reflectedCMMbuttonList == null && reflectedCMMlabelList == null) && (/*e.gui instanceof lumien.custommainmenu.gui.GuiFakeMain ||*/ e.gui instanceof lumien.custommainmenu.gui.GuiCustom)) {
            try {
                reflectedCMMbuttonList = btnlstField.get(e.gui);
                if (reflectedCMMbuttonList == null || ((java.util.List) reflectedCMMbuttonList).size() == 0) {
                    reflectedCMMbuttonList = null;
                }

                reflectedCMMlabelList = labelListField.get(e.gui);
            } catch (IllegalAccessException ex) {
                OfflineAuth.error("Failed to reflect button or label list");
                ex.printStackTrace();
                return;
            }
            if (reflectedCMMbuttonList != null) {
                for (Object gb : ((java.util.List) reflectedCMMbuttonList)) {
                    if (((GuiButton) gb).id >= 6000) {
                        lumien.custommainmenu.gui.GuiCustomButton gb_ = (lumien.custommainmenu.gui.GuiCustomButton) gb;

                        if (gb_.b.name.equals(Config.cmmGuiLoginButtonName)) {
                            gb_.b.action = (lumien.custommainmenu.lib.actions.IAction) IActionObjectGuiLoginWrapper.getActionOpenGuiLogin();
                        } else if (gb_.b.name.equals(Config.cmmServerJoinButtonName)) {
                            gb_.b.action = (lumien.custommainmenu.lib.actions.IAction) IActionJoinServerWrapper.getActionJoinServer();
                        }
                    }
                }
            }
            if (reflectedCMMlabelList != null) {
                for (Object label : ((java.util.List) reflectedCMMlabelList)) {
                    lumien.custommainmenu.gui.GuiCustomLabel label_ = (lumien.custommainmenu.gui.GuiCustomLabel) label;

                    lumien.custommainmenu.configuration.elements.Text text = null;
                    try {
                        text = (lumien.custommainmenu.configuration.elements.Text) textField.get(label_);
                    } catch (IllegalAccessException ex) {
                        OfflineAuth.error("Failed to reflect label text");
                        ex.printStackTrace();
                        return;
                    }
                    if (text == null) {
                        continue;
                    }
                    if (text.name.equals(Config.cmmGuiLoginButtonName)) {
                        text.action = (lumien.custommainmenu.lib.actions.IAction) IActionObjectGuiLoginWrapper.getActionOpenGuiLogin();
                    } else if (text.name.equals(Config.cmmServerJoinButtonName)) {
                        text.action = (lumien.custommainmenu.lib.actions.IAction) IActionJoinServerWrapper.getActionJoinServer();
                    }
                }
            }
        }

        /* Attaching the "Auth" button to the multiplayer menu */
        if (e.gui instanceof GuiMultiplayer) {
            GuiMultiplayer multiplayerGui = (GuiMultiplayer) e.gui;
            //System.out.println(multiplayerGui);
            if (multiplayerGui.field_146803_h.field_148197_o != OfflineAuth.varInstanceClient.selectedServerIndex) {
                OfflineAuth.varInstanceClient.selectedServerIndex = multiplayerGui.field_146803_h.field_148197_o;
                if (OfflineAuth.varInstanceClient.selectedServerIndex == -1) {
                    return;
                }
                OfflineAuth.varInstanceClient.selectedServerData = ((ServerListEntryNormal) multiplayerGui.field_146803_h.field_148198_l.get(OfflineAuth.varInstanceClient.selectedServerIndex)).field_148301_e;
                //System.out.println("Changed server to " + OfflineAuth.varInstanceClient.selectedServerData.serverName);

                /*try {
                    //reflectedBtnLst = btnlst.get(e.gui);

                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }*/

                OAServerData oasd = Util.getOAServerDatabyIP(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), Util.getPort(OfflineAuth.varInstanceClient.selectedServerData));
                if (oasd != null) {
                    try {
                        Util.offlineMode(oasd.getDisplayName());
                    } catch (IllegalAccessException ex) {
                        OfflineAuth.error("Failed to get server data");
                        ex.printStackTrace();
                        return;
                    }
                }

                validText = "?";
                validColor = Color.GRAY.getRGB();


                OfflineAuth.varInstanceClient.serverStatusVibecheckThread = new Thread(new Runnable() {
                    public void run() {
                        ResponseObject stat = null;
                        try {
                            if (oasd == null) {
                                validText = "\u2718";
                                validColor = Color.RED.getRGB();
                                return;
                            }
                            PublicKey clientPubKey = null;
                            PrivateKey clientPriv = null;
                            if (oasd.isUsingKey()) {
                                clientPubKey = RsaKeyUtil.loadPublicKey(oasd.getPublicKeyPath());
                                clientPriv = RsaKeyUtil.loadPrivateKey(oasd.getPrivateKeyPath());
                            }
                            stat = Request.vibeCheck(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), oasd.getRestPort(), oasd.getIdentifier(), oasd.getDisplayName(), oasd.getPassword(), clientPubKey, clientPriv);
                        } catch (URISyntaxException | IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                                 InvalidAlgorithmParameterException | IllegalBlockSizeException |
                                 NoSuchPaddingException | BadPaddingException | InvalidKeyException e) {
                            setTick(oasd.getIp(), oasd.getPort(), false);
                            //validText = "\u2718";
                            //validColor = Color.RED.getRGB();
                            OfflineAuth.error(e.getMessage());
                            return;
                        } catch (NoSuchProviderException ex) {
                            throw new RuntimeException(ex);
                        }
                        if (stat != null && stat.getStatusCode() == 200) {
                            if (!stat.getDisplayName().equals("-")) {
                                setTick(oasd.getIp(), oasd.getPort(), true);
                                //validText = "\u2714";
                                //validColor = Color.GREEN.getRGB();
                            } else {
                                setTick(oasd.getIp(), oasd.getPort(), false);
                                //validText = "\u2718";
                                //validColor = Color.RED.getRGB();
                            }
                        } else {
                            setTick(oasd.getIp(), oasd.getPort(), false);
                            //validText = "\u2718";
                            //validColor = Color.RED.getRGB();
                        }
                    }
                });

                OfflineAuth.varInstanceClient.serverStatusVibecheckThread.start();
            } else {

            }
        } else if (!(e.gui instanceof ServerKeyAddGUI)){
            OfflineAuth.varInstanceClient.selectedServerIndex = -1;
        }
    }

    private void setTick(String ip, String port, boolean registered) {
        OAServerData oasd = Util.getOAServerDatabyIP(Util.getIP(OfflineAuth.varInstanceClient.selectedServerData), Util.getPort(OfflineAuth.varInstanceClient.selectedServerData));
        if (oasd == null) {
            if (registered) {
                validText = "\u2714";
                validColor = Color.GREEN.getRGB();
            } else {
                validText = "\u2718";
                validColor = Color.RED.getRGB();
            }
            return;
        }
        if (ip.equals(oasd.getIp()) && port.equals(oasd.getPort())) {
            if (registered) {
                validText = "\u2714";
                validColor = Color.GREEN.getRGB();
            } else {
                validText = "\u2718";
                validColor = Color.RED.getRGB();
            }
        }
    }

    @SubscribeEvent
    public void open(InitGuiEvent.Post e) throws IllegalAccessException {
        reflectedCMMbuttonList = null;
        reflectedCMMlabelList = null;
        if (e.gui instanceof GuiMultiplayer) {
            //e.buttonList.add(new GuiButton(17325, 270/*5*/, 5, 100, 20, "Server Re-Login"));


            if (!enabled) {
                return;
            }

            validText = "?";
            validColor = Color.GRAY.getRGB();

            if (btnlstField != null) {
                reflectedBtnLst = btnlstField.get(e.gui);
            }

            /* Backing up the displayname the user chose while launching minecraft */
            OfflineAuth.debug("Backed up displayname: " + Minecraft.getMinecraft().getSession().getUsername());
            OfflineAuth.varInstanceClient.displayNameBeforeServerJoin = Minecraft.getMinecraft().getSession().getUsername();
        }
    }

    @SubscribeEvent
    public void draw(DrawScreenEvent.Post e) {
        if (e.gui instanceof GuiMultiplayer && OfflineAuth.varInstanceClient.selectedServerIndex != -1) {
            String registeredText = I18n.format("offlineauth.registered");
            //int registeredTextWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(registeredText.replaceAll("\\P{InBasic_Latin}", "")) + 5;
            int registeredTextWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(registeredText) + 5;
            String validTextFinal = (bold ? EnumChatFormatting.BOLD : "") + validText;
            int validTextFinalWidth = 15;
            e.gui.drawString(e.gui.mc.fontRenderer, validTextFinal, e.gui.width - validTextFinalWidth, 10, validColor);
            e.gui.drawString(e.gui.mc.fontRenderer, registeredText, e.gui.width - validTextFinalWidth - registeredTextWidth, 10, Color.WHITE.getRGB());

            if (reflectedBtnLst != null) {
                for (Object gb : ((java.util.List) reflectedBtnLst)) {
                    if (((GuiButton) gb).id == Config.manageAuthButtonId) {
                        return;
                    }
                }
                String manage_authText = I18n.format("offlineauth.manage_auth");
                //int manage_authButtonWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(manage_authText.replaceAll("\\P{InBasic_Latin}", "")) + 10;
                int manage_authButtonWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(manage_authText) + 10;
                ((java.util.List) reflectedBtnLst).add(new GuiButton(Config.manageAuthButtonId, e.gui.width - validTextFinalWidth - registeredTextWidth - manage_authButtonWidth - 5, 5, /*80*/ manage_authButtonWidth, 20, manage_authText));
            }
        }
    }

    @SubscribeEvent
    public void action(ActionPerformedEvent.Post e) {
        if ((e.gui instanceof GuiMultiplayer || e.gui instanceof GuiMainMenu) && e.button.id == Config.manageAuthButtonId) {
            OfflineAuth.varInstanceClient.prevWasKeyDialog = false;
            OfflineAuth.varInstanceClient.checkingForKey = false;
            Minecraft.getMinecraft().displayGuiScreen(new GuiLogin(Minecraft.getMinecraft().currentScreen));
        }
    }
}
