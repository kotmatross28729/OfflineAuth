package trollogyadherent.offlineauth.rest;

public class StatusResponseObject {
    private String status;
    private int statusCode;

    public StatusResponseObject(String status, int statusCode) {
        this.status = status;
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
