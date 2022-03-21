package trollogyadherent.offlineauth.gui;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.request.Request;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.rest.ResponseObject;
import trollogyadherent.offlineauth.rest.StatusResponseObject;
import trollogyadherent.offlineauth.util.Util;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class GuiHandler {

    private String validText;
    private int validColor;
    private Thread validator;

    static boolean enabled = true;
    static boolean bold = true;

    public static int selectedServerIndex = -1;
    //public static ServerData selectedServerData;
    public int selectedOAserverDataRegIndex = -1;
    public String serverdataFetchStatus = "none"; // none, pending, ok

    // Access transformers don't work on stuff already touched by forge, so reflection is needed
    Field btnlst;

    public GuiHandler() {
        btnlst = ReflectionHelper.findField(net.minecraft.client.gui.GuiScreen.class, "buttonList", "field_146292_n");
        btnlst.setAccessible(true);
    }

    @SubscribeEvent
    public void attach(DrawScreenEvent.Pre e) throws IOException {
        if (e.gui instanceof GuiMultiplayer) {
            GuiMultiplayer multiplayerGui = (GuiMultiplayer) e.gui;
            //System.out.println(multiplayerGui);
            if (multiplayerGui.field_146803_h.field_148197_o != selectedServerIndex) {
                selectedServerIndex = multiplayerGui.field_146803_h.field_148197_o;
                OfflineAuth.selectedServerData = ((ServerListEntryNormal) multiplayerGui.field_146803_h.field_148198_l.get(selectedServerIndex)).field_148301_e;
                System.out.println("Changed server to " + OfflineAuth.selectedServerData.serverName);
                Object hmm = null;
                try {
                    hmm = btnlst.get(e.gui);
                    ((java.util.List) hmm).add(new GuiButton(17325, 260/*5*/, 5, 80, 20, "Manage Auth"));
                } catch (IllegalAccessException ex) {
                    ex.printStackTrace();
                }

                OAServerData oasd = Util.getOAServerDatabyIP(Util.getIP(OfflineAuth.selectedServerData), Util.getPort(OfflineAuth.selectedServerData));
                if (oasd != null) {
                    try {
                        Util.offlineMode(oasd.getUsername());
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }

                validText = "?";
                validColor = Color.GRAY.getRGB();

                Thread vibeCheckThread = new Thread(new Runnable() {
                    public void run() {
                        ResponseObject stat = null;
                        try {
                            if (oasd == null) {
                                validText = "\u2718";
                                validColor = Color.RED.getRGB();
                                return;
                            }
                            stat = Request.vibeCheck(Util.getIP(OfflineAuth.selectedServerData), oasd.getRestPort(), oasd.getUsername(), oasd.getPassword());
                        } catch (URISyntaxException e) {
                            validText = "\u2718";
                            validColor = Color.RED.getRGB();
                            OfflineAuth.error(e.getMessage());
                            return;
                        }
                        if (stat != null && stat.getStatusCode() == 200) {
                            if (stat.isValidUser()) {
                                validText = "\u2714";
                                validColor = Color.GREEN.getRGB();
                            } else {
                                validText = "\u2718";
                                validColor = Color.RED.getRGB();
                            }
                        } else {
                            validText = "\u2718";
                            validColor = Color.RED.getRGB();
                        }
                    }
                });
                vibeCheckThread.start();
            } else {

            }
        }
    }

    @SubscribeEvent
    public void open(InitGuiEvent.Post e) {
        if (e.gui instanceof GuiMultiplayer) {
            //e.buttonList.add(new GuiButton(17325, 270/*5*/, 5, 100, 20, "Server Re-Login"));

            selectedServerIndex = -1;

            if (!enabled)
                return;

            validText = "?";
            validColor = Color.GRAY.getRGB();

            //validText = "\u2714";
            //validColor = Color.GREEN.getRGB();
            //validText = "\u2718";
            //validColor = Color.RED.getRGB();

        }
    }

    @SubscribeEvent
    public void draw(DrawScreenEvent.Post e) {
        if (e.gui instanceof GuiMultiplayer && selectedServerIndex != -1) {
            e.gui.drawString(e.gui.mc.fontRenderer, "Registered:", 350, 10, Color.WHITE.getRGB());
            e.gui.drawString(e.gui.mc.fontRenderer, (bold ? EnumChatFormatting.BOLD : "") + validText, 410, 10, validColor);
        }
    }

    @SubscribeEvent
    public void action(ActionPerformedEvent.Post e) {
        if ((e.gui instanceof GuiMultiplayer || e.gui instanceof GuiMainMenu) && e.button.id == 17325) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiLogin(Minecraft.getMinecraft().currentScreen));
        }
    }
}
