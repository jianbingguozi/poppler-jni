package edu.illinois.library.poppler;

import java.awt.image.BufferedImage;

public class PopplerUtils {
    public static BufferedImage newBufferedImage(PopplerImage popplerImage) {
        int imageType;
        switch (popplerImage.getFormat()) {
            case MONO:
                imageType = BufferedImage.TYPE_BYTE_BINARY;
                break;
            case RGB24:
                imageType = BufferedImage.TYPE_INT_RGB;
                break;
            case ARGB32:
                imageType = BufferedImage.TYPE_INT_ARGB;
                break;
            case GRAY8:
                imageType = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case BGR24:
                imageType = BufferedImage.TYPE_INT_BGR;
                break;
            default:
                throw new IllegalArgumentException("Unknown image type");
        }

        final byte[] byteData = popplerImage.data();
        final int[] intData   = new int[byteData.length];
        switch (imageType) {
            case BufferedImage.TYPE_BYTE_BINARY:
            case BufferedImage.TYPE_BYTE_GRAY:  // TODO: verify this
                for (int i = 0; i < byteData.length - 1; i++) {
                    intData[i] = byteData[i];
                }
                break;
            case BufferedImage.TYPE_INT_RGB:  // TODO: verify this
                for (int i = 0; i < byteData.length - 3; i += 3) {
                    intData[i]     = byteData[i + 2]; // R
                    intData[i + 1] = byteData[i + 1]; // G
                    intData[i + 2] = byteData[i];     // B
                }
                break;
            case BufferedImage.TYPE_INT_ARGB:
                for (int i = 0; i < byteData.length - 4; i += 4) {
                    intData[i]     = byteData[i + 2]; // R
                    intData[i + 1] = byteData[i + 1]; // G
                    intData[i + 2] = byteData[i];     // B
                    intData[i + 3] = byteData[i + 3]; // A
                }
                break;
            case BufferedImage.TYPE_INT_BGR: // TODO: verify this
                for (int i = 0; i < byteData.length - 3; i += 3) {
                    intData[i]     = byteData[i];     // B
                    intData[i + 1] = byteData[i + 1]; // G
                    intData[i + 2] = byteData[i + 2]; // R
                }
                break;
        }

        final BufferedImage bufImage = new BufferedImage(
                popplerImage.getWidth(),
                popplerImage.getHeight(), imageType);
        bufImage.getRaster().setPixels(
                0, 0, bufImage.getWidth(), bufImage.getHeight(), intData);
        return bufImage;
    }
}
