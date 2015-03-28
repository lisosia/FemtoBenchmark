package usage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import usage.myutils.SerializableIntBuffer;
import femtobench.FemtoBenchmark;

class Example2 {
	public static void main(String[] args) {
		FemtoBenchmark fbm = new FemtoBenchmark(100);
		fbm.enableGCbeforeRun();
		fbm.supressSysOut();
		fbm.setNumOfPreRun(100);

		int num_of_arr = 100 * 1000;
		fbm.runBenchmark(new WriteIntWithSIB(num_of_arr), new WriteIntWithDOS(
				num_of_arr));
	}

}

class WriteIntWithDOS implements FemtoBenchmark.Testable {
	ByteArrayOutputStream bos;
	DataOutputStream dos;
	int rands[];
	int rands_size;
	PrepareIntArray pya;

	public WriteIntWithDOS(int rands_size) {
		pya = new PrepareIntArray(0);
		this.rands_size = rands_size;
	}

	public void setUp() {
		bos = new ByteArrayOutputStream();
		dos = new DataOutputStream(bos);
		rands = pya.getNextIntArray(rands_size);
	}

	public void run() throws IOException {
		for (int i = 0; i < rands.length; i++) {
			dos.writeInt(rands[i]);
		}
	}

	public void tearDown() throws IOException {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
				bos.toByteArray()));
		dis.readInt();
		dis.readInt();
		dis.readInt();
		System.out.println(dis.readInt());
	}
}

class WriteIntWithSIB implements FemtoBenchmark.Testable {
	ObjectOutputStream oos;
	ByteArrayOutputStream bos;
	SerializableIntBuffer sib;
	int rands[];
	int rands_size;
	PrepareIntArray pya;

	public WriteIntWithSIB(int rands_size) {
		pya = new PrepareIntArray(0);
		this.rands_size = rands_size;
	}

	public void setUp() throws IOException {
		bos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(bos);
		rands = pya.getNextIntArray(rands_size);
	}

	public void run() throws IOException {
		sib = new SerializableIntBuffer(rands_size);
		for (int i = 0; i < rands.length; i++) {
			sib.write(rands[i]);
		}
		oos.writeObject(sib);
	}

	public void tearDown() throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				bos.toByteArray()));
		SerializableIntBuffer sib = (SerializableIntBuffer) ois.readObject();
		System.out.println(sib.getInnerBuf()[3]);
	}
}

class PrepareIntArray {
	Random r;

	public PrepareIntArray() {
		r = new Random(0);
	}

	public PrepareIntArray(long seed) {
		r = new Random(seed);
	}

	public int[] getNextIntArray(int size) {
		int[] ret = new int[size];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = r.nextInt();
		}
		return ret;
	}

	public int[] getIncrIntArray(int size) {
		int[] ret = new int[size];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = i;
		}
		return ret;
	}
}