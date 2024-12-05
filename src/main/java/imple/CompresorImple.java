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
        this.bitWriter = new BitWriterImple(); // Inicializa bitWriter
    }

    // Recorre filename y retorna un HuffmanTable[256] contando cuántas veces aparece cada byte
    @Override
    public HuffmanTable[] contarOcurrencias(String filename) {
        HuffmanTable[] huffmantable = new HuffmanTable[256];

        for (int i = 0; i < 256; i++) {
            huffmantable[i] = new HuffmanTable(); // Inicializo el array
            huffmantable[i].setCod(Character.toString((char) i)); // Ingreso el char como un string
            huffmantable[i].setN(0); //Setea la ocurrencia en 0
        }
        try (FileInputStream archivo = new FileInputStream(filename)) { // Abro el archivo
            int buffer;
            buffer = archivo.read(); // Leo el primer byte
            while (buffer > -1) { //Mientras no sea el último...
                huffmantable[buffer].increment(); // Incremento las ocurrencias en 1
                buffer = archivo.read(); // Vuelvo a leer
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return huffmantable; // Retorno la tabla de 256 carácteres con sus respectivas ocurrencias
    }//esta bien y chequado

    // Retorna una lista ordenada donde cada nodo representa a cada byte del archivo
    @Override
    public List<HuffmanInfo> crearListaEnlazada(HuffmanTable arr[]) {
        List<HuffmanInfo> listahuffman = new ArrayList<>(); // Inicializo la lista
        for (int i = 0; i < 256; i++) { // Recorro todo el array
            if (arr[i].getN() != 0) { // Si tiene una ocurrencia...
                HuffmanInfo info = new HuffmanInfo();
                String cod = arr[i].getCod(); // Seteo el código
                info.setC(cod.charAt(0)); // Seteo el carácter
                info.setN(arr[i].getN()); // Seteo la ocurrencia
                listahuffman.add(info); // Añado a la lista
            }
        }
        Collections.sort(listahuffman, Comparator.comparingInt(HuffmanInfo::getN)); // Ordeno la lista ponderada por la ocurrencias

        return listahuffman; // Retorno la lista
    } // Funca bien y chequado

    // Convierte la lista en el árbol Huffman
    @Override
    public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista) {
        if (lista == null || lista.isEmpty()) {
            System.out.println("La lista está vacía.");
            return null; // Si la lista está vacía, retorno null
        }

        while (lista.size() > 1) { // Mientras haya más de un elemento en la lista, continúo generando el árbol

            HuffmanInfo padre = new HuffmanInfo();
            HuffmanInfo der = lista.remove(0); // Elimino de la lista y añado a la rama derecha en el arbol arbol
            padre.setRight(der);
            HuffmanInfo izq = lista.remove(0); // Elimino de la lista y añado a la rama izquierda en el arbol arbol
            padre.setLeft(izq);

            padre.setN(der.getN() + izq.getN()); // Sumo las ocurrencias de los hijos y se la seteo al padre
            padre.setC(300); // Le doy un caracter más allá de 256 para distinguir de las hojas
            lista.add(padre);  // Añado el padre a la lista
            Collections.sort(lista, Comparator.comparingInt(HuffmanInfo::getN)); // Vuelvo a ordenar la lista para mantenerla ordenada

        }

        return lista.get(0); // Devuelvo el árbol armado
    }//esta check

    // Recorre el árbol Huffman y completa los códigos en el array
    @Override
    public void generarCodigosHuffman(HuffmanInfo root, HuffmanTable arr[]) {

        HuffmanTree arbolito = new HuffmanTree(root);
        StringBuffer cod = new StringBuffer();

        // Llamada a next para cada hoja, que genera el código Huffman y lo almacena en `cod`
        for (HuffmanInfo x = arbolito.next(cod); x != null; x = arbolito.next(cod)) {
            int byteValue = x.getC(); // Obtengo el código de Huffman desde el árbol
            arr[byteValue].setCod(cod.toString()); // Paso el código de Huffman al array
        }
    }

    // Escribe el encabezado en el archivo filename+".huf", y retorna cuántos bytes ocupa el encabezado
    @Override
    public long escribirEncabezado(String filename, HuffmanTable arr[]) {
        long headerSize = 0; // Almacena el largo del encabezado
        int largoArchivo = 0; // Almacena el largo del archivo = Suma de ocurrencias de todos los carácteres
        int cantidadHojas = 0; // Almacena la cantidad de hojas en el arbol
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getN() > 0) {
                cantidadHojas++; // Cuento la cantidad de hojas
            }
        }
        //Creo el archivo .huf
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename + ".huf"))) {
            bitWriter.using(out);
            out.writeByte(cantidadHojas); // Escribo la cantidad de hojas
            headerSize++;

            for (int i = 0; i < arr.length; i++) { // Recorro el array

                if (arr[i].getN() > 0) { // Si está en el array, lo agrego en el archivo, sino lo ignoro

                    out.writeByte((byte) i);  // Escribo el valor ASCII del caracter
                    headerSize++;

                    out.writeByte((byte) arr[i].getCod().length()); //Escribo el largo del código
                    headerSize++;

                    //Escribo los bits del codigo...
                    for (int j = 0; j < arr[i].getCod().length(); j++) { // Escribe bit a bit hasta llegar al final del código de Huffman
                        if (arr[i].getCod().charAt(j) == '1') {
                            bitWriter.writeBit(1);
                        } else {
                            bitWriter.writeBit(0);
                        }
                    }
                    headerSize += Math.ceil((float) arr[i].getCod().length() / 8);
                    if (arr[i].getCod().length() % 8 != 0) { //Si el largo del código no es multiplo de 8, hago flush
                        bitWriter.flush();
                    }
                    

                    largoArchivo += arr[i].getN(); // Sumo todas las ocurrencias para calcular el largo del archivo
                }
            }
            out.writeInt(largoArchivo); // Escribo el largo del archivo

        } catch (IOException e) {
            e.printStackTrace();
        }

        return headerSize; // Retorno el largo del encabezado
    }

// Recorre el archivo filename por cada byte escribe su código en filename+".huf"
    @Override
    public void escribirContenido(String filename, HuffmanTable arr[]) {
        try (FileInputStream in = new FileInputStream(filename); DataOutputStream out = new DataOutputStream(new FileOutputStream(filename + ".huf", true))) {

            bitWriter.using(out); // Configura el BitWriter para escribir en el archivo comprimido
            int byteValue = in.read();

            while (byteValue != -1) { // Leer cada byte del archivo de entrada

                // Buscar el código de Huffman correspondiente en la tabla
                String huffmanCode = arr[byteValue].getCod();
                // Escribir el código en el archivo de salida
                for (int j = 0; j < huffmanCode.length(); j++) {
                    char bit = huffmanCode.charAt(j); // Obtener el bit en la posición j
                    bitWriter.writeBit(Character.getNumericValue(bit)); // Escribir el bit

                }
                byteValue = in.read();
            }

            bitWriter.flush(); // En caso de no haber completado un byte (no es divisible por 8), hago flush

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}