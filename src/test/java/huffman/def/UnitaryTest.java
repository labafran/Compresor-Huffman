package huffman.def;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import imple.CompresorImple;
import imple.DescompresorImple;


public class UnitaryTest {

    private static final String ENTRADA_FILENAME = "comocome.txt";
    private static final String COMPRIMIDO_FILENAME = "comocome.txt.huf";
    private static final String COPIA_FILENAME = "comocome_copia.txt";

    @BeforeEach
    public void setUp() throws Exception {
        // Crear un archivo de prueba
        try (FileOutputStream fos = new FileOutputStream(ENTRADA_FILENAME)) {
            fos.write("COMO COME COCORITO COME COMO COSMONAUTA".getBytes()); // Contenido de prueba
        }
    }

    @AfterEach
    public void tearDown() {
        // Eliminar archivos de prueba después de cada prueba
        new File(ENTRADA_FILENAME).delete();
        new File(COMPRIMIDO_FILENAME).delete();
        new File(COPIA_FILENAME).delete();
    }

    @Test
    public void testCompresor() {
        // Comprimir el archivo
        comprimirArchivo(ENTRADA_FILENAME);

        // Verificar que el archivo comprimido se haya creado
        File compressedFile = new File(COMPRIMIDO_FILENAME);
        assertTrue(compressedFile.exists(), "El archivo comprimido no se creó.");
    }

    @Test
    public void testDescompresor() {
        // Comprimir primero
        comprimirArchivo(ENTRADA_FILENAME);

        // Hacer una copia de seguridad del archivo original
        hacerCopiaDeSeguridad(ENTRADA_FILENAME, COPIA_FILENAME);

        // Descomprimir el archivo, sobrescribiendo el archivo original
        descomprimirArchivo(ENTRADA_FILENAME);

        // Verificar que el contenido del archivo descomprimido sea igual al original
        try (FileInputStream inCopia = new FileInputStream(COPIA_FILENAME);
             FileInputStream inOrig = new FileInputStream(ENTRADA_FILENAME)) {

            int datoOriginal;
            int datoDescomprimido;
            while ( (datoDescomprimido = inOrig.read()) != -1 && (datoOriginal = inCopia.read()) != -1) {
                assertEquals(datoOriginal, datoDescomprimido, "Los archivos no coinciden en el contenido.");
            }
            assertEquals(-1, datoDescomprimido, "El archivo descomprimido tiene más contenido que el original.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void comprimirArchivo(String filename) {
        CompresorImple compresor = new CompresorImple();
        HuffmanTable ocurrencias[] = compresor.contarOcurrencias(filename);
        List<HuffmanInfo> listahuffman = compresor.crearListaEnlazada(ocurrencias);
        HuffmanInfo arbol = compresor.convertirListaEnArbol(listahuffman);
        compresor.generarCodigosHuffman(arbol, ocurrencias);
        compresor.escribirEncabezado(filename, ocurrencias);
        compresor.escribirContenido(filename, ocurrencias);
    }

    public static void descomprimirArchivo(String outputFilename) {
        DescompresorImple descompresor = new DescompresorImple();
        HuffmanInfo arbol = new HuffmanInfo();
        long bytesLeidos = descompresor.recomponerArbol(outputFilename, arbol);
        descompresor.descomprimirArchivo(arbol, bytesLeidos, outputFilename);
    }

    private void hacerCopiaDeSeguridad(String originalFilename, String backupFilename) {
        try (FileInputStream in = new FileInputStream(originalFilename);
             FileOutputStream out = new FileOutputStream(backupFilename)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}