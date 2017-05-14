package genetics.picture;
import java.io.File;
import java.io.IOException;

public class Main {
	
	public static void main(String[] args) throws Exception {
		String[] images = {
//				"data/starry_night.jpg",
//				"data/great_wave.jpg",
//				"data/mona_lisa.jpg",
				"data/chrome.png",
				"data/eclipse_icon.png",
				"data/google_icon.png",
				"data/firefox.png"
		};
		Configuration config = new Configuration();
//		config.numMutations = 50;
		config.numCrossPts = 5;
		config.compSize = 75;
		config.numVertices = 3;
		config.replaceDir = false;
		
		System.out.println("IMPORTANT NOTE: To ensure proper shutdown, press ENTER to stop (not the red square)");
		
		GARunner[] runners = new GARunner[images.length];
		for (int i = 0; i < images.length; i++) {
			runners[i] = new GARunner(new File(images[i]), config);
			runners[i].start();
		}
		
		Thread shutdownThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						System.in.read();
						break;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
					
				for (GARunner runner : runners) {
					runner.terminate();
					try {
						runner.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		shutdownThread.start();

		while (true) {
			boolean done = true;
			for (GARunner runner : runners) {
				done = done && !runner.isRunning();
			}
			if (done) {
				break;
			}
			Thread.sleep(250);
		}
		
		System.in.close();
		
		shutdownThread.join();		
		
	}

}
