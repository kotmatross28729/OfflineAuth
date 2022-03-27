package trollogyadherent.offlineauth.skin.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import trollogyadherent.offlineauth.util.Util;

public class ClientPlayerData {
    public AbstractClientPlayer entityPlayer;

    public ClientPlayerData(AbstractClientPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }

    @Override
    public String toString() {
        return this.entityPlayer.getDisplayName() + ":" + Util.offlineUUID(this.entityPlayer.getDisplayName());
    }
}
