package trollogyadherent.offlineauth.gui.cmm_compat;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.CmmErrorConfirmationGUI;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.util.Util;

import java.awt.Color;

@Optional.Interface(iface = "lumien.custommainmenu.lib.actions.IAction", modid = "CustomMainMenu", striprefs = true)
public class ActionJoinServer implements lumien.custommainmenu.lib.actions.IAction {
    
    @Optional.Method(modid = "CustomMainMenu")
    @Override
    public void perform(Object source, lumien.custommainmenu.gui.GuiCustom parent) {
        OfflineAuth.debug("Joining server from cmm");
        OAServerData oasd = Util.getOAServerDatabyIP(Config.cmmDefaultServerIp, String.valueOf(Config.cmmDefaultServerPort));
        if (oasd == null) {
            OfflineAuth.error("Failed to get Custom Main Menu server data!");
            Minecraft.getMinecraft().displayGuiScreen(new CmmErrorConfirmationGUI(Minecraft.getMinecraft().currentScreen, "offlineauth.confirm_text.cmm_no_data", "offlineauth.confirm.ok", Color.RED.getRGB()));
            return;
        }
        if (oasd.getDisplayName().length() == 0) {
            OfflineAuth.error("Displayname cannot be empty!");
            Minecraft.getMinecraft().displayGuiScreen(new CmmErrorConfirmationGUI(Minecraft.getMinecraft().currentScreen, "offlineauth.confirm_text.displayname_empty", "offlineauth.confirm.ok", Color.RED.getRGB()));
            return;
        }

        /* Backing up the displayname the user chose while launching minecraft */
        OfflineAuth.debug("Backed up displayname: " + Minecraft.getMinecraft().getSession().getUsername());
        OfflineAuth.varInstanceClient.displayNameBeforeServerJoin = Minecraft.getMinecraft().getSession().getUsername();

        /* Setting new displayname, according to stored server credentials */
        try {
            Util.offlineMode(oasd.getDisplayName());
        } catch (IllegalAccessException ex) {
            OfflineAuth.error("Failed to get server data");
            Minecraft.getMinecraft().displayGuiScreen(new CmmErrorConfirmationGUI(Minecraft.getMinecraft().currentScreen, "offlineauth.confirm_text.no_server_data", "offlineauth.confirm.ok", Color.RED.getRGB()));
            ex.printStackTrace();
            return;
        }

        /* Setting server as "current" (necessary for further auth) */
        OfflineAuth.varInstanceClient.selectedServerData = new ServerData("", Config.cmmDefaultServerIp + ":" + Config.cmmDefaultServerPort);

        /* Getting server address and connecting */
        ServerData sd = new ServerData("", Config.cmmDefaultServerIp + ":" + Config.cmmDefaultServerPort);
        FMLClientHandler.instance().setupServerList();
        FMLClientHandler.instance().connectToServer(parent, sd);
    }
}
