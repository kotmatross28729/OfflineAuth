package trollogyadherent.offlineauth.skin.client;

import com.google.common.io.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.util.Util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class ClientSkinUtil {
    public static class OfflineTextureObject extends AbstractTexture
    {

        private final BufferedImage image;

        public OfflineTextureObject(BufferedImage image)
        {
            this.image = image;
        }

        public BufferedImage getImage()
        {
            return image;
        }

        @Override
        public void loadTexture(IResourceManager arg0)
        {
            deleteGlTexture();

            TextureUtil.uploadTextureImageAllocate(getGlTextureId(), image, false, false);
        }

    }

    public static BufferedImage bufferedImageFromskinCache(String name)
    {
        try
        {
            //BufferedImage result = ImageIO.read(new File(new File(Minecraft.getMinecraft().mcDataDir, "cachedImages"), name));
            BufferedImage result = ImageIO.read(new File(new File(OfflineAuth.varInstanceClient.clientSkinCachePath), name + ".png"));
            /*if (result.getWidth() != 64 || (result.getHeight() != 64 && result.getHeight() != 32)) {
                return null;
            }*/
            if (result.getHeight() == 64) {
                result = new LegacyConversion().convert(result);
            }
            return result;
        }
        catch (IOException e)
        {
            OfflineAuth.error("Can't read image: " + name);
            return null;
        }
    }

    public static void loadTexture(BufferedImage bufferedImage, ResourceLocation resourceLocation, OfflineTextureObject offlineTextureObject) {
        if (bufferedImage == null || resourceLocation == null || offlineTextureObject == null) {
            OfflineAuth.error("Error loading texture!");
            return;
        }
        if (OfflineAuth.varInstanceClient.textureManager == null) {
            OfflineAuth.varInstanceClient.textureManager = Minecraft.getMinecraft().getTextureManager();
        }
        OfflineAuth.varInstanceClient.textureManager.loadTexture(resourceLocation, offlineTextureObject);
    }

    public static ResourceLocation loadSkinFromCache(String skinName) {
        BufferedImage img = bufferedImageFromskinCache(skinName);
        if (img == null) {
            return null;
        }
        OfflineTextureObject obj = new ClientSkinUtil.OfflineTextureObject(img);
        ResourceLocation location = new ResourceLocation("offlineauth", "skins/" + skinName);
        loadTexture(img, location, obj);
        return location;
    }

    public static boolean skinPresentOnClient(String name) {
        return Util.fileExists(new File(OfflineAuth.varInstanceClient.clientSkinCachePath, name + ".png"));
    }

    public static File getSkinFile(String name) {
        if (Util.fileExists(new File(OfflineAuth.varInstanceClient.clientSkinCachePath, name + ".png"))) {
            return new File(OfflineAuth.varInstanceClient.clientSkinCachePath, name + ".png");
        } else if (Util.fileExists(new File(OfflineAuth.varInstanceClient.clientSkinsPath, name + ".png"))) {
            return new File(OfflineAuth.varInstanceClient.clientSkinsPath, name + ".png");
        }
        return null;
    }

    public static void stringToClientSkin(String base64skin, String name) throws IOException {
        Util.bs64SaveToFile(base64skin, new File(OfflineAuth.varInstanceClient.clientSkinCachePath, name + ".png"));
    }

    public static void bytesToClientSkin(byte[] bytes, String name) throws IOException {
        Util.bytesSaveToFile(bytes, new File(OfflineAuth.varInstanceClient.clientSkinCachePath, name + ".png"));
    }

    @SuppressWarnings("CheckReturnValue")
    public static void removeSkinFromCache(String name) {
        File skin = new File(OfflineAuth.varInstanceClient.clientSkinCachePath, name + ".png");
        if (skin.exists()) {
            skin.delete();
        }
    }

    public static void clearSkinCache() {
        try {
            FileUtils.cleanDirectory(new File(OfflineAuth.varInstanceClient.clientSkinCachePath));
        } catch (IOException e) {
            OfflineAuth.error("Failed to clear client skin cache");
        }
    }

    public static String localSkinPathFromName(String skinName) {
        String path = OfflineAuth.varInstanceClient.clientSkinsPath + File.separator + skinName + ".png";
        File temp = new File(path);
        if (!temp.exists()) {
            return null;
        }
        return path;
    }
    public static byte[] skinToBytes(String skinName) {
        String skinPath = localSkinPathFromName(skinName);
        if (skinPath == null) {
            return new byte[1];
        }
        try {
            return Util.fileToBytes(new File(skinPath));
        } catch (IOException e) {
            OfflineAuth.error("Couldn't read local skin: " + skinPath);
            return new byte[1];
        }
    }

    public static String[] getAvailableSkinNames() {
        File skinDir = new File(OfflineAuth.varInstanceClient.clientSkinsPath);
        String[] fileList = skinDir.list();
        if (fileList == null) {
            OfflineAuth.error("Could not get skin directory!");
            return null;
        }
        int pngCount = 0;
        for (String s : fileList) {
            if (Files.getFileExtension(s).equals("png")) {
                pngCount ++;
            }
        }
        String[] res = new String[pngCount];
        int i = 0;
        for (String s : fileList) {
            if (Files.getFileExtension(s).equals("png")) {
                res[i] = Files.getNameWithoutExtension(s);
                i++;
            }
        }
        return res;
    }


    public static void transferDefaultSkins() throws IOException {
        String[] skins = {"dream", "popbob", "herobrine", "chuck", "rezi", "popularmmos"};
        for (String skin : skins) {
            InputStream is = ClientSkinUtil.class.getResourceAsStream("/assets/offlineauth/textures/defaultskins/client/" + skin + ".png");
            if (is == null) {
                OfflineAuth.error("Default skin '" + skin + "' resource not found!");
                continue;
            }
            BufferedImage img = ImageIO.read(is);
            File output = new File(OfflineAuth.varInstanceClient.clientSkinsPath + File.separator + skin + ".png");
            ImageIO.write(img, "png", output);
        }
    }

    public static void setLastUsedOfflineSkinName(String name) {
        try {
            FileWriter writer = new FileWriter(OfflineAuth.varInstanceClient.lastUsedOfflineSkinFile);
            writer.write(name);
            writer.close();
        } catch (IOException e) {
            OfflineAuth.error("Failed to save last used offline skin to file!");
            e.printStackTrace();
        }
    }

    public static String getLastUsedOfflineSkinName() {
        try {
            return new BufferedReader(new FileReader(OfflineAuth.varInstanceClient.lastUsedOfflineSkinFile)).readLine();
        } catch (IOException e) {
            OfflineAuth.error("Failed to load last used offline skin from file!");
            e.printStackTrace();
            return null;
        }
    }
}
