package huffman.util;

import java.util.Stack;

import huffman.def.HuffmanInfo;

public class HuffmanTree
{
	private Stack<HuffmanInfo> pila=null;
	private Stack<String> pilaCod=null;

	public HuffmanTree(HuffmanInfo root)
	{
		pila=new Stack<HuffmanInfo>();
		pilaCod=new Stack<String>();
		
		pila.push(root);
		pilaCod.push("");
	}
	
	public HuffmanInfo next(StringBuffer cod)
	{
		boolean hoja=false;
		HuffmanInfo p=null;
		HuffmanInfo aux=null;
		String zz=null;
		
		while( pila.size()>0 && !hoja )
		{
			p=pila.pop();
			zz=pilaCod.pop();
		
			if( p.getRight()!=null )
			{
				pila.push(p.getRight());
				aux=new HuffmanInfo();
				aux.setN(1);
				pilaCod.push(zz+"1");
			}

			if( p.getLeft()!=null )
			{
				pila.push(p.getLeft());
				aux=new HuffmanInfo();
				aux.setN(0);
				pilaCod.push(zz+"0");
			}
						
			if( p.getLeft()==null && p.getRight()==null )
			{
				hoja=true;
				cod.delete(0, cod.length());
				cod.append(zz);
			}
			else
			{
				p=null;
			}
		}
		
		return p;
	}	
}
