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

public class Expansao_Equalizacao_ implements PlugIn, DialogListener {
    public void run(String arg) {
    	
        // Verifica se há uma imagem aberta
        int[] lista_Imagens = WindowManager.getIDList();
        if (lista_Imagens == null || lista_Imagens.length == 0) {
            IJ.showMessage("Nenhuma imagem aberta.");
            return;
        }
        
     // Obtém a imagem original (primeira imagem aberta)
        ImagePlus imagem_original = WindowManager.getImage(lista_Imagens[0]);
        ImageProcessor originalProcessor = imagem_original.getProcessor().duplicate();
        ImageProcessor workingProcessor = originalProcessor.duplicate();
        
     // Ensure the image is 8-bit grayscale
        if (imagem_original.getBitDepth() != 8) {
            IJ.showMessage("Error", "The image must be 8-bit grayscale.");
            return;
        }
        
        // Get the image processor and histogram
        int[] histogram = originalProcessor.getHistogram();
        
        criarHistograma(imagem_original);
        

        // Cria a interface gráfica
        GenericDialog caixa_dialogo = new GenericDialog("Alterar características da Imagem");
        caixa_dialogo .addRadioButtonGroup("Método:", new String[]{"Expansão", "Equalização"}, 2, 1, "Expansão");
        //caixa_dialogo.addDialogListener(this);
        caixa_dialogo.showDialog();
        
        String metodo = caixa_dialogo.getNextRadioButton();
        
        
        if (caixa_dialogo.wasOKed()) {
        	if(metodo == "Expansão"){
            	expansao(imagem_original);
            }
            else {
            	equalizacao(imagem_original);
            }
            imagem_original.updateAndDraw();
        } else {
            imagem_original.setProcessor(originalProcessor);
            imagem_original.updateAndDraw();
        }
     
        

    }

	@Override
	public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void criarHistograma(ImagePlus image) {
		
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
            xValues[i] = i; // Intensity levels (0-255)
            yValues[i] = histogram[i]; // Corresponding frequency
        }
        
        // Create the histogram plot
        @SuppressWarnings("deprecation")
		Plot plot = new Plot("Histogram", "Pixel Intensity", "Frequency", xValues, yValues);
        plot.setLineWidth(2);
        plot.setColor("black");

        // Display the plot
        PlotWindow plotWindow = plot.show();
	}
    
	public void expansao(ImagePlus image) {
		//Fac (a) = a_min + (a - a_low) * [(a_max - a_min) / (a_high - a_low)]
		
		//descobrir o a_low e a_high
		ByteProcessor processor = (ByteProcessor) image.getProcessor();
        int width = image.getWidth();
        int height = image.getHeight();
        int pixel, newPixel, high = -1, low = 256;

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
        
        IJ.log("lowest pixel: " + low);
        IJ.log("highest pixel: " + high);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
 
                pixel = processor.getPixel(x, y);
                newPixel = (int) (0 + (pixel - low) *  ((255.0-0) / (high - low)));
                
                processor.putPixel(x, y, newPixel);
            }
        }
        
        image.updateAndDraw();
        
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
        
        IJ.log("NEW lowest pixel: " + low);
        IJ.log("NEW highest pixel: " + high);
        
        
		
	}
	
	public void equalizacao(ImagePlus image) {
	/*
	 Algoritmo para a implementação da técnica
		■ Montar o histograma da imagem em uma estrutura de dados
		■ Calcular a probabilidade de ocorrência de um nível de intensidade na imagem utilizando 
		  	como base o histograma anteriormente computado
		■ Calcular a probabilidade acumulada para a sequência de tonalidades
		■ Multiplicar o valor máximo da paleta de tonalidades pela probabilidade acumulada
		■ Truncar o valor obtido gerando a nova tonalidade para a tonalidade computada
	*/
		//CRIAR Histograma
		ByteProcessor processor = (ByteProcessor) image.getProcessor();
        int width = image.getWidth();
        int height = image.getHeight();
        int[] histogram= new int[256];
        double[] pixelProbability= new double[256];
        double[] accProbability = new double[256];
        int pixel_value = 0, newPixel = 0;
        
        for (int i = 0; i < 256; i++) {
        	histogram[i] = 0;
        }
        
        

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel_value = processor.getPixel(x, y);
                histogram[pixel_value]++;
            }
        }
        
        for (int i = 0; i < 256; i++) {
        	pixelProbability[i] = ((double) histogram[i])/(width*height);
        	
        	IJ.log("Probabilidade do pixel "+ i +": " + pixelProbability[i]);
        	
        	if (i > 0) {
        	    accProbability[i] = accProbability[i-1] + pixelProbability[i];
        	} else {
        	    accProbability[i] = pixelProbability[i];
        	}
        	IJ.log("Probabilidade acumulada "+ i +": " + accProbability[i]);
        	
        	
        }
//        // Prepare data for plotting
//        double[] xValues = new double[256];
//        double[] yValues = new double[256];
//        for (int i = 0; i < 256; i++) {
//            xValues[i] = i; // Intensity levels (0-255)
//            yValues[i] = pixelProbability[i]; // Corresponding frequency
//        }
//        
//        // Create the histogram plot
//        @SuppressWarnings("deprecation")
//		Plot plot2 = new Plot("Pixel Intensity Probability", "Pixel Intensity", "Probability", xValues, yValues);
//        plot2.setLineWidth(2);
//        plot2.setColor("black");
//
//        // Display the plot
//        PlotWindow plotWindow2 = plot2.show();
//        
//        for (int i = 0; i < 256; i++) {
//            xValues[i] = i; // Intensity levels (0-255)
//            yValues[i] = accProbability[i]; // Corresponding frequency
//        }
//        
//        // Create the histogram plot
//        @SuppressWarnings("deprecation")
//		Plot plot1 = new Plot("Accumulated Pixel Intensity Probability", "Pixel Intensity", "Accumulated Probability", xValues, yValues);
//        plot1.setLineWidth(2);
//        plot1.setColor("black");
//
//        // Display the plot
//        PlotWindow plotWindow1 = plot1.show();
        
        
/*
 * 		■ Multiplicar o valor máximo da paleta de tonalidades pela probabilidade acumulada
		■ Truncar o valor obtido gerando a nova tonalidade para a tonalidade computada	
 * */
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixel_value = processor.getPixel(x, y);
                newPixel = (int) Math.min(255, Math.max(0, (255 * accProbability[pixel_value])));
                processor.putPixel(x, y, newPixel);
            }
        }
        image.updateAndDraw();
        
	}
		
}
