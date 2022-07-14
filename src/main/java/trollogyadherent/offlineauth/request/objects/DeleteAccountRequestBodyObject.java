package trollogyadherent.offlineauth.request.objects;

import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;
import trollogyadherent.offlineauth.util.RsaKeyUtil;

public class DeleteAccountRequestBodyObject {
    private String identifier;
    private String password;
    private String clientKeyToken;
    private ServerKeyTokenRegistry.TokenType type;

    public DeleteAccountRequestBodyObject(String identifier, String password, String clientKeyToken) {
        this.identifier = identifier;
        this.password = password;
        this.clientKeyToken = clientKeyToken;
        this.type = ServerKeyTokenRegistry.TokenType.DELETEACCOUNT;
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
