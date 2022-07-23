package trollogyadherent.offlineauth.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import trollogyadherent.offlineauth.OfflineAuth;
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
import java.util.List;

public class GuiHandler {

    private String validText;
    private int validColor;
    private Thread validator;
    private static int manageAuthButtonId = 420;

    static boolean enabled = true;
    static boolean bold = true;

    //public static ServerData selectedServerData;
    public int selectedOAserverDataRegIndex = -1;
    public String serverdataFetchStatus = "none"; // none, pending, ok

    // Access transformers don't work on stuff already touched by forge, so reflection is needed
    Field btnlst;
    Object reflectedBtnLst = null;

    public GuiHandler() {
        btnlst = ReflectionHelper.findField(net.minecraft.client.gui.GuiScreen.class, "buttonList", "field_146292_n");
        btnlst.setAccessible(true);
    }

    @SubscribeEvent
    public void attach(DrawScreenEvent.Pre e) {
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
                        ex.printStackTrace();
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
        if (e.gui instanceof GuiMultiplayer) {
            //e.buttonList.add(new GuiButton(17325, 270/*5*/, 5, 100, 20, "Server Re-Login"));


            if (!enabled) {
                return;
            }

            validText = "?";
            validColor = Color.GRAY.getRGB();

            reflectedBtnLst = btnlst.get(e.gui);
        }
    }

    @SubscribeEvent
    public void draw(DrawScreenEvent.Post e) {
        if (e.gui instanceof GuiMultiplayer && OfflineAuth.varInstanceClient.selectedServerIndex != -1) {
            String registeredText = I18n.format("offlineauth.registered");
            int registeredTextWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(registeredText.replaceAll("\\P{InBasic_Latin}", "")) + 5;
            String validTextFinal = (bold ? EnumChatFormatting.BOLD : "") + validText;
            int validTextFinalWidth = 15;
            e.gui.drawString(e.gui.mc.fontRenderer, validTextFinal, e.gui.width - validTextFinalWidth, 10, validColor);
            e.gui.drawString(e.gui.mc.fontRenderer, registeredText, e.gui.width - validTextFinalWidth - registeredTextWidth, 10, Color.WHITE.getRGB());

            if (reflectedBtnLst != null) {
                for (Object gb : ((java.util.List) reflectedBtnLst)) {
                    if (((GuiButton) gb).id == manageAuthButtonId) {
                        return;
                    }
                }
                String manage_authText = I18n.format("offlineauth.manage_auth");
                int manage_authButtonWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(manage_authText.replaceAll("\\P{InBasic_Latin}", "")) + 10;
                ((java.util.List) reflectedBtnLst).add(new GuiButton(manageAuthButtonId, e.gui.width - validTextFinalWidth - registeredTextWidth - manage_authButtonWidth - 5, 5, /*80*/ manage_authButtonWidth, 20, manage_authText));
            }
        }
    }

    @SubscribeEvent
    public void action(ActionPerformedEvent.Post e) {
        if ((e.gui instanceof GuiMultiplayer || e.gui instanceof GuiMainMenu) && e.button.id == manageAuthButtonId) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiLogin(Minecraft.getMinecraft().currentScreen));
        }
    }
}
