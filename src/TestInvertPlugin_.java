import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

public class TestInvertPlugin_ implements PlugIn {
	public void run(String arg) {
		ImagePlus imagem = IJ.getImage();
		for (int i = 0; i < 10; i++) {
			IJ.run(imagem,"Invert", "");
			IJ.wait(2500);
			IJ.run(imagem, "Invert", "");
		}
	}
}
