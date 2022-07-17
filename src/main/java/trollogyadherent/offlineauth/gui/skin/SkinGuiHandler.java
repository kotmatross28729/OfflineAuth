package trollogyadherent.offlineauth.gui.skin;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.GuiLogin;
import trollogyadherent.offlineauth.gui.ServerKeyAddGUI;
import trollogyadherent.offlineauth.request.Request;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.rest.ResponseObject;
import trollogyadherent.offlineauth.util.ClientUtil;
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

public class SkinGuiHandler {

    // Access transformers don't work on stuff already touched by forge, so reflection is needed
    Field btnlst;
    Object reflectedBtnLst = null;

    public SkinGuiHandler() {
        btnlst = ReflectionHelper.findField(net.minecraft.client.gui.GuiScreen.class, "buttonList", "field_146292_n");
        btnlst.setAccessible(true);
    }

    @SubscribeEvent
    public void attach(DrawScreenEvent.Pre e) {
        if (e.gui instanceof GuiOptions) {
        }
    }

    @SubscribeEvent
    public void open(InitGuiEvent.Post e) throws IllegalAccessException {
        if (e.gui instanceof GuiOptions) {
            reflectedBtnLst = btnlst.get(e.gui);
        }
    }

    /*@SubscribeEvent
    public void open2(InitGuiEvent.Post e) throws IllegalAccessException {
        if (e.gui instanceof GuiMainMenu) {
            //e.buttonList.add(new GuiButton(17325, 270, 5, 100, 20, "Server Re-Login"));
            Minecraft.getMinecraft().thePlayer = null;
        }
    }*/

    @SubscribeEvent
    public void draw(DrawScreenEvent.Post e) {
        if (e.gui instanceof GuiOptions) {

            if (reflectedBtnLst != null) {
                for (Object gb : ((List) reflectedBtnLst)) {
                    if (((GuiButton) gb).id == 69) {
                        return;
                    }
                }
                if (Minecraft.getMinecraft().getNetHandler() != null) {
                    if (ClientUtil.isSinglePlayer()) {
                        ((List) reflectedBtnLst).add(new GuiButton(69, e.gui.width - 85, 5, 80, 20, "Set Skin"));
                    } else {
                        ((List) reflectedBtnLst).add(new GuiButton(69, e.gui.width - 85, 5, 80, 20, "Upload Skin"));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void action(ActionPerformedEvent.Post e) {
        if (e.gui instanceof GuiOptions && e.button.id == 69) {
            Minecraft.getMinecraft().displayGuiScreen(new SkinManagmentGUI(Minecraft.getMinecraft().currentScreen));
        }
    }
}
