package trollogyadherent.offlineauth;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class Config {

    /* Instantiating a new config */
    public static Configuration config = new Configuration(OfflineAuth.confFile);

    /* Used as a flag to load defaults only once, otherwise saved values are overriden */
    static boolean loaded = false;

    /* Default config values */
    private static class Defaults {
        /* Client category defaults */
        public static final boolean saveButtonExits = true; // In auth management, if the "Save" button exits to previous menu, or not

        /* Server category defaults */
        public static final int port = 4567; // port on which the rest api server listens on. can't be the same as mc port
        public static final boolean allowRegistration = true;
        public static final boolean allowTokenRegistration = true;
        public static final boolean allowSkinUpload = true;
        public static final String motd = "Hello World";
        public static final String other = "Sneed's Feed & Seed";
        public static final String kickMessage = "You are not registerd on this server!";
    }

    /* Basically an enum for different config categories */
    /* Here it's used to separate what goes into client, and what into client config file */
    public static class Categories {
        public static final String generalClient = "general_client";
        public static final String generalServer = "general_server";
    }

    /* Applying defaults */
    /* Client defaults */
    public static boolean savebuttonExit = Defaults.saveButtonExits;

    /* Server defaults*/
    public static int port = Defaults.port;
    public static boolean allowRegistration = Defaults.allowRegistration;
    public static boolean allowTokenRegistration = Defaults.allowTokenRegistration;
    public static boolean allowSkinUpload = Defaults.allowSkinUpload;
    public static String motd = Defaults.motd;
    public static String other = Defaults.other;
    public static String kickMessage = Defaults.kickMessage;


    /* Sync for when config has changed, client */
    public static void synchronizeConfigurationClient(File configFile) {
        if (!loaded) {
            config.load();
            loaded = true;

            Property saveButtonExitsProperty = config.get(Categories.generalServer, "saveButtonExits", true, "\"Save\" button in server auth menu exits to the previous screen");
            savebuttonExit = saveButtonExitsProperty.getBoolean();
        }

        if(config.hasChanged()) {
            OfflineAuth.info("Saved Config");
            config.save();
        }
    }

    /* Sync for when config has changed, server */
    public static void synchronizeConfigurationServer(File configFile) {
        if (!loaded) {
            config.load();
            loaded = true;

            Property portProperty = config.get(Categories.generalServer, "port", 4567, "Port on which the server will listen to authentication requests");
            port = portProperty.getInt();

            Property allowRegistrationProperty = config.get(Categories.generalServer, "allowRegistration", true, "Allow or disallow registration of new accounts");
            allowRegistration = allowRegistrationProperty.getBoolean();

            Property allowTokenRegistrationProperty = config.get(Categories.generalServer, "allowTokenRegistration", true, "Allow or disallow registration with existing tokens");
            allowTokenRegistration = allowTokenRegistrationProperty.getBoolean();

            Property allowSkinUploadProperty = config.get(Categories.generalServer, "allowSkinUpload", true, "Allow or disallow uploading of skins");
            allowSkinUpload = allowSkinUploadProperty.getBoolean();

            Property motdProperty = config.get(Categories.generalServer, "motd", "Hello World!", "Optional MOTD");
            motd = motdProperty.getString();

            Property otherProperty = config.get(Categories.generalServer, "other", "Sneed's Feed & Seed", "Optional other. MOTD2 ??");
            other = otherProperty.getString();

            Property kickMessageProperty = config.get(Categories.generalServer, "kickMessage", Defaults.kickMessage, "Message displayed when an unregistered player gets kicked");
            kickMessage = kickMessageProperty.getString();
        }

        if(config.hasChanged()) {
            OfflineAuth.info("Saved Config");
            config.save();
        }
    }
}