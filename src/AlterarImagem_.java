/*
Criar um plugin para alterar os valores de brilho e contraste de uma imagem, bem como aplicar 
as técnicas de solarização e dessaturação na mesma.

Utilizar uma interface gráfica com quatro barras do tipo slider, uma para cada técnica, um botão "ok" e um botão "cancel".
As barras de slider alterarão as características da imagem quando movimentadas.
Quando pressionado o botão "ok" as características da imagem serão alteradas de forma definitiva, caso seja pressionado o 
botão "cancel" a imagem voltará para as suas características originais.
*/

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.DialogListener;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.awt.*;


public class AlterarImagem_ implements PlugIn, DialogListener {
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
        caixa_dialogo.addSlider("Contraste:", -255, 255, 0, 1);
        caixa_dialogo.addSlider("Solarização:", 0, 255, 255, 1);
        caixa_dialogo.addSlider("Saturação:", 0, 1, 1, 0.01);
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

        //workingProcessor.copyBits(originalProcessor, 0, 0, ij.process.Blitter.COPY);
        workingProcessor = originalProcessor.duplicate();
        imagem_original.setProcessor(workingProcessor);
        applyAdjustments(workingProcessor, sliderBrilho, sliderContraste, sliderSolarizacao, sliderSaturacao);
        imagem_original.updateAndDraw();
        return true;
    }

    private void applyAdjustments(ImageProcessor ip, double brightness, double contraste, double limiarSolarizacao, double saturacao) {
        
        int brilho = (int) brightness;
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                int[] rgb = new int[3];
                ip.getPixel(x, y, rgb);
                rgb[0] = Math.min(255, Math.max(0, rgb[0] + brilho));
                rgb[1] = Math.min(255, Math.max(0, rgb[1] + brilho));
                rgb[2] = Math.min(255, Math.max(0, rgb[2] + brilho));
                ip.putPixel(x, y, rgb);
            }
        }
        /* 	Aplica o contraste
         * - O primeiro passo para alterar o contraste de uma imagem é calcular o Fator de Contraste 
         * para um determinado nível de contraste C: F = (259*(C+255))/(255*(259-C))
		   - O segundo passo é aplicar o fator de contraste para cada um dos canais que formam cada pixel da imagem*/
        double fator = (259.0 * (contraste + 255.0)) / (255.0 * (259.0 - contraste));
        for (int y = 0; y < ip.getHeight(); y++) {
        	for (int x = 0; x < ip.getWidth(); x++) {
                int[] rgb = new int[3];
                ip.getPixel(x, y, rgb);

                // Aplica o fator de contraste para cada canal (R, G, B)
                rgb[0] = (int) Math.min(255, Math.max(0, ((fator * (rgb[0] - 128)) + 128)));
                rgb[1] = (int) Math.min(255, Math.max(0, ((fator * (rgb[1] - 128)) + 128)));
                rgb[2] = (int) Math.min(255, Math.max(0, ((fator * (rgb[2] - 128)) + 128)));

                ip.putPixel(x, y, rgb);
            }
        }
        
        // Aplica solarização
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                int[] rgb = new int[3];
                ip.getPixel(x, y, rgb);
                for (int i = 0; i < 3; i++) {
                    if (rgb[i] > limiarSolarizacao) {
                        rgb[i] = 255 - rgb[i];
                    }
                }
                ip.putPixel(x, y, rgb);
            }
        }
        
        // Aplica dessaturação
        for (int y = 0; y < ip.getHeight(); y++) {
            for (int x = 0; x < ip.getWidth(); x++) {
                int[] rgb = new int[3];
                ip.getPixel(x, y, rgb);
                int gray = (int) (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]);
                rgb[0] = (int) (gray + (saturacao * (rgb[0] - gray)));
                rgb[1] = (int) (gray + (saturacao * (rgb[1] - gray)));
                rgb[2] = (int) (gray + (saturacao * (rgb[2] - gray)));
                ip.putPixel(x, y, rgb);
            }
        }
        
    }
}