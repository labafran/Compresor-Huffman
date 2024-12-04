package huffman.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class Console
{
	private static final TriFunction<Character,Integer,String,Character> STRING= (c,kc,s)->c;
	private static final TriFunction<Character,Integer,String,Character> HEX = (c,kc,s)->isHexDigit(c)||_validKeyCode(kc)?c:null;
	private static final TriFunction<Character,Integer,String,Character> CHAR = (c,kc,s)->s.length()<3||_validKeyCode(kc)?c:null;
	private static final TriFunction<Character,Integer,String,Character> INTEGER = (c,kc,s)->Character.isDigit(c)||_validKeyCode(kc)?c:null;
	private static final TriFunction<Character,Integer,String,Character> DOUBLE = (c,kc,s)->s.isEmpty()&&c=='-' || Character.isDigit(c) || c=='.' && s.indexOf('.')<0||_validKeyCode(kc)?c:null;
	private static final TriFunction<Character,Integer,String,Character> BOOLEAN = (c,kc,s)->parseBoolean(s)!=null||_validKeyCode(kc)?c:null;
	private static final TriFunction<Character,Integer,String,Character> UPPERCASE = (c,kc,s)->Character.toUpperCase(c);
	private static final TriFunction<Character,Integer,String,Character> LOWERCASE = (c,kc,s)->Character.toLowerCase(c);
	private static final TriFunction<Character,Integer,String,Character> FLAG = (c,kc,s)->c=='1'||c=='0'&&s.length()<2||_validKeyCode(kc)?c:null;
	private static final TriFunction<Character,Integer,String,Character> AÑZ = (c,kc,s)->c>='A' && c<='Z' || c=='Ñ'||_validKeyCode(kc)?c:null;
	private static final TriFunction<Character,Integer,String,Character> AZ = (c,kc,s)->c>='A' && c<='Z'||_validKeyCode(kc)?c:null;
	
	private static Console console;
	private JFrame frame;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private boolean reading = false;
	private int inputPosition;
	private String inputText;
	private CountDownLatch latch;
	private TriFunction<Character,Integer,String,Character> currentMask = STRING;
	private int currCaretPosition=0;
	private boolean closeable = false;
	
	private int anyKey;
	public int pressAnyKey()
	{
		return pressAnyKey(()->{},-1);
	}
	
	public int pressAnyKey(Runnable r)
	{
		return pressAnyKey(r,-1);		
	}
	
	public int pressAnyKey(int k)
	{
		return pressAnyKey(()->{},k);		
	}

	public int pressAnyKey(Runnable r,int k)
	{
		try
		{
			textArea.addKeyListener(new EscuchaAnyKey(r,k));
			reading=true;
			latch = new CountDownLatch(1);
	        latch.await();

			return anyKey;			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	// singleton
	public static Console get()
	{
		return get(false);
	}
	
	// singleton
	public static Console get(boolean closeable)
	{
		if( console==null )
		{
			setWindowsAndFeel();
			console = new Console(closeable);
		}
		
		return console;
	}
	
	// constructor
	private Console(boolean closeable)
	{
		frame = new JFrame();
		textArea = new JTextArea();
		scrollPane = new JScrollPane(textArea);

		textArea.addKeyListener(new EscuchaKey());
		textArea.addMouseListener(new EscuchaMouse());
		
		_applyStyle();
		
		frame.getContentPane().add(scrollPane,BorderLayout.CENTER);

		setProportionalSize(.7,frame,null);
		center(frame,null);
		
		this.closeable=closeable;
		frame.addWindowListener(new EscuchaWindow());
		
		frame.setTitle("Console for: "+_getMainClass());
	}		

	public Console print(Object o)	
	{
		String txt = o!=null?o.toString():"null";
		
		if( !frame.isVisible() )  frame.setVisible(true);

		
		String[] reemAña = reemplazarOAñadir(_getText(),currCaretPosition,txt); 
		if( reemAña[0].length()>0 )
		{
			textArea.replaceRange(reemAña[0],currCaretPosition,currCaretPosition+reemAña[0].length());
			currCaretPosition+=reemAña[0].length();
		}
		
		if( reemAña[1].length()>0 )
		{
			textArea.insert(txt,currCaretPosition);
			currCaretPosition+=reemAña[1].length();
		}
			
		textArea.setCaretPosition(currCaretPosition);
		return console;
	}
		
	public Console println(Object s)
	{
		print(s+"\n");
		return console;
	}
	
	public Console println()
	{
		println("");
		return console;
	}
	
	public Console ln()
	{
		return println();
	}
	
	public Console skipBkp()
	{
		currCaretPosition=0;
		_setCaretPosition(currCaretPosition);
		return this;
	}

	public Console skipBkp(int n)
	{
		currCaretPosition-=n;
		_setCaretPosition(currCaretPosition);
		return this;
	}
	
	public Console skipFwd(int n)
	{
		currCaretPosition+=n;
		_setCaretPosition(currCaretPosition);
		return console;
	}

	
	public Console skipFwd()
	{
		currCaretPosition=_getText().length();
		_setCaretPosition(currCaretPosition);
		return console;
	}
	
	public String _readString(TriFunction<Character,Integer,String,Character> mask) //,BiFunction<Character,String,Character> mask)
	{
		try
		{
			reading = true;
			print("[]");
			
			_setCaretPosition(_getLenght()-1);
			inputPosition = _getLenght()-1;
			
			currentMask = mask;
	        
			latch = new CountDownLatch(1);
	        latch.await();
	        
			return inputText;			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String readString()
	{
		return _readString((c,kc,s)->c);
	}	
			
	public String readlnString()
	{
		String x = readString();
		println();
		return x;
	}	
			
	public Integer readInteger()
	{
		String s = _readString(INTEGER);
		return s!=null?Integer.parseInt(s):null;
	}
	
	public Integer readlnInteger()
	{
		Integer i = readInteger();
		println();
		return i;
	}

	public String readUppercaseString()
	{
		return _readString(UPPERCASE);
	}	
	
	public String readlnUppercaseString()
	{
		String x = readUppercaseString();
		println();
		return x;
	}
	

	public String readLowercaseString()
	{
		return _readString(LOWERCASE);
	}
	
	public String readlnLowercaseString()
	{
		String x = readLowercaseString();
		println();
		return x;
	}
	
	public Double readDouble()
	{
		String s = _readString(DOUBLE);
		return s!=null?Double.parseDouble(s):null;
	}	
	
	public Double readlnDouble()
	{
		Double d = readDouble();
		println();
		return d;
	}

	
	private int _getLenght()
	{
		return textArea.getText().length();
	}
	
	private void _setCaretPosition(int p)
	{
		textArea.setCaretPosition(p);
		currCaretPosition=p;
	}
	
	private String _getText()
	{
		return textArea.getText();
	}
	
	private static boolean _validKeyCode(int kc)
	{
		return kc==KeyEvent.VK_LEFT
		     ||kc==KeyEvent.VK_RIGHT
		     ||kc==KeyEvent.VK_BACK_SPACE
		     ||kc==KeyEvent.VK_ENTER;
	}

	
    private static String _getMainClass() 
    {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        for (StackTraceElement element : stackTrace) 
        {
            if (element.getMethodName().equals("main")) 
            {
                return element.getClassName();
            }
        }
        
        return null;
    }
    
    private static String[] reemplazarOAñadir(String original, int posicion, String reemplazo) {
        int longitudOriginal = original.length();
        int longitudReemplazo = reemplazo.length();

        String parteReemplaza;
        String parteAñadida = "";

        if (posicion + longitudReemplazo <= longitudOriginal) {
            parteReemplaza = reemplazo;
        } else {
            parteReemplaza = reemplazo.substring(0, longitudOriginal - posicion);
            parteAñadida = reemplazo.substring(longitudOriginal - posicion);
        }

        return new String[]{parteReemplaza, parteAñadida};
    }

    private void _applyStyle()
	{
		Font font = new Font("Lucida Console",Font.PLAIN,16);
		textArea.setFont(font);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.WHITE);
		textArea.setCaretColor(Color.WHITE);

		textArea.setBorder(null);
		scrollPane.setBorder(null);
	}
    
    public class EscuchaAnyKey extends KeyAdapter 
    {
        private Runnable r;
        private Integer k;

        public EscuchaAnyKey(Runnable r, int key) {
            this.r = r;
            this.k = key<0?null:key;
        }

        @Override
        public void keyPressed(KeyEvent e) 
        {
            anyKey = e.getKeyCode();
            
            // Consumir el evento para evitar la escritura en el JTextArea
            e.consume();

            if (anyKey == KeyEvent.VK_SHIFT || anyKey == KeyEvent.VK_CONTROL || anyKey == KeyEvent.VK_ALT || anyKey == KeyEvent.VK_ALT_GRAPH /*|| anyKey==KeyEvent.VK_ENTER*/) {
                return;
            }

            if (k == null || k.equals(anyKey) || anyKey==10) {
                reading = false;
                e.getComponent().removeKeyListener(this);
                latch.countDown();
                r.run();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {e.consume();}
        
        @Override
        public void keyReleased(KeyEvent e) {e.consume();}
    }
    
    private void _finalizarElPrograma()
    {
		int r = JOptionPane.showConfirmDialog(frame, "¿Esta acción finalizará el programa?", "Confirmación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if( r==JOptionPane.YES_OPTION )
		{
			frame.setVisible(false);
			frame.dispose();
			System.exit(0);
		}
    }
    
    
	class EscuchaKey implements KeyListener
	{
		private void _processKey(KeyEvent e)
		{
			if( e.isControlDown() && e.getKeyCode()==KeyEvent.VK_C || e.getKeyCode()==KeyEvent.VK_ESCAPE )
			{
				_finalizarElPrograma();
			}
			
			
			if( !reading )
			{
				e.consume();
				return;
			}
			
			inputText = _getText().substring(inputPosition,_getText().length()-1);
			Character c = currentMask.apply(e.getKeyChar(),e.getKeyCode(),inputText);
			if( c==null )
			{
				e.consume();
				return;
			}
			
			e.setKeyChar(c);
			int kc = e.getKeyCode();	

			// enter
			if(kc==KeyEvent.VK_ENTER )
			{
				e.consume();
//				textArea.append("\n");
				int len = _getLenght();
				_setCaretPosition(len);
				reading = false;
				latch.countDown();
				return;
			}
			
			int abre = inputPosition;
			int caret = textArea.getCaretPosition();
			int cierra = _getText().lastIndexOf("]");
			
			boolean consume = (kc==KeyEvent.VK_LEFT && caret<=abre)
					       || (kc==KeyEvent.VK_BACK_SPACE && caret<=abre)
					       || (kc==KeyEvent.VK_RIGHT && caret>=cierra)
					       || (kc==KeyEvent.VK_PAGE_DOWN || kc==KeyEvent.VK_PAGE_UP);
			
			if( consume )
			{
				e.consume();
				return;
			}
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			_processKey(e);
		}

		@Override
		public void keyTyped(KeyEvent e)
		{
			if(!reading) e.consume();
			Character c;
			if( (c=currentMask.apply(e.getKeyChar(),e.getKeyCode(),inputText))==null )
			{
				e.consume();
				return;
			}
			
			e.setKeyChar(c);

		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			if(!reading) e.consume();
			Character c;
			if( (c=currentMask.apply(e.getKeyChar(),e.getKeyCode(),inputText))==null )
			{
				e.consume();
				return;
			}
			
			e.setKeyChar(c);
		}				
	}

	class EscuchaMouse extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			if( reading )
			{
				_setCaretPosition(inputPosition);
			}
		}
	}
	
	class EscuchaWindow extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			if( closeable )
			{
				frame.setVisible(false);
			}
			else
			{
				_finalizarElPrograma();
			}
		}
	}
	
	private static boolean isHexDigit(char c)
	{
		c = Character.toUpperCase(c);
		return 	isInRange(c,'0','9') ||	isInRange(c,'A','F');
	}
	
	private static boolean isInRange(char c,char lo,char up)
	{
		return c>=lo && c<=up;
	}
	
	private static Boolean parseBoolean(String text)
	{
		try
		{
			return Boolean.parseBoolean(text);
		}
		catch(Exception e)
		{
		}

		return null;
	}

	private static void setWindowsAndFeel()
	{
		try
		{
		    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private static void setProportionalSize(double porc, Window child, Window parent)
	{
        // Obtener el tamaño de b
        Dimension sizeB = parent!=null?parent.getSize():getScreenSize(1);

        // Calcular el nuevo tamaño de a
        int newWidth = (int) (sizeB.width * porc);
        int newHeight = (int) (sizeB.height * porc);

        // Ajustar el tamaño de a
        child.setSize(newWidth, newHeight);
	}
	
	private static Dimension getScreenSize(double porc)
	{
		Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
		Dimension x=new Dimension((int)(d.width*porc),(int)(d.height*porc));
		return x;
	}

	private static void center(Window child, Window parent)
	{
		if(parent==null)
		{
			Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
			int x=(int)(screenSize.getWidth()-child.getWidth())/2;
			int y=(int)(screenSize.getHeight()-child.getHeight())/2;
			child.setLocation(x,y);
		}
		else
		{
			int x=parent.getX()+(parent.getWidth()-child.getWidth())/2;
			int y=parent.getY()+(parent.getHeight()-child.getHeight())/2;
			child.setLocation(x,y);
		}
	}
	
	@FunctionalInterface
	interface TriFunction<A,B,C,R> {

	    R apply(A a, B b, C c);

	    default <V> TriFunction<A, B, C, V> andThen(
	                                Function<? super R, ? extends V> after) {
	        Objects.requireNonNull(after);
	        return (A a, B b, C c) -> after.apply(apply(a, b, c));
	    }
	}

	public String fileExplorer()
	{
		return fileExplorer(".");
	}
	public String fileExplorer(String dir)
	{
		JFileChooser jfc = new JFileChooser(dir);
		int rtdo = jfc.showOpenDialog(frame);
		
		return rtdo==JFileChooser.APPROVE_OPTION?jfc.getSelectedFile().getName():null;
	}

	public void closeAndExit()
	{
		frame.setVisible(false);
		frame.dispose();
		System.exit(0);
	}
}
