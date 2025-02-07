/*
Criar um plugin para converter uma imagem RGB em Escala de Cinza utilizando cada um dos três métodos descritos 
no livro Principles of Digital Image Processing (Página 202 - Seção 8.2.1).

Deverá ser criada uma interface gráfica que viabilize a seleção de uma dentre as três estratégias disponibilizadas 
na literatura através de botões de rádio.

Também deverá ser disponibilizada uma caixa de seleção que, quando marcada, não altera a imagem original, 
mas sim cria uma nova imagem em tons de cinza.


Método 1 - média

y = wr*R + wg*G + wb*B

Método 2 - média ponderda 
	wr = 0.299		wg= 0.587		wb= 0.114
	
Método 3 - média ponderada recomendacao ITU-BT.709
	wr = 0.2125		wg= 0.7154		wb= 0.072
*/

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class RGBtoGray_ implements PlugIn {
	public void run(String arg) {
		int[] lista_Imagens = WindowManager.getIDList();
		//colocar a imagem 1 pra puxar
		ImagePlus imagem_original = WindowManager.getImage(lista_Imagens[0]);
		
		regularAVG(imagem_original);
		
		weightedAVG(imagem_original);
		
		ITU_weightedAVG(imagem_original);
	}
	
    private void regularAVG(ImagePlus image) {
        ImageProcessor processor = image.getProcessor();
        
        int width = image.getWidth();
        int height = image.getHeight();
        int gray = 0;
        ByteProcessor cinza_processor = new ByteProcessor(width, height);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] rgb = new int[3];
                processor.getPixel(x, y, rgb);
                gray = (rgb[0]+rgb[1]+rgb[2])/3;

                // Atualiza a imagem com o novo valor cinza
                cinza_processor.putPixel(x, y, gray);
                
            }
        }
        ImagePlus grayImage = new ImagePlus("Grayscale Image", cinza_processor);
        grayImage.show();
    }
    
    private void weightedAVG(ImagePlus image) {
        ImageProcessor processor = image.getProcessor();
        
        int width = image.getWidth();
        int height = image.getHeight();
        double gray = 0;
        ByteProcessor cinza_processor = new ByteProcessor(width, height);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] rgb = new int[3];
                processor.getPixel(x, y, rgb);
                gray = ((0.299*rgb[0])+(0.587*rgb[1])+(0.114*rgb[2]))/3;

                // Atualiza a imagem com o novo valor cinza
                cinza_processor.putPixel(x, y, (int) gray);
                
            }
        }
        ImagePlus grayImage = new ImagePlus("Weighted Grayscale Image", cinza_processor);
        grayImage.show();
    }
    
    private void ITU_weightedAVG(ImagePlus image) {
        ImageProcessor processor = image.getProcessor();
        
        int width = image.getWidth();
        int height = image.getHeight();
        double gray = 0;
        ByteProcessor cinza_processor = new ByteProcessor(width, height);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] rgb = new int[3];
                processor.getPixel(x, y, rgb);
                gray = ((0.2125*rgb[0])+(0.7154*rgb[1])+(0.072*rgb[2]))/3;
                // Atualiza a imagem com o novo valor cinza
                cinza_processor.putPixel(x, y, (int) gray);
                
            }
        }
        ImagePlus grayImage = new ImagePlus("ITU-BT.709 Weighted Grayscale Image", cinza_processor);
        grayImage.show();
    }
    
}