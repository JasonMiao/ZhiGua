package printer.esc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import java.io.File;

import printer.JQPrinter;
import printer.Port;
import printer.common.ImageConvert;

;

public class Image extends BaseESC {
	public enum IMAGE_MODE {
		SINGLE_WIDTH_8_HEIGHT(0x01), // ������8���
		DOUBLE_WIDTH_8_HEIGHT(0x00), // ����8���
		SINGLE_WIDTH_24_HEIGHT(0x21), // ������24���
		DOUBLE_WIDTH_24_HEIGHT(0x20); // ����24���
		private int _value;

		private IMAGE_MODE(int dots) {
			_value = dots;
		}

		public int value() {
			return _value;
		}
	}

	public Image(Port port, JQPrinter.PRINTER_TYPE printer_type) {
		super(port, printer_type);
	}

	// / <summary>
	// / ͼ���������ص���ӡ���ڴ�
	// / 1)ͼ������ɨ�跽ʽ�Ǵ����ң����ϵ���
	// / 2)�����ܴ�С:X_BYTES * Y_BYTES *8
	// / 3)X������� X_BYTES * 8
	// / 4)Y������� Y_BYTES * 8
	// / </SUMMARY>
	private boolean userImageDownloadIntoRAM(final int x_bytes,
			final int y_bytes, final byte[] data) {
		final byte[] cmd = { 0x1D, 0x2A, 0, 0 };
		if (x_bytes <= 0)
			return false;
		if (y_bytes <= 0 || y_bytes > 127)
			return false;
		final int all_data_size = x_bytes * y_bytes * 8;

		if (all_data_size > 1024)
			return false;
		if (all_data_size != data.length)
			return false;

		cmd[2] = (byte) x_bytes;
		cmd[3] = (byte) y_bytes;
		if (!port.write(cmd))
			return false;
		return port.write(data);
	}

	// / <summary>
	// / ����RAM��Ԥ�洢ͼ�񵽴�ӡ����
	// / 1)��ӡ������ӡ���ͼ��
	// / </summary>
	private boolean userImageDrawout(final ESC.IMAGE_ENLARGE mode) {
		final byte[] cmd = { 0x1D, 0x2F, 0x00 };
		cmd[2] = (byte) mode.ordinal();
		return port.write(cmd);
	}

	// / <summary>
	// / ����ͼ�񵽴�ӡ����ͼ����
	// / 1)�������̴�ӡ���,һЩ����ᵼ�´�ӡ�������
	// / A. ���лس�(\r \n ����\r\n)
	// / B ��ӡ������x���곬��������
	// / 2)ͼ��߶ȳ�����ӡ����߶ȸ߶Ȼᵼ���ϲ���ͼ��ʧ
	// / </summary>
	private boolean _drawOut(int image_width_dots, int image_height_dots,
							 ESC.IMAGE_ENLARGE mode, byte[] image_data)
	{
		int Y_Byte = (image_height_dots - 1) / 8 + 1; // λͼY�᷽�����ص���ֽ��أ�
		int X_Byte = (image_width_dots - 1) / 8 + 1; // λͼX�᷽�����ص���ֽ��أ���ʾ��ҪX_Byte��8
														// x BmpHeight��λͼƴ��Ŀ��λͼ��
		byte[] DotsBuf = new byte[Y_Byte * 8]; // ���8 x BmpHeightλͼ�ĵ������ݣ�
		for (int i = 0; i < DotsBuf.length; i++)
			DotsBuf[i] = 0;
		int DotsBufIndex = 0; // 8xBmpHeight��������
		int DotsByteIndex = 0; // ԭʼλͼ��������
		for (int i = 0; i < X_Byte; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < Y_Byte; k++) {
					DotsByteIndex = k * image_width_dots + i * 8 + j;
					if ((i << 3) + j < image_width_dots) // ����ȴ���λͼʵ�ʿ���ǣ���������Ϊ0����Ϊ����λͼ���Ϊ8������������ʵ�ʿ�ȿ��ܲ���������
						DotsBuf[DotsBufIndex++] = (byte) image_data[DotsByteIndex];
					else
						DotsBuf[DotsBufIndex++] = 0x00;
				}
			}
			DotsBufIndex = 0;
			userImageDownloadIntoRAM(1, Y_Byte, DotsBuf); // ����λͼ
			userImageDrawout(mode); // ��ӡ����λͼ
		}
		return true;
	}
	
	private boolean _drawOut(int image_width_dots, int image_height_dots,
							 ESC.IMAGE_ENLARGE mode, char[] image_data)
	{
		int Y_Byte = (image_height_dots - 1) / 8 + 1; // λͼY�᷽�����ص���ֽ��أ�
		int X_Byte = (image_width_dots - 1) / 8 + 1; // λͼX�᷽�����ص���ֽ��أ���ʾ��ҪX_Byte��8
														// x BmpHeight��λͼƴ��Ŀ��λͼ��
		byte[] DotsBuf = new byte[Y_Byte * 8]; // ���8 x BmpHeightλͼ�ĵ������ݣ�
		for (int i = 0; i < DotsBuf.length; i++)
			DotsBuf[i] = 0;
		int DotsBufIndex = 0; // 8xBmpHeight��������
		int DotsByteIndex = 0; // ԭʼλͼ��������
		for (int i = 0; i < X_Byte; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < Y_Byte; k++) {
					DotsByteIndex = k * image_width_dots + i * 8 + j;
					if ((i << 3) + j < image_width_dots) // ����ȴ���λͼʵ�ʿ���ǣ���������Ϊ0����Ϊ����λͼ���Ϊ8������������ʵ�ʿ�ȿ��ܲ���������
						DotsBuf[DotsBufIndex++] = (byte) image_data[DotsByteIndex];
					else
						DotsBuf[DotsBufIndex++] = 0x00;
				}
			}
			DotsBufIndex = 0;
			userImageDownloadIntoRAM(1, Y_Byte, DotsBuf); // ����λͼ
			userImageDrawout(mode); // ��ӡ����λͼ
		}
		return true;
	}

	// / <summary>
	// / �����������ͼ�񵽴�ӡ������
	// / 1)ͼ��߶Ȳ��ܴ��ڶԴ�ӡ������߶�
	// / 2)����ͼ��û����������������Լ�������Ӧ��x,y������ƴ�ӡ����
	// / </summary>
	public boolean drawOut(int x, int y, int image_width_dots,
						   int image_height_dots, ESC.IMAGE_ENLARGE mode, byte[] image_data)
	{
		if (!setXY(x, y))
			return false;
		return _drawOut(image_width_dots, image_height_dots, mode, image_data);
	}
	
	// / <summary>
	// / �����������ͼ�񵽴�ӡ������
	// / 1)ͼ��߶Ȳ��ܴ��ڶԴ�ӡ������߶�
	// / 2)����ͼ��û����������������Լ�������Ӧ��x,y������ƴ�ӡ����
	// / </summary>
	public boolean drawOut(int x, int y, int image_width_dots,
						   int image_height_dots, ESC.IMAGE_ENLARGE mode, char[] image_data) {
		if (!setXY(x, y))
			return false;
		return _drawOut(image_width_dots, image_height_dots, mode, image_data);
	}

	// / <summary>
	// / ����bitmap����ʹ���Զ���λͼ��ʽ����ͼ�񵽴�ӡ����
	// / 1)ͼ��߶Ȳ��ܴ��ڶԴ�ӡ������߶�
	// / 2)����ͼ��û����������������Լ�������Ӧ��x,y������ƴ�ӡ����
	// / </summary>
	public boolean drawOut(int x, int y, Bitmap bitmap) 
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width > this.maxDots)// || height > this.canvasMaxHeight)
		{
			Log.e("JQ", "w:" + width + " > " + maxDots);
			return false;
		}
		if (height > this.canvasMaxHeight) {
			Log.e("JQ", "h:" + height + " > " + canvasMaxHeight);
			return false;
		}

		ImageConvert conver = new ImageConvert();
		byte[] data = conver.CovertImageVertical(bitmap, 128, 8);

		if (data == null)
			return false;
		if (!setXY(x, y))
			return false;
		return _drawOut(width, height, ESC.IMAGE_ENLARGE.NORMAL, data);
	}

	// / <summary>
	// / ����ͼƬ·����ʹ���Զ���λͼ��ʽ����ͼ�񵽴�ӡ����
	// / 1)ͼ��߶Ȳ��ܴ��ڶԴ�ӡ������߶�
	// / 2)����ͼ��û����������������Լ�������Ӧ��x,y������ƴ�ӡ����
	// / </summary>
	public boolean drawOut(int x, int y, String image_path) {
		if (new File(image_path).exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(image_path);
			return drawOut(x, y, bitmap);
		} else {
			Log.e("JQ", "�ļ�·������:" + image_path);
			return false;
		}
	}

	// / <summary>
	// / ����bitmap�����ӡͼƬ
	// / 1)���������г��Ҽ������ͺŵ�POS��ӡ��
	// / 2)ͼ���������,������ͼ���ϻ�������
	// / </summary>
	public boolean printOut(int x, Bitmap bitmap, int sleep_time) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width > this.maxDots)
			return false;
		ImageConvert conver = new ImageConvert();
		byte[] data = conver.CovertImageVertical(bitmap, 128, 8);

		if (data == null)
			return false;
		return _printOut(x, width, height,
				ESC.IMAGE_MODE.SINGLE_WIDTH_8_HEIGHT, data, sleep_time);
	}

	// / <summary>
	// / �����ļ�·����ӡͼƬ
	// / 1)���������г��Ҽ������ͺŵ�POS��ӡ��
	// / 2)ͼ���������,������ͼ���ϻ�������
	// / </summary>
	public boolean printOut(int x, String image_path, int sleep_time)
	{
		if (new File(image_path).exists()) 
		{
			Bitmap bitmap = BitmapFactory.decodeFile(image_path);
			return printOut(x, bitmap, sleep_time);
		} else {
			Log.e("JQ", "�ļ�·������:" + image_path);
			return false;
		}
	}

	// / <summary>
	// / ���ϵ��°�һ����ͼƬ�ָ��n=((height-1)/8+1)��СͼƬ,ÿ��Сͼ���width����8���㡣
	// / 1)���������г��Ҽ��ͺŵ�POS��ӡ��
	// / </summary>
	private boolean _printOut(int x, int width, int height,
							  ESC.IMAGE_MODE mode, byte[] data, int sleep_time) {
		if (width > this.maxDots) {
			return false;
		}

		if (mode == ESC.IMAGE_MODE.SINGLE_WIDTH_8_HEIGHT
				|| mode == ESC.IMAGE_MODE.DOUBLE_WIDTH_8_HEIGHT)
		{
		} 
		else 
		{
			Log.e("JQ","ͼ��ģʽ����");
			return false;
		}

		int count; // �ָ�ɶ��ٸ�ͼƬ
		count = (height - 1) / 8 + 1;
		if (data.length != width * count) {
			Log.e("JQ","���ݳ��Ⱥ� IMAGE_MODE������ƥ��");
			return false;
		}
		byte[] cmd = { 0x1B, 0x2A };// ��������ͷ
		this.setLineSpace(0);// �����м��Ϊ0
		for (int i = 0; i < count; i++) 
		{
			this.setXY(x, 0);
			port.write(cmd);
			port.write((byte) mode.value());
			port.write((short) width);
			port.write(data, i * width, width);
			port.write("\r\n");
			try {
				Thread.sleep(sleep_time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setLineSpace(8);// �ָ�ԭʼֵ �м��Ϊ8

		port.write("\r\n");
		return true;
	}

	// / <summary>
	// / �����ļ�·����ӡ���ٴ�ӡͼƬ
	// / 1)��Ϊ��ǿ���Ӷ���ָ������ݱ��Ʒ�ƵĴ�ӡ��
	// / 2)�˷����ǰ�һ����ͼƬ���ָ��n�ัСͼƬ(�߶�Ϊbase_image_height)����ӡ��
	// / 3)������λ�������ݴ����ٶȣ����Ե���base_image_height�Ĵ�С����ò�ͬ��ͼ���ӡ�ٶȡ�
	// / 4)ʹ�ô˷�����������������������Ʊ�Ķ����������Ҫ��ͼƬ�ϻ��Ʊ�Ĵ�ӡ������ʹ��drawOut��غ���
	// / 5)��Ҫ������µĴ�ӡ���̼�ʹ��
	// / </summary>

	public boolean printOutFast(int x, String image_path, int sleep_time,
			int base_image_height) 
	{
		if (new File(image_path).exists()) 
		{
			Bitmap bitmap = BitmapFactory.decodeFile(image_path);
			return printOutFast(x, bitmap, sleep_time, base_image_height);
		} else 
		{
			Log.e("JQ", "�ļ�·������:" + image_path);
			return false;
		}		
	}

	// / <summary>
	// / ͨ��Bitmap������ٴ�ӡͼ��
	// / 1)��Ҫ������µĴ�ӡ���̼�ʹ��
	// / </summary>
	public boolean  printOutFast(int x, Bitmap bitmap, int sleep_time,
			int base_image_height) 
	{
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		if (width > this.maxDots)
			return false;
		ImageConvert conver = new ImageConvert();
		byte[] data = conver.CovertImageHorizontal(bitmap, 128);

		if (data == null)
			return false;
		return _printOutFast(x, width, height, 1, 1, data, sleep_time,
				base_image_height);
	}

	// / <summary>
	// / ���ٴ�ӡͼƬ��������
	// / 1)��Ҫ������µĴ�ӡ���̼�ʹ��
	// / </summary>
	private boolean _printOutFast(int x, int width, int height, int enlargeX,
			int enlargeY, byte[] data, int sleep_time, int base_image_height) 
	{
		if (width > this.maxDots) {
			return false;
		}

		if (enlargeX > 2 || enlargeY > 2 || enlargeX < 0 || enlargeY < 0) {
			Log.e("JQ","ͼ��Ŵ����");
			return false;
		}

		int width_byte = (width - 1) / 8 + 1;
		if (width_byte * height != data.length) {
			Log.e("JQ", "data size error");
			return false;
		}

		byte[] cmd = { 0x1B, 0x2B };
		if (base_image_height < 4) {
			base_image_height = 4;
		}
		int HeightWriteUnit = base_image_height; // ÿ��ͼƬ�ĸ߶� 4 =< u < 56

		if (width_byte * HeightWriteUnit > 2000) {
			Log.e("JQ", "����ͼƬ����̫�࣬���Сͼ��߶�");
			return false;
		}

		int HeightWrited = 0;

		int HeightLeft = height; // ʣ�µĸ߶�
		this.setLineSpace(0);// �����м��Ϊ0
		for (; HeightLeft > 0;) {
			if (HeightLeft <= HeightWriteUnit) {
				HeightWriteUnit = HeightLeft;
			}
			this.setXY(x, 0);
			port.write(cmd);
			port.write((short) width); // ͼƬ���
			port.write((short) HeightWriteUnit); // ͼƬ�߶�
			port.write((byte) enlargeX);
			port.write((byte) enlargeY);
			port.write(data, HeightWrited * width_byte, HeightWriteUnit
					* width_byte);
			HeightWrited += HeightWriteUnit;
			HeightLeft -= HeightWriteUnit;
			if (sleep_time > 0)
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		this.setLineSpace(8);// �����м��Ϊ0
		return true;
	}
}
