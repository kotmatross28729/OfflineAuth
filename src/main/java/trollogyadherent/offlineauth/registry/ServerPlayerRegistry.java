package trollogyadherent.offlineauth.registry;

import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.registry.data.ServerPlayerData;

import java.util.ArrayList;

public class ServerPlayerRegistry {
    private ArrayList<ServerPlayerData> playerDataList;

    public ServerPlayerRegistry() {
        playerDataList = new ArrayList<>();
    }

    public ServerPlayerData getPlayerDataByIdentifier(String identifier) {
        for (ServerPlayerData sd : playerDataList) {
            if (sd.identifier.equals(identifier)) {
                return sd;
            }
        }

        return null;
    }

    public ServerPlayerData getPlayerDataByDisplayName(String displayname) {
        return getPlayerDataByIdentifier(getIdentifierFromDisplayName(displayname));
    }

    public void clear() {
        playerDataList = new ArrayList<>();
    }

    public void deleteByIdentifier(String identifier) {
        ServerPlayerData sd = getPlayerDataByIdentifier(identifier);
        if (sd != null) {
            playerDataList.remove(sd);
        }
    }

    public void deleteByDisplayName(String displayname) {
        deleteByIdentifier(getIdentifierFromDisplayName(displayname));
    }

    public void add(ServerPlayerData spd) {
        deleteByIdentifier(spd.identifier);
        playerDataList.add(spd);
    }

    public void setSkin(String displayName, String skinName) {
        ServerPlayerData spd = getPlayerDataByDisplayName(displayName);
        if (spd == null) {
            return;
        }
        spd.skinName = skinName;
    }

    @Override
    public String toString() {
        if (this.playerDataList.size() == 0) {
            return "[empty]";
        }

        String res = "[";
        for (ServerPlayerData c : this.playerDataList) {
            res += c;
            res += ",";
        }
        res = res.substring(0, res.length() - 1);
        res += "]";
        return res;
    }

    public String getIdentifierFromDisplayName(String displayName) {
        for (ServerPlayerData spd : this.playerDataList) {
            if (spd.displayname.equals(displayName)) {
                return spd.identifier;
            }
        }

        return null;
    }
}
