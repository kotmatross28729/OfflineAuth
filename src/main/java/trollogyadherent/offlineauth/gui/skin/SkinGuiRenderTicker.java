package trollogyadherent.offlineauth.gui.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.skin.util.EntityUtil;
import trollogyadherent.offlineauth.gui.skin.util.FakeWorld;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.skin.client.LegacyConversion;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

@SideOnly(Side.CLIENT)
public class SkinGuiRenderTicker
{
    private static Minecraft mcClient;
    private static boolean isRegistered = false;
    private World world;
    @SuppressWarnings("unused")
    private GuiScreen savedScreen;
    //private static ItemStack[]                     playerItems;
    private static List<ItemStack> playerItems = new ArrayList<>();
    private static Random random = new Random();

    private static Set entities;
    private static Object[] entStrings;
    private static int id;

    private static boolean erroredOut = false;

    private static ResourceLocation skinResourceLocation = null;
    private static EntityClientPlayerMP clientPlayerMP = null;

    public SkinGuiRenderTicker()
    {
        mcClient = FMLClientHandler.instance().getClient();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event)
    {

        if (!erroredOut && (mcClient.currentScreen instanceof SkinManagmentGUI))
        {
            try
            {
                if ((clientPlayerMP == null) || (clientPlayerMP.worldObj == null))
                    init();

                if ((world != null) && (clientPlayerMP != null))
                {
                    ScaledResolution sr = new ScaledResolution(mcClient, mcClient.displayWidth, mcClient.displayHeight);
                    final int mouseX = (Mouse.getX() * sr.getScaledWidth()) / mcClient.displayWidth;
                    final int mouseY = sr.getScaledHeight() - ((Mouse.getY() * sr.getScaledHeight()) / mcClient.displayHeight) - 1;
                    int distanceToSide = ((mcClient.currentScreen.width / 2) /*98*/) / 2 + 35;
                    float targetHeight = (float) (sr.getScaledHeight_double() / 3.0F) / 1.8F;

                    if (skinResourceLocation != null) {
                        OfflineAuth.varInstanceClient.skinLocationfield.set(clientPlayerMP, skinResourceLocation);
                    }

                    EntityUtil.drawEntityOnScreen(
                            sr.getScaledWidth() - distanceToSide,
                            (int) ((sr.getScaledHeight() / 2) + (clientPlayerMP.height * targetHeight)) - 40,
                            targetHeight,
                            sr.getScaledWidth() - distanceToSide - mouseX,
                            ((sr.getScaledHeight() / 2) + (clientPlayerMP.height * targetHeight)) - (clientPlayerMP.height * targetHeight * (clientPlayerMP.getEyeHeight() / clientPlayerMP.height)) - mouseY, clientPlayerMP);
                }
            }
            catch (Throwable e)
            {
                OfflineAuth.error("Player model rendering encountered a serious error and has been disabled for the remainder of this session.");
                e.printStackTrace();
                erroredOut = true;
                clientPlayerMP = null;
                world = null;
            }
        }
    }

    private void init()
    {
        try
        {
            boolean createNewWorld = world == null;

            if (createNewWorld)
                world = new FakeWorld();

            if (createNewWorld || (clientPlayerMP == null))
            {
                /*mcClient.thePlayer = new EntityClientPlayerMP(mcClient, world, mcClient.getSession(), null, null);
                mcClient.thePlayer.dimension = 0;
                mcClient.thePlayer.movementInput = new MovementInputFromOptions(mcClient.gameSettings);
                mcClient.thePlayer.eyeHeight = 1.82F;
                setRandomMobItem(mcClient.thePlayer);*/
                clientPlayerMP = new EntityClientPlayerMP(mcClient, world, mcClient.getSession(), null, null);
                clientPlayerMP.dimension = 0;
                clientPlayerMP.movementInput = new MovementInputFromOptions(mcClient.gameSettings);
                clientPlayerMP.eyeHeight = 1.82F;
                setRandomMobItem(clientPlayerMP);
            }

            RenderManager.instance.cacheActiveRenderInfo(world, mcClient.renderEngine, mcClient.fontRenderer, clientPlayerMP, clientPlayerMP, mcClient.gameSettings, 0.0F);
            savedScreen = mcClient.currentScreen;
        }
        catch (Throwable e)
        {
            OfflineAuth.error("Main menu mob rendering encountered a serious error and has been disabled for the remainder of this session.");
            e.printStackTrace();
            erroredOut = true;
            clientPlayerMP = null;
            world = null;
        }
    }

    private static EntityLivingBase getNextEntity(World world)
    {
        Class clazz;
        int tries = 0;
        do
        {
            if (++id >= entStrings.length)
                id = 0;
            clazz = (Class) EntityList.stringToClassMapping.get(entStrings[id]);
        }
        while (!EntityLivingBase.class.isAssignableFrom(clazz) && (++tries <= 5));

        return (EntityLivingBase) EntityList.createEntityByName((String) entStrings[id], world);
    }

    private static void setRandomMobItem(EntityLivingBase ent)
    {
        try
        {
            if (ent instanceof AbstractClientPlayer) {
                ent.setCurrentItemOrArmor(0, playerItems.get(random.nextInt(playerItems.size())));
            }
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public void register()
    {
        if (!isRegistered)
        {
            OfflineAuth.info("Enabling skin menu render ticker");
            FMLCommonHandler.instance().bus().register(this);
            isRegistered = true;
        }
    }

    public void unRegister()
    {
        if (isRegistered)
        {
            OfflineAuth.info("Disabling skin menu render ticker");
            FMLCommonHandler.instance().bus().unregister(this);
            isRegistered = false;
            world = null;
            clientPlayerMP = null;
        }
    }

    public boolean isRegistered()
    {
        return isRegistered;
    }

    static
    {
        Collections.addAll(playerItems, new ItemStack(Items.iron_sword), new ItemStack(Items.diamond_sword), new ItemStack(Items.golden_sword),
                new ItemStack(Items.diamond_pickaxe), new ItemStack(Items.iron_pickaxe), new ItemStack(Items.iron_axe),
                new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_eye), new ItemStack(Items.diamond),
                new ItemStack(Items.firework_charge), new ItemStack(Items.fireworks),
                new ItemStack(Items.nether_star), new ItemStack(Items.emerald), new ItemStack(Items.stick), new ItemStack(GameRegistry.findItem("minecraft", "golden_apple"), 1, 1));

        if (Loader.isModLoaded("witchery")) {
            Collections.addAll(playerItems, new ItemStack(GameRegistry.findItem("witchery", "ingredient"), 1, 9), new ItemStack(GameRegistry.findItem("witchery", "ingredient"), 1, 11), new ItemStack(GameRegistry.findItem("witchery", "vampirebook")));
        }

        // Get a COPY dumbass!
        entities = new TreeSet(EntityList.stringToClassMapping.keySet());
        entStrings = entities.toArray(new Object[] {});
        id = -1;
    }

    public void setSkin(String skinName) {
        File imageFile = ClientSkinUtil.getSkinFile(skinName);
        if (imageFile == null || !imageFile.exists()) {
            OfflineAuth.error("Error skin image does not exist: " + skinName);
            return;
        }
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            OfflineAuth.error("Error loading skin image " + skinName);
            return;
        }
        if (bufferedImage.getHeight() == 64) {
            bufferedImage = new LegacyConversion().convert(bufferedImage);
        }
        ClientSkinUtil.OfflineTextureObject offlineTextureObject = new ClientSkinUtil.OfflineTextureObject(bufferedImage);
        skinResourceLocation = new ResourceLocation("offlineauth", "tickerskins/" + skinName);
        ClientSkinUtil.loadTexture(bufferedImage, skinResourceLocation, offlineTextureObject);

        if (mcClient != null && clientPlayerMP != null) {
            setRandomMobItem(clientPlayerMP);
        }
    }

    public boolean isSkinResourceLocationNull() {
        return skinResourceLocation == null;
    }

    public ResourceLocation getSkinResourceLocation() {
        return skinResourceLocation;
    }
}