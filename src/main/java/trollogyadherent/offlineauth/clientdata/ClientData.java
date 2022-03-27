package trollogyadherent.offlineauth.clientdata;

import net.minecraft.client.multiplayer.ServerData;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.util.JsonUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/* Info and credentials for servers */
public class ClientData {

    /* Writes any string to file */
    public static boolean writeFile (File file, String text) {
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return false;
                }
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(text);
            bw.close();

            return true;
        } catch (IOException e) {
            OfflineAuth.error(e.getMessage());
            return false;
            //e.printStackTrace();
        }
    }

    /* Reads any file to string */
    public static String readFile (File file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
    }

    /* Used to read the data json file to a string */
    public static String readDatafile () throws IOException {
        return readFile(OfflineAuth.varInstanceClient.datafile);
    }

    /* Used to write the json file to disk */
    public static boolean saveData() {
        System.out.println("Saving data...");
        OAServerData[] oaServerDataArray = OfflineAuth.varInstanceClient.OAserverDataCache.toArray(new OAServerData[0]);
        return writeFile(OfflineAuth.varInstanceClient.datafile, JsonUtil.objectToJsonList(oaServerDataArray));
    }

    /* Reads data file and saves in memory */
    public static boolean loadData() {
        OfflineAuth.info("Loading data...");
        if (!OfflineAuth.varInstanceClient.datafile.exists()) {
            OfflineAuth.info("Data file does not exist");
            OfflineAuth.varInstanceClient.OAserverDataCache = new ArrayList<>();
            if (!saveData()) {
                return false;
            }
        } else {
            try {
                // Basically parses the content of offlineauth.json to an ArrayList of OAServerData objects
                OfflineAuth.varInstanceClient.OAserverDataCache = new ArrayList<>(Arrays.asList(((OAServerData[]) JsonUtil.jsonToObjectList(readDatafile(), OAServerData[].class))));
                System.out.println(OfflineAuth.varInstanceClient.OAserverDataCache);
                System.out.println(OfflineAuth.varInstanceClient.OAserverDataCache.size());
            } catch (IOException e) {
                OfflineAuth.error(e.getMessage());
                return false;
            }
        }
        return true;
    }
}
