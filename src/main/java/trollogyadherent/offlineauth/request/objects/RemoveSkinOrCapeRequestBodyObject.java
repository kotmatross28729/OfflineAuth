package trollogyadherent.offlineauth.request.objects;

import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;

public class RemoveSkinOrCapeRequestBodyObject {
    private String identifier;
    private String password;
    private String clientKeyToken;
    private ServerKeyTokenRegistry.TokenType type;

    public RemoveSkinOrCapeRequestBodyObject(String identifier, String password, String clientKeyToken) {
        this.identifier = identifier;
        this.password = password;
        this.clientKeyToken = clientKeyToken;
        this.type = ServerKeyTokenRegistry.TokenType.REMOVESKINORCAPE;
    }

    public String getIdentifier() {
        return identifier;
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
