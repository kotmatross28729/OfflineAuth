package trollogyadherent.offlineauth;

import net.minecraftforge.common.config.ConfigCategory;
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
        public static final String motd = "Hello World"; //unused
        public static final String other = "Sneed's Feed & Seed"; //unused
        public static final String kickMessage = "You are not registerd on this server!";
        public static final boolean userListPublic = false;
        public static final String accountDeletionKickMessage = "Your account has been terminated ;_;";
        public static final boolean allowOpsTokenGen = true;
        public static final boolean allowOpsUUIDChange = false;
        public static final boolean allowDisplayNameChange = true;
        public static final boolean allowOpsDisplayNameChange = true;
        public static final int maxSkinBytes = 500000;
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
    public static boolean userListPublic = Defaults.userListPublic;
    public static String accountDeletionKickMessage = Defaults.accountDeletionKickMessage;
    public static boolean allowOpsTokenGen = Defaults.allowOpsTokenGen;
    public static boolean allowOpsUUIDChange = Defaults.allowOpsUUIDChange;
    public static boolean allowOpsDisplayNameChange = Defaults.allowOpsDisplayNameChange;
    public static boolean allowDisplayNameChange = Defaults.allowDisplayNameChange;
    public static int maxSkinBytes = Defaults.maxSkinBytes;

    /* Sync for when config has changed, client */
    public static void synchronizeConfigurationClient(File configFile, boolean force) {
        if (!loaded || force) {
            config.load();
            loaded = true;

            Property saveButtonExitsProperty = config.get(Categories.generalClient, "saveButtonExits", Defaults.saveButtonExits, "Save button in server auth menu exits to the previous screen");
            savebuttonExit = saveButtonExitsProperty.getBoolean();
        }

        if(config.hasChanged()) {
            OfflineAuth.info("Saved Config");
            config.save();
        }
    }

    /* Sync for when config has changed, server */
    public static void synchronizeConfigurationServer(File configFile, boolean force) {
        if (!loaded || force) {
            config.load();
            loaded = true;

            Property portProperty = config.get(Categories.generalServer, "port", Defaults.port, "Port on which the server will listen to authentication requests");
            port = portProperty.getInt();

            Property allowRegistrationProperty = config.get(Categories.generalServer, "allowRegistration", Defaults.allowRegistration, "Allow or disallow registration of new accounts");
            allowRegistration = allowRegistrationProperty.getBoolean();

            Property allowTokenRegistrationProperty = config.get(Categories.generalServer, "allowTokenRegistration", Defaults.allowTokenRegistration, "Allow or disallow registration with existing tokens");
            allowTokenRegistration = allowTokenRegistrationProperty.getBoolean();

            Property allowSkinUploadProperty = config.get(Categories.generalServer, "allowSkinUpload", Defaults.allowSkinUpload, "Allow or disallow uploading of skins");
            allowSkinUpload = allowSkinUploadProperty.getBoolean();

            Property motdProperty = config.get(Categories.generalServer, "motd", Defaults.motd, "Optional MOTD");
            motd = motdProperty.getString();

            Property otherProperty = config.get(Categories.generalServer, "other", Defaults.other, "Optional other. MOTD2 ??");
            other = otherProperty.getString();

            Property kickMessageProperty = config.get(Categories.generalServer, "kickMessage", Defaults.kickMessage, "Message displayed when an unregistered player gets kicked");
            kickMessage = kickMessageProperty.getString();

            Property userListPublicProperty = config.get(Categories.generalServer, "userListPublic", Defaults.userListPublic, "Whether a rest password is needed to get the list of accounts via rest");
            userListPublic = userListPublicProperty.getBoolean();

            Property accountDeletionKickMessageProperty = config.get(Categories.generalServer, "accountDeletionKickMessage", Defaults.accountDeletionKickMessage, "Message displayed if a connected player's account gets deleted");
            accountDeletionKickMessage = accountDeletionKickMessageProperty.getString();

            Property allowOpsTokenGenProperty = config.get(Categories.generalServer, "allowOpsTokenGen", Defaults.allowOpsTokenGen, "Allow or disallow OP's to generate invite tokens");
            allowOpsTokenGen = allowOpsTokenGenProperty.getBoolean();

            Property allowOpsUUIDChangeProperty = config.get(Categories.generalServer, "allowOpsUUIDChange", Defaults.allowOpsUUIDChange, "Allow or disallow OP's to change user uuids");
            allowOpsUUIDChange = allowOpsUUIDChangeProperty.getBoolean();

            Property allowOpsDisplayNameChangeProperty = config.get(Categories.generalServer, "allowOpsDisplayNameChange", Defaults.allowOpsDisplayNameChange, "Allow or disallow OP's to change user displaynames");
            allowOpsDisplayNameChange = allowOpsDisplayNameChangeProperty.getBoolean();

            Property allowDisplayNameChangeProperty = config.get(Categories.generalServer, "allowDisplayNameChange", Defaults.allowDisplayNameChange, "Allow or disallow users to change displaynames");
            allowDisplayNameChange = allowDisplayNameChangeProperty.getBoolean();

            Property maxSkinBytesProperty = config.get(Categories.generalServer, "maxSkinBytes", Defaults.maxSkinBytes, "Maximum amount of bytes allowed in incoming skin upload request");
            maxSkinBytes = maxSkinBytesProperty.getInt();
        }

        if(config.hasChanged()) {
            OfflineAuth.info("Saved Config");
            config.save();
        }
    }

    public static String[] getServerConfigStrings() {
        ConfigCategory cat = config.getCategory("general_server");
        return cat.keySet().toArray(new String[0]);
    }

    public static String[] getClientConfigStrings() {
        ConfigCategory cat = config.getCategory("general_client");
        return cat.keySet().toArray(new String[0]);
    }

    public static String[] getAllConfigStrings() {
        String[] temp = new String[getServerConfigStrings().length + getClientConfigStrings().length];
        String[] server = getServerConfigStrings();
        String[] client = getClientConfigStrings();

        for (int i = 0; i < server.length; i ++) {
            temp[i] = server[i];
        }

        for (int i = server.length, j = 0; j < client.length; i ++, j ++) {
            temp[i] = client[j];
        }

        return temp;
    }

    public static ConfigCategory getStringCategory(String str) {
        for (String s : getClientConfigStrings()) {
            if (s.equals(str)) {
                return config.getCategory("general_client");
            }
        }

        for (String s : getServerConfigStrings()) {
            if (s.equals(str)) {
                return config.getCategory("general_server");
            }
        }

        return null;
    }

    public static Property getPropertyByString(String str) {
        return getStringCategory(str).get(str);
    }
}