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



/*
 
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.DialogListener;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.awt.*;
import java.awt.event.*;

public class ModifyImage_ implements PlugIn, DialogListener {
    private ImagePlus imagem_original;
    private ImageProcessor originalProcessor;
    private ImageProcessor workingProcessor;
    private GenericDialog caixa_dialogo;

    public void run(String arg) {
        // Verifica se há uma imagem aberta
        int[] lista_Imagens = WindowManager.getIDList();
        if (lista_Imagens == null || lista_Imagens.length == 0) {
            IJ.showMessage("Nenhuma imagem aberta.");
            return;
        }
        
        // Obtém a imagem original (primeira imagem aberta)
        imagem_original = WindowManager.getImage(lista_Imagens[0]);
        originalProcessor = imagem_original.getProcessor().duplicate();
        workingProcessor = originalProcessor.duplicate();

        // Cria a interface gráfica
        caixa_dialogo = new GenericDialog("Alterar características da Imagem");
        caixa_dialogo.addSlider("Brilho:", -255, 255, 0, 5);
        caixa_dialogo.addSlider("Contraste:", 50, 200, 100, 5);
        caixa_dialogo.addSlider("Solarização:", 0, 255, 128, 1);
        caixa_dialogo.addSlider("Saturação:", 0, 100, 0, 1);
        caixa_dialogo.addCheckbox("Visualizar em tempo real", true);
        caixa_dialogo.addDialogListener(this);
        caixa_dialogo.showDialog();

        // Aplica as alterações definitivas ou restaura a imagem original
        if (caixa_dialogo.wasOKed()) {
            imagem_original.updateAndDraw();
        } else {
            imagem_original.setProcessor(originalProcessor);
            imagem_original.updateAndDraw();
        }
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        if (gd.wasCanceled()) return false;

        double sliderBrilho = gd.getNextNumber();
        double sliderContraste = gd.getNextNumber();
        double sliderSolarizacao = gd.getNextNumber();
        double sliderSaturacao = gd.getNextNumber();
        boolean realTimePreview = gd.getNextBoolean();

        if (realTimePreview) {
            workingProcessor.copyBits(originalProcessor, 0, 0, ij.process.Blitter.COPY);
            applyAdjustments(workingProcessor, sliderBrilho, sliderContraste, sliderSolarizacao, sliderSaturacao);
            imagem_original.setProcessor(workingProcessor);
            imagem_original.updateAndDraw();
        }
        return true;
    }

    private void applyAdjustments(ImageProcessor ip, double brightness, double contrast, double solarizeThreshold, double desaturation) {
        ip.multiply(contrast / 100.0);
        ip.add(brightness);

        // Aplica solarização
//        for (int y = 0; y < ip.getHeight(); y++) {
//            for (int x = 0; x < ip.getWidth(); x++) {
//                int pixel = ip.getPixel(x, y);
//                if (pixel > solarizeThreshold) {
//                    ip.putPixel(x, y, 255 - pixel);
//                }
//            }
//        }
     // Aplica solarização corretamente para imagens coloridas
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                int[] rgb = new int[3];
                ip.getPixel(x, y, rgb);
                for (int i = 0; i < 3; i++) {
                    if (rgb[i] > solarizeThreshold) {
                        rgb[i] = 255 - rgb[i];
                    }
                }
                ip.putPixel(x, y, rgb);
            }
        }
        // Aplica dessaturação
        if (desaturation > 0) {
            for (int y = 0; y < ip.getHeight(); y++) {
                for (int x = 0; x < ip.getWidth(); x++) {
                    int[] rgb = new int[3];
                    ip.getPixel(x, y, rgb);
                    int gray = (int) (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
                    rgb[0] = (int) (rgb[0] * (1 - desaturation / 100.0) + gray * (desaturation / 100.0));
                    rgb[1] = (int) (rgb[1] * (1 - desaturation / 100.0) + gray * (desaturation / 100.0));
                    rgb[2] = (int) (rgb[2] * (1 - desaturation / 100.0) + gray * (desaturation / 100.0));
                    ip.putPixel(x, y, rgb);
                }
            }
        }
    }
}
 */
