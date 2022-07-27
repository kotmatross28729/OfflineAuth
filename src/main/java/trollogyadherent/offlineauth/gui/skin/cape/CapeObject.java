package trollogyadherent.offlineauth.gui.skin.cape;

import net.minecraft.util.ResourceLocation;

public class CapeObject {
    private final ResourceLocation[] frameResourceLocations;
    private final float frameDuration;
    private float partialtickCounter;
    private int frameCounter;

    public CapeObject(ResourceLocation[] frameResourceLocations, float frameDuration) {
        this.frameResourceLocations = frameResourceLocations;
        this.frameDuration = 10;//frameDuration;
        this.partialtickCounter = 0;
        this.frameCounter = 0;
    }

    public float getFrameDuration() {
        return frameDuration;
    }

    ResourceLocation getFrame(int index) {
        if (frameResourceLocations == null || frameResourceLocations.length == 0) {
            return null;
        }
        if (index >= frameResourceLocations.length) {
            index = 0;
        }
        return frameResourceLocations[index];
    }

    /* One tick is 50 ms */
    public ResourceLocation getCurrentFrame(float partialTicks) {
        if (frameResourceLocations.length == 0) {
            return null;
        }
        if (frameDuration == -1) {
            return frameResourceLocations[0];
        }
        partialtickCounter += partialTicks;
        if (partialtickCounter >= frameDuration) {
            frameCounter = (frameCounter + 1) % frameResourceLocations.length;
            partialtickCounter = 0;
        }
        return getFrame(frameCounter);
    }

    public ResourceLocation getCurrentFrame() {
        if (frameResourceLocations.length == 0) {
            return null;
        }
        if (frameDuration == -1) {
            return frameResourceLocations[0];
        }
        return getFrame(frameCounter);
    }
}
