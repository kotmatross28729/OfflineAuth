package trollogyadherent.offlineauth;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigMixins {
	public static boolean blockServerUtilitiesDisplayNameChange = true;
	public static boolean profileCacheOfflineMode = true;
	static final String categoryMixins = "Mixins";
	
	public static void loadMixinConfig(File configFile) {
		Configuration config = new Configuration(configFile);
		
		blockServerUtilitiesDisplayNameChange = config.getBoolean(
				"blockServerUtilitiesDisplayNameChange",
				categoryMixins,
				true,
				"Disables the use of nicknames in the Server Utilities, as changing the display name will result in an actual account \"ban\".");
		
		profileCacheOfflineMode = config.getBoolean(
				"profileCacheOfflineMode",
				categoryMixins,
				true,
				"If server is in offline mode, switches PlayerProfileCache to permanent offline mode (whitelist, ban, etc. will use offline UUID).");
		
		if (config.hasChanged()) {
			config.save();
		}
	}
	
}
