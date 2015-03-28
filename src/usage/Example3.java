package usage;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import femtobench.FemtoBenchmark;
import femtobench.FemtoBenchmark.Testable;

public class Example3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FemtoBenchmark fbm = new FemtoBenchmark(100);
		fbm.enableGCbeforeRun();
		fbm.supressSysOut();
		fbm.setNumOfPreRun(1000);

		int num_of_arr = 100 * 1000;
		fbm.runBenchmark(new writeArray(num_of_arr), new writeInts(num_of_arr));
	}

}

class writeArray implements Testable {
	int a[];
	ByteArrayOutputStream bos;

	public writeArray(int size) {
		a = new int[size];
		for (int i = 0; i < a.length; i++) {
			a[i] = i;
		}
	}

	public void setUp() {
		bos = new ByteArrayOutputStream();
	}

	public void run() throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(a);
	}

	public void tearDown() {

	}
}

class writeInts implements Testable {
	int a[];
	ByteArrayOutputStream bos;

	public writeInts(int size) {
		a = new int[size];
		for (int i = 0; i < a.length; i++) {
			a[i] = i;
		}
	}

	public void setUp() {
		bos = new ByteArrayOutputStream();
	}

	public void run() throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeInt(a.length);
		for (int i = 0; i < a.length; i++) {
			oos.writeInt(a[i]);
		}
	}

	public void tearDown() {

	}
}