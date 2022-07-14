package trollogyadherent.offlineauth.request.objects;

import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;

public class ChangeDisplaynameRequestBodyObject {
    private String identifier;
    private String password;
    private String newDisplayName;
    private String clientKeyToken;
    private ServerKeyTokenRegistry.TokenType type;

    public ChangeDisplaynameRequestBodyObject(String identifier, String password, String newDisplayName, String clientKeyToken) {
        this.identifier = identifier;
        this.password = password;
        this.newDisplayName = newDisplayName;
        this.clientKeyToken = clientKeyToken;
        this.type = ServerKeyTokenRegistry.TokenType.CHANGEDISPLAYNAME;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public String getNewDisplayName() {
        return newDisplayName;
    }

    public String getClientKeyToken() {
        return clientKeyToken;
    }

    public ServerKeyTokenRegistry.TokenType getType() {
        return type;
    }
}
