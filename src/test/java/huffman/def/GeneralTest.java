package huffman.def;

import java.util.List;

import huffman.util.Console;
import imple.CompresorImple;
import imple.DescompresorImple;


public class GeneralTest {

    public static void main(String[] args) {

        Console console = Console.get();

        console.println("Seleccione una opción:\n0. Cerrar Programa\n1. Comprimir archivo\n2. Descomprimir archivo\n");
        int opcion = console.readlnInteger();
        while (opcion != 0) {

            switch (opcion) {
                case 1 -> {
                    String filename = console.println("Seleccione un archivo").fileExplorer();
                    console.println("Compresion del archivo: " + filename);
                    comprimirArchivo(filename);
                    console.println("Compresion exitosa! \n\n\n\n");
                }

                case 2 -> {
                    String filename = console.println("Seleccione un archivo").fileExplorer();
                    console.println("Descompresion del archivo: " + filename);
                    descomprimirArchivo(filename);
                    console.println("Descompresion exitosa! \n\n\n\n");
                }
                default -> console.println("Opción no válida. Por favor ingrese una opción válida.");
            }
            console.println("Seleccione una opción:\n0. Cerrar Programa\n1. Comprimir archivo\n2. Descomprimir archivo\n");
            opcion = console.readlnInteger();
        }
        console.closeAndExit();

    }

    //Implemento los métodos
    public static void comprimirArchivo(String filename) {
        CompresorImple compresor = new CompresorImple();
        HuffmanTable ocurrencias[] = compresor.contarOcurrencias(filename);
        List<HuffmanInfo> listahuffman = compresor.crearListaEnlazada(ocurrencias);
        HuffmanInfo arbol = compresor.convertirListaEnArbol(listahuffman);
        compresor.generarCodigosHuffman(arbol, ocurrencias);
        compresor.escribirEncabezado(filename, ocurrencias);
        compresor.escribirContenido(filename, ocurrencias);
        
    }

     public static void descomprimirArchivo(String filename) {
        filename = filename.substring(0, filename.lastIndexOf(".")); //Pasa el .huf como un archivo sin descomprimir para no tener que adaptar todo despues
        DescompresorImple descompresor = new DescompresorImple();
        HuffmanInfo arbol = new HuffmanInfo();
        long bytesLeidos = descompresor.recomponerArbol(filename, arbol);
        descompresor.descomprimirArchivo(arbol, bytesLeidos, filename);
        
    }
}