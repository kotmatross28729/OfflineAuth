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
import trollogyadherent.offlineauth.varinstances.client.VarInstanceClient;

///import trollogyadherent.offlineauth.data.GsonTester;
///import trollogyadherent.offlineauth.server.ServerHandler;
///import trollogyadherent.offlineauth.server.ServerPinger;

import java.io.File;

public class ClientProxy extends CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items,
    // etc., and register them with the GameRegistry."
    public void preInit(FMLPreInitializationEvent event) 	{
        super.preInit(event);
        OfflineAuth.varInstanceClient = new VarInstanceClient();

        /* Config, sync is in common proxy */
        MinecraftForge.EVENT_BUS.register(new GuiHandler());
        //Config.synchronizeConfigurationClient(event.getSuggestedConfigurationFile());

        /* Data file containing server infos */
        OfflineAuth.varInstanceClient.datafile = new File(Minecraft.getMinecraft().mcDataDir.getPath(), "offlineauth.json");


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
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent evt) {
        System.out.println("bruuuuuh");
        if (evt.modID.equals("offlineauth")) {
            if (OfflineAuth.confFile != null) {
                Config.synchronizeConfigurationClient(OfflineAuth.confFile);
            }
        }
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent event) {
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