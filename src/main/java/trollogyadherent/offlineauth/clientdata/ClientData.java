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

public class ClientData {
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

    public static String readFile (File file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
    }

    public static String readDatafile () throws IOException {
        return readFile(OfflineAuth.datafile);
    }

    public static boolean saveData() {
        System.out.println("Saving data...");
        OAServerData[] oaServerDataArray = OfflineAuth.OAserverDataCache.toArray(new OAServerData[0]);
        return writeFile(OfflineAuth.datafile, JsonUtil.objectToJsonList(oaServerDataArray));
    }

    public static boolean loadData() {
        OfflineAuth.info("Loading data...");
        if (!OfflineAuth.datafile.exists()) {
            OfflineAuth.info("Data file does not exist");
            OfflineAuth.OAserverDataCache = new ArrayList<>();
            if (!saveData()) {
                return false;
            }
        } else {
            try {
                // Basically parses the content of offlineauth.json to an ArrayList of OAServerData objects
                OfflineAuth.OAserverDataCache = new ArrayList<>(Arrays.asList(((OAServerData[]) JsonUtil.jsonToObjectList(readDatafile(), OAServerData[].class))));
                System.out.println(OfflineAuth.OAserverDataCache);
                System.out.println(OfflineAuth.OAserverDataCache.size());
            } catch (IOException e) {
                OfflineAuth.error(e.getMessage());
                return false;
            }
        }
        return true;
    }
}
