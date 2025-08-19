package trollogyadherent.offlineauth.mixinHelper.CPM;

import com.tom.cpm.shared.MinecraftClientAccess;
import com.tom.cpm.shared.config.Player;
import com.tom.cpm.shared.definition.ModelDefinitionLoader;
import net.minecraft.entity.player.EntityPlayer;
public class CPMHelper {
	
	@SuppressWarnings("unchecked")
	public static boolean isUsingCPMModel(EntityPlayer player) {
		Player<?> player_CPM = MinecraftClientAccess.get().getDefinitionLoader().loadPlayer(player.getGameProfile(), ModelDefinitionLoader.PLAYER_UNIQUE);
		return player_CPM != null && player_CPM.getModelDefinition0() != null;
	}
}
