package genetics.picture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Test {
	public static void main(String[] args) throws IOException {
		Configuration c = new Configuration();
		Genome g = new Genome(new BufferedImage(10000, 10000, BufferedImage.TYPE_3BYTE_BGR), c);
		ImageIO.write(ImageUtil.getImage(g.getData(), 1024, c), "jpg", new File("test.jpg"));
	}
}
