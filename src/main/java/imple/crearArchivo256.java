package imple;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class crearArchivo256
{
public static void main(String[] args) {
        String filename = "byteFile.bin";

        // Escritura de todos los bytes del 0 al 255 en el archivo
        try (FileOutputStream fos = new FileOutputStream(filename);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            for (int i = 0; i <= 255; i++) {
                bos.write(i); // Escribe el byte en el archivo
            }
            bos.flush(); // Asegura que todos los datos se escriban en el archivo
            System.out.println("Bytes del 0 al 255 escritos en el archivo.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Lectura de todos los bytes desde el archivo
        try (FileInputStream fis = new FileInputStream(filename);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte byteLeido;
            byteLeido = (byte)fis.read();
            System.out.println("Bytes leídos del archivo:");
            for (int i = 0; i<256; i++) { // Lee byte por byte
                System.out.println("Byte leído: " + byteLeido);
                byteLeido = (byte)fis.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}