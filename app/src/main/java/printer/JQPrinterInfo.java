package printer;

public class JQPrinterInfo {
	
	public final static int STATE_NOPAPER_UNMASK = 0x01;
	public final static int STATE_OVERHEAT_UNMASK = 0x02;
	public final static int STATE_BATTERYLOW_UNMASK = 0x04;
	public final static int STATE_PRINTING_UNMASK = 0x08;
	public final static int STATE_COVEROPEN_UNMASK = 0x10;
	/*
	 * ȱʡ���캯��
	 */
	public JQPrinterInfo()
	{
	}

	public void stateReset()
	{
		isNoPaper = false;
		isOverHeat = false;
		isBatteryLow = false;
		isPrinting = false; 
		isCoverOpen = false;
	}
	/*
	 * ��ӡ��ȱֽ
	 * 0x01
	 */
	public boolean isNoPaper;

	/*
	 * ��ӡ����ӡͷ����
	 * 0x02
	 */
	public boolean isOverHeat;
	
	/*
	 * ��ӡ����ص�ѹ����
	 * 0x04
	 */
	public boolean isBatteryLow;
	
	/*
	 * ��ӡ�����ڴ�ӡ
	 * 0x08
	 */
	public boolean isPrinting;
	
	/*
	 * ��ӡ��ֽ�ָ�δ�ر�
	 * 0x10
	 */
	public boolean isCoverOpen;
}
