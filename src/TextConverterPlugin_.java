import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TextConverterPlugin_ implements PlugIn {
    @Override
    public void run(String arg) {
        ImagePlus imp = IJ.getImage();
        ImageProcessor ip = imp.getProcessor();
        int width = ip.getWidth();
        int height = ip.getHeight();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("rgb_values.txt"))) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = ip.getPixel(x, y);
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = pixel & 0xff;
                    writer.write(String.format("(%d, %d, %d)\n", red, green, blue));
                }
            }
            IJ.showMessage("RGB values saved to rgb_values.txt");
        } catch (IOException e) {
            IJ.error("Error writing to file: " + e.getMessage());
        }
    }
}
