package trollogyadherent.offlineauth.rest;


public class OAServerData {
    boolean validServer;
    String ip;
    String port;
    String restPort;
    String identifier;
    String displayName;
    String password;
    boolean useKey;
    String privateKeyPath;
    String publicKeyPath;

    boolean registrationOpen;
    boolean registrationTokenOpen;
    boolean skinUploadAllowed;

    public OAServerData(boolean validServer, String ip, String port, String restPort, String identifier, String displayName, String password, boolean useKey, String privateKeyPath, String publicServerKeyPath, boolean registrationOpen, boolean registrationTokenOpen, boolean skinUploadAllowed) {
        this.validServer = validServer;
        this.ip = ip;
        this.port = port;
        this.restPort = restPort;
        this.identifier = identifier;
        this.displayName = displayName;
        this.password = password;
        this.useKey = useKey;
        this.privateKeyPath = privateKeyPath;
        this.publicKeyPath = publicServerKeyPath;

        this.registrationOpen = registrationOpen;
        this.registrationTokenOpen = registrationTokenOpen;
        this.skinUploadAllowed = skinUploadAllowed;
    }

    public OAServerData (boolean validServer) {
        this.validServer = validServer;
    }

    public OAServerData(String ip, String port) {
    }

    public boolean isValidServer() {
        return validServer;
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

    public boolean isRegistrationOpen() {
        return registrationOpen;
    }

    public boolean isRegistrationTokenOpen() {
        return registrationTokenOpen;
    }

    public boolean isSkinUploadAllowed() {
        return skinUploadAllowed;
    }

    public void setValidServer(boolean validServer) {
        this.validServer = validServer;
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

    public void setRegistrationOpen(boolean registrationOpen) {
        this.registrationOpen = registrationOpen;
    }

    public void setRegistrationTokenOpen(boolean registrationTokenOpen) {
        this.registrationTokenOpen = registrationTokenOpen;
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

    public void setSkinUploadAllowed(boolean skinUploadAllowed) {
        this.skinUploadAllowed = skinUploadAllowed;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
