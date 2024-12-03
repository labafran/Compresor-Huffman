package huffman.util;

import huffman.def.HuffmanInfo;

public class HuffmanTreeDemo
{
	public static void main(String[] args)
	{
		// crea el árbol "COMO COME COCORITO..."
		HuffmanInfo root = demoCocorito();
		
		// instancio 
		HuffmanTree ht = new HuffmanTree(root);
		
		// tendrá el código Huffman de cada hoja
		StringBuffer cod = new StringBuffer();
		
		HuffmanInfo hoja = ht.next(cod);
		while( hoja!=null )
		{
			System.out.println(hoja+": ["+cod+"]");
			hoja = ht.next(cod);
		}
	}
	
	public static HuffmanInfo demoCocorito()
	{
		// nivel 5 (ultimo nivel)
		HuffmanInfo nS=node('S',1,null,null);
		HuffmanInfo nR=node('R',1,null,null);
		HuffmanInfo nN=node('N',1,null,null);
		HuffmanInfo nI=node('I',1,null,null);

		// nivel 4
		HuffmanInfo a2=node(256+2,2,nS,nR);
		HuffmanInfo a1=node(256+1,2,nN,nI);
		HuffmanInfo nT=node('T',2,null,null);
		HuffmanInfo nE=node('E',2,null,null);
		HuffmanInfo nA=node('A',2,null,null);
		HuffmanInfo nU=node('U',1,null,null);

		// nivel 3
		HuffmanInfo nC=node('C',7,null,null);
		HuffmanInfo nM=node('M',5,null,null);
		HuffmanInfo nESP=node(' ',5,null,null);
		HuffmanInfo a5=node(256+5,4,a2,a1);
		HuffmanInfo a4=node(256+4,4,nT,nE);
		HuffmanInfo a3=node(256+3,3,nA,nU);

		// nivel 2
		HuffmanInfo a8=node(256+8,12,nC,nM);
		HuffmanInfo nO=node('O',11,null,null);
		HuffmanInfo a7=node(256+7,9,nESP,a5);
		HuffmanInfo a6=node(256+6,7,a4,a3);

		// nivel 1
		HuffmanInfo a10=node(256+10,23,a8,nO);
		HuffmanInfo a9=node(256+9,16,a7,a6);

		// nivel 0 (raiz)
		HuffmanInfo a11=node(256+11,39,a10,a9);

		return a11;
	}
	
	public static HuffmanInfo demoBeeGees()
	{
		// nivel 3
		HuffmanInfo nC=node('C',2,null,null);
		HuffmanInfo nD=node('D',1,null,null);
		HuffmanInfo a1=node(256,3,nC,nD);

		// nivel 2
		HuffmanInfo nB=node('B',3,null,null);
		HuffmanInfo a2=node(257,6,a1,nB);
		
		// nivel 1
		HuffmanInfo nA=node('A',4,null,null);
		HuffmanInfo a3=node(258,10,a2,nA);
		
		return a3;
	}

	private static HuffmanInfo node(int c, int n, HuffmanInfo izq, HuffmanInfo der)
	{
		HuffmanInfo node=new HuffmanInfo();
		node.setC(c);
		node.setN(n);
		node.setLeft(izq);
		node.setRight(der);
		return node;
	}
	
}
