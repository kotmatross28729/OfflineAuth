package trollogyadherent.offlineauth.clientdata;

import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.util.JsonUtil;
import trollogyadherent.offlineauth.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/* Info and credentials for servers */
public class ClientData {
    /* Used to read the data json file to a string */
    public static String readDatafile () throws IOException {
        return Util.readFile(OfflineAuth.varInstanceClient.datafile);
    }

    /* Used to write the json file to disk */
    public static boolean saveData() {
        OfflineAuth.info("Saving data...");
        OAServerData[] oaServerDataArray = OfflineAuth.varInstanceClient.OAserverDataCache.toArray(new OAServerData[0]);
        return Util.writeFile(OfflineAuth.varInstanceClient.datafile, JsonUtil.objectToJsonList(oaServerDataArray));
    }

    /* Reads data file and saves in memory */
    public static boolean loadData() {
        OfflineAuth.info("Loading data...");
        if (!OfflineAuth.varInstanceClient.datafile.exists()) {
            OfflineAuth.info("Data file does not exist");
            OfflineAuth.varInstanceClient.OAserverDataCache = new ArrayList<>();
        } else {
            try {
                // Basically parses the content of offlineauth.json to an ArrayList of OAServerData objects
                OfflineAuth.varInstanceClient.OAserverDataCache = new ArrayList<>(Arrays.asList(((OAServerData[]) JsonUtil.jsonToObjectList(readDatafile(), OAServerData[].class))));
            } catch (IOException e) {
                OfflineAuth.error(e.getMessage());
                return false;
            }
        }
        if (Util.getOAServerDatabyIP(Config.cmmDefaultServerIp, String.valueOf(Config.cmmDefaultServerPort)) == null) {
            OfflineAuth.varInstanceClient.OAserverDataCache.add(new OAServerData(true, Config.cmmDefaultServerIp, String.valueOf(Config.cmmDefaultServerPort), String.valueOf(Config.cmmDefaultAuthPort), "", "", "", false, "", "", false, false, false));
        }
        return saveData();
    }
}
