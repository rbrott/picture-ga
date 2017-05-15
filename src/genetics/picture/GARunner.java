package genetics.picture;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;

public class GARunner extends Thread {

	private boolean running = true;
	private BufferedImage orig, small;
	private Configuration c;
	private List<Genome> population;
	private Stats s;
	private String name;
	private File outputDir;
	
	public GARunner(File file, Configuration config) {
		this(file, file.getName().split("\\.")[0], config);
	}
	
	public GARunner(File file, String name, Configuration config) {
		c = config;
		s = new Stats();
		this.name = name;
		String path = file.getParent();
		if (path == null) {
			path = "";
		}
		path += "/" + name;
		outputDir = new File(path);
		if (!c.replaceDir && outputDir.exists()) {
			int i = 1;
			do {
				i++;
				outputDir = new File(path + i);
			} while (outputDir.exists());
			this.name += i;
		}
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		try {
			orig = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		small = ImageUtil.resize(orig, c.compSize, c.compSize);

		synchronized (this) {
			population = new ArrayList<>();
			for (int i = 0; i < c.populationSize; i++) {
				population.add(new Genome(small, c));
			}
		}
		
		if (c.writeConfig) {
			writeConfig();
		}
	}
	
	private void writeConfig() {
		File configFile = new File(outputDir.getPath() + "/config.txt");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(configFile));
			pw.println(name + " configuration");
			pw.println("==================================");
			pw.println("# cross pts:\t" + c.numCrossPts);
			pw.println("mut prob:\t" + c.mutationProb);
			pw.println("mut amt:\t" + c.mutAmount);
			pw.println("pop size:\t" + c.populationSize);
			pw.println("elite size:\t" + c.eliteSize);
			pw.println("generations:\t" + c.generations);
			pw.println("comp size:\t" + c.compSize);
			pw.println("out size:\t" + c.outSize);
			pw.println("num shapes:\t" + c.numShapes);
			pw.println("num vertices:\t" + c.numVertices);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeStats() {
		File statsFile = new File(outputDir.getPath() + "/stats.txt");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(statsFile));
			pw.println(name + " stats");
			pw.println("==================================");
			pw.println("generations:\t" + s.generations);
			pw.println("best fitness:\t" + s.bestFitness);
			pw.println("avg ms/gen:\t" + s.avgMsPerGen);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		long lastTime = 0;
		
		while (running && (s.generations < c.generations || c.generations == -1)) {
			if (lastTime == 0) {
				lastTime = System.currentTimeMillis();
			} else {
				long now = System.currentTimeMillis();
				s.avgMsPerGen = 0.2 * (now - lastTime) + 0.8 * s.avgMsPerGen;
				lastTime = now;
			}
			
			synchronized (this) {
				population.sort(new Comparator<Genome>() {
					@Override
					public int compare(Genome a, Genome b) {
						return Double.compare(b.getFitness(), a.getFitness());
					}
				});

				Genome best = population.get(0);
				if (s.generations % 25 == 0) {
					Genome worst = population.get(population.size() - 1);
					double mean = 0, variance = 0;
					for (Genome g : population) {
						mean += g.getFitness();
					}
					mean /= c.populationSize;
					for (Genome g : population) {
						variance += Math.pow(g.getFitness() - mean, 2);
					}
					System.out.println(String.format("%s:\tgen %d:\tf[%.2f%%, %.2f%%, %.2f%%, %.2f%%]\t(%.1fms)",
							name, s.generations, 100 * best.getFitness(), 100 * worst.getFitness(), 100 * mean, 100 * Math.sqrt(variance), s.avgMsPerGen));
					try {
						ImageIO.write(ImageUtil.getImage(best.getData(), c.outSize, c), "jpg", new File(outputDir.getPath() + "/" + s.generations + ".jpg"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (best.getFitness() > s.bestFitness) {
					s.bestFitness = best.getFitness();
				}
				
//				if (c.selCutoff > 0) {
//					for (int i = 0; i < c.selCutoff; i++) {
//						population.remove(population.size() - 1);
//					}
//				}
				
				List<Genome> nextPop = new ArrayList<>();
				
				// crossover and mutation
				for (int j = 0; j < c.populationSize - c.eliteSize; j++) {
					Genome parent1 = sampleGenome(population);
					Genome parent2 = sampleGenome(population);
					nextPop.add(new Genome(parent1, parent2));
				}
				
				// elitism
				for (int j = 0; j < c.eliteSize; j++) {
					nextPop.add(population.get(j));
				}
				
				population = nextPop;	
			}
			
			s.generations++;
		}
		
		terminate();
		
	}
	
	public String getImageName() {
		return name;
	}
	
	public List<Genome> getPopulation() {
		return population;
	}
	
	public Configuration getConfig() {
		return c;
	}
	
	public Stats getStats() {
		return s;
	}
	
	public File getOutputDir() {
		return outputDir;
	}
	
	public void terminate() {
		if (running) {
			running = false;
			if (c.writeStats) {
				writeStats();
			}
		}
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public static Genome sampleGenome(List<Genome> genomes) {
		double sum = 0;
		for (Genome g : genomes) {
			sum += g.getFitness();
		}
		double sel = sum * Math.random();
		int i = 0;
		while (sel > genomes.get(i).getFitness()) {
			sel -= genomes.get(i).getFitness();
			i++;
		}
		return genomes.get(i);
	}
	
}
