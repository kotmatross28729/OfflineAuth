package trollogyadherent.offlineauth.request.objects;

public class RegisterRequestBodyObject {
    private String identifier;
    private String displayname;
    private String password;
    private String uuid;
    private String token;
    private String pubKey;

    public RegisterRequestBodyObject(String identifier, String displayname, String password, String uuid, String token, String pubKey) {
        this.identifier = identifier;
        this.displayname = displayname;
        this.password = password;
        this.uuid = uuid;
        this.token = token;
        this.pubKey = pubKey;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayname() {
        return displayname;
    }

    public String getPassword() {
        return password;
    }

    public String getUuid() {
        return uuid;
    }

    public String getToken() {
        return token;
    }

    public String getPubKey() {
        return pubKey;
    }
}
