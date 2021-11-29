import edu.illinois.library.poppler.PopplerDocument;
import edu.illinois.library.poppler.PopplerImage;
import edu.illinois.library.poppler.PopplerPage;
import edu.illinois.library.poppler.PopplerPageRenderer;
import edu.illinois.library.poppler.PopplerUtils;

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


            do {
                PopplerImage image = renderer.renderPage(
                        page, dpi, dpi, 0, 0, intWidth, intHeight);

                BufferedImage bImage = PopplerUtils.newBufferedImage(image);
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
}
