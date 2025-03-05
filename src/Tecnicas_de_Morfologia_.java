/*
Desenvolver um plugin para a aplicação de operações morfológicas em imagens binárias. Implementar as operações de dilatação, erosão, fechamento, abertura e borda (outline) para imagens binárias.

Deverá ser criada uma interface gráfica contendo botões de rádio para a escolha da técnica a ser utilizada.

O elemento estruturante a ser utilizado será quadrado com dimensões 3 x 3 para todas as técnicas.
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

public class Tecnicas_de_Morfologia_ implements PlugIn, DialogListener  {
    
    private ImagePlus imagem_original;
    private ByteProcessor originalProcessor, workingProcessor, workingProcessor2;
    private GenericDialog caixa_dialogo;
    private String metodo;
    private int width, height;
	
	public void run(String arg) {
    	 
        // Verifica se há uma imagem aberta, se nao abre uma imagem binaria para teste 
        int[] lista_Imagens = WindowManager.getIDList();
        if (lista_Imagens == null || lista_Imagens.length == 0) {
            imagem_original = new ImagePlus("/Users/iagocampista/Pictures/blobs-binary.png");
            imagem_original.show(); 
        }
        else {
        	imagem_original = WindowManager.getCurrentImage();
        }
        
       
        originalProcessor = (ByteProcessor) imagem_original.getProcessor();
        width = originalProcessor.getWidth();
        height = originalProcessor.getHeight();
        //workingProcessor = (ByteProcessor) originalProcessor.duplicate();
        
        //cria 2 processadores auxiliares, sendo o workingProcessor o processor que terá a imagem final
        // e o workingProcessor2 o auxiliar que será a imagem intermediaria nos processos de abertura e fechamento
        workingProcessor = new ByteProcessor(width, height);
        workingProcessor2 = new ByteProcessor(width, height);
        
        imagem_original.setProcessor(workingProcessor);

        //Confere se a imagem é 8 bits 
        if (imagem_original.getBitDepth() != 8) {
            IJ.showMessage("Error", "A imagem precisa ser em escala de cinza.");
            return;
        }
        
        // Cria a interface gráfica
        caixa_dialogo = new GenericDialog("Alterar características da Imagem");
        caixa_dialogo .addRadioButtonGroup("Filtro:", new String[]{"Dilatação", "Erosão", "Fechamento", "Abertura", "Borda (Outline)"}, 6, 1, "");
        caixa_dialogo.addDialogListener(this);
        caixa_dialogo.showDialog();
        
        
        // Aplica as alterações definitivas ou restaura a imagem original
        if (caixa_dialogo.wasOKed()) {
            imagem_original.updateAndDraw();
            //imagem2.updateAndDraw();
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
        
        switch (metodo) {
        case "Dilatação":
            dilatacao(originalProcessor, workingProcessor);
            break;
        case "Erosão":
            erosao(originalProcessor, workingProcessor);
            break;
        case "Fechamento":
            dilatacao(originalProcessor, workingProcessor2);
            erosao(workingProcessor2, workingProcessor);
            break;
        case "Abertura":
            erosao(originalProcessor, workingProcessor2);
            dilatacao(workingProcessor2, workingProcessor);
            break;
        case "Borda (Outline)":
            erosao(originalProcessor, workingProcessor2);
            subtracao(originalProcessor, workingProcessor2, workingProcessor);
            break;
        default:
            return false;
    }
        imagem_original.updateAndDraw();
        return true;
    }
	
    
    public void dilatacao(ByteProcessor originalProcessor, ByteProcessor workingProcessor) {
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
            	
                if(originalProcessor.getPixel(x, y) == 255) {

	                for (int ky = -1; ky <= 1; ky++) {
	                    for (int kx = -1; kx <= 1; kx++) {
	                    	workingProcessor.putPixel(x+kx, y+ky, 255);
	                    }
	                }
	                
                }
                else workingProcessor.putPixel(x, y, 0);
            
            }
        }
        
    }
    
    public void erosao(ByteProcessor originalProcessor, ByteProcessor workingProcessor) {
        boolean isFull = true;
        
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
            	
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                    	if(originalProcessor.getPixel(x+kx, y+ky) == 0) {
                    		isFull = false;
                    	}
                    }
                }
	            
                if(isFull) {
                	workingProcessor.putPixel(x, y, 255);
                }
                else {
                	workingProcessor.putPixel(x, y, 0);
                	
                }
                isFull = true;
                
            
            }
        }
    }
    public void subtracao(ByteProcessor processor1, ByteProcessor processor2, ByteProcessor processorResult) {
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
            	processorResult.putPixel(x, y, processor1.getPixel(x, y) - processor2.getPixel(x,y));
            
            }
        }
    }

    
}
