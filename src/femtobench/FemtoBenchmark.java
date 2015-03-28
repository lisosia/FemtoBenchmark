package femtobench;

import java.io.OutputStream;
import java.io.PrintStream;

public class FemtoBenchmark {
	public final int DEFAULT_REPEAT_NUM = 100;
	private boolean enableGCbeforeRun = false;
	private boolean supressSysOut = false;
	private int numOfPreRun = 100;
	final int repeat;

	public FemtoBenchmark() {
		repeat = DEFAULT_REPEAT_NUM;
	}

	public FemtoBenchmark(int num_of_repeat) {
		if (num_of_repeat < 2) {
			throw new IllegalArgumentException("num_of_repeat >= 1 is needed");
		}
		repeat = num_of_repeat;
	}

	/**
	 * call System.gc() before every call of Testable#run(). Fairness of
	 * measurement may increase but measurement time also increses considerably.
	 */
	public void enableGCbeforeRun() {
		this.enableGCbeforeRun = true;
	}

	/**
	 * Supress System.out before call of Testable#setUp,run,TearDown
	 */
	public void supressSysOut() {
		this.supressSysOut = true;
	}

	/**
	 * by default, setUp(),run(),tearDown() of each Testable methods is called
	 * 100 times before measurement to tell JVM that testing method is hot sopt.
	 * variance of measurement time may increase if disabled.
	 */
	public void setNumOfPreRun(int numOfPreRun) {
		this.numOfPreRun = numOfPreRun;
	}

	public void runBenchmark(Testable... methods) {

		System.out.print("Doing pre-measurement: call each method#run "
				+ String.valueOf(numOfPreRun) + " times.");
		ManipulateSysOut.supress();
		for (int j = 0; j < this.numOfPreRun; j++) {
			for (int i = 0; i < methods.length; i++) {
				Testable m = methods[i];
				try {
					m.setUp();
					m.run();
					m.tearDown();
				} catch (Exception e) {
					errorOccurs(m.getClass().getSimpleName(), e);
				}
			}
		}
		ManipulateSysOut.enable();
		System.out.println("   Done");

		long[][] times = new long[methods.length][repeat];
		Analysis[] benchmarks = new Analysis[methods.length];
		System.out.print("Test each Testable method#run "
				+ String.valueOf(repeat) + " times");
		if (supressSysOut) {
			ManipulateSysOut.supress();
		}
		System.gc();
		for (int j = 0; j < repeat; j++) {
			for (int i = 0; i < methods.length; i++) {
				Testable m = methods[i];
				try {
					m.setUp();
					if (enableGCbeforeRun) {
						System.gc();
					}
					long start = System.nanoTime();
					m.run();
					long end = System.nanoTime();
					m.tearDown();
					times[i][j] = end - start;
				} catch (Exception e) {
					errorOccurs(m.getClass().getSimpleName(), e);
				}
			}
		}

		if (supressSysOut) {
			ManipulateSysOut.enable();
		}
		System.out.println("   Done");

		for (int i = 0; i < methods.length; i++) {
			benchmarks[i] = Analysis.analyze(times[i]);
		}

		int maxLen = 4;
		for (int i = 0; i < methods.length; i++) {
			maxLen = Math.max(maxLen, methods[i].getClass().getSimpleName()
					.length());
		}
		Analysis.pre_print(maxLen);
		for (int i = 0; i < methods.length; i++) {
			benchmarks[i].print(methods[i].getClass().getSimpleName(), maxLen);
		}
	}

	private static void errorOccurs(String methodName, Exception e) {
		System.err.println("Exception occured when calling: " + methodName);
		System.err.println("Stacktrace is here: ");
		e.printStackTrace();
		System.err.println("Stop Benchmark...");
		System.exit(1);
	}

	static class Analysis {
		final double mean;
		final double sigma;

		public Analysis(double mean, double sigma) {
			this.mean = mean;
			this.sigma = sigma;
		}

		// public long getMean(){return mean;}
		// public long getSigma(){return sigma;}

		static void pre_print(int nameSpace) {
			System.out.println("\n          << Result >>");
			String format = "%" + String.valueOf(nameSpace) + "s | %s%n";
			System.out.printf(format, "Name",
					" mean +- sigma ([sigma/mean*100] %) [nano sec]");
		}

		void print(String method_name, int nameSpace) {
			String format = "%" + String.valueOf(nameSpace)
					+ "s | %4.0f +- %2.0f (%1.1f %%)%n";
			System.out.printf(format, method_name, mean, sigma, sigma / mean
					* 100);
		}

		static Analysis analyze(long[] nanos) {
			if (nanos.length < 2) {
				throw new IllegalArgumentException("nanos size < 2");
			}
			double mean = 0;
			for (int i = 0; i < nanos.length; i++) {
				mean += nanos[i];
			}
			mean /= nanos.length;
			double sigma = 0;
			for (int i = 0; i < nanos.length; i++) {
				sigma += (nanos[i] - mean) * (nanos[i] - mean);
			}

			sigma /= (nanos.length);
			sigma = Math.sqrt(sigma);
			return new Analysis(mean, sigma);
		}
	}

	public static interface Testable {
		public abstract void setUp() throws Exception;

		public abstract void run() throws Exception;

		public abstract void tearDown() throws Exception;
	}

}

final class ManipulateSysOut {
	final static PrintStream originalSysout = System.out;
	final static PrintStream dummyStream = new PrintStream(new OutputStream() {
		public void write(int b) {
		}
	});

	private ManipulateSysOut() {
	}

	static void supress() {
		System.out.flush();
		System.setOut(dummyStream);
	}

	static void enable() {
		System.err.flush();
		System.setOut(originalSysout);
	}
}