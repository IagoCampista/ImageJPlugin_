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

public class Componentes_Conexos_ implements PlugIn  {
   
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
       //Cria uma imagem nova em branco (ou preto neste caso) do mesmo tamnho da imagem original
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
        	   //Confere se um pixel na imagem original não é preto ao mesmo tempo que confere se este mesmo pixel na imagem a ser rotulada é preto
        	   // ou seja, se ele faz parte de um componente conexo e ainda não foi rotulado
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
                       for(int i = 0; i<adjacentes.length; i++) {
                           int x_a, y_a;
                           x_a = adjacentes[i][0];
                           y_a = adjacentes[i][1];
                           
                           //confere se o pixel esta dentro da imagem
                           if(!(x_a < 0 || x_a >= width || y_a < 0 || y_a >= height)) {
                        	   //confere se o pixel adjacente tem o mesmo valor que o pixel em questao , ou seja se fazem parte do mesmo componente conexo e se ainda nao foi rotulado 
                               if(originalProcessor.getPixel(px, py) == originalProcessor.getPixel(x_a, y_a) && labeledProcessor.getPixel(x_a, y_a) == 0 ) {
                                   labeledProcessor.putPixel(x_a, y_a, label);
                                   //adiciona o pixel adjacente na lista para ser analizado na sequencia
                                   listaPixels.add(new int[] {x_a, y_a});
                               }
                           }
                       }
                   }
                   label += 50; // Incrementa o valor do rótulo para o próximo componente para que cada componente tenha um tom de cinza
                   if (label > 255) label = 50; // Reinicia o valor do rótulo se ultrapassar 255
               }
           }
       }
           
       imagem_rotulada.show();
       
       
   }
}