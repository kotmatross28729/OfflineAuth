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
        
        imageWidth = buffImg.getWidth();
        imageHeight = buffImg.getHeight() / 2;
        
        BufferedImage localBufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics localGraphics = localBufferedImage.getGraphics();
        localGraphics.drawImage(buffImg, 0, 0, null);
        localGraphics.dispose();

        imageData = ((DataBufferInt) localBufferedImage.getRaster().getDataBuffer()).getData();

        setAreaOpaque(0, 0, imageHeight, imageHeight/2);
        setAreaTransparent(imageHeight, 0, imageHeight*2, imageHeight);
        setAreaOpaque(0, imageHeight/2, imageHeight*2, imageHeight);

        return localBufferedImage;
    }
    
    //2:1 -> 1:1
    public BufferedImage convertToNew(BufferedImage buffImg) {
        if (buffImg == null)
            return null;
        
        imageWidth = buffImg.getWidth();
        imageHeight = buffImg.getHeight() * 2;
        
        BufferedImage localBufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics localGraphics = localBufferedImage.getGraphics();
        localGraphics.drawImage(buffImg, 0, 0, null);
        
        //Copy
        localGraphics.drawImage(localBufferedImage, getMagicNumber(24, imageHeight), getMagicNumber(48, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber( 4, imageHeight), getMagicNumber(16, imageHeight), getMagicNumber( 8, imageHeight),getMagicNumber(20, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(28, imageHeight), getMagicNumber(48, imageHeight), getMagicNumber(24, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber( 8, imageHeight), getMagicNumber(16, imageHeight), getMagicNumber(12, imageHeight),getMagicNumber(20, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(20, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(16, imageHeight), getMagicNumber(64, imageHeight), getMagicNumber( 8, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber(12, imageHeight),getMagicNumber(32, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(24, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber(64, imageHeight), getMagicNumber( 4, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber( 8, imageHeight),getMagicNumber(32, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(28, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(24, imageHeight), getMagicNumber(64, imageHeight), getMagicNumber( 0, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber( 4, imageHeight),getMagicNumber(32, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(32, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(28, imageHeight), getMagicNumber(64, imageHeight), getMagicNumber(12, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber(16, imageHeight),getMagicNumber(32, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(40, imageHeight), getMagicNumber(48, imageHeight), getMagicNumber(36, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(44, imageHeight), getMagicNumber(16, imageHeight), getMagicNumber(48, imageHeight),getMagicNumber(20, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(44, imageHeight), getMagicNumber(48, imageHeight), getMagicNumber(40, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(48, imageHeight), getMagicNumber(16, imageHeight), getMagicNumber(52, imageHeight),getMagicNumber(20, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(36, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(32, imageHeight), getMagicNumber(64, imageHeight), getMagicNumber(48, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber(52, imageHeight),getMagicNumber(32, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(40, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(36, imageHeight), getMagicNumber(64, imageHeight), getMagicNumber(44, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber(48, imageHeight),getMagicNumber(32, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(44, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(40, imageHeight), getMagicNumber(64, imageHeight), getMagicNumber(40, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber(44, imageHeight),getMagicNumber(32, imageHeight), null);
        localGraphics.drawImage(localBufferedImage, getMagicNumber(48, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(44, imageHeight), getMagicNumber(64, imageHeight), getMagicNumber(52, imageHeight), getMagicNumber(20, imageHeight), getMagicNumber(56, imageHeight),getMagicNumber(32, imageHeight), null);
        
        localGraphics.dispose();
        imageData = ((DataBufferInt) localBufferedImage.getRaster().getDataBuffer()).getData();
        setAreaOpaque2(0, 0, imageHeight/2, imageHeight/4);
        setAreaTransparent2(imageHeight/2, 0, imageHeight, imageHeight/2);
        setAreaOpaque2(0, imageHeight/4, imageHeight, imageHeight/2);
        setAreaTransparent2(0, imageHeight/2, imageHeight/4, getMagicNumber(48, imageHeight));
        setAreaTransparent2(imageHeight/4, imageHeight/2, getMagicNumber(40, imageHeight), getMagicNumber(48, imageHeight));
        setAreaTransparent2(getMagicNumber(40, imageHeight), imageHeight/2, getMagicNumber(56, imageHeight), getMagicNumber(48, imageHeight));
        setAreaTransparent2(0, getMagicNumber(48, imageHeight), imageHeight/4, imageHeight);
        setAreaOpaque2(imageHeight/4, getMagicNumber(48, imageHeight), getMagicNumber(48, imageHeight), imageHeight);
        setAreaTransparent2(getMagicNumber(48, imageHeight), getMagicNumber(48, imageHeight), imageHeight, imageHeight);
        return localBufferedImage;
    }
    
    //Fucking magic numbers
    public static int getMagicNumber(int to, int from) {
        return Math.round(from / ((64F / (float)to)));
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
