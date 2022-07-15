package trollogyadherent.offlineauth.request.objects;

import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;

public class UploadSkinRequestBodyObject {
    private String identifier;
    private String password;
    private byte[] skinBytes;
    private String clientKeyToken;
    private ServerKeyTokenRegistry.TokenType type;

    public UploadSkinRequestBodyObject(String identifier, String password, byte[] skinBytes, String clientKeyToken) {
        this.identifier = identifier;
        this.password = password;
        this.skinBytes = skinBytes;
        this.clientKeyToken = clientKeyToken;
        this.type = ServerKeyTokenRegistry.TokenType.UPLOADSKIN;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getSkinBytes() {
        return skinBytes;
    }

    public String getClientKeyToken() {
        return clientKeyToken;
    }

    public ServerKeyTokenRegistry.TokenType getType() {
        return type;
    }
}

