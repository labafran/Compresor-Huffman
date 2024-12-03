package huffman.def;

import java.io.InputStream;

public interface BitReader
{
	public void using(InputStream is);
	public int readBit();
	public void flush();
}
