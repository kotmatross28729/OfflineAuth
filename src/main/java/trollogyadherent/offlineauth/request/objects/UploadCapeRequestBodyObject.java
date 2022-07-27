package trollogyadherent.offlineauth.request.objects;

import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;

public class UploadCapeRequestBodyObject {
    private String identifier;
    private String password;
    private byte[] capeBytes;
    private String clientKeyToken;
    private ServerKeyTokenRegistry.TokenType type;

    public UploadCapeRequestBodyObject(String identifier, String password, byte[] capeBytes, String clientKeyToken) {
        this.identifier = identifier;
        this.password = password;
        this.capeBytes = capeBytes;
        this.clientKeyToken = clientKeyToken;
        this.type = ServerKeyTokenRegistry.TokenType.UPLOADCAPE;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getCapeBytes() {
        return capeBytes;
    }

    public String getClientKeyToken() {
        return clientKeyToken;
    }

    public ServerKeyTokenRegistry.TokenType getType() {
        return type;
    }
}

