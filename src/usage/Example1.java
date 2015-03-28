package usage;

import java.io.IOException;
import java.util.Random;

import femtobench.FemtoBenchmark;

class Example1 {
	public static void main(String[] args) {
		FemtoBenchmark fbm = new FemtoBenchmark(1000);
		// fbm.enableGCbeforeRun();
		fbm.setNumOfPreRun(100);
		fbm.runBenchmark(new MethodA(), new MethodB());
	}

}

class MethodA implements FemtoBenchmark.Testable {
	private Random r;

	public void setUp() {
		r = new Random();
	}

	public void run() throws IOException {
		int n = 1;

		for (int i = 0; i < 1000; i++) {
			n += r.nextInt();
		}
		Integer.toString(n);
	}

	public void tearDown() {
		r = null;
	}
}

class MethodB implements FemtoBenchmark.Testable {
	private Random r;

	public void setUp() {
		r = new Random();
	}

	public void run() throws IOException {
		float n = 1;
		for (int i = 0; i < 1000; i++) {
			n += r.nextFloat();
		}
		Float.toString(n);
	}

	public void tearDown() {
		r = null;
	}
}