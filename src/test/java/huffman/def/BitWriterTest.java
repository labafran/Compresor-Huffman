package huffman.def;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import imple.Factory;

public class BitWriterTest
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
		BitWriter bw = Factory.getBitWriter();
		bw.using(fos);
		bw.writeBit(0);
		bw.writeBit(1);
		bw.writeBit(0);
		bw.writeBit(0);
		bw.writeBit(0);
		bw.writeBit(0);
		bw.writeBit(0);
		bw.writeBit(1);
		fos.close();
		
		FileInputStream fis = new FileInputStream(FILENAME);
		char c = (char)fis.read();
		fis.close();
		assertEquals('A',c);			
	}
	
	@Test
	public void test2() throws Exception
	{
		FileOutputStream fos = new FileOutputStream(FILENAME);
		BitWriter bw = Factory.getBitWriter();
		bw.using(fos);
		bw.writeBit(0);
		bw.writeBit(1);
		bw.writeBit(0);
		bw.writeBit(0);
		bw.writeBit(1);
		bw.flush();
		
		FileInputStream fis = new FileInputStream(FILENAME);
		char c = (char)fis.read();
		fis.close();
		assertEquals('H',c);
	}
}
