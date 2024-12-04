package imple;

import java.io.IOException;
import java.io.OutputStream;

import huffman.def.BitWriter;

public class BitWriterImple implements BitWriter 
{
	private OutputStream archivo;
    public int bitCount; 
    public byte a = 0;
    
    @Override
    public void using(OutputStream os)
    {
        archivo = os;
    }

    @Override
    public void writeBit(int bit) 
    {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("El bit debe ser 0 o 1.");
        }
        a = (byte) ((a << 1) | bit); 
        bitCount++;
        if (bitCount == 8) {
            try {
                archivo.write(this.a);
                a = 0;
                bitCount = 0;
            } catch (IOException e) {
                e.printStackTrace();
           }
        }
    }
        
    @Override
    public void flush() 
    {
        if (bitCount > 0) {
            a = (byte) (a << (8 - bitCount));
        try {
            archivo.write(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
      }
      a=0;
      bitCount=0;
    }
}
