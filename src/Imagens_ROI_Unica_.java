
/*
Implementar um plugin para identificar as ROIs existentes em uma imagem e salvar cada uma destas como uma imagem individual no sistema de arquivos. 
O plugin deverá solicitar um diretório de entrada e um diretório de saída. Todas as imagens existentes no diretório de entrada deverão ter os seus 
ROIs extraídos e gravados no diretório de saída.

Pegando como exemplo as imagens microscópicas analisadas na tarefa da segunda aula, é como se gravássemos cada uma das células detectadas na 
amostra como uma imagem independente no sistema de arquivos.

Cada aluno deverá utilizar a imagem indicada na planilha de controle dos trabalhos.
Imagens disponíveis em: https://drive.google.com/drive/folders/1rcUPLtsBTt6v-7fxHhu0u8yW3QKX-8Y3?usp=sharing

Sugestão de algoritmo:

Ler o diretório de origem
Ler o diretório de destino
Para cada arquivo do diretório de origem
    Obter a imagem original
    Transformar a imagem para 8-Bits
    Realizar o Threshold na imagem

    Executar o comando Analyze Particles para identificar automaticamente as ROIs presentes na imagem, adicioná-los no RoiManager
    Colocar a imagem RGB cuja máscara foi aplicada, abaixo do overlay de análise
    Para todas as ROIs existentes no ROIManager
          Setar o ROI na imagem de origem (Imagem Colorida)
          Transformar o ROI em um ImagePlus
          Gravar a imagem no diretório de destino

Obs:

Está liberado o uso de qualquer comando de alto nível como Make Binary,  Fill Holes e Analyze Particles...,  etc, que poderão ser executados com IJ.run.
*/

import java.io.File;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Overlay;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.frame.RoiManager;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Imagens_ROI_Unica_ implements PlugIn  {
   
   private ImagePlus imagem_original, imagem_auxiliar;
   private ByteProcessor originalProcessor, workingProcessor, workingProcessor2;
   private ImageProcessor ip;
   private GenericDialog caixa_dialogo;
   private String diretorio_origem, diretorio_destino;
   private int width, height;
	
	public void run(String arg) {
		
		// Verifica se há uma imagem aberta, se nao abre uma imagem binaria para teste 
        int[] lista_Imagens = WindowManager.getIDList();
        if (lista_Imagens == null || lista_Imagens.length == 0) {
            imagem_original = new ImagePlus("/Users/iagocampista/Library/CloudStorage/GoogleDrive-iagocampista@gmail.com/Other computers/Meu computador/Dados PC-GoogleDrive/Estudos 2024.2/Processamento de Imagens/imagensArvores/arvores32.jpg");
            imagem_auxiliar = imagem_original.duplicate();
            imagem_original.show(); 
        }
        else {
        	imagem_original = WindowManager.getCurrentImage();
        	imagem_auxiliar = imagem_original.duplicate();
        }
        diretorio_origem = "/Users/iagocampista/Library/CloudStorage/GoogleDrive-iagocampista@gmail.com/Other computers/Meu computador/Dados PC-GoogleDrive/Estudos 2024.2/Processamento de Imagens/imagensArvores/ArvoresTeste/";
        diretorio_destino ="/Users/iagocampista/Library/CloudStorage/GoogleDrive-iagocampista@gmail.com/Other computers/Meu computador/Dados PC-GoogleDrive/Estudos 2024.2/Processamento de Imagens/imagensArvores/Arvores Individuais/";
  


       // Converte a imagem para 8 bits
       workingProcessor = (ByteProcessor) imagem_original.getProcessor().duplicate().convertToByte(true);
       imagem_auxiliar.setProcessor(workingProcessor);
       
       //Realizar o Threshold na imagem
       IJ.run(imagem_auxiliar, "Threshold...", "lower=50 upper=150");
       
       // Converte para máscara binária
       IJ.run(imagem_auxiliar, "Convert to Mask", "");
       
       
       //imagem_auxiliar.show();
       
       //imp imagem original
       
       // Executa o comando Analyze Particles
       RoiManager roiManager = new RoiManager();
       ResultsTable rt = new ResultsTable();
       IJ.run(imagem_auxiliar, "Analyze Particles...", "size=800-Infinity show=Nothing add");

       // Adiciona as ROIs ao RoiManager
       roiManager = RoiManager.getInstance();
       if (roiManager == null) {
           roiManager = new RoiManager();
       }

    // Coloca a imagem RGB abaixo do overlay de análise
       ImagePlus originalImp = imagem_original.duplicate();
       Overlay overlay = imagem_original.getOverlay();
       if (overlay != null) {
           originalImp.setOverlay(overlay);
       }
       
       if(roiManager.getCount()<40) {
    	// Processa cada ROI no RoiManager
           for (int i = 0; i < roiManager.getCount(); i++) {
               Roi roi = roiManager.getRoi(i);
               originalImp.setRoi(roi);
               ImagePlus roiImp = new ImagePlus("ROI", originalImp.getProcessor().crop());
               IJ.save(roiImp, diretorio_destino + i + "_" + originalImp.getTitle());
           }
           IJ.log("Imagem processada e salva.");
       }
       else {
    	   IJ.log("Quantidade de ROIs superior a 40, imagens nao salvas pois pode se tratar de um erro de analise. Qtd de Rois: "+String.valueOf(roiManager.getCount()));
       }

    

       
   
       
  

   }
}
   

