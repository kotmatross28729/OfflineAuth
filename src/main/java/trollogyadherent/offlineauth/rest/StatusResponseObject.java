package trollogyadherent.offlineauth.rest;

import trollogyadherent.offlineauth.util.JsonUtil;
import trollogyadherent.offlineauth.util.Util;

public class StatusResponseObject {
    private Object status;
    private int statusCode;

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
        } else {
            return status.toString();
        }
    }

    public int getStatusCode() {
        return statusCode;
    }
}
