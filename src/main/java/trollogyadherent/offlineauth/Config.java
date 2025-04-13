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
        public static final int manageAuthButtonId = 420;
        public static final boolean facesInTabMenu = true;
        public static final boolean showQuestionMarkIfUnknown = true;
        public static final boolean showUseKey = true;
        public static final boolean showConfigInAuth = true;
        public static final boolean clearSkinCacheOnLogin = true;

        /* Client - Custom Main Menu integration */
        public static final String cmmDefaultServerIp = "localhost";
        public static final int cmmDefaultServerPort = 25565;
        public static final int cmmDefaultAuthPort = 4567;
        public static final String cmmGuiLoginButtonName = "auth";
        public static final String cmmServerJoinButtonName = "join_server";


        /* Server category defaults */
        public static final int port = 4567; // port on which the rest api server listens on. can't be the same as mc port
        public static final boolean allowRegistration = true;
        public static final boolean allowTokenRegistration = true;
        public static final boolean allowSkinUpload = true;
        public static final boolean allowCapeUpload = true;
        public static final String motd = "Hello World"; //unused
        public static final String other = "Sneed's Feed & Seed"; //unused
        public static final String kickMessage = "You are not registered on this server!";
        public static final boolean userListPublic = false;
        public static final String accountDeletionKickMessage = "Your account has been terminated ;_;";
        public static final boolean allowOpsTokenGen = true;
        public static final boolean allowOpsUUIDChange = false;
        public static final boolean allowDisplayNameChange = true;
        public static final boolean allowOpsDisplayNameChange = true;
        public static final int maxSkinBytes = 500000;
        public static final int maxCapeBytes = 1300000;
        public static final boolean debugEnabled = false;
        public static final int secondsBeforeKick = 5;
        public static final boolean secureEachEntityEvent = true;
    }

    /* Basically an enum for different config categories */
    /* Here it's used to separate what goes into client, and what into client config file */
    public static class Categories {
        public static final String generalClient = "general_client";
        public static final String customMainMenuClient = "custommainmenu_client";
        public static final String generalServer = "general_server";
        public static final String generalCommon = "general_common";
    }

    /* Applying defaults */
    /* Client defaults */
    public static boolean savebuttonExit = Defaults.saveButtonExits;
    public static  int manageAuthButtonId = Defaults.manageAuthButtonId;
    public static boolean facesInTabMenu = Defaults.facesInTabMenu;
    public static boolean showQuestionMarkIfUnknown = Defaults.showQuestionMarkIfUnknown;
    public static boolean showUseKey = Defaults.showUseKey;
    public static boolean showConfigInAuth = Defaults.showConfigInAuth;
    public static boolean clearSkinCacheOnLogin = Defaults.clearSkinCacheOnLogin;

    /* Client - Custom Main Menu defaults */
    public static String cmmDefaultServerIp = Defaults.cmmDefaultServerIp;
    public static int cmmDefaultServerPort = Defaults.cmmDefaultServerPort;
    public static int cmmDefaultAuthPort = Defaults.cmmDefaultAuthPort;
    public static String cmmGuiLoginButtonName = Defaults.cmmGuiLoginButtonName;
    public static String cmmServerJoinButtonName = Defaults.cmmServerJoinButtonName;

    /* Server defaults*/
    public static int port = Defaults.port;
    public static boolean allowRegistration = Defaults.allowRegistration;
    public static boolean allowTokenRegistration = Defaults.allowTokenRegistration;
    public static boolean allowSkinUpload = Defaults.allowSkinUpload;
    public static boolean allowCapeUpload = Defaults.allowCapeUpload;
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
    public static int maxCapeBytes = Defaults.maxCapeBytes;
    public static boolean debugEnabled = Defaults.debugEnabled;
    public static int secondsBeforeKick = Defaults.secondsBeforeKick;
    public static boolean secureEachEntityEvent = Defaults.secureEachEntityEvent;

    public static void synchronizeConfigurationCommon() {
            Property debugEnabledProperty = config.get(Categories.generalCommon, "debugEnabled", Defaults.debugEnabled, "Show debug info");
            debugEnabled = debugEnabledProperty.getBoolean();
    }

    /* Sync for when config has changed, client */
    public static void synchronizeConfigurationClient(File configFile, boolean force, boolean load) {
        if (!loaded || force) {
            if (load) {
                config.load();
            }
            loaded = true;

            synchronizeConfigurationCommon();

            Property saveButtonExitsProperty = config.get(Categories.generalClient, "saveButtonExits", Defaults.saveButtonExits, "Save button in server auth menu exits to the previous screen");
            savebuttonExit = saveButtonExitsProperty.getBoolean();

            Property facesInTabMenuProperty = config.get(Categories.generalClient, "facesInTabMenu", Defaults.facesInTabMenu, "Show player faces in tab menu (disable if causes incompatibility)");
            facesInTabMenu = facesInTabMenuProperty.getBoolean();
    
            Property showQuestionMarkIfUnknownProperty = config.get(Categories.generalClient, "showQuestionMarkIfUnknown", Defaults.showQuestionMarkIfUnknown, "Should show question mark if player skin unknown? Otherwise shows steve's face");
            showQuestionMarkIfUnknown = showQuestionMarkIfUnknownProperty.getBoolean();
    
            Property clearSkinCacheOnLoginProperty = config.get(Categories.generalClient, "clearSkinCacheOnLogin", Defaults.clearSkinCacheOnLogin, "Should clear skin cache when logging into the server. When false, useful for integration with TabFaces's server selection menu");
            clearSkinCacheOnLogin = clearSkinCacheOnLoginProperty.getBoolean();

            //Property debugEnabledProperty = config.get(Categories.generalCommon, "debugEnabled", Defaults.debugEnabled, "Show debug info");
            //debugEnabled = debugEnabledProperty.getBoolean();

            Property manageAuthButtonIdProperty = config.get(Categories.generalClient, "manageAuthButtonId", Defaults.manageAuthButtonId, "Id of the Manage Auth button");
            manageAuthButtonId = manageAuthButtonIdProperty.getInt();

            Property showUseKeyProperty = config.get(Categories.generalClient, "showUseKey", Defaults.showUseKey, "Show or hide creation of accounts using keypair instead of password");
            showUseKey = showUseKeyProperty.getBoolean();

            Property showConfigInAuthProperty = config.get(Categories.generalClient, "showConfigInAuth", Defaults.showConfigInAuth, "Show or hide the config button in auth menu");
            showConfigInAuth = showConfigInAuthProperty.getBoolean();

            /* CMM compat */
            Property cmmDefaultServerIpProperty = config.get(Categories.customMainMenuClient, "cmmDefaultServerIp", Defaults.cmmDefaultServerIp, "Ip address of the Custom Main Menu default server");
            cmmDefaultServerIp = cmmDefaultServerIpProperty.getString();

            Property cmmDefaultServerPortProperty = config.get(Categories.customMainMenuClient, "cmmDefaultServerPort", Defaults.cmmDefaultServerPort, "Server port for Custom Main Menu default server");
            cmmDefaultServerPort = cmmDefaultServerPortProperty.getInt();

            Property cmmDefaultAuthPortProperty = config.get(Categories.customMainMenuClient, "cmmDefaultAuthPort", Defaults.cmmDefaultAuthPort, "Auth port for Custom Main Menu default server");
            cmmDefaultAuthPort = cmmDefaultAuthPortProperty.getInt();

            Property cmmGuiLoginButtonNameProperty = config.get(Categories.customMainMenuClient, "cmmGuiLoginButtonName", Defaults.cmmGuiLoginButtonName, "Name of the Custom Main Menu button that should open the login gui");
            cmmGuiLoginButtonName = cmmGuiLoginButtonNameProperty.getString();

            Property cmmServerJoinButtonNameProperty = config.get(Categories.customMainMenuClient, "cmmServerJoinButtonName", Defaults.cmmServerJoinButtonName, "Name of the Custom Main Menu button that joins the server");
            cmmServerJoinButtonName = cmmServerJoinButtonNameProperty.getString();
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

            synchronizeConfigurationCommon();

            Property portProperty = config.get(Categories.generalServer, "port", Defaults.port, "Port on which the server will listen to authentication requests");
            port = portProperty.getInt();

            Property allowRegistrationProperty = config.get(Categories.generalServer, "allowRegistration", Defaults.allowRegistration, "Allow or disallow registration of new accounts");
            allowRegistration = allowRegistrationProperty.getBoolean();

            Property allowTokenRegistrationProperty = config.get(Categories.generalServer, "allowTokenRegistration", Defaults.allowTokenRegistration, "Allow or disallow registration with existing tokens");
            allowTokenRegistration = allowTokenRegistrationProperty.getBoolean();

            Property allowSkinUploadProperty = config.get(Categories.generalServer, "allowSkinUpload", Defaults.allowSkinUpload, "Allow or disallow uploading of skins");
            allowSkinUpload = allowSkinUploadProperty.getBoolean();

            Property allowCapeUploadProperty = config.get(Categories.generalServer, "allowCapeUpload", Defaults.allowSkinUpload, "Allow or disallow uploading of capes");
            allowCapeUpload = allowCapeUploadProperty.getBoolean();

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

            Property maxCapeBytesProperty = config.get(Categories.generalServer, "maxCapeBytes", Defaults.maxSkinBytes, "Maximum amount of bytes allowed in incoming cape upload request");
            maxCapeBytes = maxCapeBytesProperty.getInt();

            Property secondsBeforeKickProperty = config.get(Categories.generalServer, "secondsBeforeKick", Defaults.secondsBeforeKick, "How much seconds should elapse before the server kicks unauthenticated players (if the modpack is large, you might need to increase this)");
            secondsBeforeKick = secondsBeforeKickProperty.getInt();

            Property secureEachEntityEventProperty = config.get(Categories.generalServer, "secureEachEntityEvent", Defaults.secureEachEntityEvent, "Cancelling every single EntityEvent coming from a player who is not yet authenticated. Might be CPU intensive, so it can be turned off");
            secureEachEntityEvent = secureEachEntityEventProperty.getBoolean();
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