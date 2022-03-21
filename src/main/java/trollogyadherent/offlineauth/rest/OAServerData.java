package trollogyadherent.offlineauth.rest;


public class OAServerData {
    boolean validServer;
    String ip;
    String port;
    String username;
    String password;

    boolean registrationOpen;
    boolean registrationTokenOpen;
    boolean skinUploadAllowed;

    public OAServerData(boolean validServer, String ip, String port, String username, String password, boolean registrationOpen, boolean registrationTokenOpen, boolean skinUploadAllowed) {
        this.validServer = validServer;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.registrationOpen = registrationOpen;
        this.registrationTokenOpen = registrationTokenOpen;
        this.skinUploadAllowed = skinUploadAllowed;
    }

    public OAServerData (boolean validServer) {
        this.validServer = validServer;
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

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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

    public void setUsername(String username) {
        this.username = username;
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

    public void setSkinUploadAllowed(boolean skinUploadAllowed) {
        this.skinUploadAllowed = skinUploadAllowed;
    }
}
