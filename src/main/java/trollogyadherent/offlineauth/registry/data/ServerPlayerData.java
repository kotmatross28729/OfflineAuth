package trollogyadherent.offlineauth.registry.data;

public class ServerPlayerData {
    public String identifier;
    public String displayname;
    public String uuid;
    public String skinName;

    public ServerPlayerData(String identifier, String displayname, String uuid, String skinName) {
        this.identifier = identifier;
        this.displayname = displayname;
        this.uuid = uuid;
        this.skinName = skinName;
    }

    @Override
    public String toString() {
        return this.identifier + ":" + this.displayname + ":" + this.skinName;
    }
}
