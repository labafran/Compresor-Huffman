package huffman.def;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import imple.Factory;

public class BitReaderTest
{
	private static final String FILENAME = "test.x";
	
	@AfterEach
	public void afterEach()
	{
		File f = new File(FILENAME);
		f.delete();
	}
	
	@Test
	public void test1() throws Exception
	{
		FileOutputStream fos = new FileOutputStream(FILENAME);
		fos.write('A');
		fos.write('B');
		fos.write('C');
		fos.close();
		
		FileInputStream fis = new FileInputStream(FILENAME);

		BitReader br = Factory.getBitReader();
		br.using(fis);

		// A
		assertEquals(0,br.readBit());
		assertEquals(1,br.readBit());
		assertEquals(0,br.readBit());
		assertEquals(0,br.readBit());
		assertEquals(0,br.readBit());
		assertEquals(0,br.readBit());
		assertEquals(0,br.readBit());
		assertEquals(1,br.readBit());
		
		// B
		int c = fis.read();
		assertEquals('B',c);
		
		// C
		assertEquals(0,br.readBit());
		assertEquals(1,br.readBit());
		assertEquals(0,br.readBit());
		assertEquals(0,br.readBit());
		assertEquals(0,br.readBit());
		assertEquals(0,br.readBit());
		assertEquals(1,br.readBit());
		assertEquals(1,br.readBit());
		
		// EOF
		assertEquals(-1,br.readBit());
		
		fis.close();
	}
	
	@Test
	public void test2() throws Exception
	{
		FileOutputStream fos = new FileOutputStream(FILENAME);
		fos.write('A');
		fos.close();
		
		BitReader br = Factory.getBitReader();
		FileInputStream fis = new FileInputStream(FILENAME);
		br.using(fis);

		int i;
		for(i=0; br.readBit()>=0; i++)
		{
		}
		
		assertEquals(8,i);
		
		fis.close();
		
	}
	
	@Test
	public void test3() throws Exception
	{
		FileOutputStream fos = new FileOutputStream(FILENAME);
		fos.write('A');
		fos.write('B');
		fos.write('C');
		fos.close();
		
		BitReader br = Factory.getBitReader();
		FileInputStream fis = new FileInputStream(FILENAME);
		br.using(fis);

		int i;
		for(i=0; br.readBit()>=0; i++);
		
		assertEquals(24,i);
		
		fis.close();
		
	}

}
