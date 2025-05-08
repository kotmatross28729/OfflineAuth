package trollogyadherent.offlineauth.rest;

import java.util.ArrayList;
public class StatusResponseObject {
    private final Object status;
    private final int statusCode;

    public StatusResponseObject(Object status, int statusCode) {
        this.status = status;
        this.statusCode = statusCode;
    }

    public String getStatus() {
        if (status instanceof String) {
            return status.toString();
        } else if (status instanceof String[]) {
            StringBuilder sb = new StringBuilder();
            for (String s : (String[]) status) {
                sb.append(s).append(",");
            }
            return "[" + sb.deleteCharAt(sb.length() - 1) + "]";
        } else if (status != null) {
            return status.toString();
        } else {
            return "Error: status is null";
        }
    }
    
    public String getStatusCooldownRemainedTime(int part) {
        if(status instanceof ArrayList<?> statusArray) {
            return (String) statusArray.get(part);
        }
        return "Error: status is null ";
    }

    public int getStatusCode() {
        return statusCode;
    }
}
