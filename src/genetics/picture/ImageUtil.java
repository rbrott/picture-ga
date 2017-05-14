package genetics.picture;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

public class ImageUtil {

	public static BufferedImage getImage(byte[] g, int size, Configuration c) {
		BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2d = (Graphics2D) image.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		int[] s = new int[c.genomeSize()];
		for (int i = 0; i < c.numShapes; i++) {
			loadScaledData(g, i, size, c, s);
			g2d.setColor(new Color(s[0], s[1], s[2], s[3]));
			GeneralPath tri = new GeneralPath();
			tri.moveTo(s[4], s[5]);
			for (int j = 1; j < c.numVertices; j++) {
				tri.lineTo(s[4 + 2 * j], s[5 + 2 * j]);
			}
			tri.closePath();
			g2d.fill(tri);
		}
		return image;
	}
	
	private static void loadScaledData(byte[] g, int i, int size, Configuration c, int[] out) {
		int index = i * c.geneSize();
		// RGBA color
		out[0] = 0xFF & g[index];
		out[1] = 0xFF & g[index + 1];
		out[2] = 0xFF & g[index + 2];
		out[3] = 0xFF & g[index + 3];
		// vertices
		for (int j = 0; j < c.numVertices; j++) {
			out[4 + 2 * j] = ((0xFF & g[index + 4 + 2 * j]) * size) / 255;
			out[5 + 2 * j] = ((0xFF & g[index + 5 + 2 * j]) * size) / 255;
		}
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
