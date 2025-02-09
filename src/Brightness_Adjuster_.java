import ij.*;
import ij.gui.*;
import ij.plugin.PlugIn;
import ij.process.*;
import java.awt.*;

public class Brightness_Adjuster_ implements PlugIn {
    private ImagePlus imp;
    private ImageProcessor ip;
    private ImageProcessor originalIp;
    private int brightness = 0;
    private int contrast = 100;

    public void run(String arg) {
        imp = IJ.getImage();
        if (imp == null) {
            IJ.error("No image open");
            return;
        }
        ip = imp.getProcessor();
        originalIp = ip.duplicate();
        createDialog();
    }

    private void createDialog() {
        GenericDialog gd = new GenericDialog("Image Adjustments");
        gd.addSlider("Brightness:", -100, 100, 0);
        gd.addSlider("Contrast:", 50, 200, 100);
        gd.addPreviewCheckbox(null);
        gd.addDialogListener((GenericDialog gd1, AWTEvent e) -> {
            brightness = (int) gd1.getNextNumber();
            contrast = (int) gd1.getNextNumber();
            updateImage();
            return true;
        });
        gd.showDialog();
        
        if (gd.wasCanceled()) {
            restoreOriginal();
        }
    }

    private void updateImage() {
        ip.copyBits(originalIp, 0, 0, Blitter.COPY);
        ip.multiply(contrast / 100.0);
        ip.add(brightness);
        imp.updateAndDraw();
    }

    private void restoreOriginal() {
        ip.copyBits(originalIp, 0, 0, Blitter.COPY);
        imp.updateAndDraw();
    }
}
