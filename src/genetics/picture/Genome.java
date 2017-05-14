package genetics.picture;
import java.awt.image.BufferedImage;

public class Genome {
	private byte[] data;
	private double fitness;
	private BufferedImage refImage;
	private Configuration c;
	
	public Genome(BufferedImage image, Configuration config) {
		refImage = image;
		c = config;
		data = new byte[c.genomeSize()];
		for (int j = 0; j < c.genomeSize(); j++) {
			data[j] = (byte) (256 * Math.random());
		}
		updateFitness();
	}
	
	public Genome(Genome parent1, Genome parent2) {
		refImage = parent1.refImage;
		c = parent1.c;
		data = new byte[c.genomeSize()];
		
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
			byte[] src;
			if (i % 2 == 1) {
				src = parent1.getData();
			} else {
				src = parent2.getData();
			}
			int start = crossoverPts[i - 1], end = crossoverPts[i];
			System.arraycopy(src, start, data, start, end - start);
		}
		
		updateFitness();
	}
	
	public byte[] getData() {
		return data;
	}
	
	public double getFitness() {
		return fitness;
	}
	
	private void updateFitness() {
		BufferedImage image = ImageUtil.getImage(data, c.compSize, c);
		fitness = ImageUtil.compare(refImage, image);
	}
	
	public void bitMutate(int k) {
		for (int i = 0; i < k; i++) {
			int numIndex = (int) (c.genomeSize() * Math.random());
			int bitIndex = (int) (8 * Math.random());
			data[numIndex] ^= (1 << bitIndex);
		}
		updateFitness();
	}
	
	public void mutate(int k) {
		for (int i = 0; i < k; i++) {
			int gene = (int) (c.numShapes * Math.random());
			switch (Mutation.random()) {
			case COLOR_CHANGE:
				for (int j = 0; j < 4; j++) {
					data[gene * c.geneSize() + j] = (byte) (256 * Math.random());
				}
				break;
			case VERTEX_CHANGE:
				int vertex = (int) (c.numVertices * Math.random());
				data[gene * c.geneSize() + 4 + 2 * vertex] = (byte) (256 * Math.random());
				data[gene * c.geneSize() + 5 + 2 * vertex] = (byte) (256 * Math.random());
				break;
			case SWAP:
				int otherGene = (int) (c.numShapes * Math.random());
				byte[] temp = new byte[c.geneSize()];
				System.arraycopy(data, gene * c.geneSize(), temp, 0, c.geneSize());
				System.arraycopy(data, otherGene * c.geneSize(), data, gene * c.geneSize(), c.geneSize());
				System.arraycopy(temp, 0, data, otherGene * c.geneSize(), c.geneSize());
				break;
			}
		}
		updateFitness();
	}
	
}
