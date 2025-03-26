/*
Cada um dos componentes deverá ser pintado com um tom de cinza.
Obs: Utilizar poucos componentes conexos e uma imagem pequena para teste

Entrada: Imagem Binária Î = (Di, I), e Relação de Adjacência A;
Saída: Imagem Rotulada J' = (Di, J), onde, inicialmente, J(p) = 0 [para todo p pertecente a Dj], tal que p é um pixel da imagem e Dj, 
é o domínio da Imagem J. Note que Dj, = Di;
Auxiliares: FIFO Q e varivel inteira label = 1;

Para todo pixel p pertecente a Di, tal que J(p) = 0, faça
	J(p) = label
	insira p em Q
	
	Enquanto Q != 0
		Remova p de Q
		Para todo q pertecente a A(p), tal que q pertecence a Dj, exceto o proprio p, faça
			Se J(q) = 0 e I(p) = I(q), faça
				J(q) = J(p)
				insira q em Q
	label = label + 1
*/

import java.io.File;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import java.util.LinkedList;
import java.util.Queue;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Plugin_Teste_ implements PlugIn  {
   
   private ImagePlus imagem_original, imagem_rotulada;
   private ByteProcessor originalProcessor, labeledProcessor, workingProcessor;
   private int width, height, label;
   private int [][] adjacentes;
   private Queue<int[]> listaPixels = new LinkedList<>();
   
   
   
   public void run(String arg) {
       // Verifica se há uma imagem aberta, se nao abre uma imagem binaria para teste 
       int[] lista_Imagens = WindowManager.getIDList();
       if (lista_Imagens == null || lista_Imagens.length == 0) {
           imagem_original = new ImagePlus("/Users/iagocampista/Pictures/TesteComponentesConexos.png");
           imagem_original.show(); 
       }
       else {
           imagem_original = WindowManager.getCurrentImage();
       }
       
       //Confere se a imagem é 8 bits 
       if (imagem_original.getBitDepth() != 8) {
           IJ.showMessage("Error", "A imagem precisa ser em escala de cinza.");
           return;
       }
       imagem_rotulada = imagem_original.duplicate();
       imagem_rotulada.setTitle("Rotulos");
       
       originalProcessor = (ByteProcessor) imagem_original.getProcessor();
       width = originalProcessor.getWidth();
       height = originalProcessor.getHeight();
       labeledProcessor = new ByteProcessor(width, height);
       imagem_rotulada.setProcessor(labeledProcessor);
       
       label = 50;       
       
       for (int y = 0; y < height; y++) {
           for (int x = 0; x < width; x++) {
        	   
               if(originalProcessor.getPixel(x, y) != 0 && labeledProcessor.getPixel(x, y) == 0) {
                   
                   listaPixels.add(new int[] {x,y});
                   
                   while(!listaPixels.isEmpty()) {
                       int[] pixel = listaPixels.remove();
                       int px = pixel[0];
                       int py = pixel[1];
                       adjacentes = new int[][]  {
                           //Vizinhança 4 do pixel atual
                           {px-1, py},
                           {px+1, py},
                           {px, py-1},
                           {px, py+1}
                       };
                       for(int i = 0; i<4; i++) {
                           int x_a, y_a;
                           x_a = adjacentes[i][0];
                           y_a = adjacentes[i][1];
                           //confere se o pixel esta dentro da imagem
                           if(!(x_a < 0 || x_a >= width || y_a < 0 || y_a >= height)) {
                               if(labeledProcessor.getPixel(x_a, y_a) == 0 && originalProcessor.getPixel(px, py) == originalProcessor.getPixel(x_a, y_a)) {
                                   labeledProcessor.putPixel(x_a, y_a, label);
                                   listaPixels.add(new int[] {x_a, y_a});
                               }
                           }
                       }
                   }
                   label += 50; // Incrementa o valor do rótulo para o próximo componente
                   if (label > 255) label = 50; // Reinicia o valor do rótulo se ultrapassar 255
               }
           }
       }
           
       imagem_rotulada.show();
       
       
   }
}