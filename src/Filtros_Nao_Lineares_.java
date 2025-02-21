/*
Desenvolver um plugin para aplicar o filtro não linear de Sobel na vertical e na horizontal em uma imagem corrente.  
Apresentar os resultados em duas novas imagens.

Em seguida deverá ser criada e apresentada uma terceira imagem com a junção dos dois resultados. (Fórmula presente no último slide da aula)

Implementar também o filtro não linear de mediana.

Criar uma interface para escolher entre os filtros Sobel e Mediana.
*/

import java.awt.AWTEvent;
import java.util.Arrays;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Filtros_Nao_Lineares_ implements PlugIn, DialogListener  {
    
    private ImagePlus imagem_original, imagem_v, imagem_h, imagem_resultado;
    private ByteProcessor originalProcessor, workingProcessor, verticalProcessor, horizontalProcessor, resultProcessor;
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
        imagem_v = imagem_original.duplicate();
        imagem_h = imagem_original.duplicate();
        imagem_resultado = imagem_original.duplicate();
        
        originalProcessor = (ByteProcessor) imagem_original.getProcessor();
        workingProcessor = (ByteProcessor) originalProcessor.duplicate();
        
        verticalProcessor = (ByteProcessor) originalProcessor.duplicate();
    	imagem_v.setProcessor(verticalProcessor);
    	
        horizontalProcessor = (ByteProcessor) originalProcessor.duplicate();
    	imagem_h.setProcessor(horizontalProcessor);
    	
        resultProcessor = (ByteProcessor) originalProcessor.duplicate();
    	imagem_resultado.setProcessor(resultProcessor);
 
        //Confere se a imagem é 8 bits 
        if (imagem_original.getBitDepth() != 8) {
            IJ.showMessage("Error", "A imagem precisa ser em escala de cinza.");
            return;
        }
        
        // Cria a interface gráfica
        caixa_dialogo = new GenericDialog("Alterar características da Imagem");
        caixa_dialogo .addRadioButtonGroup("Filtro:", new String[]{"Sobel", "Mediana"}, 2, 1, "");
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
        
        if(metodo == "Sobel"){
        	kernel = new double[][] {
        	    {-1.0, 0.0, 1.0},
        	    {-2.0, 0.0, 2.0},
        	    {-1.0, 0.0, 1.0}
        	};
        	aplica_filtro(workingProcessor, verticalProcessor, kernel);
        	imagem_v.show();
        	
        	kernel = new double[][] {
        	    {1.0, 2.0, 1.0},
        	    {0.0, 0.0, 0.0},
        	    {-1.0, -2.0, -1.0}
        	};
        	aplica_filtro(workingProcessor, horizontalProcessor, kernel);
        	imagem_h.show();
        	
        	compila_bordas(verticalProcessor, horizontalProcessor, resultProcessor);
        	imagem_resultado.show();
        }
        else if(metodo == "Mediana"){
        	
        	mediana(originalProcessor, workingProcessor);
        }
        else {
        	return false;
        }
        return true;
    }
    
    public void mediana(ByteProcessor originalProcessor, ByteProcessor workingProcessor) {
        
        int width = originalProcessor.getWidth();
        int height = originalProcessor.getHeight();
        int cont, pixelValue, mediana, newPixel;
        int [] vetorPixels = new int[9];
        
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                cont = 0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        pixelValue = originalProcessor.getPixel(x + kx, y + ky);
                        vetorPixels[cont] = pixelValue;
                        cont++;
                        
                    }
                }

                Arrays.sort(vetorPixels);
                mediana = vetorPixels[4];
                newPixel = Math.max(0, Math.min(255, mediana)); 
                workingProcessor.putPixel(x, y, newPixel);
            }
        }
        
       
        
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
                int novoValor = (int) Math.max(0, Math.min(255, sum)); 
                workingProcessor.putPixel(x, y, novoValor);
            }
        }
        
    }

	
	public void compila_bordas(ByteProcessor processor1, ByteProcessor processor2, ByteProcessor resultProcessor) {
        int width = processor1.getWidth();
        int height = processor1.getHeight();
        int pixel1, pixel2, newPixel;

        
        for (int y = 1; y < height-1; y++) {
            for (int x = 1; x < width-1; x++) {
            	pixel1 = processor1.getPixel(x, y);
            	pixel2 = processor2.getPixel(x, y);
            	newPixel = (int) Math.sqrt((pixel1*pixel1) + (pixel2*pixel2));
            	
            	resultProcessor.putPixel(x, y, newPixel);
            }
        }
        
	}
		
}
