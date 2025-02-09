///*
//Criar um plugin para converter uma imagem RGB em Escala de Cinza utilizando cada um dos três métodos descritos 
//no livro Principles of Digital Image Processing (Página 202 - Seção 8.2.1).
//
//Deverá ser criada uma interface gráfica que viabilize a seleção de uma dentre as três estratégias disponibilizadas 
//na literatura através de botões de rádio.
//
//Também deverá ser disponibilizada uma caixa de seleção que, quando marcada, não altera a imagem original, 
//mas sim cria uma nova imagem em tons de cinza.
//
//
//Método 1 - média
//
//y = wr*R + wg*G + wb*B
//
//Método 2 - média ponderda 
//	wr = 0.299		wg= 0.587		wb= 0.114
//	
//Método 3 - média ponderada recomendacao ITU-BT.709
//	wr = 0.2125		wg= 0.7154		wb= 0.072
//*/
//
//import ij.IJ;
//import ij.ImagePlus;
//import ij.WindowManager;
//import ij.gui.GenericDialog;
//import ij.plugin.PlugIn;
//import ij.process.ByteProcessor;
//import ij.process.ImageProcessor;
//
//public class RGBtoGray_ implements PlugIn {
//	public void run(String arg) {
//		// Verifica se há uma imagem aberta
//        int[] lista_Imagens = WindowManager.getIDList();
//        if (lista_Imagens == null || lista_Imagens.length == 0) {
//            IJ.showMessage("Nenhuma imagem aberta.");
//            return;
//        }
//
//        // Cria a interface gráfica
//        GenericDialog gd = new GenericDialog("Conversão para Escala de Cinza");
//        gd.addRadioButtonGroup("Método de conversão:", new String[]{"Média", "Média Ponderada", "Média Ponderada ITU-BT.709"}, 3, 1, "Média");
//        gd.addCheckbox("Criar nova imagem em tons de cinza", true);
//        gd.showDialog();
//
//        // Verifica se o usuário cancelou o diálogo
//        if (gd.wasCanceled()) {
//            return;
//        }
//
//        // Obtém as escolhas do usuário
//        String metodo = gd.getNextRadioButton();
//        boolean criarNovaImagem = gd.getNextBoolean();
//        IJ.log("metodo:  "+metodo);
//        IJ.log("boolean:  "+criarNovaImagem);
//
//        // Obtém a imagem original
//        ImagePlus imagem_original = WindowManager.getImage(lista_Imagens[0]);
//
//        // Aplica o método selecionado
//        ImagePlus grayImage = null;
//        switch (metodo) {
//            case "Média":
//                grayImage = regularAVG(imagem_original, criarNovaImagem);
//                break;
//            case "Média Ponderada":
//                grayImage = weightedAVG(imagem_original, criarNovaImagem);
//                break;
//            case "Média Ponderada ITU-BT.709":
//                grayImage = ITU_weightedAVG(imagem_original, criarNovaImagem);
//                break;
//        }
//
//        // Exibe a imagem resultante
//        if (grayImage != null) {
//            grayImage.show();
//        }
//	}
//	
//    private ImagePlus regularAVG(ImagePlus image, boolean criarNovaImagem) {
//        //ImageProcessor processor = image.getProcessor();
//    	ImageProcessor processor = criarNovaImagem ? image.getProcessor().duplicate() : image.getProcessor();
//    	
//        int width = image.getWidth();
//        int height = image.getHeight();
//        int gray = 0;
//        ByteProcessor cinza_processor = new ByteProcessor(width, height);
//        
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int[] rgb = new int[3];
//                processor.getPixel(x, y, rgb);
//                gray = (rgb[0]+rgb[1]+rgb[2])/3;
//
//                // Atualiza a imagem com o novo valor cinza
//                cinza_processor.putPixel(x, y, gray);
//                
//            }
//        }
//        image.updateAndDraw();
//        return criarNovaImagem ? new ImagePlus("Grayscale Image (Média)", cinza_processor) : image;
////        ImagePlus grayImage = new ImagePlus("Grayscale Image", cinza_processor);
////        grayImage.show();
//    }
//    
//    private ImagePlus weightedAVG(ImagePlus image, boolean criarNovaImagem) {
//        ImageProcessor processor = image.getProcessor();
//        
//        int width = image.getWidth();
//        int height = image.getHeight();
//        double gray = 0;
//        ByteProcessor cinza_processor = new ByteProcessor(width, height);
//        
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int[] rgb = new int[3];
//                processor.getPixel(x, y, rgb);
//                gray = ((0.299*rgb[0])+(0.587*rgb[1])+(0.114*rgb[2]))/3;
//
//                // Atualiza a imagem com o novo valor cinza
//                cinza_processor.putPixel(x, y, (int) gray);
//                
//            }
//        }
//        return criarNovaImagem ? new ImagePlus("Grayscale Image (Média Ponderada)", cinza_processor) : image;
////        ImagePlus grayImage = new ImagePlus("Weighted Grayscale Image", cinza_processor);
////        grayImage.show();
//    }
//    
//    private ImagePlus ITU_weightedAVG(ImagePlus image, boolean criarNovaImagem) {
//        ImageProcessor processor = image.getProcessor();
//        
//        int width = image.getWidth();
//        int height = image.getHeight();
//        double gray = 0;
//        ByteProcessor cinza_processor = new ByteProcessor(width, height);
//        
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int[] rgb = new int[3];
//                processor.getPixel(x, y, rgb);
//                gray = ((0.2125*rgb[0])+(0.7154*rgb[1])+(0.072*rgb[2]))/3;
//                // Atualiza a imagem com o novo valor cinza
//                cinza_processor.putPixel(x, y, (int) gray);
//                
//            }
//        }
//        return criarNovaImagem ? new ImagePlus("Grayscale Image (ITU-BT.709)", cinza_processor) : image;
////        ImagePlus grayImage = new ImagePlus("ITU-BT.709 Weighted Grayscale Image", cinza_processor);
////        grayImage.show();
//    }
//    
//}



import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class RGBtoGray_ implements PlugIn {
    public void run(String arg) {
        // Verifica se há uma imagem aberta
        int[] lista_Imagens = WindowManager.getIDList();
        if (lista_Imagens == null || lista_Imagens.length == 0) {
            IJ.showMessage("Nenhuma imagem aberta.");
            return;
        }

        // Cria a interface gráfica
        GenericDialog gd = new GenericDialog("Conversão para Escala de Cinza");
        gd.addRadioButtonGroup("Método de conversão:", new String[]{"Média", "Média Ponderada", "Média Ponderada ITU-BT.709"}, 3, 1, "Média");
        gd.addCheckbox("Criar nova imagem em tons de cinza", true);
        gd.showDialog();

        // Verifica se o usuário cancelou o diálogo
        if (gd.wasCanceled()) {
            return;
        }

        // Obtém as escolhas do usuário
        String metodo = gd.getNextRadioButton();
        boolean criarNovaImagem = gd.getNextBoolean();
        
        // Obtém a imagem original
        ImagePlus imagem_original = WindowManager.getImage(lista_Imagens[0]);
        
        // Aplica o método selecionado
        switch (metodo) {
            case "Média":
                convertToGray(imagem_original, criarNovaImagem, 1.0 / 3, 1.0 / 3, 1.0 / 3, "Grayscale Image (Média)");
                break;
            case "Média Ponderada":
                convertToGray(imagem_original, criarNovaImagem, 0.299, 0.587, 0.114, "Grayscale Image (Média Ponderada)");
                break;
            case "Média Ponderada ITU-BT.709":
                convertToGray(imagem_original, criarNovaImagem, 0.2125, 0.7154, 0.072, "Grayscale Image (ITU-BT.709)");
                break;
        }
    }

    private void convertToGray(ImagePlus image, boolean criarNovaImagem, double wr, double wg, double wb, String title) {
        ImageProcessor processor = image.getProcessor();
        int width = image.getWidth();
        int height = image.getHeight();
        ByteProcessor cinza_processor = criarNovaImagem ? new ByteProcessor(width, height) : null;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] rgb = new int[3];
                processor.getPixel(x, y, rgb);
                int gray = (int) (wr * rgb[0] + wg * rgb[1] + wb * rgb[2]);
                
                if (criarNovaImagem) {
                    cinza_processor.putPixel(x, y, gray);
                } else {
                	int[] cinza = new int[3];
                	cinza[0]=gray;
                	cinza[1]=gray;
                	cinza[2]=gray;
                    processor.putPixel(x, y, cinza);
                }
            }
        }

        if (criarNovaImagem) {
            new ImagePlus(title, cinza_processor).show();
        } else {
            image.updateAndDraw();
        }
    }
}
