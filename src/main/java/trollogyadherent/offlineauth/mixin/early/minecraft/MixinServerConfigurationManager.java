package trollogyadherent.offlineauth.mixin.early.minecraft;

import net.minecraft.server.management.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(value = ServerConfigurationManager.class, priority = 999)
public class MixinServerConfigurationManager {
	//TODO: todo
}
