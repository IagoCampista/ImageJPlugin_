import ij.plugin.PlugIn;
import ij.ImagePlus; 
import ij.IJ;
import ij.process.ImageProcessor;

public class Inverter_Imagem_Nivel_Pixel implements PlugIn {
	public void run(String arg) {
		ImagePlus imagem = IJ.getImage();
		inverter_imagem(imagem);
		IJ.wait(1500);
		inverter_imagem(imagem);
	}	
	
	public void inverter_imagem(ImagePlus imagem)
	{
		ImageProcessor processador = imagem.getProcessor();
		
		if (imagem.getType() == ImagePlus.GRAY8) {
			inverter_pixels_8bits(processador, imagem.getWidth(), imagem.getHeight());
			IJ.log("Imagem Cinza");
		}
		else {
			if (imagem.getType() == ImagePlus.COLOR_RGB) {
				inverter_pixels_RGB(processador, imagem.getWidth(), imagem.getHeight());
				IJ.log("Imagem Colorida");
			}
			else {
				IJ.error("Imagem não suportada");
			}
		}	
		imagem.updateAndDraw();
	}	
	
	public void inverter_pixels_8bits(ImageProcessor processador, int largura_imagem, int altura_imagem)
	{
		int x, y, valorPixel;
		IJ.log(String.valueOf(largura_imagem));
		IJ.log(String.valueOf(altura_imagem));
		for (x = 0; x < largura_imagem; x++) {
			for (y = 0; y < altura_imagem; y++) {
				valorPixel = processador.getPixel(x, y);
				processador.putPixel(x, y, 255 - valorPixel);
			}	
		}
	}
	
	public void inverter_pixels_RGB(ImageProcessor processador, int largura_imagem, int altura_imagem)
	{
		int x, y, valorPixel[] = {0,0,0};
		
		for (x = 0; x < largura_imagem; x++) {
			for (y = 0; y < altura_imagem; y++) {
				valorPixel = processador.getPixel(x, y, valorPixel);
				valorPixel[0] = 255 - valorPixel[0];
				valorPixel[1] = 255 - valorPixel[1];
				valorPixel[2] = 255 - valorPixel[2];
				processador.putPixel(x, y, valorPixel);
			}	
		}
	}
}
