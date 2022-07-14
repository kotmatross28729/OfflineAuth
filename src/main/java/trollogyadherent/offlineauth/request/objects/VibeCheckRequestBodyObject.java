package trollogyadherent.offlineauth.request.objects;

import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;

public class VibeCheckRequestBodyObject {
    private String identifier;
    private String displayname;
    private String password;
    private String clientKeyToken;
    private ServerKeyTokenRegistry.TokenType type;

    public VibeCheckRequestBodyObject(String identifier, String displayname, String password, String clientKeyToken) {
        this.identifier = identifier;
        this.displayname = displayname;
        this.password = password;
        this.clientKeyToken = clientKeyToken;
        this.type = ServerKeyTokenRegistry.TokenType.VIBECHECK;
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

    public String getClientKeyToken() {
        return clientKeyToken;
    }

    public ServerKeyTokenRegistry.TokenType getType() {
        return type;
    }
}
