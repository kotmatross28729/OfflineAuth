package trollogyadherent.offlineauth.asm;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.Name("OfflineAuthEarlyMixins")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class OfflineAuthEarlyMixins implements IFMLLoadingPlugin, IEarlyMixinLoader {
	
	@Override
	public String getMixinConfig() {
		return "mixins.offlineauth.early.json";
	}
	
	@Override
	public List<String> getMixins(Set<String> loadedCoreMods) {
		List<String> mixins = new ArrayList<>();
		
		if (loadedCoreMods.contains("serverutils.core.ServerUtilitiesCore")) {
			mixins.add("serverutilities.MixinPlayerHeadIcon");
		}
		
		return mixins;
	}
	
	@Override
	public String[] getASMTransformerClass() {
		return null;
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
		
	}
	
	@Override
	public String getAccessTransformerClass() {
		return null;
	}
		
}
