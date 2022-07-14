package trollogyadherent.offlineauth.database;

public class DBPlayerData {
    String identifier;
    String displayname;
    String uuid;
    String passwordHash;
    String salt;
    String skinBase64;
    String publicKey;

    public DBPlayerData(String identifier, String displayname, String passwordHash, String salt, String skinBase64, String publicKey) {
        this.identifier = identifier;
        this.displayname = displayname;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.skinBase64 = skinBase64;
        this.publicKey = publicKey;
    }

    public DBPlayerData(String data) {
        String[] splitData = data.split(",");
        identifier = splitData[0];
        displayname = splitData[1];
        uuid = splitData[2];
        passwordHash = splitData[3];
        salt = splitData[4];
        skinBase64 = splitData[5];
        if (splitData.length == 7) {
            publicKey = splitData[6];
        } else {
            publicKey = "";
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public String getSkinBase64() {
        return skinBase64;
    }

    public String getDisplayname() { return displayname; }

    public String getUuid() { return uuid; }

    public String getPublicKey() {
        return publicKey;
    }
}
