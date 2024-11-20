package imple;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import huffman.def.Descompresor;
import huffman.def.HuffmanInfo;

public class DescompImple implements Descompresor {

    private BitReaderImple bitReader;

    public DescompImple() {
        this.bitReader = new BitReaderImple(); // Inicializa bitReader
    }
// ** en todos los casos filename es el nombre del archivo original **

    // Restaura el árbol leyendo el encabezado desde el archivo filename+".huf" 
    @Override
    public long recomponerArbol(String filename, HuffmanInfo arbol) {
        long bytesLeidos = 0;
        arbol.setC(300); // Le doy un valor de NO hoja a la raíz 
        try (DataInputStream in = new DataInputStream(new FileInputStream(filename + ".huf"))) {
            bitReader.using(in);
            int cantHojas = in.read(); //Leo la cantidad de hojas
            bytesLeidos++;
            for(int i=0; i<cantHojas; i++) { // Por cada hoja...
                char c = (char) in.read(); // Leo el caracter
                bytesLeidos++;
                int  cantBits = in.read(); // Leo la cantidad de bits del código Huffman
                bytesLeidos++;
                int bit;
                StringBuilder codigo = new StringBuilder();
                for(int j=0; j<cantBits; j++){
                    bit = bitReader.readBit(); // Leo cada bit del código
                    codigo.append(bit); // Cuando completo un byte lo agrego al string
                }
                bytesLeidos += Math.ceil((float)cantBits/8); // Redondeo hacia arriba la cantidad de bytes, si es 1.1 bytes sube a 2 bytes

                if (cantBits%8!=0) {
                    bitReader.flush();
                }
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
                aux.setC((int) c); // Al llegar a la hoja, le asigno el caracter
            }                    
        } catch (IOException e){
            System.out.println("Error al leer el archivo");
            e.printStackTrace();
        }
        return bytesLeidos;
    }

    // Recorre bit por bit el archivo filename+".huf", decodifica y escribe 
    // cada byte decodificado en el arhivo filename
    @Override
    public void descomprimirArchivo(HuffmanInfo root, long n, String filename) {
        try (DataInputStream in = new DataInputStream(new FileInputStream(filename + ".huf")); PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            bitReader.using(in);
            for(int i=0;i<n;i++){
                in.readByte(); // Ignoro el encabezado
            }
            int largoArchivo = in.readInt();
            HuffmanInfo aux = root;
            int cont=0;
            int bit=bitReader.readBit();
            cont++;
            while(bit!=-1 && largoArchivo>=0){

                if(aux.getC()==300){
                    if(bit==1){
                        aux = aux.getRight();
                    }else{
                        aux = aux.getLeft();
                    }
                }else{
                    out.write((char) aux.getC());
                    largoArchivo--;
                    aux = root;
                }

                bit = bitReader.readBit();
                cont++;
            }


        } catch (IOException e){
            System.out.println("Error al leer el archivo");
            e.printStackTrace();
        }
    }
}