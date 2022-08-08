package trollogyadherent.offlineauth.request.objects;

import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;

public class UploadSkinOrCapeRequestBodyObject {
    private String identifier;
    private String password;
    private byte[] imageBytes;
    private String clientKeyToken;
    private ServerKeyTokenRegistry.TokenType type;

    public UploadSkinOrCapeRequestBodyObject(String identifier, String password, byte[] imageBytes, String clientKeyToken) {
        this.identifier = identifier;
        this.password = password;
        this.imageBytes = imageBytes;
        this.clientKeyToken = clientKeyToken;
        this.type = ServerKeyTokenRegistry.TokenType.UPLOADSKINORCAPE;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public String getClientKeyToken() {
        return clientKeyToken;
    }

    public ServerKeyTokenRegistry.TokenType getType() {
        return type;
    }
}

