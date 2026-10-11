package Engine.Global;

import java.awt.Color;
import java.awt.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class Util {
    public static InputStream open(String s) {
		InputStream in = null;
		try {
			in = new FileInputStream(s);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

        return in;
    }
		
    public static Image getImage(String path) {
        try {
            InputStream in = new FileInputStream(path);
            // Chargement d'une image utilisable dans Swing
            return ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printError(String msg) {
        System.err.println("ERROR: " + msg);
    }

    public static void printWarning(String msg) {
        System.out.println("WARNING: " + msg);
    }

    public static Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }

    public static Color setColorOppacity(Color original, int alpha) {
        return new Color(original.getRed(), original.getGreen(), original.getBlue(), alpha);
    }
}
