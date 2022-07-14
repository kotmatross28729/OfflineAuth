package trollogyadherent.offlineauth.registry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.ArrayList;

public class ClientEntityPlayerRegistry {
    private ArrayList<EntityPlayer> playerEntities;

    public ClientEntityPlayerRegistry() {
        this.playerEntities = new ArrayList<>();
    }

    public EntityPlayer getPlayerEntityByDisplayName(String displayname) {
        for (EntityPlayer epmp : this.playerEntities) {
            if (epmp.getDisplayName().equals(displayname)) {
                return epmp;
            }
        }
        return null;
    }

    public void add(EntityPlayer epmp) {
        if (getPlayerEntityByDisplayName(epmp.getDisplayName()) == null) {
            this.playerEntities.add(epmp);
        }
    }

    public void removeByDisplayName(String displayname) {
        EntityPlayer epmp = getPlayerEntityByDisplayName(displayname);
        if (epmp != null) {
            this.playerEntities.remove(epmp);
        }
    }

    public void clear() {
        this.playerEntities = new ArrayList<>();
    }
}
