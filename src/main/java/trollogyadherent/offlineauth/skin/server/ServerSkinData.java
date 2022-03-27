package trollogyadherent.offlineauth.skin.server;

public class ServerSkinData {
    public String uuid;
    public String skinName;

    public ServerSkinData(String uuid, String skinName) {
        this.uuid = uuid;
        this.skinName = skinName;
    }

    @Override
    public String toString() {
        return this.uuid + ":" + this.skinName;
    }
}
