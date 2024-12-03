package imple;

import huffman.def.BitReader;
import huffman.def.BitWriter;
import huffman.def.Compresor;
import huffman.def.Descompresor;

public class Factory
{
	public static BitWriter getBitWriter()
	{
		return new BitWriterImple();
	}

	public static BitReader getBitReader()
	{
		return new BitReaderImple();
	}

	public static Compresor getCompresor()
	{
		return null;
	}
	
	public static Descompresor getDescompresor()
	{
		return null;
	}
}
