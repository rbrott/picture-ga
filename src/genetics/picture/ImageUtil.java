package genetics.picture;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

public class ImageUtil {

	public static BufferedImage getImage(double[] g, int size, Configuration c) {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		for (int i = 0; i < c.genomeSize(); i += c.geneSize()) {
			g2d.setColor(new Color(
					(float) g[i],
					(float) g[i+1],
					(float) g[i+2],
					(float) g[i+3]
					));
			GeneralPath tri = new GeneralPath();
			tri.moveTo(size * g[i+4], size * g[i+5]);
			for (int j = 1; j < c.numVertices; j++) {
				tri.lineTo(size * g[i + 4 + 2 * j], size * g[i + 5 + 2 * j]);
			}
			tri.closePath();
			g2d.fill(tri);
		}
		return image;
	}
	
	public static double compare(BufferedImage a, BufferedImage b) {
		DataBuffer otherData = a.getData().getDataBuffer();
		DataBuffer refData = b.getData().getDataBuffer();
		double dist = 0;
		for (int i = 0; i < refData.getSize(); i++) {
			int otherByte = 0xFF & otherData.getElem(i);
			int refByte = 0xFF & refData.getElem(i);
			int diff = otherByte - refByte;
			dist += (diff * diff);
		}
		dist /= refData.getSize();
		dist /= 256 * 256;
		return 1.0 - dist;
	}
	
	public static BufferedImage resize(BufferedImage image, int width, int height) {
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = (Graphics2D) resized.getGraphics();
		g2d.drawImage(image.getScaledInstance(width, height, Image.SCALE_DEFAULT), 0, 0, null);
		return resized;
	}

}
