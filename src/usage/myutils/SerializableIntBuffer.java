package usage.myutils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SerializableIntBuffer implements Serializable {

	private static final long serialVersionUID = -2895353993786810342L;
	private transient int[] buf;
	private transient int pos;

	public SerializableIntBuffer(int allocate) {
		if (allocate < 0)
			throw new IllegalArgumentException();
		buf = new int[allocate];
	}

	public int[] getInnerBuf() {
		return buf;
	}

	public void write(int n) {
		buf[pos++] = n;
	}

	public void close() {
		buf = null;
	}

	public void rewind() {
		pos = 0;
	}

	public int read() {
		return buf[pos++];
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt(pos);
		for (int i = 0; i < pos; ++i) {
			out.writeInt(buf[i]);
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		pos = in.readInt();
		buf = new int[pos];
		for (int i = 0; i < pos; ++i) {
			buf[i] = in.readInt();
		}
	}

}
