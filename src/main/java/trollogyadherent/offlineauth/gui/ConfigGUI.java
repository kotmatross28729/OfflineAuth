package trollogyadherent.offlineauth.gui;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import trollogyadherent.offlineauth.Config;

class ConfigGUI extends GuiConfig {

    private static IConfigElement ce = new ConfigElement(Config.config.getCategory(Config.Categories.generalClient));

    ConfigGUI(GuiScreen parent) {
        super(parent, ImmutableList.of(ce), "offlineauth", "offlineauth", false, false, "Config for ReAuth", "");
    }

}
