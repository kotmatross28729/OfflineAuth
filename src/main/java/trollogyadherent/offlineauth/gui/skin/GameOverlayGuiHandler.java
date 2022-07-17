package trollogyadherent.offlineauth.gui.skin;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import trollogyadherent.offlineauth.util.ClientUtil;

import java.lang.reflect.Field;
import java.util.List;

public class GameOverlayGuiHandler {

    // Access transformers don't work on stuff already touched by forge, so reflection is needed
    Field btnlst;
    Object reflectedBtnLst = null;

    public GameOverlayGuiHandler() {
        //btnlst = ReflectionHelper.findField(GuiIngameForge.class, "buttonList", "field_146292_n");
        //btnlst.setAccessible(true);
    }

    @SubscribeEvent
    public void attach(DrawScreenEvent.Pre e) {
        //if (e.gui instanceof GuiOptions) {}
    }

    @SubscribeEvent
    public void open(RenderGameOverlayEvent.Pre e) throws IllegalAccessException {
        System.out.println(Minecraft.getMinecraft().currentScreen);
        //if () {
            //reflectedBtnLst = btnlst.get(e.gui);
        //}
    }

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

    // making a skin stealer?? or maybe teleport requests?
    @SubscribeEvent
    public void action(ActionPerformedEvent.Post e) {
        /*
        if (e.gui instanceof GuiOptions && e.button.id == 69) {
            Minecraft.getMinecraft().displayGuiScreen(new SkinManagmentGUI(Minecraft.getMinecraft().currentScreen));
        }
        */
    }
}
