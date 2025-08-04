package trollogyadherent.offlineauth.rest;


public class OAServerData {
    String ip;
    String port;
    String restPort;
    String identifier;
    String displayName;
    String password;
    boolean useKey;
    String privateKeyPath;
    String publicKeyPath;

    public OAServerData(String ip, String port, String restPort, String identifier, String displayName, String password, boolean useKey, String privateKeyPath, String publicServerKeyPath) {
        this.ip = ip;
        this.port = port;
        this.restPort = restPort;
        this.identifier = identifier;
        this.displayName = displayName;
        this.password = password;
        this.useKey = useKey;
        this.privateKeyPath = privateKeyPath;
        this.publicKeyPath = publicServerKeyPath;
    }
    
    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getRestPort() {
        return restPort;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public boolean isUsingKey() {
        return useKey;
    }

    public String getPrivateKeyPath() {
        return this.privateKeyPath;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setRestPort(String restPort) {
        this.restPort = restPort;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setUseKey(boolean useKey) {
        this.useKey = useKey;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }

    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
