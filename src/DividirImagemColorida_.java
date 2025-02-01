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
		IJ.log("chegou vermelho");
		imagem_vermelha.show();
		IJ.wait(1000);
		extractChannel(imagem_verde, 1);
		IJ.log("chegou verde");
		imagem_verde.show();
		IJ.wait(1000);
		extractChannel(imagem_azul, 2);
		IJ.log("chegou azul");
		imagem_azul.show();
		
	}
	// color = 0 -> red; =1 -> green; =2 -> blue 
    private void extractChannel(ImagePlus image, int color) {
        ImageProcessor processor = image.getProcessor();
        int width = image.getWidth();
        int height = image.getHeight();
        
        // Loop through each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the RGB values of the pixel
                int[] rgb = new int[3];
                processor.getPixel(x, y, rgb);

                // Extract the selected channel and set the other channels to 0
                switch (color) {
                    case 0:
                        rgb[1] = rgb[0]; // Green channel
                        rgb[2] = rgb[0]; // Blue channel
                        break;
                    case 1:
                        rgb[0] = rgb[1]; // Red channel
                        rgb[2] = rgb[1]; // Blue channel
                        break;
                    case 2:
                        rgb[0] = rgb[2]; // Red channel
                        rgb[1] = rgb[2]; // Green channel
                        break;
                    default:
                    	IJ.log(String.valueOf(color));
                        IJ.error("Invalid channel selected.");
                        return;
                }

                // Update the pixel with the modified RGB values
                processor.putPixel(x, y, rgb);
                
            }
        }
        // Update the image display
        //image.updateAndDraw();
    }
}

