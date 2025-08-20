package trollogyadherent.offlineauth.gui.skin;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
import trollogyadherent.offlineauth.Config;
import trollogyadherent.offlineauth.ConfigMixins;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.skin.cape.CapeObject;
import trollogyadherent.offlineauth.gui.skin.util.EntityUtil;
import trollogyadherent.offlineauth.gui.skin.util.FakeWorld;
import trollogyadherent.offlineauth.skin.client.ClientSkinUtil;
import trollogyadherent.offlineauth.skin.client.LegacyConversion;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class SkinGuiRenderTicker {
    private static Minecraft mcClient;
    private World world;
    private static final List<ItemStack> playerItems = new ArrayList<>();
    private static final Random random = new Random();
    private static boolean erroredOut = false;
    public static ResourceLocation skinResourceLocation = null;
    public static ResourceLocation capeResourceLocation = null;
    public static CapeObject capeObject = null;
    public static EntityClientPlayerMP clientPlayerMP = null;
    public static float yaw = 0;
    public static float pitch = 1;
    public static float angle = 180;
    public static float x = 0;
    public static float y = 1;
    public static float z = 0.03F;
    public static int xVelocity = 0;
    private static int previousMouseX = -1;
    private static boolean wasMousePressed = false;

    public SkinGuiRenderTicker() {
        mcClient = FMLClientHandler.instance().getClient();
    }
    
    public void reset() {
        OfflineAuth.info("Resetting skin menu render ticker");
        world = null;
        clientPlayerMP = null;
    }
    
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTick(TickEvent.RenderTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            if (!erroredOut && (mcClient.currentScreen instanceof SkinManagmentGUI)) {
                try {
                    if ((clientPlayerMP == null) || (clientPlayerMP.worldObj == null))
                        init();
                    
                    if ((world != null) && (clientPlayerMP != null)) {
                        
                        //If GUI is opened from server menu
                        //Fixes a bunch of NullPointerException (RenderLivingEvent is fired but thePlayer == null)
                        if (mcClient.thePlayer == null) {
                            mcClient.thePlayer = clientPlayerMP;
                        }
                        
                        ScaledResolution sr = new ScaledResolution(mcClient, mcClient.displayWidth, mcClient.displayHeight);
                        final int mouseX = (Mouse.getX() * sr.getScaledWidth()) / mcClient.displayWidth;
                        
                        final int mouseY = sr.getScaledHeight() - ((Mouse.getY() * sr.getScaledHeight()) / mcClient.displayHeight) - 1;
                        int distanceToSide = ((mcClient.currentScreen.width / 2) /*98*/) / 2 + 35;
                        float targetHeight = (float) (sr.getScaledHeight_double() / 3.0F) / 1.8F;
                        
                        if (skinResourceLocation != null) {
                            OfflineAuth.varInstanceClient.skinLocationField.set(clientPlayerMP, skinResourceLocation);
                        }
                        
                        if (Mouse.isButtonDown(0)) {
                            if (!wasMousePressed) {
                                wasMousePressed = true;
                                xVelocity = 0;
                            }
                            if (previousMouseX == -1) {
                                previousMouseX = mouseX;
                            } else if (mouseX != previousMouseX) {
                                int movementDelta = Math.abs(previousMouseX - mouseX);
                                if (previousMouseX >= mouseX) {
                                    xVelocity += Math.min(10, movementDelta);
                                } else {
                                    xVelocity -= Math.min(10, movementDelta);
                                }
                                previousMouseX = mouseX;
                            }
                        } else {
                            previousMouseX = -1;
                            wasMousePressed = false;
                        }
                        
                        if (xVelocity > 10) {
                            xVelocity = 10;
                        }
                        if (xVelocity < -10) {
                            xVelocity = -10;
                        }
                        
                        yaw += xVelocity;
                        
                        if (xVelocity > 0) {
                            xVelocity -= (int) event.renderTickTime;
                        } else {
                            xVelocity += (int) event.renderTickTime;
                        }
                        
                        EntityUtil.drawEntityOnScreen(
                                sr.getScaledWidth() - distanceToSide,
                                (int) (((float) sr.getScaledHeight() / 2) + (clientPlayerMP.height * targetHeight)) - 40,
                                targetHeight,
                                yaw,
                                yaw,
                                pitch,
                                clientPlayerMP);
                    }
                } catch (Throwable e) {
                    OfflineAuth.error("Player model rendering encountered a serious error and has been disabled for the remainder of this session.");
                    e.printStackTrace();
                    erroredOut = true;
                    clientPlayerMP = null;
                    world = null;
                }
            }
        }
    }
    
    private void init() {
        try {
            boolean createNewWorld = world == null;

            if (createNewWorld)
                world = new FakeWorld();

            if (createNewWorld || (clientPlayerMP == null)) {
                clientPlayerMP = new EntityClientPlayerMP(mcClient, world, mcClient.getSession(), null, null);
                clientPlayerMP.dimension = 0;
                clientPlayerMP.movementInput = new MovementInputFromOptions(mcClient.gameSettings);
                clientPlayerMP.eyeHeight = 1.82F;
                
                setRandomMobItem(clientPlayerMP);
            }

            RenderManager.instance.cacheActiveRenderInfo(world, mcClient.renderEngine, mcClient.fontRenderer, clientPlayerMP, clientPlayerMP, mcClient.gameSettings, 0.0F);
        }
        catch (Throwable e) {
            OfflineAuth.error("Main menu mob rendering encountered a serious error and has been disabled for the remainder of this session.");
            e.printStackTrace();
            erroredOut = true;
            clientPlayerMP = null;
            world = null;
        }
    }
    
    private static void setRandomMobItem(EntityLivingBase ent) {
        try {
            if (ent instanceof AbstractClientPlayer) {
                ent.setCurrentItemOrArmor(0, playerItems.get(random.nextInt(playerItems.size())));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    static {
        Collections.addAll(playerItems, new ItemStack(Items.iron_sword), new ItemStack(Items.diamond_sword), new ItemStack(Items.golden_sword),
                new ItemStack(Items.diamond_pickaxe), new ItemStack(Items.iron_pickaxe), new ItemStack(Items.iron_axe),
                new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_eye), new ItemStack(Items.diamond),
                new ItemStack(Items.firework_charge), new ItemStack(Items.fireworks), new ItemStack(Items.emerald), new ItemStack(Items.stick));

        if (OfflineAuth.isWitcheryLoaded) {
            Collections.addAll(playerItems, new ItemStack(GameRegistry.findItem("witchery", "ingredient"), 1, 9), new ItemStack(GameRegistry.findItem("witchery", "ingredient"), 1, 11), new ItemStack(GameRegistry.findItem("witchery", "vampirebook")));
        }
    }

    public void setSkin(String skinName) {
        File imageFile = ClientSkinUtil.getSkinFile(skinName);
        if (imageFile == null || !imageFile.exists()) {
            OfflineAuth.error("Error skin image does not exist: " + skinName);
            return;
        }
        if (!Util.pngIsSane(imageFile)) {
            OfflineAuth.error("Error loading skin image, not sane" + skinName);
            return;
        }
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            OfflineAuth.error("Error loading skin image " + skinName);
            return;
        }
        if(ConfigMixins.basicSkinBackport) {
            //2:1 -> 1:1
            if (bufferedImage.getWidth() / bufferedImage.getHeight() == 2) {
                bufferedImage = new LegacyConversion().convertToNew(bufferedImage);
            }
        } else {
            //1:1 -> 2:1
            if (bufferedImage.getWidth() == bufferedImage.getHeight()) {
                bufferedImage = new LegacyConversion().convertToOld(bufferedImage);
            }
        }
        skinResourceLocation = new ResourceLocation("offlineauth", "tickerskins/" + skinName);
        ClientSkinUtil.loadTexture(bufferedImage, skinResourceLocation);

        if (mcClient != null && clientPlayerMP != null) {
            setRandomMobItem(clientPlayerMP);
        }
    }

    public void setCape(String capeName) {
        File imageFile = ClientSkinUtil.getCapeFile(capeName);
        if (imageFile == null || !imageFile.exists()) {
            OfflineAuth.error("Error cape image does not exist: " + capeName);
            return;
        }
		if (imageFile.getName().endsWith(".png")) {
            if (!Util.pngIsSane(imageFile)) {
                OfflineAuth.error("Error loading cape image, not sane: " + capeName);
                return;
            }
        } else {
            if (ClientSkinUtil.getGifFrames(imageFile) == null) {
                OfflineAuth.error("Error loading cape image " + capeName);
                return;
            }
        }

        capeObject = ClientSkinUtil.getCapeObject(capeName);
    }

    public ResourceLocation getSkinResourceLocation() {
        return skinResourceLocation;
    }

    public ResourceLocation getCapeResourceLocation() {
        return capeResourceLocation;
    }

    public CapeObject getCapeObject() {
        return capeObject;
    }
}