package trollogyadherent.offlineauth;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import org.apache.logging.log4j.Logger;
import trollogyadherent.offlineauth.varinstances.client.VarInstanceClient;
import trollogyadherent.offlineauth.varinstances.server.VarInstanceServer;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/* TODO: admin command that changes player uuid */
/* TODO: players should be assigned uuids made up randomly by the server and kept in the db like skins */
/* TODO: password reset tokens */
/* TODO: spam prevention, lock registration if mass registration detected*/
/* TODO: configurable custom default skin (serverside) */
/* TODO: selectable uuid */
/* TODO: button to delete the server pubkey from the cache, in case somehow the server configs get yeeten */

// Disregard That, I ...

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


    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc, and register them with the GameRegistry."
    public void preInit(FMLPreInitializationEvent event) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        LOG = event.getModLog();
        String debugVar = System.getenv("MCMODDING_DEBUG_MODE");
        DEBUG_MODE = debugVar != null;
        OfflineAuth.info("Debugmode: " + DEBUG_MODE);
        proxy.preInit(event);
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

    public static void debug(String message) {
        if (DEBUG_MODE || Config.debugEnabled) {
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
}