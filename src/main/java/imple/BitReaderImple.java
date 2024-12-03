package imple;

import java.io.IOException;
import java.io.InputStream;

import huffman.def.BitReader;

public class BitReaderImple implements BitReader
{
	private InputStream archivo;
   	private int buffer;
  	private int bitPos = 8;

	@Override
	public void using(InputStream is)
	{
    	archivo = is;
	}

	@Override
	public int readBit()
	{ 
		
		if (bitPos == 8) {
        	try {
        		buffer = archivo.read();
        	} catch (IOException e) {
            	e.printStackTrace();
        	}
        	if (buffer == -1) {
            	return -1;
			}
        	bitPos = 0;
      	}
     
    	int bit = (buffer >> (7 - bitPos)) & 1;
    	bitPos++;
	  
    	return bit;
	
	}
	
	@Override
	public void flush()
	{
		if(bitPos%8!=0){
			buffer=0;
			bitPos=8;
		}
	}
}
