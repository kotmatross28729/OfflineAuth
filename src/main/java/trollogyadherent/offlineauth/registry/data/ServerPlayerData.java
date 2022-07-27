package trollogyadherent.offlineauth.registry.data;

public class ServerPlayerData {
    public String identifier;
    public String displayname;
    public String uuid;
    public String skinName;
    public boolean hasCape;

    public ServerPlayerData(String identifier, String displayname, String uuid, String skinName, boolean hasCape) {
        this.identifier = identifier;
        this.displayname = displayname;
        this.uuid = uuid;
        this.skinName = skinName;
        this.hasCape = hasCape;
    }

    @Override
    public String toString() {
        return this.identifier + ":" + this.skinName + ":" + this.hasCape;
    }
}
