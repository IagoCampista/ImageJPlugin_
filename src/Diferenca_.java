import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Diferenca_ implements PlugIn {
    public void run(String arg) {
    	
        // Verifica se há uma imagem aberta
        int[] lista_Imagens = WindowManager.getIDList();
        if (lista_Imagens == null || lista_Imagens.length == 0) {
            IJ.showMessage("Nenhuma imagem aberta.");
            return;
        }
        // Obtém a imagem original

       diferenca(WindowManager.getImage(lista_Imagens[0]), WindowManager.getImage(lista_Imagens[1]), WindowManager.getImage(lista_Imagens[2]));
    }

    
    
    
    private void diferenca(ImagePlus imagem_original, ImagePlus image1, ImagePlus image2) {
    	
        ImageProcessor processor1 = image1.getProcessor();
        int width1 = image1.getWidth();
        int height1 = image1.getHeight();
        ImageProcessor processor2 = image2.getProcessor();
        
        ByteProcessor cinza_processor = new ByteProcessor(width1, height1);
        
        int g1, g2, gf;
        

        for (int y = 0; y < height1; y++) {
            for (int x = 0; x < width1; x++) {
                g1 = processor1.getPixel(x, y);
                g2 = processor2.getPixel(x, y);
                
                gf = 50+((Math.max(g1,g2)+10) - Math.min(g1,g2));
                cinza_processor.putPixel(x, y, gf);
                
            }
        }
        new ImagePlus("AQUI A DIFERNECA", cinza_processor).show();
        
    }
}
