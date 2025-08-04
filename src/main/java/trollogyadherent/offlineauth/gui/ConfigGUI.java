package trollogyadherent.offlineauth.gui;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.clientdata.ClientData;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.util.Util;

public class ConfigGUI extends GuiConfig {

    private static IConfigElement ceClient = new ConfigElement(Config.config.getCategory(Config.Categories.generalClient));
    private static IConfigElement ceCommon = new ConfigElement(Config.config.getCategory(Config.Categories.generalCommon));
    private static IConfigElement ceCMM = new ConfigElement(Config.config.getCategory(Config.Categories.customMainMenuClient));

    public ConfigGUI(GuiScreen parent) {
        //this.parentScreen = parent;
        super(parent, ImmutableList.of(ceClient, ceCommon, ceCMM), "offlineauth", "offlineauth", false, false, I18n.format("offlineauth.configgui.title"), OfflineAuth.confFile.getAbsolutePath());
        OfflineAuth.debug("Instantiating config gui");
    }

    @Override
    public void initGui()
    {
        // You can add buttons and initialize fields here
        super.initGui();
        OfflineAuth.debug("Initializing config gui");
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        // You can do things like create animations, draw additional elements, etc. here
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        OfflineAuth.debug("Config button id " + b.id + " pressed");
        super.actionPerformed(b);
        /* "Done" button */
        if (b.id == 2000) {
            /* Syncing config */
            Config.synchronizeConfigurationClient(true, false);

            /* Adding CMM server data if it is new */
            if (Util.getOAServerDataByIP(Config.cmmDefaultServerIp, String.valueOf(Config.cmmDefaultServerPort)) == null) {
                OfflineAuth.varInstanceClient.OAServerDataCache.add(new OAServerData(Config.cmmDefaultServerIp, String.valueOf(Config.cmmDefaultServerPort), String.valueOf(Config.cmmDefaultAuthPort), "", "", "", false, "", ""));
                ClientData.saveData();
            }
        }
    }

}
