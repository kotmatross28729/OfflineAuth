package trollogyadherent.offlineauth.asm;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class OfflineAuthLateMixins implements ILateMixinLoader {
	@Override
	public String getMixinConfig() {
		return "mixins.offlineauth.late.json";
	}
	
	@Override
	public List<String> getMixins(Set<String> loadedMods) {
		List<String> mixins = new ArrayList<>();
		
		if (loadedMods.contains("serverutilities")) {
			mixins.add("serverutilities.MixinServerUtilitiesPlayerEventHandler");
		}
		
		if (loadedMods.contains("tabfaces")) {
			mixins.add("tabfaces.MixinClientRegistry"); //Tab menu | Chat : ✅ (dynamic)
			mixins.add("tabfaces.MixinClientUtil"); 	//Server menu     : ✅ (cached)
		}
		
		if (loadedMods.contains("betterquesting")) {
			mixins.add("betterquesting.MixinEntityPlayerPreview");
			mixins.add("betterquesting.MixinPanelPlayerPortrait");
		}
		
		return mixins;
	}
}
