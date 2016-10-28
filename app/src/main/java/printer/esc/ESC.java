package printer.esc;


import printer.JQPrinter;
import printer.Port;

public class ESC 
{
	 public enum CARD_TYPE_MAIN
     {
         CDT_AT24Cxx(0x01),
         CDT_SLE44xx(0x11),
         CDT_CPU(0x21);
        private int _value;
 		private CARD_TYPE_MAIN(int type)
 		{
 			_value = type;
 		}		
 		public int value()
 		{
 			return _value;
 		}
     };
     
	public static enum BAR_TEXT_POS
	{
		NONE,
		TOP,
		BOTTOM,				
	}
	public static enum BAR_TEXT_SIZE
	{
		ASCII_12x24,
		ASCII_8x16,						
	}
	
	public static enum BAR_UNIT
	{
		x1(1),
		x2(2),
		x3(3),
		x4(4);
		private int _value;
		private BAR_UNIT(int dots)
		{
			_value = dots;
		}		
		public int value()
		{
			return _value;
		}
	}
	public static class LINE_POINT
	{
		public int startPoint;
		public int endPoint;
		public LINE_POINT(){};
		public LINE_POINT(int start_point, int end_point)
		{
			startPoint = (short)start_point;
			endPoint = (short)end_point;
		}
	}
	/*
	 * ö�����ͣ��ı��Ŵ�ʽ
	 */
	public static enum 	TEXT_ENLARGE
	{
		NORMAL(0x00),                        //�����ַ� 
        HEIGHT_DOUBLE(0x01),                 //�����ַ�
        WIDTH_DOUBLE(0x10),                  //�����ַ�
        HEIGHT_WIDTH_DOUBLE (0x11);           //���߱����ַ�
        
        private int _value;
		private TEXT_ENLARGE(int mode)
		{
			_value = mode;
		}		
		public int value()
		{
			return _value;
		}
	}
	/*
	 * ö�����ͣ�����߶�
	 */
	public static enum FONT_HEIGHT
	{
		x24,                     
        x16,
        x32,
        x48,
        x64,        
	}
	
	private byte[] cmd ={0,0,0,0,0,0,0,0};
	private Port port;
	public Text text;
	public Image image;
	public Graphic graphic;
	public Barcode barcode;
	public CardReader card_reader;
	public ESC(Port port, JQPrinter.PRINTER_TYPE printer_type)
	{
		if (port== null)
			return;
		this.port = port;
		text = new Text(port,printer_type);
		image = new Image(port,printer_type);
		graphic = new Graphic(port,printer_type);
		barcode = new Barcode(port,printer_type);
		card_reader = new CardReader(port,printer_type);
	}

	public static enum IMAGE_MODE
    {
        SINGLE_WIDTH_8_HEIGHT(0x01),        //������8���
        DOUBLE_WIDTH_8_HEIGHT(0x00),        //����8���
        SINGLE_WIDTH_24_HEIGHT(0x21),       //������24���
        DOUBLE_WIDTH_24_HEIGHT(0x20);       //����24���
        private int _value;
		private IMAGE_MODE(final int mode)
		{
			_value = mode;
		}
		public int value()
		{
			return _value;
		}
    }
	public static enum IMAGE_ENLARGE
	{
		NORMAL,//����
		HEIGHT_DOUBLE,//���� 
		WIDTH_DOUBLE,//����
		HEIGHT_WIDTH_DOUBLE	//���߱���	
	}
	
	public boolean init()
	{
		cmd[0] = 0x1B;	cmd[1] = 0x40;
		return port.write(cmd,0,2);		
	}
	
	/*
	 * ���Ѵ�ӡ��������ʼ��
	 */
	public boolean wakeUp() 
	{
		if(!port.writeNULL())
			return false;
		try {Thread.sleep(50);} catch (InterruptedException e) {}
		return init();
	}
	
	public boolean getState(byte []ret,int timerout_read)
    {
		port.flushReadBuffer();
		cmd[0] = 0x10;
		cmd[1] = 0x04;
		cmd[2] = 0x05;
		if (!port.write(cmd,0,3))
			return false;
		if (!port.read(ret, 2,timerout_read))
			return false;
		return true;
    }
	/*
	 * ���лس�
	 */
	public boolean feedEnter()
	{
		byte []cmd={0x0D,0x0A};
		return port.write(cmd);
	}
	/*
	 * ��ֽ����
	 * �������:
	 * --int lines:����
	 */
	public boolean feedLines(int lines)
	{
		byte []cmd={0x1B,0x64,00};
		cmd[2] = (byte)lines;
		return port.write(cmd);
	}
	/*
	 * ��ֽ����
	 * �������:
	 * --int dots:���ٸ���
	 */
	public boolean feedDots(int dots)
	{
		byte []cmd={0x1B,0x4A,00};
		cmd[2] = (byte)dots;
		return port.write(cmd);
	}
}


