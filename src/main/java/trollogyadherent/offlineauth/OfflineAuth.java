package trollogyadherent.offlineauth;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.texture.TextureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.iq80.leveldb.DB;
import trollogyadherent.offlineauth.rest.OAServerData;
import trollogyadherent.offlineauth.varinstances.client.VarInstanceClient;
import trollogyadherent.offlineauth.varinstances.server.VarInstanceServer;

import java.io.File;
import java.util.ArrayList;

/* TODO: fix skins, the server should keep a copy of the skincache too, just strip it of shit that might not be present on the server */
/* TODO: logging in with a key instead of password, keypicker and generator */
/* TODO: proper command perms */
/* TODO: server/client commands to change config options, command to reload file */
/* TODO: Token management */
/* TODO: Privileged API requests (listing users, viewing/adding/revoking tokens) */
/* TODO: uploading and using skins */
/* TODO: spam prevention, lock registration if mass registration detected*/
/* TODO: account deletion confirmation gui */
/* TODO: look into JWT auth https://github.com/rjozefowicz/sparkjava-jwt*/
/* TODO: configurable custom default skin (serverside) */
/* TODO: setting a global skin (client) */
/* TODO: make view password button */
/* TODO: selectable uuid */

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.7.10]")
public class OfflineAuth {

    private static Logger LOG = LogManager.getLogger(Tags.MODID);
    static File confFile;

    @SidedProxy(clientSide= Tags.GROUPNAME + ".ClientProxy", serverSide=Tags.GROUPNAME + ".CommonProxy")
    public static CommonProxy proxy;

    public static VarInstanceClient varInstanceClient;
    public static VarInstanceServer varInstanceServer;


    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc, and register them with the GameRegistry."
    public void preInit(FMLPreInitializationEvent event) {
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
        LOG.debug(message);
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