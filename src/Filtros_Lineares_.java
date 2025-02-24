/*
Desenvolver um plugin para aplicar o filtro passa-baixas de média, um dos filtros passa-altas e um dos filtros de borda apresentados nos slides da aula.

Deverá ser apresentada uma interface gráfica com as descrições dos filtros e botões de rádio para viabilizar a seleção do filtro a ser aplicado.

Utilizar um kernels de dimensões 3 x 3.
*/

import java.awt.AWTEvent;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Filtros_Lineares_ implements PlugIn, DialogListener  {
    
    private ImagePlus imagem_original;
    private ByteProcessor originalProcessor;
    private ByteProcessor workingProcessor;
    private GenericDialog caixa_dialogo;
    private String metodo;
    private double [][] kernel;
	
	public void run(String arg) {
    	 
        // Verifica se há uma imagem aberta
        int[] lista_Imagens = WindowManager.getIDList();
        if (lista_Imagens == null || lista_Imagens.length == 0) {
            IJ.showMessage("Nenhuma imagem aberta.");
            return;
        }
        
        // Obtém a imagem original (primeira imagem aberta)
        imagem_original = WindowManager.getImage(lista_Imagens[0]);
        originalProcessor = (ByteProcessor) imagem_original.getProcessor().duplicate();
        workingProcessor = (ByteProcessor) originalProcessor.duplicate();
 
        //Confere se a imagem é 8 bits 
        if (imagem_original.getBitDepth() != 8) {
            IJ.showMessage("Error", "A imagem precisa ser em escala de cinza.");
            return;
        }
        
        // Cria a interface gráfica
        caixa_dialogo = new GenericDialog("Alterar características da Imagem");
        caixa_dialogo .addRadioButtonGroup("Filtro:", new String[]{"Passa-baixa", "Passa-alta", "Borda"}, 3, 1, "");
        caixa_dialogo.addDialogListener(this);
        caixa_dialogo.showDialog();
        
        
        // Aplica as alterações definitivas ou restaura a imagem original
        if (caixa_dialogo.wasOKed()) {
            imagem_original.updateAndDraw();
        } else {
        	//reverte a imagem para o estado original
            imagem_original.setProcessor(originalProcessor);
            imagem_original.updateAndDraw();
        }
    }
    
    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        if (gd.wasCanceled()) return false;

        metodo = caixa_dialogo.getNextRadioButton();
        
        //cria um novo processor e seta ele pra ser o processor da imagem aberta
        workingProcessor = (ByteProcessor) originalProcessor.duplicate();
        imagem_original.setProcessor(workingProcessor);
        
        if(metodo == "Passa-baixa"){
        	kernel = new double[][] {
        	    {1.0/9, 1.0/9, 1.0/9},
        	    {1.0/9, 1.0/9, 1.0/9},
        	    {1.0/9, 1.0/9, 1.0/9}
        	};
        	aplica_filtro(originalProcessor, workingProcessor, kernel);
        }
        else if(metodo == "Passa-alta"){
        	
        	kernel = new double[][] {
        	    {-1, -1, -1},
        	    {-1, 8, -1},
        	    {-1, -1, -1}
        	};
        	aplica_filtro(originalProcessor, workingProcessor, kernel);
        }
        else if(metodo == "Borda"){
        	kernel = new double[][] {
        		//Norte
        	    {1, 1, 1},
        	    {1, -2, 1},
        	    {-1, -1, -1}
        	};
        	aplica_filtro(originalProcessor, workingProcessor, kernel);

        }
        else {
        	return false;
        }
        return true;
    }
	
    
    public void aplica_filtro(ByteProcessor originalProcessor, ByteProcessor workingProcessor, double[][] kernel) {
     
        int width = originalProcessor.getWidth();
        int height = originalProcessor.getHeight();
        double sum;
        
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                sum = 0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        double pixel_value = (double) originalProcessor.getPixel(x + kx, y + ky);
                        sum += pixel_value * kernel[ky + 1][kx + 1];
                    }
                }
                int newPixel = (int) Math.max(0, Math.min(255, sum)); 
                workingProcessor.putPixel(x, y, newPixel);
            }
        }
        
    }

}
