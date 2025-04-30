package trollogyadherent.offlineauth;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.event.*;
import trollogyadherent.offlineauth.clientdata.ClientData;
import trollogyadherent.offlineauth.event.ClientEventListener;
import trollogyadherent.offlineauth.gui.GuiHandler;
import trollogyadherent.offlineauth.gui.skin.GameOverlayGuiHandler;
import trollogyadherent.offlineauth.gui.skin.SkinGuiHandler;
import trollogyadherent.offlineauth.gui.skin.SkinGuiRenderTicker;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.varinstances.client.VarInstanceClient;

///import trollogyadherent.offlineauth.data.GsonTester;
///import trollogyadherent.offlineauth.server.ServerHandler;
///import trollogyadherent.offlineauth.server.ServerPinger;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class ClientProxy extends CommonProxy {

    //private SkinGuiRenderTicker skinGuiRenderTicker;

    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc., and register them with the GameRegistry."
    public void preInit(FMLPreInitializationEvent event) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        super.preInit(event);
        OfflineAuth.varInstanceClient = new VarInstanceClient();

        /* Config, sync is in common proxy */
        MinecraftForge.EVENT_BUS.register(new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new SkinGuiHandler());
        MinecraftForge.EVENT_BUS.register(new GameOverlayGuiHandler(Minecraft.getMinecraft()));
        //Config.synchronizeConfigurationClient(event.getSuggestedConfigurationFile());

        /* Data file containing server infos */
        OfflineAuth.varInstanceClient.datafile = new File(OfflineAuth.varInstanceClient.serverDataJSONpath);


        ///OfflineAuth.serverPinger = new ServerPinger();
        ///FMLCommonHandler.instance().bus().register(new ServerHandler());
        ///Secure.init();
        ///GsonTester.test();

        /* List containing all cached OAServerData objects */
        ClientData.loadData();

        /*  */
        /*ClientListener clientListener = new ClientListener();
        MinecraftForge.EVENT_BUS.register(clientListener);
        FMLCommonHandler.instance().bus().register(clientListener);*/

        ClientEventListener clientPlayerJoined = new ClientEventListener();
        MinecraftForge.EVENT_BUS.register(clientPlayerJoined);
        FMLCommonHandler.instance().bus().register(clientPlayerJoined);

        /* If there are no default skins in the default skin directory, unpacks its own skins */
        File defaultSkinDir = new File(OfflineAuth.varInstanceClient.clientSkinsPath);
        String[] fileList = defaultSkinDir.list();
        if (fileList == null) {
            OfflineAuth.error("Could not get default server skin directory!");
        } else {
            if (fileList.length == 0) {
                OfflineAuth.info("No skins present in the default skin directory, populating it with default ones");
                ClientSkinUtil.transferDefaultSkins();
            }
        }

        /* If there are no default skins in the default cape directory, unpacks its own capes */
        File defaultCapeDir = new File(OfflineAuth.varInstanceClient.clientCapesPath);
        fileList = defaultCapeDir.list();
        if (fileList == null) {
            OfflineAuth.error("Could not get default server capes directory!");
        } else {
            if (fileList.length == 0) {
                OfflineAuth.info("No capes present in the default cape directory, populating it with default ones");
                ClientSkinUtil.transferDefaultCapes();
            }
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
        if (evt.modID.equals("offlineauth")) {
            if (OfflineAuth.confFile != null) {
                Config.synchronizeConfigurationClient(false, true);
            }
        }
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent event) {
        //skinGuiRenderTicker = new SkinGuiRenderTicker();
        //FMLCommonHandler.instance().bus().register(skinGuiRenderTicker);
        //MinecraftForge.EVENT_BUS.register(skinGuiRenderTicker);
        OfflineAuth.varInstanceClient.skinGuiRenderTicker = new SkinGuiRenderTicker();
        FMLCommonHandler.instance().bus().register(OfflineAuth.varInstanceClient.skinGuiRenderTicker);
        MinecraftForge.EVENT_BUS.register(OfflineAuth.varInstanceClient.skinGuiRenderTicker);

        super.postInit(event);
    }

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        super.serverAboutToStart(event);
    }

    // register server commands in this event handler
    public void serverStarting(FMLServerStartingEvent event) {
        super.serverStarting(event);
    }

    public void serverStarted(FMLServerStartedEvent event) {
        super.serverStarted(event);
    }

    public void serverStopping(FMLServerStoppingEvent event) {
        super.serverStopping(event);
    }

    public void serverStopped(FMLServerStoppedEvent event) {
        super.serverStopped(event);
    }

}