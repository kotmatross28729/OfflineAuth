package trollogyadherent.offlineauth.skin.server;

import java.util.ArrayList;

public class ServerSkinRegistry {
    private ArrayList<ServerSkinData> skinDataList;

    public ServerSkinRegistry() {
        skinDataList = new ArrayList<>();
    }

    public ServerSkinData getSkinDataByUUID(String uuid) {
        for (ServerSkinData sd : skinDataList) {
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
        ServerSkinData sd = getSkinDataByUUID(uuid);
        if (sd != null) {
            skinDataList.remove(sd);
        }
    }

    public void add(String uuid, String skinName) {
        deleteByUUID(uuid);
        skinDataList.add(new ServerSkinData(uuid, skinName));
    }

    @Override
    public String toString() {
        String res = "[";
        for (ServerSkinData c : this.skinDataList) {
            res += c;
            res += ",";
        }
        res = res.substring(0, res.length() - 1);
        res += "]";
        return res;
    }
}
