package trollogyadherent.offlineauth.registry.newreg;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import trollogyadherent.offlineauth.gui.skin.cape.CapeObject;

import java.util.ArrayList;

public class ClientRegistry {
    private ArrayList<Data> playerEntities;

    public ClientRegistry() {
        this.playerEntities = new ArrayList<>();
    }

    public EntityPlayer getPlayerEntityByDisplayName(String displayName) {
        for (Data data : this.playerEntities) {
            if (data.entityPlayer.getDisplayName().equals(displayName)) {
                return data.entityPlayer;
            }
        }
        return null;
    }

    public String getSkinNameByDisplayName(String displayName) {
        for (Data data : this.playerEntities) {
            if (data.entityPlayer.getDisplayName().equals(displayName)) {
                return data.skinName;
            }
        }
        return null;
    }

    public void insert(String skinName, ResourceLocation skinResourceLocation, EntityPlayer epmp, CapeObject capeObject) {
        if (epmp == null) {
            return;
        }
        if (getPlayerEntityByDisplayName(epmp.getDisplayName()) == null) {
            this.playerEntities.add(new Data(skinName, skinResourceLocation, epmp, capeObject));
        }
    }

    Data getDataByDisplayName(String displayName) {
        for (Data data : this.playerEntities) {
            if (data.entityPlayer.getDisplayName().equals(displayName)) {
                return data;
            }
        }
        return null;
    }

    public void removeByDisplayName(String displayname) {
        Data data = getDataByDisplayName(displayname);
        if (data != null) {
            this.playerEntities.remove(data);
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
        ResourceLocation skinResourceLocation;
        EntityPlayer entityPlayer;
        boolean skinNameIsBeingQueried;
        ResourceLocation tabMenuResourceLocation;
        CapeObject capeObject;

        Data (String skinName, ResourceLocation skinResourceLocation, EntityPlayer entityPlayer, CapeObject capeObject) {
            this.skinName = skinName;
            this.skinResourceLocation = skinResourceLocation;
            this.entityPlayer = entityPlayer;
            this.skinNameIsBeingQueried = false;
            this.capeObject = capeObject;
        }
    }
}
