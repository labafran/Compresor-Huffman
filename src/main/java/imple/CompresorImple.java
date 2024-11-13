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

public class CompresorImple implements Compresor{
	
	// Recorre filename y retorna un HuffmanTable[256] contando cuántas veces aparece cada byte
	@Override
	public HuffmanTable[] contarOcurrencias(String filename){
		HuffmanTable [] huffmantable = new HuffmanTable[256];
		
		for(int i=0;i < 256; i++){
			huffmantable [i]= new HuffmanTable();
			huffmantable[i].setCod(Character.toString((char) i));
			huffmantable[i].setN(0);//pone la ocurrencia en 0
		}
		try(FileInputStream archivo = new FileInputStream(filename)){
			int buffer;
			buffer=archivo.read();
			while (buffer>-1) {
				huffmantable[buffer].increment();
				buffer=archivo.read();
			}

		}catch (IOException e) {
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
            String cod = arr[i].getCod().trim(); // Elimina espacios en blanco

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
	public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista){
		if(lista==null || lista.isEmpty()){
			return null;
		}

		while(lista.size()>1){
			HuffmanInfo der = lista.remove(0);
			HuffmanInfo izq = lista.remove(0);
			HuffmanInfo padre = new HuffmanInfo();
			padre.setRight(der);
			padre.setLeft(izq);
			padre.setN(der.getN()+izq.getN());
			padre.setC(300); // 300 = raiz
			lista.add(padre);
		}
		
		return lista.get(0);
	}
	
	// Recorre el árbol Huffman y completa los códigos en el array
	@Override
	public void generarCodigosHuffman(HuffmanInfo root,HuffmanTable arr[]){
        
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
	public long escribirEncabezado(String filename,HuffmanTable arr[]){
		long headerSize = 0;

		try (DataOutputStream out = new DataOutputStream(new FileOutputStream(filename + ".huf"))) {
			// Recorre el array 
			for (int i = 0; i < arr.length; i++) {
				HuffmanTable entry = arr[i];
				
				if (entry != null && entry.getCod() != null) {
					// Escribir el número (n) asociado
					out.writeInt(entry.getN());
					
					// Escribir la longitud del código
					String huffmanCode = entry.getCod();
					out.writeInt(huffmanCode.length());
					
					// Escribir el código de Huffman en sí
					out.writeBytes(huffmanCode);
					
					// Actualizar el tamaño del encabezado (4 bytes para n + 4 bytes para la longitud del código + tamaño del código en bytes)
					headerSize += 4 + 4 + huffmanCode.length();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		return headerSize;
	}

	
	// Recorre el archivo filename por cada byte escribe su código en filename+".huf"
	@Override
	public void escribirContenido(String filename,HuffmanTable arr[]){
		try (FileInputStream in = new FileInputStream(filename);
		DataOutputStream out = new DataOutputStream(new FileOutputStream(filename + ".huf", true))) {
		
	   int byteValue;
	   // Leer cada byte del archivo de entrada
	   while ((byteValue = in.read()) != -1) {
		   // Buscar el código de Huffman correspondiente en la tabla
		   String huffmanCode = arr[byteValue].getCod();
		   
		   // Escribir el código en el archivo de salida
		   out.writeBytes(huffmanCode);
	   }
   } catch (IOException e) {
	   e.printStackTrace();
   }
	}	
	
} 