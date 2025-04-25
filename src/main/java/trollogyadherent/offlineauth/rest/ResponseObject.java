package trollogyadherent.offlineauth.rest;

public class ResponseObject {
    private boolean registrationOpen;
    private boolean registrationTokenOpen;
    private boolean skinUploadAllowed;
    private String displayName;
    private int statusCode;
    private boolean displayNameChangeAllowed;

    public ResponseObject(boolean registrationOpen, boolean registrationTokenOpen, boolean skinUploadAllowed, String displayName, boolean displayNameChangeAllowed, int statusCode) {
        this.registrationOpen = registrationOpen;
        this.registrationTokenOpen = registrationTokenOpen;
        this.skinUploadAllowed = skinUploadAllowed;
        this.displayName = displayName;
        this.statusCode = statusCode;
        this.displayNameChangeAllowed = displayNameChangeAllowed;
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

    public String getDisplayName() {
        return displayName;
    }

    public int getStatusCode() {
        return statusCode;
    }
    public boolean isDisplayNameChangeAllowed() {
        return displayNameChangeAllowed;
    }
}
