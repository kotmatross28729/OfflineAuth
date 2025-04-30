package trollogyadherent.offlineauth.gui.cmm_compat;

import cpw.mods.fml.common.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.GuiLogin;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.util.Util;

@Optional.Interface(iface = "lumien.custommainmenu.lib.actions.IAction", modid = "CustomMainMenu", striprefs = true)
public class ActionOpenGuiLogin implements lumien.custommainmenu.lib.actions.IAction {
    
    @Optional.Method(modid = "CustomMainMenu")
    @Override
    public void perform(Object source, lumien.custommainmenu.gui.GuiCustom parent) {
        OfflineAuth.debug("Opening auth menu from cmm");
        OAServerData oasd = Util.getOAServerDataByIP(Config.cmmDefaultServerIp, String.valueOf(Config.cmmDefaultServerPort));
        if (oasd == null) {
            OfflineAuth.error("Failed to get Custom Main Menu server data!");
            return;
        }
        OfflineAuth.varInstanceClient.selectedServerData = new ServerData("", Config.cmmDefaultServerIp + ":" + Config.cmmDefaultServerPort);
        Minecraft.getMinecraft().displayGuiScreen(new GuiLogin(Minecraft.getMinecraft().currentScreen));
    }
}
