package genetics.picture;

public class Configuration {
	public double mutationProb = 0.01;
	public int numMutations = 25;
	public int populationSize = 50;
	public int generations = -1;
	public int eliteSize = 5;
	public int compSize = 125;
	public int outSize = 500;
	public int numShapes = 50;
	public int numVertices = 4;
	public int numCrossPts = 5;
	public boolean writeConfig = true;
	public boolean writeStats = true;
	public boolean replaceDir = true;
	
	public int geneSize() {
		return 4 + 2 * numVertices;
	}
	
	public int genomeSize() {
		return geneSize() * numShapes;
	}
}
