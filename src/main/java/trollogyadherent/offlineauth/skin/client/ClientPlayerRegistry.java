package trollogyadherent.offlineauth.skin.client;

import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.util.Util;

import java.util.ArrayList;

public class ClientPlayerRegistry {
    private ArrayList<ClientPlayerData> playerDataList;

    public ClientPlayerRegistry() {
        this.playerDataList = new ArrayList<>();
    }

    public void add(ClientPlayerData clientPlayerData) {
        if (getByUUID(Util.offlineUUID(clientPlayerData.entityPlayer.getDisplayName())) != null) {
            //OfflineAuth.error("Error adding clientPlayerData to clientPlayerRegistry, duplicate entry with uuid " + Util.offlineUUID(clientPlayerData.entityPlayer.getDisplayName()));
            //return;
            deleteByUUID(Util.offlineUUID(clientPlayerData.entityPlayer.getDisplayName()));
        }
        this.playerDataList.add(clientPlayerData);
    }

    public ClientPlayerData getByUUID(String uuid) {
        for (ClientPlayerData c : this.playerDataList) {
            if (Util.offlineUUID(c.entityPlayer.getDisplayName()).equals(uuid)) {
                return c;
            }
        }
        return null;
    }

    public void deleteByUUID(String uuid) {
        ClientPlayerData c = getByUUID(uuid);
        if (c != null) {
            this.playerDataList.remove(c);
        }
    }

    public void clear() {
        this.playerDataList = new ArrayList<>();
    }

    @Override
    public String toString() {
        String res = "[";
        for (ClientPlayerData c : this.playerDataList) {
            res += c;
            res += ",";
        }
        res = res.substring(0, res.length() - 1);
        res += "]";
        return res;
    }
}
