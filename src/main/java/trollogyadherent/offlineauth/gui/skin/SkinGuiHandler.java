package trollogyadherent.offlineauth.gui.skin;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.util.ClientUtil;

import java.lang.reflect.Field;
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
                        ((List) reflectedBtnLst).add(new GuiButton(69, e.gui.width - 85, 5, 80, 20, I18n.format("offlineauth.set_skin")));
                    } else {
                        ((List) reflectedBtnLst).add(new GuiButton(69, e.gui.width - 85, 5, 80, 20, I18n.format("offlineauth.upload_skin")));
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

    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Specials.Pre e) {
        if (!(Minecraft.getMinecraft().currentScreen instanceof SkinManagmentGUI)) {
            return;
        }
        // some voodoo that happens in the minecraft setHideCape function
        byte b0 = SkinGuiRenderTicker.clientPlayerMP.dataWatcher.getWatchableObjectByte(16);
        SkinGuiRenderTicker.clientPlayerMP.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 1 << /*p_82239_1_*/ 1)));
        if (!((SkinManagmentGUI)Minecraft.getMinecraft().currentScreen).capeCheckbox.isChecked()) {
            return;
        }
        if (((SkinManagmentGUI)Minecraft.getMinecraft().currentScreen).elytraCheckbox != null && ((SkinManagmentGUI)Minecraft.getMinecraft().currentScreen).elytraCheckbox.isChecked() && OfflineAuth.varInstanceClient.skinGuiRenderTicker.getCapeObject() != null) {
            OfflineAuth.varInstanceClient.skinGuiRenderTicker.getCapeObject().getCurrentFrame(e.partialRenderTick);

            // some voodoo that happens in the minecraft setHideCape function
            //byte b0 = SkinGuiRenderTicker.clientPlayerMP.dataWatcher.getWatchableObjectByte(16);
            //SkinGuiRenderTicker.clientPlayerMP.dataWatcher.updateObject(16, Byte.valueOf((byte)(b0 | 1 << /*p_82239_1_*/ 1)));


            return;
        }
        if (OfflineAuth.varInstanceClient.skinGuiRenderTicker.getCapeObject() == null) {
            return;
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(OfflineAuth.varInstanceClient.skinGuiRenderTicker.getCapeObject().getCurrentFrame(e.partialRenderTick));
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 0.125F);
        GL11.glRotatef(SkinGuiRenderTicker.angle, SkinGuiRenderTicker.x, SkinGuiRenderTicker.y, SkinGuiRenderTicker.z);
        e.renderer.modelBipedMain.renderCloak(0.0625F);
        GL11.glPopMatrix();
    }
}
