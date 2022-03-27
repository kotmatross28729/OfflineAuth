package trollogyadherent.offlineauth.skin.client;

import java.io.IOException;
import java.util.ArrayList;

public class ClientSkinRegistry {
    private ArrayList<ClientSkinData> skinDataList;

    public ClientSkinRegistry() {
        skinDataList = new ArrayList<>();
    }

    public ClientSkinData getSkinDataByUUID(String uuid) {
        for (ClientSkinData sd : skinDataList) {
            if (sd.uuid.equals(uuid)) {
                return sd;
            }
        }

        return null;
    }

    public void clear() {
        skinDataList = new ArrayList<>();
    }

    public void deleteByUUID(String uuid) {
        ClientSkinData sd = getSkinDataByUUID(uuid);
        if (sd != null) {
            skinDataList.remove(sd);
        }
    }

    public void add(String uuid, String skinName) throws IOException {
        deleteByUUID(uuid);
        skinDataList.add(new ClientSkinData(uuid, skinName));
    }

    @Override
    public String toString() {
        String res = "[";
        for (ClientSkinData c : this.skinDataList) {
            res += c;
            res += ",";
        }
        res = res.substring(0, res.length() - 1);
        res += "]";
        return res;
    }
}
