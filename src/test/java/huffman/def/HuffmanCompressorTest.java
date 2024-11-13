package huffman.def;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import imple.CompresorImple;
import imple.DescompImple;

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

                case 2:
                    if (file.exists()) {
                        System.out.println("Descompresion del archivo: " + filename);
                        descomprimirArchivo(filename);
                    } else {
                        System.out.println("El archivo no existe.");
                    }

                    break;
                default:
                    System.out.println("Opción no válida.");

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

        escribirArbol(filename,arbol);

        long headerSize = compresor.escribirEncabezado(filename, ocurrencias);
        compresor.escribirContenido(filename, ocurrencias);
    }

    public static void descomprimirArchivo(String filename) {
        DescompImple descompresor = new DescompImple();
        
        HuffmanInfo arbol = leerArbol(filename);

        long bytesLeidos = descompresor.recomponerArbol(filename, arbol);
        descompresor.descomprimirArchivo(arbol, bytesLeidos, filename);
    }

//Escribir Arbol (Solución Temporal)
    public static void escribirArbol(String filename, HuffmanInfo arbol) {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename + ".bin"))) {
            escribirNodo(dos, arbol);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void escribirNodo(DataOutputStream dos, HuffmanInfo nodo) throws IOException {
        if (nodo == null) {
            dos.writeBoolean(false); // Indica que el nodo es nulo
            return;
        }

        dos.writeBoolean(true); // Indica que el nodo no es nulo
        dos.writeChar(nodo.getC()); // Escribir el carácter
        dos.writeInt(nodo.getN()); // Escribir la frecuencia

        // Recursivamente escribir los nodos izquierdo y derecho
        escribirNodo(dos, nodo.getLeft());
        escribirNodo(dos, nodo.getRight());
    }

//Leer Arbol (Solución Temporal)
    public static HuffmanInfo leerArbol(String filename) {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filename + ".bin"))) {
            return leerNodo(dis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HuffmanInfo leerNodo(DataInputStream dis) throws IOException {
        if (!dis.readBoolean()) {
            return null; // Si es nulo, retornar null
        }

        char caracter = dis.readChar(); // Leer el carácter
        int frecuencia = dis.readInt(); // Leer la frecuencia

        HuffmanInfo nodo = new HuffmanInfo(caracter, frecuencia);
        nodo.setLeft(leerNodo(dis)); // Leer el nodo izquierdo
        nodo.setRight(leerNodo(dis)); // Leer el nodo derecho

        return nodo;
    }
}

