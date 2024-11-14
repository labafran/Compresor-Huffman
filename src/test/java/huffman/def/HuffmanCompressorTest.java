package huffman.def;


import java.io.File;
import java.util.List;
import java.util.Scanner;

import imple.CompresorImple;

public class HuffmanCompressorTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Seleccione una opción:");
        System.out.println("0. Cerrar Programa");
        System.out.println("1. Comprimir archivo");
        System.out.println("2. Descomprimir archivo");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer
        while (opcion != 0) {
            System.out.print("Ingrese la ruta y el nombre del archivo (ejemplo: archivo.txt): ");
            String filename = scanner.nextLine();

            File file = new File(filename);

            switch (opcion) {
                case 1:
                    if (file.exists()) {
                        System.out.println("Compresion del archivo: " + filename);
                        comprimirArchivo(filename);
                    } else {
                        System.out.println("El archivo no existe.");
                    }
                    break;

                /*case 2:
                    if (file.exists()) {
                        System.out.println("Descompresion del archivo: " + filename);
                        descomprimirArchivo(filename);
                    } else {
                        System.out.println("El archivo no existe.");
                    }

                    break;*/
                default:
                    System.out.println("Opción no válida. Por favor ingrese una opción válida.");

                    break;
            }
            System.out.println("Seleccione una opción:");
            System.out.println("0. Cerrar Programa");
            System.out.println("1. Comprimir archivo");
            System.out.println("2. Descomprimir archivo");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer
        }
        scanner.close();

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

/*     public static void descomprimirArchivo(String filename) {
        DescompImple descompresor = new DescompImple();
        long bytesLeidos = descompresor.recomponerArbol(filename, arbol);
        descompresor.descomprimirArchivo(arbol, bytesLeidos, filename);
    }*/
}