package genetics.picture;

public class Configuration {
	public double mutationProb = 0.01;
	public double mutAmount = 0.1;
	public int populationSize = 50;
	public int generations = -1;
	public int eliteSize = 0;
	public int compSize = 125;
	public int outSize = 1024;
	public int numShapes = 125;
	public int numVertices = 3;
	public int numCrossPts = 3;
	public int selCutoff = 0;
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
