package huffman.def;

import java.util.List;

public interface Compresor
{
	public HuffmanTable[] contarOcurrencias(String filename);
	public List<HuffmanInfo> crearListaEnlazada(HuffmanTable arr[]);
	public HuffmanInfo convertirListaEnArbol(List<HuffmanInfo> lista);
	public void generarCodigosHuffman(HuffmanInfo root,HuffmanTable arr[]);
	public long escribirEncabezado(String filename,HuffmanTable arr[]);
	public void escribirContenido(String filename,HuffmanTable arr[]);	
}
