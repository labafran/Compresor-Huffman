package huffman.def;

public interface Descompresor
{
	public long recomponerArbol(String filename,HuffmanInfo arbol);
	public void descomprimirArchivo(HuffmanInfo root,long n,String filename);
}
