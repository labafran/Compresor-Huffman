package huffman.def;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileInputStream;

import org.junit.jupiter.api.Test;

import imple.Factory;

public class CompresorTest
{
	@Test
	public void testContarOcurrencias()
	{
		try(FileInputStream fis = new FileInputStream("beegees.txt"))
		{
			Compresor c = Factory.getCompresor();
			HuffmanTable arr[] = c.contarOcurrencias("beegees.txt");
			
			assertEquals(4,arr['A'].getN());
			assertEquals(3,arr['B'].getN());
			assertEquals(2,arr['C'].getN());
			assertEquals(1,arr['D'].getN());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
