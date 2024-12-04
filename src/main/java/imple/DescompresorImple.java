package imple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import huffman.def.Descompresor;
import huffman.def.HuffmanInfo;

public class DescompresorImple implements Descompresor {
    private BitReaderImple bitReader;
   
    public DescompresorImple() {
        this.bitReader = new BitReaderImple(); // Inicializa bitReader
    }
    
    // ** en todos los casos filename es el nombre del archivo original **
    // Restaura el arbol leyendo el encabezado desde el archivo filename+".huf"
    @Override
    public long recomponerArbol(String filename,HuffmanInfo arbol){
        long bytesLeidos = 0;
        arbol.setC(300); // Le doy un valor de NO hoja a la raíz 
        try (DataInputStream in = new DataInputStream(new FileInputStream(filename + ".huf"))) {
            bitReader.using(in);
            int cantHojas = in.readByte(); //Leo la cantidad de hojas
            bytesLeidos++;
            if(cantHojas==0){
                cantHojas=256;
            }
            for(int i=0; i<cantHojas; i++) { // Por cada hoja...
                int c = in.readByte(); // Leo el caracter
                c = c & 0xff; // Lo hago "unsigned"
                bytesLeidos++;
                int cantBits = in.readByte(); // Leo la cantidad de bits del codigo Huffman
                bytesLeidos++;
                int bit;
                StringBuilder codigo = new StringBuilder();
                for(int j=0; j<cantBits; j++){
                    bit = bitReader.readBit(); // Leo cada bit del codigo
                    codigo.append(bit); // Cuando completo un byte lo agrego al string
                }
                bitReader.flush();
                bytesLeidos += Math.ceil((float)cantBits/8); // Redondeo hacia arriba la cantidad de bytes, si es 1.1 bytes sube a 2 bytes
                
                String camino = codigo.toString();
                HuffmanInfo aux = arbol; //Creo un aux para recorrer el arbol

                for(int j=0; j<camino.length(); j++){
                    if(camino.charAt(j)=='1'){ //Si es 1 reviso la rama derecha

                        if(aux.getRight()==null){ //Si no tiene rama derecha, la creo
                            aux.setRight(new HuffmanInfo());
                            aux.getRight().setC(300); // Le asigno el caracter 300 a lo que no sea hoja
                        }
                        aux = aux.getRight(); // Me muevo a la rama derecha
                            
                    }else if(camino.charAt(j)=='0'){ //Si es 0, reviso la rama izquierda
                        if(aux.getLeft()==null){ //Si no tiene rama izquierda, la creo
                            aux.setLeft(new HuffmanInfo());
                            aux.getLeft().setC(300); // Le asigno el caracter 300 a lo que no sea hoja

                        }
                        aux = aux.getLeft(); // Me muevo a la rama izquierda
                    }
                }
                aux.setC(c); // Al llegar a la hoja, le asigno el caracter
            }                    
            in.close();
        } catch (IOException e){
            System.out.println("Error al leer el archivo");
            e.printStackTrace();
        }
        
        return bytesLeidos;
    }

    @Override
	public void descomprimirArchivo(HuffmanInfo root,long n,String filename){
        try (DataInputStream in = new DataInputStream(new FileInputStream(filename+".huf")); DataOutputStream out = new DataOutputStream(new FileOutputStream(filename))){
            bitReader.using(in);
            for(int i=0;i<n;i++){
                in.readByte();
            }

            //Leido el cabezado, paso a leer la cantidad de caracteres del archivo
            int longArchivo = in.readInt();
            //Leo el contenido codificado hasta completar la longitud de archivo
            HuffmanInfo arbolAux = root;
            int bit = bitReader.readBit();
            while(longArchivo>0){
                
                if(arbolAux.getC()==300){
                    if(bit==1){
                        bit = bitReader.readBit();
                        arbolAux = arbolAux.getRight();
                    }else{
                        bit = bitReader.readBit();
                        arbolAux = arbolAux.getLeft();
                    }
                }else{
                    out.writeByte(arbolAux.getC()); //Escribo el caracter
                    longArchivo--; // Resto 1 a la longitud del archivo, puesto que ya encontramos 1 caracter
                    arbolAux = root; // Vuelvo a la raiz para iterar nuevamente
                }
            }
            in.close();
            out.close();

            
        } catch (IOException e) {
            System.out.println("Error al leer el archivo");
            e.printStackTrace();
        }
    }
}