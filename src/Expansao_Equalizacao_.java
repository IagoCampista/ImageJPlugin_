/*
Fazer um plugin para implementar as técnicas de Expansão e Equalização de Histograma.

Deverá ser criada uma interface gráfica contendo botões de rádio para especificar qual estratégia será aplicada.

Deverão existir os botões Ok e Cancel para aplicar ou cancelar a aplicação das operações.
*/

import java.awt.AWTEvent;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.gui.Plot;
import ij.gui.PlotWindow;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Expansao_Equalizacao_ implements PlugIn, DialogListener  {
    
    private ImagePlus imagem_original;
    private ByteProcessor originalProcessor;
    private ByteProcessor workingProcessor;
    private GenericDialog caixa_dialogo;
    private int[] histogram;
    private String metodo;
	
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
//        ByteProcessor processor = (ByteProcessor) image.getProcessor();
 
        //Confere se a imagem é 8 bits 
        if (imagem_original.getBitDepth() != 8) {
            IJ.showMessage("Error", "A imagem precisa ser em escala de cinza.");
            return;
        }
        
        histogram = criarHistograma(imagem_original, "Histograma Original");
        
        // Cria a interface gráfica
        caixa_dialogo = new GenericDialog("Alterar características da Imagem");
        caixa_dialogo .addRadioButtonGroup("Método:", new String[]{"Expansão", "Equalização"}, 2, 1, "");
        caixa_dialogo.addDialogListener(this);
        caixa_dialogo.showDialog();
        
        
        // Aplica as alterações definitivas ou restaura a imagem original
        if (caixa_dialogo.wasOKed()) {
            imagem_original.updateAndDraw();
            if(metodo == "Expansão"){
            	criarHistograma(imagem_original, "Histograma depois da Expansão");
            }
            else {
            	criarHistograma(imagem_original, "Histograma depois da Equalização");
            }
        } else {
            imagem_original.setProcessor(originalProcessor);
            imagem_original.updateAndDraw();
        }
    }
    
    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        if (gd.wasCanceled()) return false;
        if (gd.wasOKed()) return false;

        metodo = caixa_dialogo.getNextRadioButton();

        workingProcessor = (ByteProcessor) originalProcessor.duplicate();
        imagem_original.setProcessor(workingProcessor);
        if(metodo == "Expansão"){
        	expansao(imagem_original);
        }
        else {
        	equalizacao(imagem_original, histogram);
        }
        return true;
    }
	
	@SuppressWarnings("deprecation")
	public int[] criarHistograma(ImagePlus image, String titulo) {
		
		ByteProcessor processor = (ByteProcessor) image.getProcessor();
        int width = image.getWidth();
        int height = image.getHeight();
        int[] histogram= new int[256];
        for (int i = 0; i < 256; i++) {
        	histogram[i] = 0;
        }
        int pixel_value = 0;
        

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel_value = processor.getPixel(x, y);
                histogram[pixel_value]++;
            }
        }
        
        // Prepare data for plotting
        double[] xValues = new double[256];
        double[] yValues = new double[256];
        for (int i = 0; i < 256; i++) {
            xValues[i] = i; // Valores do pixel (0-255)
            yValues[i] = histogram[i]; // Frequencia correspondente
        }
        
        // Create the histogram plot
        @SuppressWarnings("deprecation")
		Plot plot = new Plot(titulo, "Pixel Intensity", "Frequency", xValues, yValues);
        plot.setLineWidth(2);
        plot.setColor("black");

        // Display the plot
        PlotWindow plotWindow = plot.show();
        
        return histogram;
	}
    
	public void expansao(ImagePlus image) {
		ByteProcessor processor = (ByteProcessor) image.getProcessor();
        int width = image.getWidth();
        int height = image.getHeight();
        int pixel, newPixel, high = -1, low = 256;
        
        
        //descobrir o a_low e a_high
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
 
                pixel = processor.getPixel(x, y);
             
                
                if (pixel < low) {
                    low = pixel;
                } 
                if (pixel > high){
                	high = pixel;
                }
            }
        }
        
        //Fac (a) = a_min + (a - a_low) * [(a_max - a_min) / (a_high - a_low)]
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
 
                pixel = processor.getPixel(x, y);
                newPixel = (int) (0 + (pixel - low) *  ((255.0-0) / (high - low)));
                
                processor.putPixel(x, y, newPixel);
            }
        }
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
 
                pixel = processor.getPixel(x, y);
             
                
                if (pixel < low) {
                    low = pixel;
                } 
                if (pixel > high){
                	high = pixel;
                }
            }
        }
        
	}
	
	public void equalizacao(ImagePlus image, int[] histogram) {
	/*
	 Algoritmo para a implementação da técnica
		■ Montar o histograma da imagem em uma estrutura de dados
		■ Calcular a probabilidade de ocorrência de um nível de intensidade na imagem utilizando 
		  	como base o histograma anteriormente computado
		■ Calcular a probabilidade acumulada para a sequência de tonalidades
		■ Multiplicar o valor máximo da paleta de tonalidades pela probabilidade acumulada
		■ Truncar o valor obtido gerando a nova tonalidade para a tonalidade computada
	*/
		
		ByteProcessor processor = (ByteProcessor) image.getProcessor();
        int width = image.getWidth();
        int height = image.getHeight();
        double[] pixelProbability= new double[256];
        double[] accProbability = new double[256];
        int pixel_value = 0, newPixel = 0;
        
        //Histograma já vem pronto
        
        //Probabilidade de ocorrência de cada nivel de intensidade
        //Calcular a probabilidade acumulada para a sequência de tonalidades
        for (int i = 0; i < 256; i++) {
        	pixelProbability[i] = ((double) histogram[i])/(width*height);
        	
        	if (i > 0) {
        	    accProbability[i] = accProbability[i-1] + pixelProbability[i];
        	} else {
        	    accProbability[i] = pixelProbability[i];
        	}
        	
        }
        
        //■ Multiplicar o valor máximo da paleta de tonalidades pela probabilidade acumulada
		//■ Truncar o valor obtido gerando a nova tonalidade para a tonalidade computada	

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel_value = processor.getPixel(x, y);
                newPixel = (int) Math.min(255, Math.max(0, (255 * accProbability[pixel_value])));
                processor.putPixel(x, y, newPixel);
            }
        }
        
	}
		
}
