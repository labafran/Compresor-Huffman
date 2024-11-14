package imple;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import huffman.def.Compresor;
import huffman.def.HuffmanInfo;
import huffman.def.HuffmanTable;
import huffman.util.HuffmanTree;

public class CompresorImple implements Compresor {
    private BitWriterImple bitWriter;

    public CompresorImple() {
        this.bitWriter = new BitWriterImple(); // Initialize bitWriter here
    }
    

    // Recorre filename y retorna un HuffmanTable[256] contando cuántas veces aparece cada byte
    @Override
    public HuffmanTable[] contarOcurrencias(String filename) {
        HuffmanTable[] huffmantable = new HuffmanTable[256];

        for (int i = 0; i < 256; i++) {
            huffmantable[i] = new HuffmanTable();
            huffmantable[i].setCod(Character.toString((char) i));
            huffmantable[i].setN(0);//pone la ocurrencia en 0
        }
        try (FileInputStream archivo = new FileInputStream(filename)) {
            int buffer;
            buffer = archivo.read();
            while (buffer > -1) {
                huffmantable[buffer].increment();
                buffer = archivo.read();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return huffmantable;
    }

    // Retorna una lista ordenada donde cada nodo representa a cada byte del archivo
    @Override
    public List<HuffmanInfo> crearListaEnlazada(HuffmanTable arr[]) {
        List<HuffmanInfo> listahuffman = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            if (arr[i].getN() != 0) { // Asegúrate de que haya ocurrencias
                HuffmanInfo info = new HuffmanInfo();
                String cod = arr[i].getCod();

                // Intenta analizar solo si getCod() no es una cadena vacía
                if (!cod.isEmpty()) {
                    try {
                        info.setC(cod.charAt(0));
                    } catch (NumberFormatException e) {
                        System.err.println("Error al convertir cod: " + cod);
                        info.setC(i); // Usa el índice 'i' como valor predeterminado si hay un error
                    }
                } else {
                    info.setC(i); // Usa el índice 'i' como valor predeterminado si getCod() está vacío
                }
                info.setN(arr[i].getN());
                listahuffman.add(info);
            }
        }
        Collections.sort(listahuffman, Comparator.comparingInt(HuffmanInfo::getN));

        return listahuffman;
    }

    // Convierte la lista en el árbol Huffman
    @Override
    public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista) {
        if (lista == null || lista.isEmpty()) {
            return null;
        }

        while (lista.size() > 1) {
            HuffmanInfo padre = new HuffmanInfo();
            HuffmanInfo der = lista.remove(0);
            padre.setRight(der);
            HuffmanInfo izq = lista.remove(0);
            padre.setLeft(izq);
            padre.setN(der.getN() + izq.getN());
            padre.setC(300); // 300 = raiz
            lista.add(padre);
            Collections.sort(lista, Comparator.comparingInt(HuffmanInfo::getN));
            System.out.println("Tamaño de la lista: " + lista.size());
            System.out.println("Valor de la raíz: " + padre.getN());
        }

        return lista.get(0);
    }

    // Recorre el árbol Huffman y completa los códigos en el array
    @Override
    public void generarCodigosHuffman(HuffmanInfo root, HuffmanTable arr[]) {

        HuffmanTree arbolito = new HuffmanTree(root);
        StringBuffer cod = new StringBuffer();

        // Llamada a next para cada hoja, que genera el código Huffman y lo almacena en `cod`
        for (HuffmanInfo x = arbolito.next(cod); x != null; x = arbolito.next(cod)) {
            int byteValue = x.getC();
            arr[byteValue].setCod(cod.toString());

            // Imprime el carácter y su código
            System.out.println("Caracter: " + (char) byteValue + " - Código: " + cod);
        }
    }

    // Escribe el encabezado en el archivo filename+".huf", y retorna cuántos bytes ocupa el encabezado
    @Override
    public long escribirEncabezado(String filename, HuffmanTable arr[]) {
        long headerSize = 0;

        //Busco la altura del arbol
        int alturaArbol = 0;
        for (int i = 0; i < arr.length; i++) {
            if (alturaArbol < arr[i].getCod().length()) { //Busco el código más largo, que representa la hoja más baja, haciendo que el largo equivalga al nivel de la hoja
                alturaArbol = arr[i].getCod().length(); //Si el código es más largo, actualizo la altura
            }
        }
        //Creo el archivo .huf
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename + ".huf"))) {
            //Escribo la altura del arbol
            out.writeInt(alturaArbol);
            //Recorro el array
            for (int i = 0; i < arr.length; i++) {
                if (arr[i].getN() > 0) { //Si está en el array, lo agrego en el archivo, sino lo ignoro
                    out.writeByte((byte) i);  //Escribo el valor ASCII del caracter
                    out.writeByte((byte) arr[i].getCod().length()); //Escribo el largo del código
                    //Escribo los bits del codigo...
                    byte[] bytes = new byte[(arr[i].getCod().length() + 7) / 8]; // Calcular la cantidad de bytes necesarios para el código
                    for (int j = 0; j < arr[i].getCod().length(); j++) { //Escribo bit a bit
                        if (arr[i].getCod().charAt(j) == '1' && arr[i].getN()>0) {
                            bytes[j / 8] |= (1 << (7 - (j % 8))); // Establecer el bit correspondiente
                        }
                    }
                    out.write(bytes);
                    if(arr[i].getCod().length()%8!=0){ //Si el largo del código no es multiplo de 8, hago flush
                        bitWriter.flush();
                    }
                }
            }
            int largoArchivo=0;
            for(int i=0;i<arr.length;i++){
                largoArchivo += arr[i].getN(); //Sumo todas las ocurrencias para calcular el largo del archivo
            }
            out.writeInt(largoArchivo); //Escribo el largo del archivo

        }catch(IOException e) {
            e.printStackTrace();
        }
        return headerSize ;
    }

// Recorre el archivo filename por cada byte escribe su código en filename+".huf"
@Override
public void escribirContenido(String filename, HuffmanTable arr[]) {
        try (FileInputStream in = new FileInputStream(filename); DataOutputStream out = new DataOutputStream(new FileOutputStream(filename + ".huf", true))) {

            bitWriter.using(out); // Configura el BitWriter para escribir en el archivo comprimido

            int byteValue;
            // Leer cada byte del archivo de entrada
            while ((byteValue = in.read()) != -1) {
                // Buscar el código de Huffman correspondiente en la tabla
                String huffmanCode = arr[byteValue].getCod();

                // Escribir el código en el archivo de salida
                for (char bit : huffmanCode.toCharArray()) {
                    bitWriter.writeBit(Character.getNumericValue(bit)); // Escribir cada bit
                }
            }

            // Asegúrate de que todos los bits se escriban antes de cerrar el flujo
            bitWriter.flush(); // Llama a flush para escribir cualquier bit restante
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}