import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

public class TestInvertPlugin_2 implements PlugIn {
public void run(String arg) {
	ImagePlus imagem = IJ.getImage();
	IJ.run(imagem,"Invert", "");
	IJ.wait(2500);
	IJ.run(imagem, "Invert", "");
	}
}
