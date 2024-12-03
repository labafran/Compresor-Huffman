package huffman.def;

import java.io.OutputStream;

public interface BitWriter
{
	public void using(OutputStream os);
	public void writeBit(int bit);
	public void flush();
}
