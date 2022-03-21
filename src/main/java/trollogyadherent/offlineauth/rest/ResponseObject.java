package trollogyadherent.offlineauth.rest;

public class ResponseObject {
    private boolean registrationOpen;
    private boolean registrationTokenOpen;
    private boolean skinUploadAllowed;
    private boolean validUser;
    private String motd;
    private String other;
    private int statusCode;

    public ResponseObject(boolean registrationOpen, boolean registrationTokenOpen, boolean skinUploadAllowed, boolean validUser, String motd, String other, int statusCode) {
        this.registrationOpen = registrationOpen;
        this.registrationTokenOpen = registrationTokenOpen;
        this.skinUploadAllowed = skinUploadAllowed;
        this.validUser = validUser;
        this.motd = motd;
        this.other = other;
        this.statusCode = statusCode;
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

    public boolean isValidUser() {
        return validUser;
    }

    public String getMotd() {
        return motd;
    }

    public String getOther() {
        return other;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
