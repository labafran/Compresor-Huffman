//TODO LIST
//1. Revisar xq no admite otros formatos de archivo
//2. Aplicar Console (huffman/util/Console.java) en vez de estar usando el console por defecto

package huffman.def;


import java.io.Console;
import java.util.List;

import imple.CompresorImple;
import imple.DescompresorImple;


public class HuffmanCompressorTest {

    public static void main(String[] args) {

        Console console = System.console();

        console.printf("Seleccione una opción:\n0. Cerrar Programa\n1. Comprimir archivo\n2. Descomprimir archivo\n");
        int opcion = Integer.parseInt(console.readLine());
        while (opcion != 0) {
            String filename = console.readLine("Introduzca el nombre del archivo (con extensión, por ejemplo test.txt): ");

            switch (opcion) {
                case 1 -> {
                    System.out.println("Compresion del archivo: " + filename);
                    comprimirArchivo(filename);
                }

                case 2 -> {
                    System.out.println("Descompresion del archivo: " + filename);
                    descomprimirArchivo(filename);
                }
                default -> System.out.println("Opción no válida. Por favor ingrese una opción válida.");
            }
            System.out.println("Seleccione una opción:");
            System.out.println("0. Cerrar Programa");
            System.out.println("1. Comprimir archivo");
            System.out.println("2. Descomprimir archivo");
            opcion = Integer.parseInt(console.readLine());
        }

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
        DescompresorImple descompresor = new DescompresorImple();
        HuffmanInfo arbol = new HuffmanInfo();
        long bytesLeidos = descompresor.recomponerArbol(filename, arbol);
        descompresor.descomprimirArchivo(arbol, bytesLeidos, filename);
    }
}