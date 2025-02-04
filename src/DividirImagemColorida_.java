/*
 * Criar um plugin para transformar uma imagem RGB em três imagens 
 * em escala de cinza e apresentar as imagens resultantes na tela do ImageJ.
 * */

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class DividirImagemColorida_ implements PlugIn {
	public void run(String arg) {
		ImagePlus imagem_original = IJ.getImage();
		ImagePlus imagem_vermelha = imagem_original.duplicate();
		ImagePlus imagem_verde = imagem_original.duplicate();
		ImagePlus imagem_azul = imagem_original.duplicate();
		extractChannel(imagem_vermelha, 0);
		imagem_vermelha.show();
		extractChannel(imagem_verde, 1);
		imagem_verde.show();
		extractChannel(imagem_azul, 2);
		imagem_azul.show();
		
	}
	// color = 0 -> red; =1 -> green; =2 -> blue 
    private void extractChannel(ImagePlus image, int color) {
        ImageProcessor processor = image.getProcessor();
        int width = image.getWidth();
        int height = image.getHeight();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] rgb = new int[3];
                processor.getPixel(x, y, rgb);

                //Copia os valores de canal {color} para os demais canais
                switch (color) {
                    case 0:
                        rgb[1] = rgb[0];
                        rgb[2] = rgb[0];
                        break;
                    case 1:
                        rgb[0] = rgb[1];
                        rgb[2] = rgb[1];
                        break;
                    case 2:
                        rgb[0] = rgb[2];
                        rgb[1] = rgb[2];
                        break;
                    default:
                    	IJ.log(String.valueOf(color));
                        IJ.error("Canal invalido.");
                        return;
                }
                // Atualiza a imagem com o novo valor rgb em cinza
                processor.putPixel(x, y, rgb);
                
            }
        }
    }
}

