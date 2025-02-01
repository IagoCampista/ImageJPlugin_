import ij.plugin.PlugIn;
import ij.ImagePlus; 
import ij.IJ;

public class Inverter_Imagem_Alto_Nivel implements PlugIn {
	public void run(String arg) {
		ImagePlus imagem = IJ.getImage();
		IJ.run(imagem, "Invert", "");
		IJ.wait(1500);
		IJ.run(imagem, "Invert", "");
	}
}
