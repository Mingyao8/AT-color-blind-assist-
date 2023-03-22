package AT;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class color_blind{
	public static void main(String args[]) throws IOException {
		BufferedImage img = null;
		File f = null;
		// read image
		try {
			f = new File("C:/Users/user/Desktop/AT/test_3.jpg");
			img = ImageIO.read(f);
		} catch (IOException e) {
			System.out.println(e);
		}
		// get image info
		int width = img.getWidth();
		int height = img.getHeight();
		// processing
		for (int i = 0; i <= width - 1; i++) {
			for (int j = 0; j <= height - 1; j++) {
				Color origin = new Color(img.getRGB(i, j));
				// show pixels value
				System.out.println(origin);
				int ored = origin.getRed();
				int ogreen = origin.getGreen();
				int minus = 255-ored;
				// reset image
				if (ored > 170 &&  ogreen < 200 ) {
					int reset = new Color((ored + (minus/2)),0,0).getRGB();
					img.setRGB(i, j, reset);
				}

			}
		}

		try {
			f = new File("C:/Users/user/Desktop/AT/after_3.jpg");
			ImageIO.write(img, "jpg", f);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
