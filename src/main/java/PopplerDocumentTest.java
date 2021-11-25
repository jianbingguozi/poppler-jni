import edu.illinois.library.poppler.PopplerDocument;
import edu.illinois.library.poppler.PopplerImage;
import edu.illinois.library.poppler.PopplerPage;
import edu.illinois.library.poppler.PopplerPageRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;

class PopplerDocumentTest {

    public static void main(String[] args)throws Exception  {
        PopplerPageRenderer renderer = new PopplerPageRenderer();
        renderer.setRenderHint(PopplerPageRenderer.RenderHint.ANTIALIASING);
        renderer.setRenderHint(PopplerPageRenderer.RenderHint.TEXT_ANTIALIASING);
        renderer.setRenderHint(PopplerPageRenderer.RenderHint.TEXT_HINTING);

        byte[] data = Files.readAllBytes(FileSystems.getDefault().getPath("/workspace/src/test/resources/pdf-multipage.pdf"));

        PopplerDocument doc = null;
        PopplerPage page    = null;
        try {
            doc                 = PopplerDocument.load(data);
            int pageSizeCount = doc.numPages();
            System.out.println(pageSizeCount);
            int pageNum = 0;
            page                = doc.getPage(pageNum);
            final int dpi       = 72;
            final double scale  = dpi / 72.0;
            double width        = page.getPageRect().width() * scale;
            double height       = page.getPageRect().height() * scale;
            switch (page.getOrientation()) {
                case LANDSCAPE:
                case SEASCAPE:
                    double tmp = width;
                    width      = height;
                    height     = tmp;
                    break;
            }
            final int intWidth  = (int) Math.round(width);
            final int intHeight = (int) Math.round(height);


            for (int i = 0; i < pageSizeCount; i++) {
                PopplerPage p = doc.getPage(i);

            }

            do {
                PopplerImage image = renderer.renderPage(
                        page, dpi, dpi, 0, 0, intWidth, intHeight);

                BufferedImage bImage = newBufferedImage(image);
                ImageIO.write(bImage, "PNG", new File("/workspace/tmp/demo"+pageNum+".png"));
                pageNum++;
            } while (pageNum < pageSizeCount && (page = doc.getPage(pageNum)) != null);



        } finally {
            if (doc != null) {
                doc.nativeDestroy();
            }
            if (page != null) {
                page.nativeDestroy();
            }
        }
    }

    static BufferedImage newBufferedImage(PopplerImage popplerImage) {
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
