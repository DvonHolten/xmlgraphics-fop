/*
 * $Id$
 * Copyright (C) 2001 The Apache Software Foundation. All rights reserved.
 * For details on use and redistribution please refer to the
 * LICENSE file included with these sources.
 */

package org.apache.fop.image;

// AWT
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.BufferedImage;
import java.awt.color.ColorSpace;

// JAI
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
// Sun codec
import com.sun.media.jai.codec.FileCacheSeekableStream;

// FOP
import org.apache.fop.pdf.PDFColor;
import org.apache.fop.image.analyser.ImageReader;

/**
 * FopImage object using JAI.
 * @author Eric SCHAEFFER
 * @see AbstractFopImage
 * @see FopImage
 */
public class JAIImage extends AbstractFopImage {

    public JAIImage(FopImage.ImageInfo imgReader) {
        super(imgReader);
    }

    protected void loadImage() {
        try {
            com.sun.media.jai.codec.FileCacheSeekableStream seekableInput =
              new FileCacheSeekableStream(inputStream);
            RenderedOp imageOp = JAI.create("stream", seekableInput);
            inputStream.close();
            inputStream = null;

            this.height = imageOp.getHeight();
            this.width = imageOp.getWidth();

            ColorModel cm = imageOp.getColorModel();
            this.bitsPerPixel = 8;
            // this.bitsPerPixel = cm.getPixelSize();
            this.colorSpace = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);

            BufferedImage imageData = imageOp.getAsBufferedImage();
            int[] tmpMap = imageData.getRGB(0, 0, this.width,
                                            this.height, null, 0, this.width);

            if (cm.hasAlpha()) {
                int transparencyType = cm.getTransparency(); // java.awt.Transparency. BITMASK or OPAQUE or TRANSLUCENT
                if (transparencyType == java.awt.Transparency.OPAQUE) {
                    this.isTransparent = false;
                } else if (transparencyType ==
                    java.awt.Transparency.BITMASK) {
                    if (cm instanceof IndexColorModel) {
                        this.isTransparent = false;
                        byte[] alphas = new byte[
                                          ((IndexColorModel) cm).getMapSize()];
                        byte[] reds = new byte[
                                        ((IndexColorModel) cm).getMapSize()];
                        byte[] greens = new byte[
                                          ((IndexColorModel) cm).getMapSize()];
                        byte[] blues = new byte[
                                         ((IndexColorModel) cm).getMapSize()];
                        ((IndexColorModel) cm).getAlphas(alphas);
                        ((IndexColorModel) cm).getReds(reds);
                        ((IndexColorModel) cm).getGreens(greens);
                        ((IndexColorModel) cm).getBlues(blues);
                        for (int i = 0;
                                i < ((IndexColorModel) cm).getMapSize();
                                i++) {
                            if ((alphas[i] & 0xFF) == 0) {
                                this.isTransparent = true;
                                this.transparentColor = new PDFColor(
                                                            (int)(reds[i] & 0xFF),
                                                            (int)(greens[i] & 0xFF),
                                                            (int)(blues[i] & 0xFF));
                                break;
                            }
                        }
                    } else {
                        // TRANSLUCENT
                        /*
                         * this.isTransparent = false;
                         * for (int i = 0; i < this.width * this.height; i++) {
                         * if (cm.getAlpha(tmpMap[i]) == 0) {
                         * this.isTransparent = true;
                         * this.transparentColor = new PDFColor(cm.getRed(tmpMap[i]), cm.getGreen(tmpMap[i]), cm.getBlue(tmpMap[i]));
                         * break;
                         * }
                         * }
                         * // or use special API...
                         */
                        this.isTransparent = false;
                    }
                } else {
                    this.isTransparent = false;
                }
            } else {
                this.isTransparent = false;
            }

            // Should take care of the ColorSpace and bitsPerPixel
            this.bitmapsSize = this.width * this.height * 3;
            this.bitmaps = new byte[this.bitmapsSize];
            for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < this.width; j++) {
                    int p = tmpMap[i * this.width + j];
                    int r = (p >> 16) & 0xFF;
                    int g = (p >> 8) & 0xFF;
                    int b = (p) & 0xFF;
                    this.bitmaps[3 * (i * this.width + j)] =
                      (byte)(r & 0xFF);
                    this.bitmaps[3 * (i * this.width + j) + 1] =
                      (byte)(g & 0xFF);
                    this.bitmaps[3 * (i * this.width + j) + 2] =
                      (byte)(b & 0xFF);
                }
            }

        }
        catch (Exception ex) {
            /*throw new FopImageException("Error while loading image "
                                         + "" + " : "
                                         + ex.getClass() + " - "
                                         + ex.getMessage());
             */}
    }

}

