package trollogyadherent.offlineauth.request.objects;

import trollogyadherent.offlineauth.registry.ServerKeyTokenRegistry;

public class ChallengeRequestBodyObject {
    private String identifier;
    private String pubKey;
    private ServerKeyTokenRegistry.TokenType type;

    public ChallengeRequestBodyObject(String identifier, String pubKey, ServerKeyTokenRegistry.TokenType type) {
        this.identifier = identifier;
        this.pubKey = pubKey;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPubKey() {
        return pubKey;
    }

    public ServerKeyTokenRegistry.TokenType getType() {
        return type;
    }
}
