package huffman.def;

public class HuffmanInfo
{
	private int c;
	private int n;
	private HuffmanInfo left = null;
	private HuffmanInfo right = null;
	
	public HuffmanInfo(){};
	public HuffmanInfo(int c,int n)
	{
		setC(c);
		setN(n);
	}
	
	public int getC()
	{
		return c;
	}
	public void setC(int c)
	{
		this.c=c;
	}
	public int getN()
	{
		return n;
	}
	public void setN(int n)
	{
		this.n=n;
	}
	public HuffmanInfo getLeft()
	{
		return left;
	}
	public void setLeft(HuffmanInfo left)
	{
		this.left=left;
	}
	public HuffmanInfo getRight()
	{
		return right;
	}
	public void setRight(HuffmanInfo right)
	{
		this.right=right;
	}
	
	@Override
	public String toString()
	{
		return (char)c+" ("+n+")";
	}
}
