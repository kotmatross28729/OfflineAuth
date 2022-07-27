package trollogyadherent.offlineauth.registry;

import trollogyadherent.offlineauth.util.Util;

import java.util.HashMap;

public class ServerKeyTokenRegistry {
    private HashMap<String, TokenAndType> map;
    private HashMap<String, Integer> tokenUsagecount;

    public ServerKeyTokenRegistry() {
        map = new HashMap<>();
        tokenUsagecount = new HashMap<>();
    }

    String concatIdentType(String identifier, TokenType type) {
        return identifier + ":" + type.name();
    }
    TokenAndType insertTokenAndType(String identifier, TokenType type) {
        String token = Util.randomAlphanum();
        TokenAndType tat = new TokenAndType(token, type);
        map.put(concatIdentType(identifier, type), tat);
        tokenUsagecount.put(concatIdentType(identifier, type), 0);
        return tat;
    }

    public TokenAndType getTokenAndType(String identifier, TokenType type) {
        if (map.get(concatIdentType(identifier, type)) != null) {
            if (tokenUsagecount.get(concatIdentType(identifier, type)) == 1) {
                return insertTokenAndType(identifier, type);
            } else {
                tokenUsagecount.put(concatIdentType(identifier, type), 1);
                return map.get(concatIdentType(identifier, type));
            }
        } else {
            return insertTokenAndType(identifier, type);
        }
    }

    public boolean grantIdentifierAccess(String identifier, String token, TokenType type) {
        if (map.get(concatIdentType(identifier, type)) == null) {
            return  false;
        }

        TokenType realType = map.get(concatIdentType(identifier, type)).type;
        if (!type.equals(realType)) {
            return false;
        }

        TokenAndType tat = getTokenAndType(identifier, type);

        return token.equals(tat.token);
    }

    public enum TokenType {
        VIBECHECK,
        REGISTER,
        CHANGEPW,
        CHANGEDISPLAYNAME,
        DELETEACCOUNT,
        TOKEN,
        UPLOADSKIN,
        UPLOADCAPE,
        REMOVESKINORCAPE
    }

    public class TokenAndType {
        public String token;
        public TokenType type;

        public TokenAndType(String token, TokenType type) {
            this.token = token;
            this.type = type;
        }
    }
}
