package trollogyadherent.offlineauth;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;
import trollogyadherent.offlineauth.util.Util;
import trollogyadherent.offlineauth.varinstances.client.VarInstanceClient;
import trollogyadherent.offlineauth.varinstances.server.VarInstanceServer;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

///

////Not now...

/* TODO: password reset tokens */   //See how registration tokens work
// | ===CONFIGS: 1)ALLOW_PASSWORD_RESET_TOKENS; 2)ALLOW_OPS_GEN_PASSWORD_RESET_TOKENS; === |

/* TODO: REGISTRATION LOCKDOWN */
// | ===CONFIGS: 1)ENABLE_LOCKDOWN; 
//               2)LOCKDOWN_LEVEL_1_RPM;      3)LOCKDOWN_LEVEL_2_RPM;      4)LOCKDOWN_LEVEL_3_RPM;
//               5)LOCKDOWN_LEVEL_1_DOWNTIME; 6)LOCKDOWN_LEVEL_2_DOWNTIME; 7)LOCKDOWN_LEVEL_3_DOWNTIME;
// | ===
// CODE: 429 Too Many Requests

/* TODO: IND. IP SPAM BLOCK */
// | ===CONFIGS: 1)ENABLE_IP_SPAM_BLOCK; 2)IP_SPAM_BLOCK_RPM; === |
// CODE: 403 Forbidden

///


//todo: rest of v6 compat:
// - CommandBanIp: Either remove the use of Pattern (and check differently) or make Pattern for ipv6 (how many are there anyway?)
// - ServerConfigurationManager.initializeConnectionToPlayer: change getSocketAddress().toString() to support v6    
// - NetHandlerLoginServer.func_147317_d: change getSocketAddress().toString() to support v6    

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.7.10]", guiFactory = "trollogyadherent.offlineauth.gui.GuiFactory")
public class OfflineAuth {

    private static Logger LOG;// = LogManager.getLogger(Tags.MODID);

    public static File confFile;

    @SidedProxy(clientSide= Tags.GROUPNAME + ".ClientProxy", serverSide=Tags.GROUPNAME + ".CommonProxy")
    public static CommonProxy proxy;

    public static String rootPath = "offlineauth";

    public static VarInstanceClient varInstanceClient;
    public static VarInstanceServer varInstanceServer;
    public static boolean DEBUG_MODE;
    public final static int maxPngDimension = 2500;
    public static boolean isEFRLoaded;
    public static boolean isWitcheryLoaded;
    public static boolean isCMMLoaded;
    public static boolean isSSBLoaded;
    public static boolean isTFLoaded;
    public static boolean isCPMLoaded;
    
    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc., and register them with the GameRegistry."
    public void preInit(FMLPreInitializationEvent event) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        isEFRLoaded = Loader.isModLoaded("etfuturum");
        isWitcheryLoaded = Loader.isModLoaded("witchery");
        isCMMLoaded = Loader.isModLoaded("CustomMainMenu");
        isSSBLoaded = Loader.isModLoaded("simpleskinbackport");
    
        isTFLoaded = Loader.isModLoaded("tabfaces");
        isCPMLoaded = Loader.isModLoaded("customplayermodels");
        
        LOG = event.getModLog();
        String debugVar = System.getenv("MCMODDING_DEBUG_MODE");
        DEBUG_MODE = debugVar != null;
        proxy.preInit(event);
        
        //todo: delete later
        Util.RUN_IP_PORT_TESTS();
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        proxy.serverAboutToStart(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        proxy.serverStarted(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        proxy.serverStopping(event);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        proxy.serverStopped(event);
    }
    
    public static boolean isDebugMode() {
        if (Config.config != null) {
            return (Config.debugEnabled || DEBUG_MODE);
        }
        return (DEBUG_MODE);
    }

    public static void debug(String message) {
        if (isDebugMode()) {
            LOG.info("DEBUG: " + message);
        }
    }

    public static void info(String message) {
        LOG.info(message);
    }
    public static void warn(String message) {
        LOG.warn(message);
    }

    public static void error(String message) {
        LOG.error(message);
    }
    
    public static void fatal(String message) {
        LOG.fatal(message);
    }
}