/*
 *Criar um plugin para gerar uma imagem RGB através da junção das imagens 8-bits geradas 
 *pelos plugins anteriores e que eram representantes dos canais.
 */
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.WindowManager;

public class RemontarImagemPlugin_ implements PlugIn {
	public void run(String arg) {
		IJ.log(String.valueOf(WindowManager.getImageCount()));
		int[] lista_Imagens = WindowManager.getIDList();
		for (int i=0; i< WindowManager.getImageCount();i++) {
			IJ.log(String.valueOf(lista_Imagens[i])+" ");
		}
		
		ImagePlus imagem_vermelha = WindowManager.getImage( lista_Imagens[0]);
		ImagePlus imagem_verde = WindowManager.getImage( lista_Imagens[1]);
		ImagePlus imagem_azul = WindowManager.getImage( lista_Imagens[2]);
		ImagePlus imagem_final = imagem_vermelha.duplicate();
		remontarImagem(imagem_final, imagem_vermelha, imagem_verde, imagem_azul);
		imagem_final.show();
		IJ.log("chegou final");
		
	}
	//adicioar checagem de qual 'e a r qual 'e a g e qual 'e a b, e nao atribuir hardcoded
    private void remontarImagem(ImagePlus imagem_final, ImagePlus imagem_vermelha, ImagePlus imagem_verde, ImagePlus imagem_azul) {
        ImageProcessor processor_vermelho = imagem_vermelha.getProcessor();
        ImageProcessor processor_verde = imagem_verde.getProcessor();
        ImageProcessor processor_azul = imagem_azul.getProcessor();
        ImageProcessor processor_final = imagem_final.getProcessor();
        
        int width = imagem_vermelha.getWidth();
        int height = imagem_vermelha.getHeight();
        
        // Loop through each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get the RGB values of the pixel
            	int[] image1 = new int[3];
                int[] image2 = new int[3];
                int[] image3 = new int[3];
                int[] image_final = new int[3];
                //IJ.log(String.valueOf(rgb[0])+" "+String.valueOf(rgb[1])+" "+String.valueOf(rgb[2]));
                processor_vermelho.getPixel(x, y, image1);
                processor_verde.getPixel(x, y, image2);
                processor_azul.getPixel(x, y, image3);
                //IJ.log(String.valueOf(rgb[0])+" "+String.valueOf(rgb[1])+" "+String.valueOf(rgb[2]));
                image_final[0]=image1[0];
                image_final[1]=image2[1];
                image_final[2]=image3[2];
                

                // Update the pixel with the modified RGB values
                processor_final.putPixel(x, y, image_final);
                
            }
        }
        // Update the image display
        imagem_final.updateAndDraw();
        

    }
}

