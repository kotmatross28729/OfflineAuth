package trollogyadherent.offlineauth.skin.client;

import com.google.common.io.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import trollogyadherent.offlineauth.OfflineAuth;
import trollogyadherent.offlineauth.gui.skin.cape.CapeObject;
import trollogyadherent.offlineauth.util.GifDecoder;
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
            if (!Util.pngIsSane(new File(new File(OfflineAuth.varInstanceClient.clientSkinCachePath), name + ".png"))) {
                OfflineAuth.error("Image not sane: " + name);
                return null;
            }
            BufferedImage result = ImageIO.read(new File(new File(OfflineAuth.varInstanceClient.clientSkinCachePath), name + ".png"));
            if (result == null) {
                return null;
            }
            if(!OfflineAuth.isSSBLoaded) {
                if (result.getHeight() == 64) {
                    result = new LegacyConversion().convert(result);
                }
            }
            return result;
        }
        catch (IOException e)
        {
            OfflineAuth.error("Can't read image: " + name);
            return null;
        }
    }
    
    
    public static BufferedImage bufferedImageFromskinCacheQuiet(String name) {
        try {
            if (!Util.pngIsSane(new File(new File(OfflineAuth.varInstanceClient.clientSkinCachePath), name + ".png"))) {
                return null;
            }
            BufferedImage result = ImageIO.read(new File(new File(OfflineAuth.varInstanceClient.clientSkinCachePath), name + ".png"));
            if (result == null) {
                return null;
            }
            if(!OfflineAuth.isSSBLoaded) {
                if (result.getHeight() == 64) {
                    result = new LegacyConversion().convert(result);
                }
            }
            return result;
        }
        catch (IOException e) {
            return null;
        }
    }

    public static void loadTexture(BufferedImage bufferedImage, ResourceLocation resourceLocation) {
        if (bufferedImage == null || resourceLocation == null) {
            OfflineAuth.error("Error loading texture!");
            return;
        }
        if (OfflineAuth.varInstanceClient.textureManager == null) {
            OfflineAuth.varInstanceClient.textureManager = Minecraft.getMinecraft().getTextureManager();
        }
        OfflineTextureObject offlineTextureObject = new ClientSkinUtil.OfflineTextureObject(bufferedImage);
        OfflineAuth.varInstanceClient.textureManager.loadTexture(resourceLocation, offlineTextureObject);
    }

    public static ResourceLocation loadSkinFromCache(String skinName) {
        BufferedImage img = bufferedImageFromskinCache(skinName);
        if (img == null) {
            return null;
        }
        ResourceLocation location = new ResourceLocation("offlineauth", "skins/" + skinName);
        loadTexture(img, location);
        return location;
    }
    
    public static ResourceLocation loadSkinFromCacheQuiet(String skinName) {
        BufferedImage img = bufferedImageFromskinCacheQuiet(skinName);
        if (img == null) {
            return null;
        }
        ResourceLocation location = new ResourceLocation("offlineauth", "skins/" + skinName);
        loadTexture(img, location);
        return location;
    }

    public static boolean skinPresentOnClient(String name) {
        return Util.fileExists(new File(OfflineAuth.varInstanceClient.clientSkinCachePath, name + ".png"));
    }

    public static boolean capePresentOnClient(String name) {
        return Util.fileExists(new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".png")) || Util.fileExists(new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".gif"));
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

    public static void bytesToClientCape(byte[] bytes, String name) throws IOException {
        if (Util.imageIsSane(new ByteArrayInputStream(bytes))) {
            if (Util.imageIsPng(new ByteArrayInputStream(bytes))) {
                Util.bytesSaveToFile(bytes, new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".png"));
            } else {
                Util.bytesSaveToFile(bytes, new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".gif"));
            }
        }
    }

    @SuppressWarnings("CheckReturnValue")
    public static void removeSkinFromCache(String name) {
        File skin = new File(OfflineAuth.varInstanceClient.clientSkinCachePath, name + ".png");
        if (skin.exists()) {
            skin.delete();
        }
    }

    public static void removeCapeFromCache(String name) {
        File skin = new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".png");
        if (skin.exists()) {
            skin.delete();
        }
        skin = new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".gif");
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
        byte[] res = Util.fileToBytes(new File(skinPath));
        if (res == null) {
            OfflineAuth.error("Couldn't read local skin: " + skinPath);
            return new byte[1];
        }
        return res;
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
            if (!Util.pngIsSane(ClientSkinUtil.class.getResourceAsStream("/assets/offlineauth/textures/defaultskins/client/" + skin + ".png"))) {
                OfflineAuth.error("Default skin '" + skin + "' is not sane!");
                continue;
            }
            BufferedImage img = ImageIO.read(is);
            File output = new File(OfflineAuth.varInstanceClient.clientSkinsPath + File.separator + skin + ".png");
            ImageIO.write(img, "png", output);
        }
    }

    public static void transferDefaultCapes() throws IOException {
        String[] capes = {"Forest Dusk.png", "Pepe Lunar.gif"};
        for (String cape : capes) {
            InputStream is = ClientSkinUtil.class.getResourceAsStream("/assets/offlineauth/textures/defaultcapes/client/" + cape);
            if (is == null) {
                OfflineAuth.error("Default skin '" + cape + "' resource not found!");
                continue;
            }
            if (!Util.imageIsSane(ClientSkinUtil.class.getResourceAsStream("/assets/offlineauth/textures/defaultcapes/client/" + cape))) {
                OfflineAuth.error("Default skin '" + cape + "' is not sane!");
                continue;
            }
            saveBytesToCapes(is, cape);
        }
    }

    public static void saveBytesToCapes(InputStream is, String filename) {
        try {
            Util.inputStreamSaveToFile(is, new File(OfflineAuth.varInstanceClient.clientCapesPath + File.separator + filename));
        } catch (IOException e) {
            OfflineAuth.error("Failed to save cape: " + filename);
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
            File temp = new File(OfflineAuth.varInstanceClient.lastUsedOfflineSkinFile);
            if (!temp.exists()) {
                return null;
            }
            return new BufferedReader(new FileReader(OfflineAuth.varInstanceClient.lastUsedOfflineSkinFile)).readLine();
        } catch (IOException e) {
            OfflineAuth.error("Failed to load last used offline skin from file!");
            e.printStackTrace();
            return null;
        }
    }

    public static void removeLastUsedOfflineSkinName() {
        File temp = new File(OfflineAuth.varInstanceClient.lastUsedOfflineSkinFile);
        if (temp.exists()) {
            temp.delete();
        }
    }

    public static void setLastUsedOfflineCapeName(String name) {
        try {
            FileWriter writer = new FileWriter(OfflineAuth.varInstanceClient.lastUsedOfflineCapeFile);
            writer.write(name);
            writer.close();
        } catch (IOException e) {
            OfflineAuth.error("Failed to save last used offline cape to file!");
            e.printStackTrace();
        }
    }

    public static String getLastUsedOfflineCapeName() {
        try {
            File temp = new File(OfflineAuth.varInstanceClient.lastUsedOfflineCapeFile);
            if (!temp.exists()) {
                return null;
            }
            return new BufferedReader(new FileReader(OfflineAuth.varInstanceClient.lastUsedOfflineCapeFile)).readLine();
        } catch (IOException e) {
            OfflineAuth.error("Failed to load last used offline cape from file!");
            e.printStackTrace();
            return null;
        }
    }

    public static void removeLastUsedOfflineCapeName() {
        File temp = new File(OfflineAuth.varInstanceClient.lastUsedOfflineCapeFile);
        if (temp.exists()) {
            temp.delete();
        }
    }

    public static File getCapeFile(String name) {
        if (Util.fileExists(new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".png"))) {
            return new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".png");
        } else if (Util.fileExists(new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".gif"))) {
            return new File(OfflineAuth.varInstanceClient.clientCapeCachePath, name + ".gif");
        } else if (Util.fileExists(new File(OfflineAuth.varInstanceClient.clientCapesPath, name + ".png"))) {
            return new File(OfflineAuth.varInstanceClient.clientCapesPath, name + ".png");
        } else if (Util.fileExists(new File(OfflineAuth.varInstanceClient.clientCapesPath, name + ".gif"))) {
            return new File(OfflineAuth.varInstanceClient.clientCapesPath, name + ".gif");
        }
        return null;
    }

    public static String[] getAvailableCapeNames() {
        File capeDir = new File(OfflineAuth.varInstanceClient.clientCapesPath);
        String[] fileList = capeDir.list();
        if (fileList == null) {
            OfflineAuth.error("Could not get cape directory!");
            return null;
        }
        int pngCount = 0;
        for (String s : fileList) {
            if (Files.getFileExtension(s).equals("png") || Files.getFileExtension(s).equals("gif")) {
                pngCount ++;
            }
        }
        String[] res = new String[pngCount];
        int i = 0;
        for (String s : fileList) {
            if (Files.getFileExtension(s).equals("png") || Files.getFileExtension(s).equals("gif")) {
                res[i] = Files.getNameWithoutExtension(s);
                i++;
            }
        }
        return res;
    }

    /* in milliseconds */
    public static float getGifFrameLength(File gif) {
        GifDecoder gifDecoder = new GifDecoder();
        InputStream is;
        try {
            is = new FileInputStream(gif);
        } catch (FileNotFoundException e) {
            OfflineAuth.error("Failed to read gif: " + gif.getAbsolutePath());
            e.printStackTrace();
            return -1;
        }
        try {
            gifDecoder.read(is);
        } catch (IOException e) {
            OfflineAuth.error("Failed to read gif (possibly not sane): " + gif.getAbsolutePath());
            return -1;
        }
        int n = gifDecoder.getFrameCount();
        if (n == 0) {
            return -1;
        } else {
            return gifDecoder.getDelay(0);
        }
    }


    public static BufferedImage[] getGifFrames(File gif) {
        GifDecoder gifDecoder = new GifDecoder();
        InputStream is;
        try {
            is = new FileInputStream(gif);
        } catch (FileNotFoundException e) {
            OfflineAuth.error("Failed to read gif: " + gif.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
        try {
            gifDecoder.read(is);
        } catch (IOException e) {
            OfflineAuth.error("Failed to read gif (possibly not sane): " + gif.getAbsolutePath());
            return null;
        }
        int n = gifDecoder.getFrameCount();
        if (n == 0) {
            return null;
        }
        BufferedImage[] res = new BufferedImage[n];
        for (int i = 0; i < n; i++) {
            res[i] = gifDecoder.getFrame(i);  // frame i
        }
        return res;
    }

    public static BufferedImage getFirstGifFrame(File gif) {
        GifDecoder gifDecoder = new GifDecoder();
        InputStream is;
        try {
            is = new FileInputStream(gif);
        } catch (FileNotFoundException e) {
            OfflineAuth.error("Failed to read gif: " + gif.getAbsolutePath());
            e.printStackTrace();
            return null;
        }
        try {
            gifDecoder.read(is);
        } catch (IOException e) {
            OfflineAuth.error("Failed to read gif (possibly not sane): " + gif.getAbsolutePath());
            return null;
        }
        int n = gifDecoder.getFrameCount();
        if (n == 0) {
            return null;
        }
        return gifDecoder.getFrame(0);
    }

    public static CapeObject getCapeObject(String name) {
        File capeFile = getCapeFile(name);
        if (capeFile == null || !capeFile.exists()) {
            return new CapeObject(new ResourceLocation[0], -1);
        }

        float frameLen = -1;
        if (capeFile.getName().endsWith(".gif")) {
            frameLen = getGifFrameLength(capeFile);
        }
        BufferedImage[] bufferedImages = null;
        if (capeFile.getName().endsWith(".png")) {
            if (!Util.pngIsSane(capeFile)) {
                OfflineAuth.error("Failed to read cape image, not sane: " + name);
                return new CapeObject(new ResourceLocation[0], -1);
            }
            bufferedImages = new BufferedImage[1];
            try {
                bufferedImages[0] = ImageIO.read(capeFile);
            } catch (IOException e) {
                OfflineAuth.error("Failed to read cape image: " + name);
                return new CapeObject(new ResourceLocation[0], -1);
            }
        } else {
            BufferedImage[] res = getGifFrames(capeFile);
            if (res != null) {
                bufferedImages = res;
            }
        }
        if (bufferedImages == null) {
            return new CapeObject(new ResourceLocation[0], -1);
        }
        ResourceLocation[] resourceLocations = new ResourceLocation[bufferedImages.length];
        /*try {
            if (bufferedImages.length > 0) {
                File outputfile = new File(OfflineAuth.rootPath + File.separator + "test.png");
                ImageIO.write(bufferedImages[0], "png", outputfile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        for (int i = 0; i < bufferedImages.length; i ++) {
            ResourceLocation location = new ResourceLocation("offlineauth", "capes/" + name + i);
            loadTexture(bufferedImages[i], location);
            resourceLocations[i] = location;
        }
        return new CapeObject(resourceLocations, frameLen);
    }
}
