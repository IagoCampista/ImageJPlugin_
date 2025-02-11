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
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class RGBemCinza_ implements PlugIn {
    public void run(String arg) {
    	
        // Verifica se há uma imagem aberta
        int[] lista_Imagens = WindowManager.getIDList();
        if (lista_Imagens == null || lista_Imagens.length == 0) {
            IJ.showMessage("Nenhuma imagem aberta.");
            return;
        }
        // Obtém a imagem original
        ImagePlus imagem_original = WindowManager.getImage(lista_Imagens[0]);

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
        
        
        // Aplica o método selecionado
        switch (metodo) {
            case "Média":
                convertToGray(imagem_original, criarNovaImagem, 1.0/3, 1.0/3, 1.0/3, "Imagem em Escala de Cinza (Média)");
                break;
            case "Média Ponderada":
                convertToGray(imagem_original, criarNovaImagem, 0.299, 0.587, 0.114, "Imagem em Escala de Cinza (Média Ponderada)");
                break;
            case "Média Ponderada ITU-BT.709":
                convertToGray(imagem_original, criarNovaImagem, 0.2125, 0.7154, 0.072, "Imagem em Escala de Cinza (ITU-BT.709)");
                break;
        }
    }

    private void convertToGray(ImagePlus image, boolean criarNovaImagem, double wr, double wg, double wb, String title) {
        ImageProcessor original_processor = image.getProcessor();
        int width = image.getWidth();
        int height = image.getHeight();
        ByteProcessor cinza_processor = criarNovaImagem ? new ByteProcessor(width, height) : null;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] rgb = new int[3];
                original_processor.getPixel(x, y, rgb);
                int gray = (int) (wr * rgb[0] + wg * rgb[1] + wb * rgb[2]);
                
                if (criarNovaImagem) {
                    cinza_processor.putPixel(x, y, gray);
                } else {
                	int[] cinza = new int[3];
                	cinza[0]=gray;
                	cinza[1]=gray;
                	cinza[2]=gray;
                    original_processor.putPixel(x, y, cinza);
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
