package trollogyadherent.offlineauth;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.*;
import net.minecraftforge.common.MinecraftForge;
import trollogyadherent.offlineauth.command.*;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.event.ServerEventListener;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.rest.Rest;
import trollogyadherent.offlineauth.util.ServerUtil;
import trollogyadherent.offlineauth.util.Util;
import trollogyadherent.offlineauth.varinstances.server.VarInstanceServer;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc, and register them with the GameRegistry."
    public void preInit(FMLPreInitializationEvent event) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        if (Util.isServer()) {
            OfflineAuth.varInstanceServer = new VarInstanceServer();
        }

        /* Config */
        OfflineAuth.confFile = event.getSuggestedConfigurationFile();
        if (Util.isServer()) {
            Config.synchronizeConfigurationServer(event.getSuggestedConfigurationFile(), false);
        } else {
            Config.synchronizeConfigurationClient(event.getSuggestedConfigurationFile(), false);
        }

        OfflineAuth.warn("I am " + Tags.MODNAME + " at version " + Tags.VERSION + " and group name " + Tags.GROUPNAME);

        FMLCommonHandler.instance().bus().register(this);

        /* Generate server cryptographic keypair if none are present */
        if (Util.isServer()) {
            ServerUtil.generateServerKeys();
        }

        /* Start Spark server */
        if (Util.isServer()) {
            Rest.restStart();
        }

        /* Initialize database */
        if (Util.isServer()) {
            Database.initialize();
        }

        /* Packets */
        //OfflineAuth.simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("offlineuath");
        //OfflineAuth.simpleNetworkWrapper.registerMessage(PacketS2C.PacketS2CHandler.class, PacketS2C.class, 0, Side.CLIENT);
        PacketHandler.initPackets();

        /* Listener listening for player joins */
        if (Util.isServer()) {
            ServerEventListener playerJoinedHandler = new ServerEventListener();
            MinecraftForge.EVENT_BUS.register(playerJoinedHandler);
            FMLCommonHandler.instance().bus().register(playerJoinedHandler);
        }
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent event) {

    }

    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent event) {

    }

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {

    }

    // register server commands in this event handler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandUUID());

        if (Util.isServer()) {
            event.registerServerCommand(new CommandRegisterPlayerServer());
            event.registerServerCommand(new CommandPlayerExistsServer());
            event.registerServerCommand(new CommandDeletePlayer());
            event.registerServerCommand(new CommandConnectDBServer());
            event.registerServerCommand(new CommandTest());
            event.registerServerCommand(new CommandSkinChange());
            event.registerServerCommand(new CommandSetRestPassword());
            event.registerServerCommand(new CommandListUsers());
            event.registerServerCommand(new CommandDeleteRestPassword());
            event.registerServerCommand(new CommandGenToken());
            event.registerServerCommand(new CommandGetMyUUID());
            event.registerServerCommand(new CommandChangePlayerUUID());
            event.registerServerCommand(new CommandConfig());
        }

    }

    public void serverStarted(FMLServerStartedEvent event) {

    }

    public void serverStopping(FMLServerStoppingEvent event) {

    }

    public void serverStopped(FMLServerStoppedEvent event) {

    }
}
