package trollogyadherent.offlineauth.request.objects;

import trollogyadherent.offlineauth.misc.Unused;

@Unused
public class TokenRequestBodyObject {
    private String identifier;
    private String encryptedChallenge;

    public TokenRequestBodyObject(String identifier, String encryptedchallenge) {
        this.identifier = identifier;
        this.encryptedChallenge = encryptedchallenge;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getEncryptedChallenge() {
        return encryptedChallenge;
    }
}
