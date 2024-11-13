package imple;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import huffman.def.BitReader;
import huffman.def.Descompresor;
import huffman.def.HuffmanInfo;

public class DescompImple implements Descompresor {
// ** en todos los casos filename es el nombre del archivo original **

    // Restaura el árbol leyendo el encabezado desde el archivo filename+".huf" 
    @Override
    public long recomponerArbol(String filename, HuffmanInfo arbol) {
        long bytesLeidos = 0;
        try (DataInputStream in = new DataInputStream(new FileInputStream(filename + ".huf"))) {
            while (in.available() > 0) {
                // Leer el valor del carácter como un entero
                int n = in.readInt();
                bytesLeidos += 4;

                // Lee la longitud del código de Huffman (cantidad de bits)
				
                // Lee el código de Huffman
                int codeLength = in.readInt(); // Leer la longitud del código
                bytesLeidos += 4;
                byte[] codeBytes = new byte[codeLength];
				in.readFully(codeBytes);
                String huffmanCode = new String(codeBytes);
                bytesLeidos += codeLength;

                // Insertar el carácter en el árbol
                HuffmanInfo currentNode = arbol;
                for (char bit : huffmanCode.toCharArray()) {
                    if (bit == '0') {
                        // Ir a la izquierda o crear nodo izquierdo
                        if (currentNode.getLeft() == null) {
                            currentNode.setLeft(new HuffmanInfo());
                        }
                        currentNode = currentNode.getLeft();
                    } else if (bit == '1') {
                        // Ir a la derecha o crear nodo derecho
                        if (currentNode.getRight() == null) {
                            currentNode.setRight(new HuffmanInfo());
                        }
                        currentNode = currentNode.getRight();
                    }
                }
                // Asignar el carácter al nodo hoja final
                currentNode.setC(n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytesLeidos;
    }

    // Recorre bit por bit el archivo filename+".huf", decodifica y escribe 
    // cada byte decodificado en el arhivo filename
    @Override
    public void descomprimirArchivo(HuffmanInfo root, long n, String filename) {

        try (FileInputStream in = new FileInputStream(filename + ".huf"); FileOutputStream out = new FileOutputStream(filename)) {

            BitReader bitReader = new BitReaderImple();
            bitReader.using(in);  // Configura el BitReader para leer del archivo comprimido
            HuffmanInfo currentNode = root;
            long bytesWritten = 0;

            // Leer bit a bit hasta alcanzar el número esperado de bytes decodificados
            while (bytesWritten < n) {
                int bit = bitReader.readBit();

                // Verificar si se ha alcanzado el final del archivo
                if (bit == -1) {
                    break;
                }

                // Navegar por el árbol de Huffman usando el bit leído
                if (bit == 0) {
                    currentNode = currentNode.getLeft();
                } else if (bit == 1) {
                    currentNode = currentNode.getRight();
                }

                // Si llegamos a un nodo hoja, escribimos el carácter decodificado
                if (currentNode.getLeft() == null && currentNode.getRight() == null) {
                    out.write(currentNode.getC());  // Escribir el carácter en el archivo de salida
                    bytesWritten++;
                    currentNode = root;  // Reiniciar al nodo raíz para el siguiente símbolo
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
