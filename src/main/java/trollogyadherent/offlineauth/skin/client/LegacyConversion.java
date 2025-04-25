package trollogyadherent.offlineauth.skin.client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class LegacyConversion {
    private int[] imageData;
    private int imageWidth;
    private int imageHeight;
    
    //1:1 -> 2:1
    public BufferedImage convertToOld(BufferedImage buffImg) {
        if (buffImg == null)
            return null;
    
        if(buffImg.getWidth() == 0 || buffImg.getHeight() == 0) 
            return null;
        
        imageWidth = buffImg.getWidth();
        imageHeight = buffImg.getHeight() / 2;
        
        BufferedImage localBufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics localGraphics = localBufferedImage.getGraphics();
        localGraphics.drawImage(buffImg, 0, 0, null);
        localGraphics.dispose();

        imageData = ((DataBufferInt) localBufferedImage.getRaster().getDataBuffer()).getData();

        setAreaOpaque(0,             0,                     imageHeight,  imageHeight/2);
        setAreaTransparent( imageHeight,          0, imageHeight*2,                   imageHeight);
        setAreaOpaque(0, imageHeight/2,   imageHeight*2,                   imageHeight);

        return localBufferedImage;
    }
    
    //2:1 -> 1:1
    public BufferedImage convertToNew(BufferedImage buffImg) {
        if (buffImg == null)
            return null;
        
        if(buffImg.getWidth() == 0 || buffImg.getHeight() == 0)
            return null;
        
        imageWidth = buffImg.getWidth();
        imageHeight = buffImg.getHeight() * 2;
        int RATIO = imageHeight / 64;
        
        BufferedImage localBufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics localGraphics = localBufferedImage.getGraphics();
        localGraphics.drawImage(buffImg, 0, 0, null);
        
        //Copy
        localGraphics.drawImage(localBufferedImage, 24 * RATIO, 48 * RATIO, 20 * RATIO, 52 * RATIO, 4  * RATIO, 16 * RATIO, 8  * RATIO, 20 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 28 * RATIO, 48 * RATIO, 24 * RATIO, 52 * RATIO, 8  * RATIO, 16 * RATIO, 12 * RATIO, 20 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 20 * RATIO, 52 * RATIO, 16 * RATIO, 64 * RATIO, 8  * RATIO, 20 * RATIO, 12 * RATIO, 32 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 24 * RATIO, 52 * RATIO, 20 * RATIO, 64 * RATIO, 4  * RATIO, 20 * RATIO, 8  * RATIO, 32 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 28 * RATIO, 52 * RATIO, 24 * RATIO, 64 * RATIO, 0         , 20 * RATIO, 4  * RATIO, 32 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 32 * RATIO, 52 * RATIO, 28 * RATIO, 64 * RATIO, 12 * RATIO, 20 * RATIO, 16 * RATIO, 32 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 40 * RATIO, 48 * RATIO, 36 * RATIO, 52 * RATIO, 44 * RATIO, 16 * RATIO, 48 * RATIO, 20 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 44 * RATIO, 48 * RATIO, 40 * RATIO, 52 * RATIO, 48 * RATIO, 16 * RATIO, 52 * RATIO, 20 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 36 * RATIO, 52 * RATIO, 32 * RATIO, 64 * RATIO, 48 * RATIO, 20 * RATIO, 52 * RATIO, 32 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 40 * RATIO, 52 * RATIO, 36 * RATIO, 64 * RATIO, 44 * RATIO, 20 * RATIO, 48 * RATIO, 32 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 44 * RATIO, 52 * RATIO, 40 * RATIO, 64 * RATIO, 40 * RATIO, 20 * RATIO, 44 * RATIO, 32 * RATIO, null);
        localGraphics.drawImage(localBufferedImage, 48 * RATIO, 52 * RATIO, 44 * RATIO, 64 * RATIO, 52 * RATIO, 20 * RATIO, 56 * RATIO, 32 * RATIO, null);
    
    
        localGraphics.dispose();
        imageData = ((DataBufferInt) localBufferedImage.getRaster().getDataBuffer()).getData();
        setAreaOpaque2(      0        ,  0        , 32 * RATIO, 16 * RATIO);
        setAreaTransparent2(32 * RATIO,  0        , 64 * RATIO, 32 * RATIO);
        setAreaOpaque2(      0        , 16 * RATIO, 64 * RATIO, 32 * RATIO);
        setAreaTransparent2( 0        , 32 * RATIO, 16 * RATIO, 48 * RATIO);
        setAreaTransparent2(16 * RATIO, 32 * RATIO, 40 * RATIO, 48 * RATIO);
        setAreaTransparent2(40 * RATIO, 32 * RATIO, 56 * RATIO, 48 * RATIO);
        setAreaTransparent2( 0        , 48 * RATIO, 16 * RATIO, 64 * RATIO);
        setAreaOpaque2(     16 * RATIO, 48 * RATIO, 48 * RATIO, 64 * RATIO);
        setAreaTransparent2(48 * RATIO, 48 * RATIO, 64 * RATIO, 64 * RATIO);
        return localBufferedImage;
    }
    
    private boolean hasTransparency(int p_hasTransparency_1_, int p_hasTransparency_2_, int p_hasTransparency_3_, int p_hasTransparency_4_) {
        for (int i = p_hasTransparency_1_; i < p_hasTransparency_3_; i++) {
            for (int j = p_hasTransparency_2_; j < p_hasTransparency_4_; j++) {
                int k = imageData[(i + j * imageWidth)];
                if ((k >> 24 & 0xFF) < 128)
                    return true;
            }
        }
        return false;
    }
    
    private boolean hasTransparency2(int p_78435_1_, int p_78435_2_, int p_78435_3_, int p_78435_4_) {
        for (int i1 = p_78435_1_; i1 < p_78435_3_; ++i1) {
            for (int j1 = p_78435_2_; j1 < p_78435_4_; ++j1) {
                int k1 = this.imageData[i1 + j1 * this.imageWidth];
                
                if ((k1 >> 24 & 255) < 128) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setAreaOpaque(int p_setAreaOpaque_1_, int p_setAreaOpaque_2_, int p_setAreaOpaque_3_, int p_setAreaOpaque_4_) {
        for (int i = p_setAreaOpaque_1_; i < p_setAreaOpaque_3_; i++)
            for (int j = p_setAreaOpaque_2_; j < p_setAreaOpaque_4_; j++)
                imageData[(i + j * imageWidth)] |= -16777216;
    }
    
    private void setAreaOpaque2(int p_78433_1_, int p_78433_2_, int p_78433_3_, int p_78433_4_) {
        for (int i1 = p_78433_1_; i1 < p_78433_3_; ++i1) {
            for (int j1 = p_78433_2_; j1 < p_78433_4_; ++j1) {
                this.imageData[i1 + j1 * this.imageWidth] |= -16777216;
            }
        }
    }

    private void setAreaTransparent(int p_setAreaTransparent_1_, int p_setAreaTransparent_2_, int p_setAreaTransparent_3_, int p_setAreaTransparent_4_) {
        if (hasTransparency(p_setAreaTransparent_1_, p_setAreaTransparent_2_, p_setAreaTransparent_3_, p_setAreaTransparent_4_))
            return;

        for (int i = p_setAreaTransparent_1_; i < p_setAreaTransparent_3_; i++)
            for (int j = p_setAreaTransparent_2_; j < p_setAreaTransparent_4_; j++)
                imageData[(i + j * imageWidth)] &= 16777215;
    }
    
    private void setAreaTransparent2(int p_78434_1_, int p_78434_2_, int p_78434_3_, int p_78434_4_) {
        if (!this.hasTransparency2(p_78434_1_, p_78434_2_, p_78434_3_, p_78434_4_)) {
            for (int i1 = p_78434_1_; i1 < p_78434_3_; ++i1) {
                for (int j1 = p_78434_2_; j1 < p_78434_4_; ++j1) {
                    this.imageData[i1 + j1 * this.imageWidth] &= 16777215;
                }
            }
        }
    }
    
}
