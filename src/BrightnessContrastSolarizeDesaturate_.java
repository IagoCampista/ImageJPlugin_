import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class BrightnessContrastSolarizeDesaturate_ implements PlugIn {
    private ImagePlus originalImage;
    private ImagePlus workingImage;
    private ImageProcessor originalProcessor;
    private ImageProcessor workingProcessor;

    public void run(String arg) {
        // Verifica se há uma imagem aberta
        originalImage = IJ.getImage();
        if (originalImage == null) {
            IJ.showMessage("Nenhuma imagem aberta.");
            return;
        }

        // Cria uma cópia da imagem original para trabalhar
        workingImage = originalImage.duplicate();
        workingImage.setTitle("Working Image"); // Renomeia a cópia para identificação
        workingImage.show();
        originalProcessor = originalImage.getProcessor().duplicate();
        workingProcessor = workingImage.getProcessor();

        // Cria a interface gráfica
        GenericDialog gd = new GenericDialog("Ajustes de Imagem");
        gd.addSlider("Brilho:", -255, 255, 0);
        gd.addSlider("Contraste:", 0, 200, 100);
        gd.addSlider("Solarização:", 0, 255, 128);
        gd.addSlider("Dessaturação:", 0, 100, 0);
        gd.addCheckbox("Visualizar em tempo real", true);
        gd.showDialog();

        // Verifica se o usuário cancelou o diálogo
        if (gd.wasCanceled()) {
            workingImage.close(); // Fecha a cópia de trabalho
            return;
        }

        // Obtém os valores iniciais dos sliders
        double brightness = gd.getNextNumber();
        double contrast = gd.getNextNumber();
        double solarizeThreshold = gd.getNextNumber();
        double desaturation = gd.getNextNumber();
        boolean realTimePreview = gd.getNextBoolean();

        // Aplica as alterações em tempo real enquanto o diálogo estiver aberto
        while (gd.isVisible()) {
            brightness = gd.getNextNumber();
            contrast = gd.getNextNumber();
            solarizeThreshold = gd.getNextNumber();
            desaturation = gd.getNextNumber();
            realTimePreview = gd.getNextBoolean();

            if (realTimePreview) {
                applyAdjustments(workingProcessor, brightness, contrast, solarizeThreshold, desaturation);
                workingImage.updateAndDraw(); // Atualiza a imagem em tempo real
                IJ.log("Image updated with brightness: " + brightness + ", contrast: " + contrast); // Debug
            }
        }

        // Aplica as alterações definitivas ou restaura a imagem original
        if (!gd.wasCanceled()) {
            applyAdjustments(originalImage.getProcessor(), brightness, contrast, solarizeThreshold, desaturation);
            originalImage.updateAndDraw(); // Atualiza a imagem original
            IJ.log("Final adjustments applied to the original image."); // Debug
        } else {
            originalImage.setProcessor(originalProcessor); // Restaura a imagem original
            originalImage.updateAndDraw();
            IJ.log("Changes discarded. Original image restored."); // Debug
        }

        workingImage.close(); // Fecha a cópia de trabalho
    }

    private void applyAdjustments(ImageProcessor ip, double brightness, double contrast, double solarizeThreshold, double desaturation) {
        // Aplica brilho e contraste
        ip.multiply(contrast / 100.0);
        ip.add(brightness);

        // Aplica solarização
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                int pixel = ip.getPixel(x, y);
                if (pixel > solarizeThreshold) {
                    ip.putPixel(x, y, 255 - pixel);
                }
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