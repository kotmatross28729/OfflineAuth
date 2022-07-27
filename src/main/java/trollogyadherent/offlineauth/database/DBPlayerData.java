package trollogyadherent.offlineauth.database;

public class DBPlayerData {
    String identifier;
    String displayname;
    String uuid;
    String passwordHash;
    String salt;
    byte[] skinBytes;
    byte[] capeBytes;
    String publicKey;

    public DBPlayerData(String identifier, String displayname, String passwordHash, String salt, String uuid, String publicKey, byte[] skinBytes, byte[] capeBytes) {
        this.identifier = identifier;
        this.displayname = displayname;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.uuid = uuid;
        this.publicKey = publicKey;
        this.skinBytes = skinBytes;
        this.capeBytes = capeBytes;
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

    public byte[] getSkinBytes() {
        return skinBytes;
    }
    public byte[] getCapeBytes() {
        return capeBytes;
    }

    public String getDisplayname() { return displayname; }

    public String getUuid() { return uuid; }

    public String getPublicKey() {
        return publicKey;
    }
}
