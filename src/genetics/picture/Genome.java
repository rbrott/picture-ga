package genetics.picture;
import java.awt.image.BufferedImage;

public class Genome {
	private double[] data;
	private double fitness;
	private BufferedImage refImage;
	private Configuration c;
	
	public Genome(BufferedImage image, Configuration config) {
		refImage = image;
		c = config;
		data = new double[c.genomeSize()];
		for (int j = 0; j < data.length; j++) {
			data[j] = Math.random();
//			if (j % c.geneSize() == 3) {
//				data[j] *= 0.5;
//			}
		}
		updateFitness();
	}
	
	public Genome(Genome parent1, Genome parent2) {
		refImage = parent1.refImage;
		c = parent1.c;
		data = new double[c.genomeSize()];
		
		int[] crossoverPts = new int[c.numCrossPts + 2];
		crossoverPts[1] = c.genomeSize();
		for (int i = 2; i < crossoverPts.length; i++) {
			int pt = (int) (c.genomeSize() * Math.random());
			int j = i;
			while (j > 0 && crossoverPts[j - 1] > pt) {
				crossoverPts[j] = crossoverPts[j - 1];
				j--;
			}
			crossoverPts[j] = pt;
		}
		for (int i = 1; i < crossoverPts.length; i++) {
			double[] src;
			if (i % 2 == 1) {
				src = parent1.getData();
			} else {
				src = parent2.getData();
			}
			int start = crossoverPts[i - 1], end = crossoverPts[i];
			System.arraycopy(src, start, data, start, end - start);
		}
		
		for (int i = 0; i < c.genomeSize(); i++) {
			if (Math.random() < c.mutationProb) {
				double val = data[i];
				val += (2 * c.mutAmount * Math.random()) - c.mutAmount;
				if (val > 1) {
					val = 1;
				} else if (val < 0) {
					val = 0;
				}
				data[i] = val;
			}
		}
		
		updateFitness();
	}
	
	public double[] getData() {
		return data;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	private void updateFitness() {
		BufferedImage image = ImageUtil.getImage(data, c.compSize, c);
		fitness = ImageUtil.compare(refImage, image);
	}
	
}
