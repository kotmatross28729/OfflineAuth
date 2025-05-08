package trollogyadherent.offlineauth;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.common.MinecraftForge;
import trollogyadherent.offlineauth.command.CommandChangePlayerDisplayname;
import trollogyadherent.offlineauth.command.CommandConfig;
import trollogyadherent.offlineauth.command.CommandDeleteCape;
import trollogyadherent.offlineauth.command.CommandDeletePlayer;
import trollogyadherent.offlineauth.command.CommandDeleteSkin;
import trollogyadherent.offlineauth.command.CommandGenToken;
import trollogyadherent.offlineauth.command.CommandGetMyName;
import trollogyadherent.offlineauth.command.CommandGetMyUUID;
import trollogyadherent.offlineauth.command.CommandGetServerFingerprint;
import trollogyadherent.offlineauth.command.CommandListUsers;
import trollogyadherent.offlineauth.command.CommandPlayerExistsServer;
import trollogyadherent.offlineauth.command.CommandRegCooldown;
import trollogyadherent.offlineauth.command.CommandRegisterPlayerServer;
import trollogyadherent.offlineauth.command.CommandTest;
import trollogyadherent.offlineauth.database.Database;
import trollogyadherent.offlineauth.event.ServerEventListener;
import trollogyadherent.offlineauth.packet.PacketHandler;
import trollogyadherent.offlineauth.rest.Rest;
import trollogyadherent.offlineauth.skin.server.ServerSkinUtil;
import trollogyadherent.offlineauth.util.ServerUtil;
import trollogyadherent.offlineauth.util.Util;
import trollogyadherent.offlineauth.varinstances.server.VarInstanceServer;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc., and register them with the GameRegistry."
    public void preInit(FMLPreInitializationEvent event) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        if (Util.isServer()) {
            OfflineAuth.varInstanceServer = new VarInstanceServer();
        }
        
        /* Config */
        OfflineAuth.confFile = event.getSuggestedConfigurationFile();
        if (Util.isServer()) {
            Config.synchronizeConfigurationServer(false);
        } else {
            Config.synchronizeConfigurationClient(false, true);
        }

        OfflineAuth.info("Debug Mode: " + OfflineAuth.isDebugMode());

        /* Initialize database */
        if (Util.isServer()) {
            if(!Database.initialize()) {
                OfflineAuth.varInstanceServer = null;
                OfflineAuth.info("Database not initialized, returning from preinit...");
                return;
            }
        }

        FMLCommonHandler.instance().bus().register(this);

        /* Generate server cryptographic keypair if none are present */
        if (Util.isServer()) {
            ServerUtil.generateServerKeys();
        }

        /* Start Spark server */
        if (Util.isServer()) {
            Rest.restStart();
        }

        /* If there are no default skins in the default skin directory, unpacks its own skins */
        if (Util.isServer()) {
            File defaultSkinDir = new File(OfflineAuth.varInstanceServer.defaultServerSkinsPath);
            String[] fileList = defaultSkinDir.list();
            if (fileList == null) {
                OfflineAuth.error("Could not get default server skin directory!");
                return;
            }
            if (fileList.length == 0) {
                OfflineAuth.info("No skins present in the default skin directory, populating it with default ones");
                ServerSkinUtil.transferDefaultSkins();
            }
        }

        /* Clearing skin cache */
        if (Util.isServer()) {
            ServerSkinUtil.clearSkinAndCapeCache();
        }

        /* Packets */
        //OfflineAuth.simpleNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("offlineauth");
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
        if (Util.isServer()) {
            if (OfflineAuth.DEBUG_MODE) {
                //event.registerServerCommand(new CommandUUID());
                //event.registerServerCommand(new CommandConnectDBServer());
                event.registerServerCommand(new CommandTest());
                //event.registerServerCommand(new CommandSetRestPassword());
                //event.registerServerCommand(new CommandGetMyUUID());
                //event.registerServerCommand(new CommandChangePlayerUUID());
                //event.registerServerCommand(new CommandDeleteRestPassword());
            }

            event.registerServerCommand(new CommandRegisterPlayerServer());
            event.registerServerCommand(new CommandPlayerExistsServer());
            event.registerServerCommand(new CommandDeletePlayer());
            event.registerServerCommand(new CommandListUsers());
            event.registerServerCommand(new CommandGenToken());
            event.registerServerCommand(new CommandConfig());
            event.registerServerCommand(new CommandDeleteSkin());
            event.registerServerCommand(new CommandGetServerFingerprint());
            event.registerServerCommand(new CommandDeleteCape());
            event.registerServerCommand(new CommandChangePlayerDisplayname());
            event.registerServerCommand(new CommandRegCooldown());
        }
        event.registerServerCommand(new CommandGetMyName());
        event.registerServerCommand(new CommandGetMyUUID());
    }

    public void serverStarted(FMLServerStartedEvent event) {
        //Annihilate online-mode
        // (in fact, at this point online-mode is already disabled via mixins, this is just a visual fatality for server.properties)
        if (Util.isServer() && MinecraftServer.getServer().isDedicatedServer()) {
            DedicatedServer dedicatedServer = (DedicatedServer) MinecraftServer.getServer();
            
            if(dedicatedServer.getBooleanProperty("online-mode", false)) {
                OfflineAuth.error("online-mode=true in server.properties, disabling...");
                
                if (dedicatedServer.getBooleanProperty("online-mode", false))
                    dedicatedServer.setProperty("online-mode", false);
                
                OfflineAuth.info("online-mode was successfully disabled in server.properties");
            }
        }
    }

    public void serverStopping(FMLServerStoppingEvent event) {
        if (Util.isServer()) {
            Database.close();
        }
    }

    public void serverStopped(FMLServerStoppedEvent event) {

    }
}
