package trollogyadherent.offlineauth.registry.newreg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.skin.cape.CapeObject;

import java.util.ArrayList;

public class ClientRegistry {
    private ArrayList<Data> playerEntities;

    public ClientRegistry() {
        this.playerEntities = new ArrayList<>();
    }

    public EntityPlayer getPlayerEntityByDisplayName(String displayName) {
        for (Data data : this.playerEntities) {
            if (data.displayName.equals(displayName)) {
                return data.entityPlayer;
            }
        }
        return null;
    }

    public void setEntityPlayer(String displayName, EntityPlayer entityPlayer) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return;
        }
        data.entityPlayer = entityPlayer;
    }

    public String getSkinNameByDisplayName(String displayName) {
        for (Data data : this.playerEntities) {
            if (data.displayName.equals(displayName)) {
                return data.skinName;
            }
        }
        return null;
    }

    public void insert(String skinName, ResourceLocation skinResourceLocation, EntityPlayer epmp, CapeObject capeObject, String displayName) {
        if (epmp == null) {
            //return;
        }
        if (getPlayerEntityByDisplayName(displayName) == null) {
            this.playerEntities.add(new Data(skinName, skinResourceLocation, epmp, capeObject, displayName));
        } else if (getDataByDisplayName(displayName) != null) {
            if (skinName != null)
                setSkinName(displayName, skinName);
            if (skinResourceLocation != null)
                setResourceLocation(displayName, skinResourceLocation);
            if (epmp != null)
                setEntityPlayer(displayName, epmp);
            if (capeObject != null)
                setCapeObject(displayName, capeObject);
        }
    }

    public Data getDataByDisplayName(String displayName) {
        for (Data data : this.playerEntities) {
            if (data.displayName.equals(displayName)) {
                return data;
            }
        }
        return null;
    }

    public void removeByDisplayName(String displayname) {
        OfflineAuth.debug("(removeByDisplayName): displayname: " + displayname);
        Data data = getDataByDisplayName(displayname);
        if (data != null) {
            OfflineAuth.debug("Data found, removing");
            this.playerEntities.remove(data);
        } else {
            OfflineAuth.debug("Data not found, not removing");
        }
    }

    public void setSkinName(String displayName, String skinName) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return;
        }
        data.skinName = skinName;
    }

    public void setResourceLocation(String displayName, ResourceLocation skinResourceLocation) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return;
        }
        data.skinResourceLocation = skinResourceLocation;
    }

    public ResourceLocation getResourceLocation(String displayName) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return null;
        }
        return data.skinResourceLocation;
    }

    public void setSkinNameIsBeingQueried(String displayName, boolean status) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return;
        }
        data.skinNameIsBeingQueried = status;
    }

    public boolean skinNameIsBeingQueried(String displayName) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return false;
        }
        return data.skinNameIsBeingQueried;
    }

    public void setTabMenuResourceLocation(String displayName, ResourceLocation tabMenuResourceLocation) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return;
        }
        data.tabMenuResourceLocation = tabMenuResourceLocation;
    }

    public ResourceLocation getTabMenuResourceLocation(String displayName) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return null;
        }
        return data.tabMenuResourceLocation;
    }

    public CapeObject getCapeObject(String displayName) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return null;
        }
        return data.capeObject;
    }

    public void setCapeObject(String displayName, CapeObject capeObject) {
        Data data = getDataByDisplayName(displayName);
        if (data == null) {
            return;
        }
        data.capeObject = capeObject;
    }

    public void clear() {
        this.playerEntities = new ArrayList<>();
    }

    private class Data {
        String skinName;
        String displayName;
        ResourceLocation skinResourceLocation;
        EntityPlayer entityPlayer;
        boolean skinNameIsBeingQueried;
        ResourceLocation tabMenuResourceLocation;
        CapeObject capeObject;

        Data (String skinName, ResourceLocation skinResourceLocation, EntityPlayer entityPlayer, CapeObject capeObject, String displayName) {
            this.skinName = skinName;
            this.skinResourceLocation = skinResourceLocation;
            this.entityPlayer = entityPlayer;
            this.skinNameIsBeingQueried = false;
            this.capeObject = capeObject;
            this.displayName = displayName;
        }
    }
}
