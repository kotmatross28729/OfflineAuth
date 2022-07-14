package trollogyadherent.offlineauth.request.objects;

public class ChangePasswordRequestBodyObject {
    private String identifier;
    private String password;
    private String newPassword;

    public ChangePasswordRequestBodyObject(String identifier, String password, String newPassword) {
        this.identifier = identifier;
        this.password = password;
        this.newPassword = newPassword;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
