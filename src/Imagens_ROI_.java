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

public class Imagens_ROI_ implements PlugIn  {
    
    private ImagePlus imagem_original, imagem_auxiliar;
    private ByteProcessor originalProcessor, workingProcessor, workingProcessor2;
    private GenericDialog caixa_dialogo;
    private String diretorio_origem, diretorio_destino;

	
	public void run(String arg) {
   
        
        // Cria a interface gráfica
        caixa_dialogo = new GenericDialog("Selecionar diretório");
        caixa_dialogo.addDirectoryField("Diretorio de entrada:", "/");
        caixa_dialogo.addDirectoryField("Diretorio de sáida:", "/");
        //caixa_dialogo.addDialogListener(this);
        
        caixa_dialogo.showDialog();
        
        
        // Aplica as alterações definitivas ou restaura a imagem original
        if (caixa_dialogo.wasOKed()) {
            diretorio_origem = caixa_dialogo.getNextString();
            diretorio_destino = caixa_dialogo.getNextString();
            IJ.log("Origem: " + diretorio_origem);
            IJ.log("Destino: " + diretorio_destino);
            //imagem2.updateAndDraw();
        } else {
        	
        }
        // Processa cada arquivo no diretório de origem
        File diretorio = new File(diretorio_origem);
        File[] arquivos = diretorio.listFiles();
        
        if (arquivos == null) {
            IJ.error("Nenhum arquivo encontrado no diretório de origem.");
            return;
        }
        

        for (File arquivo : arquivos) {
        	// confere se o arquivo é uma imagem e mais especificamente uma jpg ou png
            if (arquivo.isFile() && (arquivo.getName().endsWith(".png") || arquivo.getName().endsWith(".jpg"))) {
                
            	// Abre a imagem
                imagem_original = IJ.openImage(arquivo.getAbsolutePath());
                if (imagem_original == null) {
                    IJ.log("Não foi possível abrir a imagem: " + arquivo.getName());
                    continue;
                }
                else {
                	IJ.log("Imagem: " + arquivo.getName());
                }
                
                // Cria uma imagem auxiliar em 8 bits para fazer a analise
                imagem_auxiliar = imagem_original.duplicate();
                workingProcessor = (ByteProcessor) imagem_original.getProcessor().duplicate().convertToByte(true);
                imagem_auxiliar.setProcessor(workingProcessor);
                
                //Realizar o Threshold na imagem
                IJ.setAutoThreshold(imagem_auxiliar, "Otsu");
                //IJ.run(imagem_auxiliar, "Threshold...", "lower=50 upper=150");
                
                // Converte para binária
                IJ.run(imagem_auxiliar, "Make Binary", "");
                
                //IJ.run(imagem_auxiliar, "Fill Holes", "");
                
                //Intancia um novo Roi Manager
                RoiManager roiManager = new RoiManager();
                
                // Executa o comando Analyze Particles - size 800 foi um teste empírico, baseando-se na medida das arvores menores
                IJ.run(imagem_auxiliar, "Analyze Particles...", "size=2000-Infinity show=Nothing add");

                // Adiciona as ROIs ao RoiManager
                roiManager = null;
                roiManager = RoiManager.getInstance();
                if (roiManager == null) {
                    roiManager = new RoiManager();
                }

                
                if(roiManager.getCount()<40) {

                	// Processa cada ROI no RoiManager
                	for (int i = 0; i < roiManager.getCount(); i++) {
	                	Roi roi = roiManager.getRoi(i);
	                	
	                	imagem_original.setRoi(roi);
	                	ImagePlus imagem_Roi = new ImagePlus("ROI", imagem_original.getProcessor().crop());
	                	if (imagem_Roi.getWidth() > 0 && imagem_Roi.getHeight() > 0) {
	                		IJ.save(imagem_Roi, diretorio_destino + i + "_" + imagem_original.getTitle());
	                	}
	                }
                	
	                IJ.log("Imagem " + arquivo.getName() +" processada e salva.");
                }

            	else {
            		IJ.log("Quantidade de ROIs da imagem: " + arquivo.getName() +" superior a 40, imagens nao salvas pois pode se tratar de um erro de analise. Qtd de Rois: "+String.valueOf(roiManager.getCount()));
            	}
                roiManager.reset(); 
            }
        }
    }
}
