package trollogyadherent.offlineauth.database;

public class PlayerData {
    String username;
    String passwordHash;
    String salt;
    String skinBase64;

    public PlayerData(String username, String passwordHash, String salt, String skinBase64) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.skinBase64 = skinBase64;
    }

    public PlayerData(String data) {
        String[] splitData = data.split(",");
        username = splitData[0];
        passwordHash = splitData[1];
        salt = splitData[2];
        skinBase64 = splitData[3];
    }

    public String getUsername() {
        return username;
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
}
