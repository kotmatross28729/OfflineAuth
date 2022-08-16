package trollogyadherent.offlineauth.gui;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;

@SideOnly(Side.CLIENT)
class ConfigGUI extends GuiConfig {

    private static IConfigElement ce = new ConfigElement(Config.config.getCategory(Config.Categories.generalClient));
    private static IConfigElement ceCMM = new ConfigElement(Config.config.getCategory(Config.Categories.customMainMenuClient));

    ConfigGUI(GuiScreen parent) {
        super(parent, ImmutableList.of(ce, ceCMM), "offlineauth", "offlineauth", false, false, "Config for OfflineAuth", OfflineAuth.confFile.getAbsolutePath());
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        OfflineAuth.debug("Config button id " + b.id + " pressed");
        super.actionPerformed(b);
        /* "Done" button */
        if (b.id == 2000) {
            //Config.config.save();
            Config.synchronizeConfigurationClient(OfflineAuth.confFile, true, false);
            //Config.config.save();
            //Config.config.load();
        }
    }
}
