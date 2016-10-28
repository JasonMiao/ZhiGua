package printer.esc;



import printer.JQPrinter;
import printer.Port;

public class BaseESC {
	protected Port port;
	protected JQPrinter.PRINTER_TYPE printerType;
	protected int maxDots;//�����ӡ�Ĵ����
	protected int canvasMaxHeight;//��ӡ���������߶ȣ���λdots.	
	/*
	 * ���캯��
	 */
	public BaseESC(Port port, JQPrinter.PRINTER_TYPE printer_type)
	{
		if (port== null)
			return;
		this.port = port;
		
		printerType = printer_type;
		switch(printerType)
		{
			case VMP02:
				maxDots = 384;
				canvasMaxHeight = 100;
				break;		
			case VMP02_P:
				maxDots = 384;
				canvasMaxHeight = 200;
				break;
			case ULT113x:
				maxDots = 576;
				canvasMaxHeight = 120;
				break;
			case JLP351:
				maxDots = 576;
				canvasMaxHeight = 250;
				break;
			default:
				maxDots = 576;
				canvasMaxHeight = 100;
				break;
		}				
	}
	/*
	 * ���ô�ӡ�����x��y����
	 */
	public boolean setXY(int x,int y)
	{	
		if (x < 0 || x >= maxDots || x > 0x1FF)
		{
            return false;
        }

        if (y < 0 || y >= canvasMaxHeight || y > 0x7F)
        {
            return false;
        }
        
        byte[] cmd ={0x1B, 0x24, 0x00, 0x00};
		int pos = ((x & 0x1FF) | ((y & 0x7F) << 9));
		cmd[2] = (byte)pos;
		cmd[3] = (byte)(pos>>8);
		port.write(cmd);		
        return true;
	}
	/*
	 * ���ô�ӡ������뷽ʽ
	 * ֧�ִ�ӡ����:�ı�(text),����(barcode)
	 */
	public boolean setAlign(JQPrinter.ALIGN align)
	{
		byte []cmd = { 0x1B, 0x61, 0x00};
		cmd[2] = (byte)align.ordinal();
		return port.write(cmd);
	}
	public boolean setLineSpace(int dots)
	{
        byte[] cmd = { 0x1B, 0x33, 0x00 };
        cmd[2] = (byte)dots;
		return port.write(cmd);
	}
	
	public boolean init()
	{
		byte []cmd= { 0x1B, 0x40};
		return port.write(cmd);		
	}
	
	 /// <summary>
    /// ���лس�
    /// </summary>
    public boolean enter()
    {
         byte[] cmd = { 0x0D, 0x0A };
         return port.write(cmd);
    }
	
	
}
