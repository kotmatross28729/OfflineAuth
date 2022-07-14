package trollogyadherent.offlineauth.registry;

import trollogyadherent.offlineauth.registry.data.ClientPlayerData;

import java.io.IOException;
import java.util.ArrayList;

public class ClientPlayerRegistry {
    private ArrayList<ClientPlayerData> playerDataList;

    public ClientPlayerRegistry() {
        playerDataList = new ArrayList<>();
    }

    public ClientPlayerData getPlayerDataByIdentifier(String identifier) {
        for (ClientPlayerData sd : playerDataList) {
            if (sd.identifier.equals(identifier)) {
                return sd;
            }
        }

        return null;
    }

    public ClientPlayerData getPlayerDataByDisplayName(String displayname) {
        return getPlayerDataByIdentifier(getIdentifierFromDisplayName(displayname));
    }

    public String getIdentifierFromDisplayName(String displayName) {
        for (ClientPlayerData spd : this.playerDataList) {
            if (spd.displayname.equals(displayName)) {
                return spd.identifier;
            }
        }

        return null;
    }

    public void clear() {
        playerDataList = new ArrayList<>();
    }

    public void deleteByIdentifier(String identifier) {
        ClientPlayerData sd = getPlayerDataByIdentifier(identifier);
        if (sd != null) {
            playerDataList.remove(sd);
        }
    }

    public void deleteByDisplayName(String displayname) {
        deleteByIdentifier(getIdentifierFromDisplayName(displayname));
    }

    public void add(String identifier, String displayname, String uuid, String skinName) throws IOException {
        deleteByIdentifier(uuid);
        playerDataList.add(new ClientPlayerData(identifier, displayname, uuid, skinName));
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
